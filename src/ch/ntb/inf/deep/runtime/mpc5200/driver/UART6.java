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

package ch.ntb.inf.deep.runtime.mpc5200.driver;

import java.io.IOException;

import ch.ntb.inf.deep.runtime.mpc5200.IphyCoreMpc5200tiny;
import ch.ntb.inf.deep.unsafe.US;

/**
 * <p>Driver for the <i>UART</i> of the Freescale MPC5200.</p>
 * <p><b>Remember:</b><br>
 * Depending on the baudrate configured, the effective baudrate can be different.
 * This may cause miss interpretation of the bytes sent at the receiver!
 * </p>
 */
/* Changes:
 * 3.6.2014		Urs Graf			exception handling added
 */
public class UART6 implements IphyCoreMpc5200tiny{
	public static UARTOutputStream out;
	public static UARTInputStream in;

	public static final byte NO_PARITY = 0, ODD_PARITY = 1, EVEN_PARITY = 2;

	// Error states
	public static final int IDLE_LINE_DET = 4, OVERRUN_ERR = 3, NOISE_ERR = 2,
			FRAME_ERR = 1, PARITY_ERR = 0, LENGTH_NEG_ERR = -1,
			OFFSET_NEG_ERR = -2, NULL_POINTER_ERR = -3;
	
	private static final int FIFO_LENGTH = 512;
	
	private static int state;

	/**
	 * <p>Initialize and start the <i>UART 6</i>.</p>
	 * <p>This method has to be called before using the UART6! The number of
	 * stop bits can't be set. There is always one stop bit!<p>
	 * 
	 * @param baudRate
	 *            The baud rate. Allowed Range: 64 to 500'000 bits/s.
	 * @param parity
	 *            Parity bits configuration. Possible values: {@link #NO_PARITY},
	 *            {@link #ODD_PARITY} or {@link #EVEN_PARITY}.
	 * @param data
	 *            Number of data bits. Allowed values are 5 to 8 bits. 
	 */
	public static void start(int baudRate, short parity, short data) {
		US.PUT1(PSC6Base + PSCCR, 0xa); // disable Tx, Rx
		US.PUT2(PSC6Base + PSCCSR, 0xff00); // CSR, prescaler 16
		US.PUT4(PSC6Base + PSCSICR, 0); // select UART mode
		if (parity == NO_PARITY)
			US.PUT1(PSC6Base + PSCMR1, 0x30 | (data-5) & 3); 
		else {
			if (parity == ODD_PARITY)
				US.PUT1(PSC6Base + PSCMR1, 0x24 | (data-5) & 3);
			else
				US.PUT1(PSC6Base + PSCMR1, 0x20 | (data-5) & 3);
		}
		US.PUT1(PSC6Base + PSCMR2, 0x7); // MR2, 1 stop bit
		int divider = 16500000 / baudRate; // IPB clock = 66MHz, prescaler = 4
		US.PUT1(PSC6Base + PSCCTUR, divider >> 8); 
		US.PUT1(PSC6Base + PSCCTLR, divider); 
		US.PUT1(PSC6Base + PSCTFCNTL, 0x1); // no frames
		US.PUT4(GPSPCR, US.GET4(GPSPCR) | 0x500000);	// use pins on PCS6 for UART
		US.PUT1(PSC6Base + PSCCR, 0x5); // enable Tx, Rx
		state = 1;
	}
	
	/**
	 * Writes a given byte into the transmit buffer.
	 * A call of this method is not blocking! That means
	 * the byte is lost if the buffer is already full!
	 * 
	 * @param b
	 *            Byte to write.
	 * @throws IOException 
	 *            if an error occurs while writing to this stream.
	 */
	public static void write(byte b) throws IOException {
		if (state == 0) throw new IOException("IOException");
		US.PUT1(PSC6Base + PSCTxBuf, b); 
	}

	/**
	 * Writes a given number of bytes into the transmit fifo.
	 * A call of this method is not blocking! There will only as
	 * many bytes written, which are free in the fifo.
	 * 
	 * @param buffer
	 *            Array of bytes to send.
	 * @return the number of bytes written.
	 * @throws IOException 
	 *            if an error occurs while writing to this stream.
	 */
	public static int write(byte[] buffer) throws IOException {
		return write(buffer, 0, buffer.length);
	}

	/**
	 * Writes a given number of bytes into the transmit fifo.
	 * A call of this method is not blocking! There will only as
	 * many bytes written, which are free in the fifo.
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
	public static int write(byte[] buffer, int off, int count) throws IOException{
		if (state == 0) throw new IOException("IOException");
    	int len = buffer.length;
		if ((off | count) < 0 || off > len || len - off < count) {
			throw new ArrayIndexOutOfBoundsException(len, off, count);
		}
		for (int i = 0; i < count; i++) {
			write(buffer[off + i]);
		}
		return len;
	}

	/**
	 * Returns the number of free bytes available in the transmit fifo.
	 * It is possible, to send the returned number of bytes in one
	 * nonblocking transfer.
	 * 
	 * @return the available free bytes in the transmit fifo.
	 */
	public static int availToWrite() {
		return FIFO_LENGTH - US.GET2(PSC6Base + PSCTFNUM);
	}

	/**
	 * Returns the number of bytes available in the receive fifo.
	 * 
	 * @return number of bytes in the receive fifo.
	 */
	public static int availToRead() {
		return US.GET2(PSC6Base + PSCRFNUM);
	}

	/**
	 * Reads one byte from the receive fifo. A call of
	 * this method is not blocking!
	 * 
	 * @return byte read.
	 * @throws IOException 
	 *            if no byte available.
	 */
	public static int read() throws IOException {
		return US.GET1(PSC6Base + PSCRxBuf);
	}
	
	/**
	 * Reads the given number of bytes from the UART6. A call of
	 * this method is not blocking!
	 * 
	 * @param buffer
	 *            Byte array to write the received data.
	 * @return the number of bytes read. 
	 * @throws IOException 
	 *            if no data available.
	 */
	public static int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}
	
	/**
	 * Reads the given number of bytes from the UART6. A call of
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
	public static int read(byte[] buffer, int off, int count) throws IOException {
	   	int len = buffer.length;
        if ((off | count) < 0 || off > len || len - off < count) {
        	throw new ArrayIndexOutOfBoundsException(len, off, count);
        }
		for (int i = 0; i < len; i++) {
			buffer[off + i] = (byte) read();
		}
		return len;
	}

	static {
		out = new UARTOutputStream(UARTOutputStream.pPSC6);
		in = new UARTInputStream(UARTInputStream.pPSC6);
	}
}
