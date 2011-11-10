/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.runtime.util;

import java.io.OutputStream;

/* Changes:
 * 18.12.2008	NTB/SP	creation
 */

/**
 * {@link OutputStream} adapter to write different data types as a string.
 * 
 */
public class CharOutputStream extends OutputStream{
	
	static final char [ ] chars = new char[32];
	private OutputStream out;
	
	public CharOutputStream(OutputStream out){
		this.out = out;
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#freeSpace()
	 */
	public int freeSpace() {
		return out.freeSpace();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#reset()
	 */
	public void reset() {
		out.reset();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte)
	 */
	public void write(byte b) {
		out.write(b);		
	}
	
	/**
	 * Writes a single ascii <code>char</code> (8bit) to the output stream.
	 * @param c the char to write.
	 */
	public void write(char c){
		out.write((byte)c);
	}


	/**
	 * Writes, if possible, <code>chars.length</code> ascii chars (8bit each char) to this output stream.
	 * @param chars the chars to write
	 * @return the number of written chars
	 */
	public int write(char chars[]) {
		return write(chars, 0, chars.length);
	}

	/**
	 * Writes, if possible, <code>len</code> ascii chars (8bit each char) starting at
	 * <code>offset</code> to this output stream.
	 * @param b
	 *            the char array
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of char to write
	 * @return the number of written bytes.
	 */
	public int write(char b[], int off, int len) {
		if (len < 0)
			return illegalLength;
		if (off < 0)
			return illegalOffset;
		int free = out.freeSpace();
		if (len > free)
			len = free;
		len += off;
		if (len > b.length)
			return illegalOffset;
		for (int i = off ; i < len; i++)
			out.write((byte) b[i]);
		return len;
	}

	/**
	 * Writes a string to the output stream.
	 * @param str The string to write.
	 * @return The number of written chars.
	 */
	public int write(String str) {
		int len = str.length();
		int free = out.freeSpace();
		if (len > free)
			len = free;
		for (int i = 0; i < len; i++) {
			out.write((byte) str.charAt(i));
		}
		return len;
	}
	
	/**
	 * Writes a boolean as a string to the output stream.
	 * @param bool the boolean to write.
	 */
	public void write(boolean bool) {
		if (bool)
			write("true");
		else
			write("false");
	}
	
	/**
	 * Writes a integer as a string to the output stream.
	 * @param val the integer to write.
	 */
	public void write(int val) {
		if (val == 0) out.write((byte) '0');
		else {
			boolean neg = false;
			if (val < 0) {
				neg = true;
				val *= -1;
			}
			int ctr = chars.length;
			while (val != 0) {
				chars[--ctr] = (char) ('0' + (val % 10));
				val /= 10;
			}
			if (neg)
				chars[--ctr] = '-';
			for (int i = ctr; i < chars.length; i++)
				out.write((byte)chars[i]);
		}
	}
	
	/**
	 * Writes a float as a string to the output stream.
	 * @param val the float to write.
	 */
	public void write(float val){
		int	nofChars = Double.doubleToChars(val, 6, chars);
		int	n = 0;
		while (n < nofChars) {	out.write((byte) chars[n]);	n++;	}
	}
	
	/**
	 * Writes a double as a string to the output stream.
	 * @param val the double to write.
	 */
	public void write(double val){
		int	nofChars = Double.doubleToChars(val, 15, chars);
		int	n = 0;
		while (n < nofChars) {	out.write((byte)chars[n]);	n++;	}
	}
	
}
