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
 * 13.10.2011	Martin Zueger	reset() implemented, JavaDoc fixed
 * 06.01.2010	Simon Pertschy	initial version
 */

/**
 *
 * Stream to write bytes to the SCI1.
 * Don't forget to initialize the SCI1 before using this stream.
 * 
 */
public class SCI1OutputStream extends OutputStream {

    /**
     * Writes a single byte to this stream. Only the least significant byte of
     * the integer {@code oneByte} is written to the stream.
     *
     * @param oneByte
     *            the byte to be written.
     */
	public void write(int b) {
		try {
			SCI1.write((byte)b);
		} catch (IOException e) {e.printStackTrace();}
	}

    /**
     * Equivalent to {@code write(buffer, 0, buffer.length)}.
     */
	public void write(byte buffer[]) {
		try {
			SCI1.write(buffer);
		} catch (IOException e) {e.printStackTrace();}
	}
	
    /**
     * Writes {@code count} bytes from the byte array {@code buffer} starting at
     * position {@code offset} to this stream.
     *
     * @param buffer
     *            the buffer to be written.
     * @param offset
     *            the start position in {@code buffer} from where to get bytes.
     * @param count
     *            the number of bytes from {@code buffer} to write to this
     *            stream.
     */
	public void write(byte b[], int off, int len) {
		try {
			SCI1.write(b, off, len);
		} catch (IOException e) {e.printStackTrace();}
	}

}
