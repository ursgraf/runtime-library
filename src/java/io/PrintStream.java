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

/* Changes:
 * 2010-01-06 moved and adapt to java.io.PrintStream by martin.zueger@ntb.ch 
 * 2009-12-18 created by simon.pertschy@ntb.ch 
 */

// TODO: Add missing functions: append(char c), checkError(), close(), flush(), setError(), print(long l), printf(String format, int... args)

package java.io;

/**
 * {@link OutputStream} adapter to write different data types as a string.
 * 
 */
public class PrintStream extends OutputStream{
	private static final boolean enableCR = true;
	
	static final char [ ] chars = new char[32];
	private OutputStream out;
	
	public PrintStream(OutputStream out){
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

// ---------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Terminates the line.
	 */
	public void println(){
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
	}
	
	
	/**
	 * Prints a single ascii <code>char</code> (8bit) to the output stream.
	 * @param c the char to write.
	 */
	public void print(char c){
		out.write((byte)c);
	}
	
	
	/**
	 * Prints a single ascii <code>char</code> (8bit) to the output stream and then terminates the line.
	 * @param c the char to write.
	 */
	public void println(char c){
		out.write((byte)c);
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
	}

	
	/**
	 * Prints, if possible, <code>chars.length</code> ascii chars (8bit each char) to the output stream.
	 * @param chars the chars to write
	 */
	public void print(char chars[]) {
		print(chars, 0, chars.length);
	}

	
	/**
	 * Prints, if possible, <code>chars.length</code> ascii chars (8bit each char) to the output stream and then terminates the line.
	 * @param chars the chars to write
	 */
	public void println(char chars[]) {
		print(chars, 0, chars.length);
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
	}
	
	
	/**
	 * Prints, if possible, <code>len</code> ascii chars (8bit each char) starting at
	 * <code>offset</code> to this output stream.
	 * @param b
	 *            the char array
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of char to write
	 * @return the number of written bytes.
	 */
	public int print(char b[], int off, int len) {
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
	 * Prints a string to the output stream.
	 * @param str The string to write.
	 * @return The number of written chars.
	 */
	public int print(String str) {
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
	 * Prints a string to the output stream and then terminates the line.
	 * @param str The string to write.
	 * @return The number of written chars.
	 */
	public int println(String str) {
		int len = str.length();
		int free = out.freeSpace();
		if (len > free)
			len = free - 1;
		for (int i = 0; i < len; i++) {
			out.write((byte) str.charAt(i));
		}
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
		return len;
	}
	
	
	/**
	 * Prints a boolean as a string to the output stream.
	 * @param b the boolean to write.
	 */
	public void print(boolean b) {
		if(b)
			print("true");
		else
			print("false");
	}
	
	/**
	 * Prints a boolean as a string to the output stream and then terminates the line.
	 * @param b the boolean to write.
	 */
	public void println(boolean b) {
		if(b)
			print("true");
		else
			print("false");
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
	}
	
	
	/**
	 * Prints a integer as a string to the output stream.
	 * @param val the integer to write.
	 */
	public void print(int val) {
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
	 * Prints a integer as a string to the output stream and then terminates the line.
	 * @param val the integer to write.
	 */
	public void println(int val) {
		print(val);
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
	}
	
	
	/**
	 * Prints a float as a string to the output stream.
	 * @param val the float to write.
	 */
	public void print(float val){
		int	nofChars = Double.doubleToChars(val, 6, chars);
		int	n = 0;
		while (n < nofChars) {	out.write((byte) chars[n]);	n++;	}
	}
	
	
	/**
	 * Prints a float as a string to the output stream and then terminates the line.
	 * @param val the float to write.
	 */
	public void println(float val){
		print(val);
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
	}
	
	
	/**
	 * Prints a double as a string to the output stream.
	 * @param val the double to write.
	 */
	public void print(double val){
		int	nofChars = Double.doubleToChars(val, 15, chars);
		int	n = 0;
		while (n < nofChars) {	out.write((byte)chars[n]);	n++;	}
	}
	
	
	/**
	 * Prints a double as a string to the output stream and then terminates the line.
	 * @param val the double to write.
	 */
	public void println(double val){
		print(val);
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
	}
	
	/**
	 * Prints a integer in hexadezimal notation as a string to the output stream.
	 * @param val the integer to write.
	 */
	public void printHex(int val) {
		if (val == 0) out.write((byte) '0');
		else {
			int ctr = chars.length;
			while (val != 0) {
				char ch = (char) ('0' + (val & 0xf));
				if (ch > '9') ch += 39;
				chars[--ctr] = ch;
				val = val >>> 4;
			}
			chars[--ctr] = 'x';
			chars[--ctr] = '0';
			for (int i = ctr; i < chars.length; i++)
				out.write((byte)chars[i]);
		}
	}
	
	
	/**
	 * Prints a integer in hexadezimal notation as a string to the output stream and then terminates the line.
	 * @param val the integer to write.
	 */
	public void printHexln(int val) {
		printHex(val);
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
	}

	/**
	 * Prints a long in hexadezimal notation as a string to the output stream.
	 * @param val the long to write.
	 */
	public void printHex(long val) {
		if (val == 0) out.write((byte) '0');
		else {
			int ctr = chars.length;
			while (val != 0) {
				char ch = (char) ('0' + (val & 0xf));
				if (ch > '9') ch += 39;
				chars[--ctr] = ch;
				val = val >>> 4;
			}
			chars[--ctr] = 'x';
			chars[--ctr] = '0';
			for (int i = ctr; i < chars.length; i++)
				out.write((byte)chars[i]);
		}
	}
	
	
	/**
	 * Prints a long in hexadezimal notation as a string to the output stream and then terminates the line.
	 * @param val the long to write.
	 */
	public void printHexln(long val) {
		printHex(val);
		if(enableCR) out.write((byte)'\r');
		out.write((byte)'\n');
	}

}
