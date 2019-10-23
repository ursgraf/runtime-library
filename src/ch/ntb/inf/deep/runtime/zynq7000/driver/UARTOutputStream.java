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
import java.io.OutputStream;

/**
 *
 * Stream to write bytes to a UART interface.
 * Don't forget to initialize the interface before using this stream.
 * 
 */
public class UARTOutputStream extends OutputStream {
	
	private UART port;

    /**
     * Creates an output stream on a given UART interface.
     * @param uart UART number.
     */
    public UARTOutputStream(UART uart) {
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
			port.write((byte)b);
		} catch (IOException e) {e.printStackTrace();}
	}

    /**
     * Equivalent to {@code write(buffer, 0, buffer.length)}.
     */
	public void write(byte buffer[]) {
		try {
			port.write(buffer);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void write(byte buffer[], int off, int count) {
		try {
			port.write(buffer, off, count);
		} catch (IOException e) {e.printStackTrace();}
	}
}
