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
import java.io.OutputStream;

/* Changes:
 * 3.6.2014		Urs Graf		exception handling added
 */
/**
*
* Stream to write bytes to the RN131WiFly.
* Don't forget to initialize the RN131WiFly before using this stream.
* 
*/
public class RN131WiFlyOutputStream extends OutputStream {

    /**
     * Writes a single byte to this stream. Only the least significant byte of
     * the integer {@code b} is written to the stream.
     *
     * @param oneByte
     *            the byte to be written.
     */
	public void write(int b) {
		try {
			RN131WiFly.write((byte)b);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public int write(byte[] b){
		return RN131WiFly.write(b);
	}
	
	public int write(byte[] b, int len){
		return RN131WiFly.write(b, len);
	}
	
	public int write(byte[] b, int off, int len){
		return RN131WiFly.write(b, off, len);
	}
	
	public int freeSpace() {
		return RN131WiFly.availToWrite();
	}

	public void reset() {
		RN131WiFly.clear();
	}

}
