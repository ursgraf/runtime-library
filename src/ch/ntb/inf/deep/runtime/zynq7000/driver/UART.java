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
import ch.ntb.inf.deep.runtime.arm32.IrqInterrupt;
import ch.ntb.inf.deep.runtime.util.ByteFifo;
import ch.ntb.inf.deep.runtime.zynq7000.Izynq7000;
import ch.ntb.inf.deep.unsafe.arm.US;

/**
 * <p>Interrupt controlled driver for the <i>Serial Communication Interface 1</i> or
 * the <i>Serial Communication Interface 2</i> of the Freescale MPC555.</p>
 * <p><b>Remember:</b><br>
 * Depending on the baudrate configured, the effective baudrate can be different.
 * This may cause miss interpretation of the bytes sent at the receiver! For more
 * details, please consider table 14-29 in chapter 14.8.7.3 in the <a href=
 * "http://www.ntb.ch/infoportal/_media/embedded_systems:mpc555:mpc555_usermanual.pdf"
 * >MPC555 User's manual</a>.
 * </p>
 */
public class UART extends IrqInterrupt implements Izynq7000 {

	public static final int pUART0 = 0; 
	public static final int pUART1 = 1; 
	public static final int QUEUE_LEN = 2047;

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
	private int diff; // used to access register interface for UART0 or UART1
	private static UART uart0, uart1;

	/**
	 * Returns an instance of <i>Serial Communication Interface</i> 
	 * operating the SCI1 or SCI2.
	 * @param sciNr 0 selects SCI1, 1 selects SCI2
	 * @return Instance of SCI
	 */
	public static UART getInstance(int uartNr) {
		if (uartNr == pUART0) {
			if (uart0 == null) {
				uart0 = new UART(0);
			}
			return uart0;
		} else if (uartNr == pUART1) {
			if (uart1 == null) {
				uart1 = new UART(UART1_CR - UART0_CR);
				UART rxInt = new UART(-1);
				rxInt.diff = UART1_CR - UART0_CR;
				IrqInterrupt.install(rxInt, 82);		
			}
			return uart1;
		} else return null;
	}

	private UART(int regDiff) {
		if (regDiff >= 0) {
			diff = regDiff;
			out = new UARTOutputStream(this);
			in = new UARTInputStream(this);

			rxQueue = new ByteFifo(QUEUE_LEN);
		}
	}

	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Interrupt#action()
	 */
	@Override
	public void action() {
		UART uart;
		if (diff == 0) uart = uart0; else uart = uart1;
//		if (diff == 0) intCtr1++; else intCtr2++;

		int ch = US.GET4(UART1_FIFO);
		uart.rxQueue.enqueue((byte)ch);
		US.PUT4(UART1_ISR, 1);	// clear interrupt status bit
	}

	public void start(int baudRate, short parity, short data) {
		US.PUT4(SLCR_UNLOCK, 0xdf0d);
		US.PUT4(MIO_PIN_48, 0x12e0);	// tx
		US.PUT4(MIO_PIN_49, 0x12e1);	// rx
		US.PUT4(SLCR_LOCK, 0x767b);
		US.PUT4(UART1_CR, 0x14);	// enable tx, rx
		US.PUT4(UART1_BAUDGEN, 54);	// CD = 54
		US.PUT4(UART1_BAUDDIV, 15);	// BDIV = 15
		
		US.PUT4(UART1_IER, 1);		// enable rx FIFO trigger interrupt
		US.PUT4(UART1_RX_FIFO_LEVEL, 1);		// set rx FIFO trigger level to 1
		
		US.PUT4(UART1_MR, 0x20);	// no parity
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
		while(!US.BIT(UART1_SR, 3));
		US.PUT1(UART1_FIFO, b); 
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
		return rxQueue.availToRead();
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
//		int ch = US.GET4(UART1_FIFO);
//		US.PUT4(UART1_ISR, 1);
//		return ch;
		return rxQueue.dequeue();
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
			buffer[off + i] = rxQueue.dequeue();
		}
		return len;
	}

}
