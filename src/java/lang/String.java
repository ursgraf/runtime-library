package java.lang;

import ch.ntb.inf.deep.unsafe.US;


/**
 * String class for the <code>mpc555</code> Cross environment.
 * 
 * @author hangartner
 * 
 */
public class String {
	/** The count is the number of characters in the String. */
	protected short count;
//	protected short bla;
	
	/** The value is used for character storage. */
	protected char value[];

	/** The offset is the first index of the storage that is used. */
	protected int offset;

	
	// This char arrays are used for valueOf(..)
	private static char[] m = new char[80];
	private static char[] s = new char[80];
	
	private static final int MIN_INT = 1 << 31, MAX_INT = MIN_INT - 1;

	private static final char DIGIT_SPACE = 0xA0;

//	private static final int MAX_EXP = 309, MAX_DIG = 15;
	private static final int MAX_EXP = 1024, MAX_DIG = 15;

	private static final double FACTOR = 0;

	/**
	 * Initializes a newly created <code>String</code> object so that it
	 * represents an empty character sequence. Note that use of this constructor
	 * is unnecessary since Strings are immutable.
	 */
	public String() {
		this.offset = 0;
		this.count = 0;
		this.value = new char[1];
	}

	/**
	 * Initializes a newly created <code>String</code> object so that it
	 * represents the same sequence of characters as the argument; in other
	 * words, the newly created string is a copy of the argument string. Unless
	 * an explicit copy of <code>original</code> is needed, use of this
	 * constructor is unnecessary since Strings are immutable.
	 * 
	 * @param original
	 *            a <code>String</code>.
	 */
	public String(String original) {
		int size = original.count;
		if (size == 0) {
			this.offset = 0;
			this.value = new char[] {' '};
		} else {
			char[] originalValue = new char[size];
			original.getChars(originalValue, 0);
			char[] v;
			if (originalValue.length > size) {
				// The array representing the String is bigger than the new
				// String itself. Perhaps this constructor is being called
				// in order to trim the baggage, so make a copy of the array.
				v = new char[size];
				System.chararraycopy(originalValue, original.offset, v, 0, size);
			} else {
				// The array representing the String is the same
				// size as the String, so no point in making a copy.
				v = originalValue;
			}
			this.offset = 0;
			this.count = (short)size;
			this.value = v;
		}
	}

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
		int size = value.length;
		char[] v = new char[size];
		System.chararraycopy(value, 0, v, 0, size);
		this.offset = 0;
		this.count = (short)size;
		this.value = v;
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
		if (count < 1)
			return;
		char[] v = new char[count];
		System.chararraycopy(value, offset, v, 0, count);
		this.offset = offset;
		this.count = (short)count;
		this.value = v;
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
	void getChars(char dst[], int dstBegin) {
		System.chararraycopy(value, offset, dst, dstBegin, count);
	}
	
	/**
	 * Returns the reference to the internal used char array.<br>
	 * Warning: By changing this char array, the string itself will be changed
	 * too.
	 * 
	 * @return Reference to the internal used char array.
	 */
	public char[] getCharsRef() {
		return value;
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
		if ((index < 0) || (index >= count))
			return '\0';
		else
			return value[index + offset];
	}

	/**
	 * Returns the length of this string. The length is equal to the number of
	 * 16-bit Unicode characters in the string.
	 * 
	 * @return the length of the sequence of characters represented by this
	 *         object.
	 */
	public int length() {
		return count;
//		return US.GET2(12);
	}

