package java.lang;

import ch.ntb.inf.deep.unsafe.SYS;

public class LiteralString extends String {
	public int addr;

	/**
	 * Creates a new <code>LiteralString</code> Object.
	 * 
	 */
	public LiteralString() {
	}

	/**
	 * Creates a new <code>LiteralString</code> Object at the given Address.
	 * 
	 * @param addr
	 *            Address, where the <code>LiteralString</code> Object is
	 *            located.
	 */
	public LiteralString(int addr) {
		this.addr = addr;
	}
	
	//@Override
	public byte[] getBytes() {
		if (this.count == 0)
			return new byte[] {0};
		byte[] b = new byte[count];
		for (int i = 0; i < b.length; i++) {
			b[i] = SYS.GET1(addr + i);
		}
		return b;
	}

	//@Override
	public char charAt(int index) {
		if ((index < 0) || (index >= count))
			return '\0';
		else {
			return (char) SYS.GET1(addr + index);
		}
	}
	
	//@Override
/*
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof String) {
			String anotherString = (String) anObject;
			int n = count;
			if (n == anotherString.count) {
//				char v1[] = value;
				char v2[] = anotherString.value;
				int i = 0;
				int j = anotherString.offset;
				while (n-- != 0) {
					if (SYS.GET1(addr + i++) != v2[j++])
						return false;
				}
				return true;
			}
		}
		return false;
	}
*/

	//@Override
	void getChars(char dst[], int dstBegin) {
		for (int i = 0; i < dst.length - dstBegin; i++) {
			dst[i + dstBegin] = (char) SYS.GET1(addr + i);
		}
	}
	
	//@Override
	public char[] getCharsRef() {
		if (count == 0) {
			value = new char[] {' '};
			return value;
		} else {
			value = new char[count];
			for (int i = 0; i < value.length; i++) {
				value[i] = (char) SYS.GET1(addr + i);
			}
			return value;
		}
	}

	/**
	 * Test method<br>
	 * Not for common use.
	 */
	public char charAt() {
		return 'L';
	}

}
