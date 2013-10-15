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

package java.lang;



/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */
public class String extends BString {

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
	 *            the length (count > 0).
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
	 * Returns the length of this string. The length is equal to the number of
	 * 16-bit Unicode characters in the string.
	 * 
	 * @return number of characters in the string
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


}