	/**
	 * Compares this string to the specified object. The result is
	 * <code>true</code> if and only if the argument is not <code>null</code>
	 * and is a <code>String</code> object that represents the same sequence
	 * of characters as this object.
	 * 
	 * @param anObject
	 *            the object to compare this <code>String</code> against.
	 * @return <code>true</code> if the <code>String </code>are equal;
	 *         <code>false</code> otherwise.
	 */
/*
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof String) {
			String anotherString = (String) anObject;
			int n = count;
			if (n == anotherString.count) {
				char v1[] = value;
				for (int i = 0; i < v1.length; i++) {
					OutT.print(v1[i]);
				}

//				OutT.println();
//				String str = "Hallo";
				
				char v2[] = anotherString.value;
				for (int i = 0; i < v1.length; i++) {
					OutT.print(v2[i]);
				}
				OutT.println();
				int i = offset;
				int j = anotherString.offset;
				while (n-- > 0) {
					if (v1[i++] != v2[j++])
						return false;
				}
				return true;
			}
		}
		return false;
	}
*/


//	public static String valueOf(double d) {
//		return valueOf(d, 16, 0, 0, DIGIT_SPACE);
//	}

//	public static String valueOf(double x, int precision, int minW, int expW,
//			char fillCh) {
//		// TODO: Control FACTOR
//		int n = 0, i = 0, j = 0, len = 0, p = 0, k = 0, exp = 0;
//
//		boolean neg = false;
//
//		if (precision > 0)
//			return " ";
//		
//		if (x == 0)
//			return "0.0";
//
//		exp = Math.exponent(x);
//
////		if (exp == MAX_INT) {
//		if (exp == MAX_EXP) {
//			if (fillCh == '0')
//				fillCh = DIGIT_SPACE;
//			if (x < 0)
//				neg = true;
//			x = Math.mantissa(x);
//			if (x == 1 && neg) {
////				"-inf".getChars(m, 0);
//				n = 4;
//			} else if (x == 1 && !neg) {
////				"inf".getChars(m, 0);
//				n = 3;
//			} else {
////				"nan".getChars(m, 0);
//				n = 3;
//			}
//
//			while (minW > n) {
//				s[i] = fillCh;
//				i++;
//				minW--;
//			}
//
//			while (j <= n && i < s.length) {
//				s[i] = m[j];
//				i++;
//				j++;
//			}
//		} else {
//			len = 1;
////			String strNull = new String("00");
////			strNull.getChars(m, 0);
////			"00".getChars(m, 0);
//			if (x < 0) {
//				x = -x;
//				neg = true;
//				minW--;
//			}
//
//			if (x != 0) {
//				exp = (exp - 8) * 30103 / 100000; // * log(2)
//				if (exp > 0) {
//					n = (int) (x / Math.pow(10, -exp));
//					x = x / Math.pow(10, -exp) - n;
//				} else if (exp > -MAX_EXP) {
//					n = (int) (x * Math.pow(10, -exp));
//					x = x * Math.pow(10, -exp) - n;
//				} else {
//					n = (int) (x * Math.pow(10, -exp - 2 * MAX_DIG) * FACTOR * FACTOR);
//					x = x * Math.pow(10, -exp - 2 * MAX_DIG) * FACTOR * FACTOR
//							- n;
//				}
//				p = precision - 4;
//				if (n < 1000)
//					p++;
//				if ((expW < 0) && (p > exp - expW))
//					p = exp - expW;
//				if (p >= 0) {
//					x = x + 0.5 / Math.pow(10, p); // rounding correction
//					if (x >= 1) {
//						n++;
//						x--;
//					}
//				} else if (p == -1)
//					n += 5;
//				else if (p == -2)
//					n += 50;
//				else if (p == -3)
//					n += 500;
//				i = 0;
//				k = 1000;
//				exp += 3;
//				if (n < 1000) {
//					k = 100;
//					exp--;
//				}
//				while ((i < precision) && (k > 0) || (x != 0)) {
//					if (k > 0) {
//						p = n / k;
//						n = n % k;
//						k = k / 10;
//					} else {
//						x = x * 10;
//						p = (int) x;
//						x -= p;
//					}
//					m[i] = (char) (p + '0');
//					i++;
//					if (p != 0) {
//						len = i;
//					}
//				}
//			}
//			i = 0;
//			if ((expW < 0) || (expW == 0) && (exp >= 3) && (exp <= len + 1)) {
//				n = exp + 1;
//				k = len - n;
//				if (n < 1)
//					n = 1;
//				if (expW < 0)
//					k = -expW;
//				else if (k < 1)
//					k = 1;
//				j = minW - n - k - 1;
//				p = exp;
//				if (neg && (p >= Math.max(0, n) + Math.max(0, k))) {
//					neg = false;
//					j++;
//				}
//			} else {
//				if (Math.abs(exp) >= 100)
//					expW = 3;
//				else if (expW < 2 && Math.abs(exp) > 10)
//					expW = 2;
//				else if (expW < 1)
//					expW = 1;
//
//				if (len < 2)
//					len = 2;
//				j = minW - len - 3 - expW;
//				k = len;
//				if (j > 0) {
//					k += j;
//					j = 0;
//					if (k > precision) {
//						j = k - precision;
//						k = precision;
//					}
//				}
//				n = 1;
//				k--;
//				p = 0;
//			}
//			if (neg && fillCh == '0') {
//				s[i] = '-';
//				i++;
//				neg = false;
//			}
//			while (j > 0) {
//				s[i] = fillCh;
//				i++;
//				j--;
//			}
//			if (neg && i < s.length) {
//				s[i] = '-';
//				i++;
//			}
//			j = 0;
//			while (n > 0 && i < s.length) {
//				if (p <= 0 && j < len) {
//					s[i] = m[j];
//					j++;
//				} else
//					s[i] = '0';
//				i++;
//				n--;
//				p--;
//			}
//			if (i < s.length) {
//				s[i] = '.';
//				i++;
//			}
//			while (k > 0 && i < s.length) {
//				if (p <= 0 && j < len) {
//					s[i] = m[j];
//					j++;
//				} else
//					s[i] = '0';
//				i++;
//				k--;
//				p--;
//			}
//			if (expW > 0) {
//				if (i < s.length) {
//					s[i] = 'E';
//					i++;
//				}
//				if (i < s.length) {
//					if (exp < 0) {
//						s[i] = '-';
//						exp = -exp;
//					} else
//						s[i] = '+';
//					i++;
//				}
//				if (expW == 3 && i < s.length) {
//					s[i] = (char) (exp / 100 + '0');
//					i++;
//				}
//				if (expW >= 2 && i < s.length) {
//					s[i] = (char) (exp / 10 % 10 + '0');
//					i++;
//				}
//				if (i < s.length) {
//					s[i] = (char) (exp % 10 + '0');
//					i++;
//				}
//			}
//		}
//
//		return new String(s, 0, i);
//	}

	/**
	 * Test method<br>
	 * Not for common use.
	 */
	public char charAt() {
		return 'S';
	}
	
/*
	public static void testReturn() {
		String str = "inf";
		"inf".getChars(new char[4], 4);
//		return "0.0";
	}
*/

}
