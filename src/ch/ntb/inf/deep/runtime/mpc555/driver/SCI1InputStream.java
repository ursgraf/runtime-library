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

package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.IOException;
import java.io.InputStream;

/* Changes:
 * 3.6.2014		Urs Graf		exception handling added
 * 13.10.2011	Martin Zueger	JavaDoc fixed
 * 06.01.2010	Simon Pertschy	initial version
 */

/**
 *
 * Input Stream to read bytes form the SCI1.
 * Don't forget to initialize the SCI1 before using this class.
 * 
 */
public class SCI1InputStream extends InputStream{

	/**
	 * Returns the number of bytes available from the stream.
	 * 
	 * @return number of bytes available.
	 */
	public int available() {
		return SCI1.availToRead();
	}

	/**
	 * Reads one byte from the SCI1. A call of
	 * this method is not blocking!
	 * 
	 * @return byte read
	 */
	public int read() {
		int cnt = 0;
		try {
			cnt = SCI1.read();
		} catch (IOException e) {e.printStackTrace();}
		return cnt;
	}

	/**
	 * Reads the given number of bytes from the SCI1. A call of
	 * this method is not blocking!
	 * 
	 * @param buffer
	 *            Byte array to write the received data.
	 * @return the number of bytes read. 
	 */
	public int read(byte buffer[]){
		int cnt = 0;
		try {
			cnt = SCI1.read(buffer);
		} catch (IOException e) {e.printStackTrace();}
		return cnt;
	}

	/**
	 * Reads the given number of bytes from the SCI1. A call of
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
			cnt = SCI1.read(buffer, off, count);
		} catch (IOException e) {e.printStackTrace();}
		return cnt;
	}
	
}
