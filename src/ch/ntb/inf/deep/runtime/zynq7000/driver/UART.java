/*
 * Copyright 2011 - 2013 NTB University of Applied Sciences in Technology
 * Buchs, Switzerland, http://www.ntb.ch/inf
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package ch.ntb.inf.deep.runtime.zynq7000.driver;

import java.io.IOException;

import ch.ntb.inf.deep.runtime.util.ByteFifo;
import ch.ntb.inf.deep.runtime.zynq7000.IrqInterrupt;
import ch.ntb.inf.deep.runtime.zynq7000.Izynq7000;
import ch.ntb.inf.deep.unsafe.arm.US;

/**
 * <p>Interrupt controlled driver for the <i>UART0</i> or
 * the <i>UART1</i> of the Zynq7000.</p>
 * <p><b>Remember:</b><br>
 * Depending on the baudrate configured, the effective baudrate can be different.
 * This may cause miss interpretation of the bytes sent!</p>
 */
public class UART extends IrqInterrupt implements Izynq7000 {

	public static final int pUART0 = 0; 
	public static final int pUART1 = 1; 
	public static final byte NO_PARITY = 0, ODD_PARITY = 1, EVEN_PARITY = 2;
	public static final int PORT_OPEN = 9, SR_RXEMPTY = 1, IXR_RXOVR = 0, IXR_RXFULL = 2, IXR_TXEMPTY = 3, IXR_TXFULL = 4;
	public static final int IDLE_LINE_DET = 4, OVERRUN_ERR = 3, NOISE_ERR = 2,
			FRAME_ERR = 1, PARITY_ERR = 0, LENGTH_NEG_ERR = -1,
			OFFSET_NEG_ERR = -2, NULL_POINTER_ERR = -3;
	public static final int QUEUE_LEN = 2047, HW_QUEUE_LEN = 63;
	public static final int UART_CLK = 100000000; // Hz
	
	/**
	 * Output stream to write to this <i>UART</i>.
	 */
	public UARTOutputStream out;
	/**
	 * Input stream to read from this <i>UART</i>..
	 */
	public UARTInputStream in;

	/*
	 * rxQueue: the receive queue, head points to the front item, tail to tail
	 * item plus 1: head=tail -> empty q head is moved by the interrupt proc
	 */
	
	private ByteFifo rxQueue;
	/*
	 * txQueue: the transmit queue, head points to the front item, tail to tail
	 * item plus 1: head=tail -> empty q head is moved by the interrupt proc,
	 * tail is moved by the send primitives called by the application
	 */
	private ByteFifo txQueue;
	
	private int diff; // used to access register interface for UART0 or UART1
	private static UART uart0, uart1;
	static private boolean toQueue, fromQueue;

	/**
	 * Returns an instance of <i>UART Interface</i> 
	 * operating the UART0 or UART1.
	 * @param uartNr 0 selects UART0, 1 selects UART1
	 * @return Instance of UART
	 */
	public static UART getInstance(int uartNr) {
		if (uartNr == pUART0) {
			if (uart0 == null) uart0 = new UART(0);
			return uart0;
		} else if (uartNr == pUART1) {
			if (uart1 == null) uart1 = new UART(UART1_CR - UART0_CR);
			return uart1;
		} else return null;
	}

	private UART(int regDiff) {
		diff = regDiff;
		out = new UARTOutputStream(this);
		in = new UARTInputStream(this);
		rxQueue = new ByteFifo(QUEUE_LEN);
		txQueue = new ByteFifo(QUEUE_LEN);
		if (regDiff > 0) IrqInterrupt.install(this, 82);		
		else IrqInterrupt.install(this, 59);
	}

	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.arm.IrqInterrupt#action()
	 */
	@Override
	public void action() {
		UART uart;
		if (diff == 0) uart = uart0; else uart = uart1;
		int status = US.GET4(UART0_ISR + diff);
		if ((status & (1 << IXR_RXOVR)) != 0) {
			while ((US.GET4(UART0_SR + diff) & (1 << SR_RXEMPTY)) == 0) {
				rxQueue.enqueue((byte)US.GET4(UART0_FIFO + diff));
			}
			fromQueue = true;
			US.PUT4(UART0_ISR + diff, (1 << IXR_RXOVR));	// clear interrupt status bit
		} else if ((status & (1 << IXR_TXFULL)) != 0) {
			toQueue = true;
			US.PUT4(UART0_IER + diff, (1 << IXR_TXEMPTY));	// enable tx FIFO empty
			US.PUT4(UART0_ISR + diff, (1 << IXR_TXFULL));	// clear interrupt status bit
		} else {	// must be IXR_TXEMPTY
			ByteFifo queue = uart.txQueue;
			for (int i = 0; i < queue.availToRead() && i < HW_QUEUE_LEN; i++)
				try {
					US.PUT1(UART0_FIFO + diff, queue.dequeue());
				} catch (IOException e) {}
			if (queue.availToRead() == 0) {
				toQueue = false;
				US.PUT4(UART0_IDR + diff, (1 << IXR_TXEMPTY));	// disable tx FIFO empty
			}
			US.PUT4(UART0_ISR + diff, (1 << IXR_TXEMPTY));	// clear interrupt status bit
		}
	}

