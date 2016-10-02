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

package java.lang;

import java.io.Serializable;
import ch.ntb.inf.deep.marker.Modified;

/**
 * An immutable sequence of characters/code units ({@code char}s). A
 * {@code String} is represented by array of UTF-16 values.
 * This class is highly optimized version compared to the standard String class.
 *
 * <h3><a name="backing_array">Backing Arrays</a></h3>
 * This class is implemented using a char[]. The length of the array may exceed
 * the length of the string. For example, the string "Hello" may be backed by
 * the array {@code ['H', 'e', 'l', 'l', 'o', 'W'. 'o', 'r', 'l', 'd']} with
 * offset 0 and length 5.
 *
 */
/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */
public class String extends BString implements Serializable, Comparable<String>, Modified{
	private static final long serialVersionUID = -6849794470754667710L;

	/** value is used for character storage. */
	protected char value[];

	/**
	 * Allocates a new <code>String</code> so that it represents the sequence
	 * of characters currently contained in the character array argument. The
	 * contents of the character array are copied; subsequent modification of
	 * the character array does not affect the newly created string.
	 * 
	 * @param value
	 *            the initial value of the string.
	 */
	public String(char value[]) {
		// This constructor is empty as it is replaced by the corresponding 
		// factory method allocateString(char value[])
	}

	public static String allocateString(int ref, char value[]) {
		int len = value.length;
		String str = newstring(ref, len * 2 + 4);	//TODO size of string and object
		str.count = len;
		for (int i = 0; i < len; i++) str.value[i] = value[i];
		return str;
	}

	/**
	 * Allocates a new <code>String</code> that contains characters from a
	 * subarray of the character array argument. The <code>offset</code>
	 * argument is the index of the first character of the subarray and the
	 * <code>count</code> argument specifies the length of the subarray. The
	 * contents of the subarray are copied; subsequent modification of the
	 * character array does not affect the newly created string.
	 * 
	 * @param value
	 *            array that is the source of characters.
	 * @param offset
	 *            the initial offset.
	 * @param count
	 *            the length (count &gt; 0).
	 */
	public String(char[] value, int offset, int count) {
		// This constructor is empty as it is replaced by the corresponding 
		// factory method allocateString(char[] value, int offset, int count)
	}

	public static String allocateString(int ref, char value[], int offset, int cnt) {
		String str = newstring(ref, cnt * 2 + 4);
		str.count = cnt;
		for (int i = 0; i < cnt; i++) str.value[i] = value[i+offset];
		return str;
	}

	/**
	 * Encodes this <tt>String</tt> into a sequence of bytes using the
	 * platform's default charset, storing the result into a new byte array.
	 * 
	 * @return The resultant byte array
	 */
	public byte[] getBytes() {
		if (this.count == 0)
			return new byte[] {0};
		byte[] b = new byte[this.count];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) this.value[i];
		}
		return b;
	}

	/**
	 * Copy characters from this string into dst starting at dstBegin. This
	 * method doesn't perform any range checking.
	 * @param dst
	 *            The destination array.
	 * @param dstBegin
	 *            The start offset in the destination array.
	 */
	public void getChars(char dst[], int dstBegin) {
		for (int i = 0; i < this.count; i++) {
			dst[dstBegin + i] = this.value[i + i];
		}
	}
	 
	/**
	 * Returns the <code>char</code> value at the specified index. An index
	 * ranges from <code>0</code> to <code>length() - 1</code>. The first
	 * <code>char</code> value of the sequence is at index <code>0</code>,
	 * the next at index <code>1</code>, and so on, as for array indexing.
	 * 
	 * @param index
	 *            the index of the <code>char</code> value.
	 * @return the <code>char</code> value at the specified index of this
	 *         string. The first <code>char</code> value is at index
	 *         <code>0</code>.
	 */
	public char charAt(int index) {
		return value[index];
	}

    /**
     * Compares the specified object to this string and returns true if they are
     * equal. The object must be an instance of string with the same characters
     * in the same order.
     *
     * @param anObject
     *            the object to compare.
     * @return {@code true} if the specified object is equal to this string,
     *         {@code false} otherwise.
     * @see #hashCode
     */
	public boolean equals(Object anObject) {
		if (anObject == null) return false;
		if (!(anObject instanceof String)) return false;
		String str = (String) anObject;
		int len = str.length();
		if (len > length()) return false;
		for (int i = 0; i < len; i++) {
			if (charAt(i) != str.charAt(i)) return false;
		}
		return true;
	}
	
	/**
	 * Returns the length of this string. The length is equal to the number of
	 * 16-bit Unicode characters in the string.
	 * 
	 * @return number of characters in the string
	 */
	public int length() {
		return count;
	}
	
	private static String newstring(int ref, int len) {	// stub
		return null;
	}

	/**
	 * Compares the specified string to this string using the Unicode values of
	 * the characters. Returns 0 if the strings contain the same characters in
	 * the same order. Returns a negative integer if the first non-equal
	 * character in this string has a Unicode value which is less than the
	 * Unicode value of the character at the same position in the specified
	 * string, or if this string is a prefix of the specified string. Returns a
	 * positive integer if the first non-equal character in this string has a
	 * Unicode value which is greater than the Unicode value of the character at
	 * the same position in the specified string, or if the specified string is
	 * a prefix of this string.
	 *
	 * @param string
	 *            the string to compare.
	 * @return 0 if the strings are equal, a negative integer if this string is
	 *         before the specified string, or a positive integer if this string
	 *         is after the specified string.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
	public int compareTo(String string) {
		if (string == null) throw new NullPointerException();
		int len2 = string.length();
		int len1 = length();
		int i = 0;
		while (i < len1 && i < len2) {
			int diff = charAt(i) - string.charAt(i);
			if (diff != 0) return diff;
			i++;
		}
		if (len1 < len2) return -1;
		if (len2 < len1) return 1;
		return 0;
	}


}
