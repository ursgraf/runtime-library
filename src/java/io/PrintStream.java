/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.io;

import org.deepjava.marker.Modified;

/**
 * Wraps an existing {@link OutputStream} and provides convenience methods for
 * writing common data types in a human readable format. No {@code IOException} 
 * is thrown by this class. Instead, the stack trace is written to the err-
 * stream.
 * 
 * This class puts special focus on efficiency. No strings are allocated during
 * write operations!
 */
/* Changes:
 * 27.5.2014	Urs Graf	initial import and modified
 * 3.11.2015	Urs Graf	writing to out stream simplified
 */
public class PrintStream extends OutputStream implements Modified {
	private static final boolean enableCR = true;
	
	private static final byte [ ] chars = new byte[32];
	private static final char [ ] charsChar = new char[32];
	private OutputStream out;
	
	public PrintStream(OutputStream out){
		this.out = out;
	}
	
	/**
	 * Writes one byte to the target stream. Only the least significant byte of
	 * the integer {@code oneByte} is written. 
	 	 *
	 * @param b
	 *            the byte to be written
	 */
	public void write(int b) {
		try {
			out.write(b);
		} catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * Terminates the line.
	 */
	public void println(){
		try {
			if(enableCR) out.write('\r');
			out.write('\n');
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/**
	 * Prints a single ascii <code>char</code> (8bit) to the output stream.
	 * @param c the char to write.
	 */
	public void print(char c){
		try {
			out.write(c);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/**
	 * Prints a single ascii <code>char</code> (8bit) to the output stream and then terminates the line.
	 * @param c the char to write.
	 */
	public void println(char c){
		try {
			out.write(c);
		} catch (IOException e) {e.printStackTrace();}
		println();
	}

	
	/**
	 * Prints <code>chars.length</code> ascii chars (8bit each char) to the output stream.
	 * @param chars the chars to write
	 */
	public void print(char chars[]) {
		print(chars, 0, chars.length);
	}

	
	/**
	 * Prints <code>chars.length</code> ascii chars (8bit each char) to the output stream and then terminates the line.
	 * @param chars the chars to write
	 */
	public void println(char chars[]) {
		print(chars, 0, chars.length);
		println();
	}
	
	
	/**
	 * Prints <code>len</code> ascii chars (8bit each char) starting at
	 * <code>offset</code> to this output stream.
	 * @param b
	 *            the char array
	 * @param off
	 *            the start offset in the data.
	 * @param count
	 *            the number of char to write
	 * @return the number of written bytes.
	 */
	public int print(char b[], int off, int count) {
    	int len = b.length;
        if ((off | count) < 0 || off > len || len - off < count) {
        	throw new ArrayIndexOutOfBoundsException(len, off, count);
        }
        len += off;
        try {
        	for (int i = off; i < len; i++) out.write((byte) b[i]);
        } catch (IOException e) {e.printStackTrace();}
        return len;
	}
	
	
	/**
	 * Prints a string to the output stream.
	 * @param str The string to write.
	 * @return The number of written chars.
	 */
	public int print(String str) {
		int len = str.length();
		try {
			for (int i = 0; i < len; i++) {
				out.write((byte) str.charAt(i));
			}
		} catch (IOException e) {e.printStackTrace();}
		return len;
	}
	
	
	/**
	 * Prints a string to the output stream and then terminates the line.
	 * @param str The string to write.
	 * @return The number of written chars.
	 */
	public int println(String str) {
		int len = print(str);
		println();
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
		println();
	}
	
	
	/**
	 * Prints a integer as a string to the output stream.
	 * @param val the integer to write.
	 */
	public void print(int val) {
		try {
			if (val == 0) out.write((byte) '0');
			else {
				boolean neg = false;
				if (val < 0) {
					neg = true;
					val *= -1;
				}
				int ctr = chars.length;
				while (val != 0) {
					chars[--ctr] = (byte) ('0' + (val % 10));
					val /= 10;
				}
				if (neg)
					chars[--ctr] = '-';
				out.write(chars, ctr, chars.length-ctr);
			}
		} catch(IOException e) {e.printStackTrace();} 
	}

	
	/**
	 * Prints a integer as a string to the output stream and then terminates the line.
	 * @param val the integer to write.
	 */
	public void println(int val) {
		print(val);
		println();
	}
	
	/**
	 * Prints a long as a string to the output stream.
	 * @param val the long to write.
	 */
	public void print(long val) {
		try {
			if (val == 0) out.write((byte) '0');
			else {
				boolean neg = false;
				if (val < 0) {
					neg = true;
					val *= -1;
				}
				int ctr = chars.length;
				while (val != 0) {
					chars[--ctr] = (byte) ('0' + (val % 10));
					val /= 10;
				}
				if (neg)
					chars[--ctr] = '-';
				for (int i = ctr; i < chars.length; i++)
					out.write((byte)chars[i]);
			}
		} catch(IOException e) {e.printStackTrace();} 
	}


	/**
	 * Prints a long as a string to the output stream and then terminates the line.
	 * @param val the long to write.
	 */
	public void println(long val) {
		print(val);
		println();
	}
	
	/**
	 * Prints a float as a string to the output stream.
	 * @param val the float to write.
	 */
	public void print(float val){
		int	nofChars = Double.doubleToChars(val, 6, charsChar);
		for (int i = 0; i < nofChars; i++) chars[i] = (byte)charsChar[i];
		try {
			out.write(chars, 0, nofChars);
		} catch(IOException e) {e.printStackTrace();}
	}
	
	
	/**
	 * Prints a float as a string to the output stream and then terminates the line.
	 * @param val the float to write.
	 */
	public void println(float val){
		print(val);
		println();
	}
	
	
	/**
	 * Prints a double as a string to the output stream.
	 * @param val the double to write.
	 */
	public void print(double val){
		int	nofChars = Double.doubleToChars(val, 15, charsChar);
		for (int i = 0; i < nofChars; i++) chars[i] = (byte)charsChar[i];
		try {
			out.write(chars, 0, nofChars);
		} catch(IOException e) {e.printStackTrace();}
	}
	
	
	/**
	 * Prints a double as a string to the output stream and then terminates the line.
	 * @param val the double to write.
	 */
	public void println(double val){
		print(val);
		println();
	}
	
	/**
	 * Prints a integer in hexadezimal notation as a string to the output stream.
	 * @param val the integer to write.
	 */
	public void printHex(int val) {
		try {
			if (val == 0) out.write((byte) '0');
			else {
				int ctr = chars.length;
				while (val != 0) {
					byte ch = (byte) ('0' + (val & 0xf));
					if (ch > '9') ch += 39;
					chars[--ctr] = ch;
					val = val >>> 4;
				}
				chars[--ctr] = 'x';
				chars[--ctr] = '0';
				for (int i = ctr; i < chars.length; i++)
					out.write((byte)chars[i]);
			}
		} catch(IOException e) {e.printStackTrace();} 
	}


	/**
	 * Prints a integer in hexadezimal notation as a string to the output stream and then terminates the line.
	 * @param val the integer to write.
	 */
	public void printHexln(int val) {
		printHex(val);
		println();
	}

	/**
	 * Prints a long in hexadezimal notation as a string to the output stream.
	 * @param val the long to write.
	 */
	public void printHex(long val) {
		try {
			if (val == 0) out.write((byte) '0');
			else {
				int ctr = chars.length;
				while (val != 0) {
					byte ch = (byte) ('0' + (val & 0xf));
					if (ch > '9') ch += 39;
					chars[--ctr] = ch;
					val = val >>> 4;
				}
				chars[--ctr] = 'x';
				chars[--ctr] = '0';
				for (int i = ctr; i < chars.length; i++)
					out.write((byte)chars[i]);
			}
		} catch(IOException e) {e.printStackTrace();} 

	}
	
	
	/**
	 * Prints a long in hexadezimal notation as a string to the output stream and then terminates the line.
	 * @param val the long to write.
	 */
	public void printHexln(long val) {
		printHex(val);
		println();
	}

}