	public void start(int baudRate, short parity, short data) {
		final int BDIV = 15;
		US.PUT4(UART0_BAUDGEN + diff, UART_CLK / (baudRate * (BDIV + 1)));	// CD
		US.PUT4(UART0_BAUDDIV + diff, BDIV);
		int val = 0;
		if (parity == NO_PARITY) val |= 0x20;
		else if (parity == ODD_PARITY) val |= 8;
		if (data == 6) val |= 6;
		else if (data == 7) val |= 4;
		US.PUT4(UART0_MR + diff, val);	
		US.PUT4(UART0_IER + diff, (1 << IXR_TXFULL) + (1 << IXR_RXOVR));		// enable tx FIFO full interrupt and rx FIFO trigger interrupt
		US.PUT4(UART0_TX_FIFO_LEVEL + diff, HW_QUEUE_LEN);		// set tx FIFO trigger level to maximum
		US.PUT4(UART0_RX_FIFO_LEVEL + diff, HW_QUEUE_LEN);		// set rx FIFO trigger level to maximum		
		US.PUT4(UART0_CR + diff, 0x14);	// enable tx, rx
	}

	/**
	 * Writes a given byte into the transmit buffer.
	 * A call of this method is blocking! 
	 * If the buffer is full, the method blocks for a short period of time
	 * until a small amount of space is available again.
	 * After this an IOException is thrown.
	 * 
	 * @param b
	 *            Byte to write.
	 * @throws IOException 
	 *            if an error occurs while writing to this stream.
	 */
	public void write(byte b) throws IOException { 	 
		if (toQueue) txQueue.enqueue(b);
		else US.PUT1(UART0_FIFO + diff, b); 
	}

	/**
	 * Writes a given number of bytes into the transmit buffer.
	 * A call of this method is not blocking! There will only as
	 * many bytes written, which are free in the buffer.
	 * 
	 * @param buffer
	 *            Array of bytes to send.
	 * @return the number of bytes written.
	 * @throws IOException 
	 *            if an error occurs while writing to this stream.
	 */
	public int write(byte[] buffer) throws IOException {
		return write(buffer, 0, buffer.length);
	}

	/**
	 * Writes a given number of bytes into the transmit buffer.
	 * A call of this method is not blocking! There will only as
	 * many bytes written, which are free in the buffer.
	 * 
	 * @param buffer
	 *            Array of bytes to send.
	 * @param off
	 *            Offset to the data which should be sent.
	 * @param count
	 *            Number of bytes to send.
	 * @return the number of bytes written.
	 * @throws IOException
	 *            if an error occurs while writing to this stream.
	 * @throws NullPointerException
	 *            if {@code buffer} is null.
	 * @throws IndexOutOfBoundsException
	 *            if {@code off < 0} or {@code count < 0}, or if
	 *            {@code off + count} is bigger than the length of
	 *            {@code buffer}.
	 */
	public int write(byte[] buffer, int off, int count) throws IOException {
		int len = buffer.length;
		for (int i = 0; i < count; i++) {
			write(buffer[off + i]);
		}
		return len;
	}

	/**
	 * Returns the number of bytes available in the receive buffer.
	 * 
	 * @return number of bytes in the receive buffer.
	 */
	public int availToRead() {
		while ((US.GET4(UART0_SR + diff) & (1 << SR_RXEMPTY)) == 0) {
			rxQueue.enqueue((byte)US.GET4(UART0_FIFO + diff));
		}
		int count = rxQueue.availToRead();
		fromQueue = count > 0;
		return count;
	}

	/**
	 * Reads one byte from the UART. A call of
	 * this method is not blocking!
	 * 
	 * @return byte read.
	 * @throws IOException 
	 *            if no byte available.
	 */
	public int read() throws IOException {
		if (fromQueue) {
			int ch = rxQueue.dequeue();
			fromQueue = rxQueue.availToRead() > 0;
			return ch;
		} else return US.GET4(UART0_FIFO + diff);
	}

	/**
	 * Reads the given number of bytes from the UART. A call of
	 * this method is not blocking!
	 * 
	 * @param buffer
	 *            Byte array to write the received data.
	 * @return the number of bytes read. 
	 * @throws IOException 
	 *            if no data available.
	 */
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	/**
	 * Reads the given number of bytes from the UART. A call of
	 * this method is not blocking!
	 * 
	 * @param buffer
	 *            Byte aray to write the received data.
	 * @param off
	 *            Offset in the array to start writing the data.
	 * @param count
	 *            Length (number of bytes) to read.
	 * @return the number of bytes read.
	 * @throws IOException
	 *            if an error occurs while reading from this stream.
	 * @throws NullPointerException
	 *            if {@code buffer} is null.
	 * @throws IndexOutOfBoundsException
	 *            if {@code off < 0} or {@code count < 0}, or if
	 *            {@code off + count} is bigger than the length of
	 *            {@code buffer}.
	 */
	public int read(byte[] buffer, int off, int count) throws IOException {
	   	int len = buffer.length;
        if ((off | count) < 0 || off > len || len - off < count) {
        	throw new ArrayIndexOutOfBoundsException(len, off, count);
        }
		for (int i = 0; i < count; i++) {
			if (fromQueue) {
				buffer[off + i] = rxQueue.dequeue();
				fromQueue = rxQueue.availToRead() > 0;
			} else buffer[off + i] = (byte) US.GET4(UART0_FIFO + diff);
		}
		return len;
	}

}
