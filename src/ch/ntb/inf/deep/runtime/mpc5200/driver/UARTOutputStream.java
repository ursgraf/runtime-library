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
import java.io.OutputStream;

/* Changes:
 * 3.6.2014		Urs Graf		initial version
 */

/**
 *
 * Stream to write bytes to a UART interface.
 * Don't forget to initialize the interface before using this stream.
 * 
 */
public class UARTOutputStream extends OutputStream {
	public static final int pPSC1 = 0; 
	public static final int pPSC2 = 1; 
	public static final int pPSC3 = 2; 
	public static final int pPSC4 = 3; 
	public static final int pPSC5 = 4; 
	public static final int pPSC6 = 5; 
	
	private int port;
	
    /**
     * Creates an output stream on a given UART interface.
     * @param uart UART number.
     */
    public UARTOutputStream(int uart) {
		port = uart;
	}

   /**
     * Writes a single byte to this stream. Only the least significant byte of
     * the integer {@code b} is written to the stream.
     *
     * @param b
     *            the byte to be written.
     */
	public void write(int b) {
		try {
			switch (port) {
			case pPSC1:
				UART3.write((byte)b); break;
			case pPSC2:
				UART3.write((byte)b); break;
			case pPSC3:
				UART3.write((byte)b); break;
			case pPSC4:
				UART3.write((byte)b); break;
			case pPSC5:
				UART3.write((byte)b); break;
			case pPSC6:
				UART6.write((byte)b); break;
			default:
				break;
			}
		} catch (IOException e) {e.printStackTrace();}
	}

    /**
     * Equivalent to {@code write(buffer, 0, buffer.length)}.
     */
	public void write(byte buffer[]) {
		try {
			switch (port) {
			case pPSC1:
				UART3.write(buffer); break;
			case pPSC2:
				UART3.write(buffer); break;
			case pPSC3:
				UART3.write(buffer); break;
			case pPSC4:
				UART3.write(buffer); break;
			case pPSC5:
				UART3.write(buffer); break;
			case pPSC6:
				UART6.write(buffer); break;
			default:
				break;
			}
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Writes {@code count} bytes from the byte array {@code buffer} starting at
	 * position {@code offset} to this stream.
	 *
	 * @param buffer
	 *            the buffer to be written.
	 * @param off
	 *            the start position in {@code buffer} from where to get bytes.
	 * @param count
	 *            the number of bytes from {@code buffer} to write to this
	 *            stream.
	 */
	public void write(byte buffer[], int off, int count) {
		try {
			switch (port) {
			case pPSC1:
				UART3.write(buffer, off, count); break;
			case pPSC2:
				UART3.write(buffer, off, count); break;
			case pPSC3:
				UART3.write(buffer, off, count); break;
			case pPSC4:
				UART3.write(buffer, off, count); break;
			case pPSC5:
				UART3.write(buffer, off, count); break;
			case pPSC6:
				UART6.write(buffer, off, count); break;
			default:
				break;
			}
		} catch (IOException e) {e.printStackTrace();}
	}
}
