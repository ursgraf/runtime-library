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

/**
*
* Stream to read bytes from the RN131WiFly.
* Don't forget to initialize the RN131WiFly before using this stream.
* 
*/
public class RN131WiFlyInputStream extends InputStream{

	/**
	 * Returns the number of bytes available from the stream.
	 * 
	 * @return number of bytes available.
	 */
	public int available() {
		return RN131WiFly.availToRead();
	}
	
	/**
	 * Reads one byte from the RN131WiFly. A call of this 
	 * method is not blocking!
	 * 
	 * @return byte read
	 */
	public int read() {
		int cnt = 0;
		try{
			cnt = RN131WiFly.read();
		} catch (IOException e) { e.printStackTrace(); }
		return cnt;
	}
	
	/**
	 * Reads one byte from the RN131WiFly. A call of this 
	 * method is not blocking!
	 * 
	 * @param b
	 * 			Byte array to write the received data.
	 * @return the number of bytes read.
	 */
	public int read(byte b[]){
		int cnt = 0;
		try{
			cnt = RN131WiFly.read(b);
		} catch (IOException e) { e.printStackTrace(); }
		return cnt;
	}

	/**
	 * Reads one byte from the RN131WiFly. A call of this 
	 * method is not blocking!
	 * 
	 * @param b
	 * 				Byte array to write the received data.
	 * @param off
	 * 				Offset in the array to start writing the data.
	 * @param len 
	 * 				Length (number of bytes) to read.
	 * @return the number of bytes read.
	 */
	public int read(byte b[], int off, int len){
		int cnt = 0;
		try{
			cnt = RN131WiFly.read(b, off, len);
		} catch (IOException e) { e.printStackTrace(); }
		return cnt;
	}
}
