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
import java.io.InputStream;

/* Changes:
 * 3.6.2014		Urs Graf		initial version
 */

/**
 *
 * Input Stream to read bytes from an UART interface.
 * Don't forget to initialize the interface before using this class.
 * 
 */
public class UARTInputStream extends InputStream {
	public static final int pPSC1 = 0; 
	public static final int pPSC2 = 1; 
	public static final int pPSC3 = 2; 
	public static final int pPSC4 = 3; 
	public static final int pPSC5 = 4; 
	public static final int pPSC6 = 5; 
	
	private int port;
	
    /**
     * Creates an input stream on a given SCI interface.
     * @param uart UART number.
     */
    public UARTInputStream(int uart) {
		port = uart;
	}

	/**
	 * Returns the number of bytes available from the stream.
	 * 
	 * @return number of bytes available.
	 */
	public int available() {
		switch (port) {
		case pPSC1:
			return UART3.availToRead();
		case pPSC2:
			return UART3.availToRead(); 
		case pPSC3:
			return UART3.availToRead(); 
		case pPSC4:
			return UART3.availToRead(); 
		case pPSC5:
			return UART3.availToRead(); 
		case pPSC6:
			return UART6.availToRead(); 
		default:
			break;
		}
		return 0;
	}

	/**
	 * Reads one byte from the UART3. A call of
	 * this method is not blocking!
	 * 
	 * @return byte read
	 */
	public int read() {
		int cnt = 0;
		try {
			switch (port) {
			case pPSC1:
				cnt = UART3.read(); break;
			case pPSC2:
				cnt = UART3.read(); break;
			case pPSC3:
				cnt = UART3.read(); break;
			case pPSC4:
				cnt = UART3.read(); break;
			case pPSC5:
				cnt = UART3.read(); break;
			case pPSC6:
				cnt = UART6.read(); break;
			default:
				break;
			}
		} catch (IOException e) {e.printStackTrace();}
		return cnt;
	}

	/**
	 * Reads the given number of bytes from the UART3. A call of
	 * this method is not blocking!
	 * 
	 * @param buffer
	 *            Byte array to write the received data.
	 * @return the number of bytes read. 
	 */
	public int read(byte buffer[]){
		int cnt = 0;
		try {
			switch (port) {
			case pPSC1:
				cnt = UART3.read(buffer); break;
			case pPSC2:
				cnt = UART3.read(buffer); break;
			case pPSC3:
				cnt = UART3.read(buffer); break;
			case pPSC4:
				cnt = UART3.read(buffer); break;
			case pPSC5:
				cnt = UART3.read(buffer); break;
			case pPSC6:
				cnt = UART6.read(buffer); break;
			default:
				break;
			}
		} catch (IOException e) {e.printStackTrace();}
		return cnt;
	}

	/**
	 * Reads the given number of bytes from the UART3. A call of
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
		int cnt = 0;
		try {
			switch (port) {
			case pPSC1:
				cnt = UART3.read(buffer, off, count); break;
			case pPSC2:
				cnt = UART3.read(buffer, off, count); break;
			case pPSC3:
				cnt = UART3.read(buffer, off, count); break;
			case pPSC4:
				cnt = UART3.read(buffer, off, count); break;
			case pPSC5:
				cnt = UART3.read(buffer, off, count); break;
			case pPSC6:
				cnt = UART6.read(buffer, off, count); break;
			default:
				break;
			}
		} catch (IOException e) {e.printStackTrace();}
		return cnt;
	}
	
}
