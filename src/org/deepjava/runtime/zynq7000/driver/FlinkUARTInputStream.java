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

package org.deepjava.runtime.zynq7000.driver;

import java.io.IOException;
import org.deepjava.flink.subdevices.FlinkUART;

/**
*
* Input Stream to read bytes from a UART interface.
* Don't forget to initialize the UART before using this class.
* 
*/
public class FlinkUARTInputStream extends UARTInputStream {
	
	private FlinkUART port;

	/**
	 * Creates an input stream on a given UART interface.
	 * @param uart UART number.
	 */
	public FlinkUARTInputStream(FlinkUART uart) {
		super(null);
		port = uart;
	}

	/**
	 * Returns the number of bytes available from the stream.
	 * 
	 * @return number of bytes available.
	 */
	public int available() {
		return port.availToRead();
	}

	/**
	 * Reads one byte from the UART. A call of
	 * this method is not blocking!
	 * 
	 * @return byte read
	 */
	public int read() {
		try {
			return port.read();
		} catch (IOException e) {e.printStackTrace(); return 0;}
	}

	/**
	 * Reads the given number of bytes from the UART. A call of
	 * this method is not blocking!
	 * 
	 * @param buffer
	 *            Byte array to write the received data.
	 * @return the number of bytes read. 
	 */
	public int read(byte buffer[]){
		try {
			return port.read(buffer);
		} catch (IOException e) {e.printStackTrace(); return 0;}
	}

	/**
	 * Reads the given number of bytes from the UART. A call of
	 * this method is not blocking!
	 * 
	 * @param buffer
	 *            Byte array to write the received data.
	 * @param off
	 *            Offset in the array to start writing the data.
	 * @param count
	 *            Length (number of bytes) to read.
	 * @return the number of bytes read.
	 */
	public int read(byte buffer[], int off, int count){
		try {
			return port.read(buffer, off, count);
		} catch (IOException e) {e.printStackTrace(); return 0;}
	}
	
}
