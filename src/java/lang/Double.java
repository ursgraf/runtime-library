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

import ch.ntb.inf.deep.lowLevel.LL;
import ch.ntb.inf.deep.marker.Modified;

/**
 * The wrapper for the primitive type {@code double}.
 *
 * @see java.lang.Number
 * @since 1.0
 */
/* Changes:
 * 27.5.2014	Urs Graf	initial import and modified
 */
public final class Double extends Number implements Comparable<Double>, Modified {
	private static final long serialVersionUID = -9172774392245257468L;

	/**
	 * A constant holding the positive infinity of type <code>double</code>.
	 * It is equal to the value returned by
	 * <code>Double.longBitsToDouble(0x7ff0000000000000L)</code>.
	 */
	public static final double POSITIVE_INFINITY = 1.0 / 0.0;

	/**
	 * A constant holding the negative infinity of type <code>double</code>.
	 * It is equal to the value returned by
	 * <code>Double.longBitsToDouble(0xfff0000000000000L)</code>.
	 */
	public static final double NEGATIVE_INFINITY = -1.0 / 0.0;

	/**
	 * A constant holding a Not-a-Number (NaN) value of type <code>double</code>.
	 * It is equivalent to the value returned by
	 * <code>Double.longBitsToDouble(0x7ff8000000000000L)</code>.
	 */
	public static final double NaN = 0.0d / 0.0;

	/**
	 * A constant holding the largest positive finite value of type
	 * <code>double</code>, (2-2<sup>-52</sup>)&middot;2<sup>1023</sup>.
	 * It is equal to the hexadecimal floating-point literal
	 * <code>0x1.fffffffffffffP+1023</code> and also equal to
	 * <code>Double.longBitsToDouble(0x7fefffffffffffffL)</code>.
	 */
	public static final double MAX_VALUE = 1.7976931348623157e+308; // 0x1.fffffffffffffP+1023

	/**
	 * A constant holding the smallest positive nonzero value of type
	 * <code>double</code>, 2<sup>-1074</sup>. It is equal to the
	 * hexadecimal floating-point literal <code>0x0.0000000000001P-1022</code>
	 * and also equal to <code>Double.longBitsToDouble(0x1L)</code>.
	 */
	public static final double MIN_VALUE = 4.9e-324; // 0x0.0000000000001P-1022

	/**
	 * A constant holding the smallest positive normalized value of type
	 * <code>double</code>, 2<sup>-1074</sup>. It is equal to the
	 * hexadecimal floating-point literal
	 * <code>Double.longBitsToDouble(1L &lt;&lt; 52)</code>.
	 */
	public static final double MIN_VALUE_NORM = MIN_VALUE * (double) (1L << 52);

	/**
	 * The number of bits used to represent a <tt>double</tt> value.
	 */
	public static final int SIZE = 64;

	private static final int expOffset = 0x3ff;
	public static final double INF_EXPONENT = expOffset + 1;

	private static final byte dMaxNofFractionDigits = 15;
	private static final char[] digits = new char[dMaxNofFractionDigits + 1];
	private static char[] gchars;
	private static int nofChars;
	private static final int highNaN = 0x7ff80000;
	private static final int highINF = 0x7ff00000;

	private static final double[] tene = { // exact powers of 10
		1E0, 1E1, 1E2, 1E3, 1E4, 1E5, 1E6, 1E7, 1E8, 1E9, 1E10, 1E11, 1E12, 1E13,
		1E14, 1E15, 1E16, 1E17, 1E18, 1E19, 1E20, 1E21, 1E22 };

	private static final double[] ten = { // rounded powers of 10
		1E-307, 1E-284, 1E-261, 1E-238, 1E-215, 1E-192, 1E-169, 1E-146, 1E-123,
		1E-100, 1E-77, 1E-54, 1E-31, 1E-8, 1E15, 1E38, 1E61, 1E84, 1E107,
		1E130, 1E153, 1E176, 1E199, 1E222, 1E245, 1E268, 1E291 };

	private static final int[] eq = { 
		0x96810239, // eq[ 0] = {0, 3..5, 9, 16, 23, 25, 26, 28, 31}
		0xFBBEFF64, // eq[ 1] = {2, 5, 6, 8..15, 17..21, 23..25, 27..31}
		0x1FFFFFFF, // eq[ 2] = {0..28}
		0xF85FCBEF, // eq[ 3] = {0..3, 5..9, 11, 14..20, 22, 27..31}
		0xFFFCFCC1, // eq[ 4] = {0, 6, 7, 10..15, 18..31}
		0xFFFBFFE3, // eq[ 5] = {0, 1, 5..17, 19..31}
		0xF7B5C5B3, // eq[ 6] = {0, 1, 4, 5, 7, 8, 10, 14..16, 18, 20, 21, 23..26, 28..31}
		0xF58F7FFB, // eq[ 7] = {0, 1, 3..14, 16..19, 23, 24, 26, 28..31}
		0x273F4F7F, // eq[ 8] = {0..6, 8..11, 14, 16..21, 24..26, 29}
		0xFFFFFE56, // eq[ 9] = {1, 2, 4, 6, 9..31}
		0x7FFFFFFF, // eq[ 10] = {0..30}
		0x78F9F5FF, // eq[ 11] = {0..8, 10, 12..16, 19..23, 27..30}
		0xECBFD7BF, // eq[ 12] = {0..5, 7..10, 12, 14..21, 23, 26, 27, 29..31}
		0xF9B7EEFF, // eq[ 13] = {0..7, 9..11, 13..18, 20, 21, 23, 24, 27..31}
		0xFFFFFFCF, // eq[ 14] = {0..3, 6..31}
		0x17FFBBFF, // eq[ 15] = {0..9, 11..13, 15..26, 28}
		0xFF4F2816, // eq[ 16] = {1, 2, 4, 11, 13, 16..19, 22, 24..31}
		0xBEBCCBFE, // eq[ 17] = {1..9, 11, 14, 15, 18..21, 23, 25..29, 31}
		0x3DDB7B75, // eq[ 18] = {0, 2, 4..6, 8, 9, 11..14, 16, 17, 19, 20, 22..24, 26..29}
		0x000000FC, // eq[ 19] = {2..7}
	};

	private static final int[] gr = { 
		0x69000000, // gr[ 0] = {24, 27, 29, 30}
		0x0000009B, // gr[ 1] = {0, 1, 3, 4, 7}
		0xE0000000, // gr[ 2] = {29..31}
		0x07A03410, // gr[ 3] = {4, 10, 12, 13, 21, 23..26}
		0x0003033E, // gr[ 4] = {1..5, 8, 9, 16, 17}
		0x0004001C, // gr[ 5] = {2..4, 18}
		0x084A3A4C, // gr[ 6] = {2, 3, 6, 9, 11..13, 17, 19, 22, 27}
		0x00000004, // gr[ 7] = {2}
		0xD8C0B080, // gr[ 8] = {7, 12, 13, 15, 22, 23, 27, 28, 30, 31}
		0x000001A9, // gr[ 9] = {0, 3, 5, 7, 8}
		0x00000000, // gr[ 10] = {}
		0x00000000, // gr[ 11] = {}
		0x13402800, // gr[ 12] = {11, 13, 22, 24, 25, 28}
		0x06400000, // gr[ 13] = {22, 25, 26}
		0x00000030, // gr[ 14] = {4, 5}
		0xE8004400, // gr[ 15] = {10, 14, 27, 29..31}
		0x00B0D7E9, // gr[ 16] = {0, 3, 5..10, 12, 14, 15, 20, 21, 23}
		0x41433401, // gr[ 17] = {0, 10, 12, 13, 16, 17, 22, 24, 30}
		0x00000000, // gr[ 18] = {}
		0x00000000  // gr[ 19] = {}
	};

	/**
	 * Calculates power to the base of 10
	 * 
	 * @param e
	 *            this is the exponent
	 * @return power of 10<sup>e</sup>.
	 */
	public static double powOf10(int e) {
		double r;
		if (e < -307) return 0;
		else if (e > 308) return Double.NaN;
		e += 307; 
		r = ten[e / 23] * tene[e % 23];
		if (((1 << e) & eq[e >>> 5]) != 0) return r;
		int E = Double.getExponent(r);
		r = Double.setExponent(r, 52);
		if (((1 << e) & gr[e >>> 5]) != 0) r = r - 1;
		else r = r + 1; 
		r = Double.setExponent(r, E);
		return r;
	}

    /**
     * Returns an integer corresponding to the upper 32 bits of the given
     * <a href="http://en.wikipedia.org/wiki/IEEE_754-1985">IEEE 754</a> double precision
     * {@code value}. 
     * @param arg Double argument.
     * @return Upper 32 bits.
     */
	public static int highPartToIntBits(double arg) {
		long doubleBits = LL.doubleToBits(arg);	
		return (int)(doubleBits >> 32);
	}

    /**
     * Returns an integer corresponding to the lower 32 bits of the given
     * <a href="http://en.wikipedia.org/wiki/IEEE_754-1985">IEEE 754</a> double precision
     * {@code value}. 
     * @param arg Double argument.
     * @return Lower 32 bits.
     */
	public static int lowPartToIntBits(double arg) {
		long doubleBits = LL.doubleToBits(arg);
		return (int)(doubleBits);
	}

	/**
	 * Returns the exponent of a double precision {@code value} 
	 * to the base of 2.
     * @param arg Double argument.
     * @return Exponent.
	 */
	public static int getExponent(double arg) {
		int highBits = highPartToIntBits(arg);
		return ((highBits >> 20) & 0x7ff) - expOffset;
	}

	/**
	 * Sets the exponent of a double precision {@code value} 
	 * to the base of 2 and returns the new value.
     * @param d Double
     * @param newExp New exponent.
     * @return New double value.
	 */
	public static double setExponent(double d, int newExp) {
		long bits = LL.doubleToBits(d);
		newExp += expOffset;
		bits &= 0x800fffffffffffffL;
		bits |= (long)(newExp) << 52;
		return LL.bitsToDouble(bits);
	}

	private static void putChar(char ch) {
		gchars[nofChars] = ch;
		nofChars++;
	}

	public static int doubleToChars(double val, int nofFractDigits, char[] chars) {
		gchars = chars;
		nofChars = 0;
		if (chars == null) return 0;
		int high = highPartToIntBits(val);

		if ((high & highINF) == highINF) {
			if ((high & highNaN) == highNaN) { // NaN
				putChar('N'); putChar('a');	putChar('N');
			} else { // INF
				if (high >= 0) putChar('+');
				else putChar('-');
				putChar('I'); putChar('N');	putChar('F');
			}
			//			putChar('\0');
			gchars = null;
			return nofChars;
		}

		int exp = (high & highINF) >> 20;
		if (exp != 0 && high < 0) {
			putChar('-');
			val = -val;
		}
		int low;
		if (exp == 0) { // no denormals
			high = 0;
			low = 0;
		} else { 
			if (nofFractDigits < 1) nofFractDigits = 1;
			else if (nofFractDigits > 15) nofFractDigits = 15;
			exp = (exp - expOffset) * 301029;
			if (exp % 1000000 < 0) exp = exp / 1000000 - 1;
			else exp = exp / 1000000; 
			double z = powOf10(exp + 1);
			if (val >= z) {
				val = val / z;
				exp++;
			} else {
				val = val * powOf10(-exp);
			}
			if (val >= 10) {
				val = val * 0.1 + 0.5 / powOf10(nofFractDigits);
				exp++;
			} else {
				val = val + 0.5 / powOf10(nofFractDigits);
				if (val >= 10) {
					val = val * 0.1;
					exp++;
				}
			}
			val = val * 1E7;
			high = (int) val;
			low = (int) ((val - high) * 1E8);
		}

		int dig = 15;
		while (dig > 7) {
			digits[dig] = (char) (low % 10 + '0');
			low = low / 10;
			dig--;
		}
		while (dig >= 0) {
			digits[dig] = (char) (high % 10 + '0');
			high = high / 10;
			dig--;
		}
		putChar(digits[0]);
		putChar('.');
		dig = 1;
		while (dig <= nofFractDigits) {
			putChar(digits[dig]);
			dig++;
		}
		putChar('E');
		if (exp >= 0) putChar('+');
		else {putChar('-'); exp = -exp;}
		putChar((char) (exp / 100 % 10 + '0'));
		putChar((char) (exp / 10 % 10 + '0'));
		putChar((char) (exp % 10 + '0'));
		//putChar('\0');
		gchars = null;
		return nofChars;
	}

    /**
     * Returns a {@code Double} instance for the specified double value.
     *
     * @param d
     *            the double value to store in the instance.
     * @return a {@code Double} instance containing {@code d}.
     * @since 1.5
     */
	public static Double valueOf(double d) {
		return new Double(d);
	}

	/**
	 * The value of the {@code Double}.
	 *
	 * @serial
	 */
	private final double value;

	/**
	 * Constructs a newly allocated {@code Double} object that
	 * represents the primitive {@code double} argument.
	 *
	 * @param   value   the value to be represented by the {@code Double}.
	 */
	public Double(double value) {
		this.value = value;
	}

	/**
	 * Constructs a new {@code Double} from the specified string.
	 *
	 * @param string
	 *            the string representation of a double value.
	 * @throws NumberFormatException
	 *             if {@code string} cannot be parsed as a double value.
	 * @see #parseDouble(String)
	 */
	public Double(String string) throws NumberFormatException {
		this(parseDouble(string));
	}

    /**
     * Parses the specified string as a double value.
     *
     * @param string
     *            the string representation of a double value.
     * @return a {@code Double} instance containing the double value represented
     *         by {@code string}.
     * @throws NumberFormatException
     *             if {@code string} cannot be parsed as a double value.
     * @see #parseDouble(String)
     */
	public static Double valueOf(String string) throws NumberFormatException {
		return parseDouble(string);
	}

	/**
	 * Returns the closest double value to the real number in the string.
	 *
	 * @param s
	 *            the String that will be parsed to a floating point
	 * @return the double closest to the real number
	 *
	 * @exception NumberFormatException
	 *                if the String doesn't represent a double
	 */
	public static double parseDouble(String s) throws NumberFormatException {
		if (s == null) {
			throw new NumberFormatException("Invalid double");
		}
		int length = s.length();
		if (length == 0) {
			throw new NumberFormatException("Invalid double");
		}
		char c = s.charAt(length-1);
		if (c == 'f' || c == 'F') length--;
		int start = 0;
		boolean neg = false;
		c = s.charAt(0);
		if (c == '-') {start = 1; neg = true;}
		if (c == '+') start = 1;
		int dot = length;
		for (int i = start; i < length; i++) if (s.charAt(i) == '.') dot = i;
		int esign = length;
		for (int i = start; i < length; i++) {
			char ch = s.charAt(i);
			if (ch == 'e' || ch == 'E') esign = i;
		}
		long num = 0;
		for (int i = start; i < esign; i++) {
			if (i != dot) num = num * 10 + s.charAt(i) - '0';
		}
		double res = num;
		int cnt = 0;
		for (int i = dot + 1; i < esign; i++) cnt++;
		res = res / powOf10(cnt);
		int exp = 0;
		boolean eneg = false;
		start = esign + 1;
		if (start < length) {
			c = s.charAt(start); 
			if (c == '-') {start++; eneg = true;}
			if (c == '+') start ++;
		}
		for (int i = start; i < length; i++) {
			exp = exp * 10 + s.charAt(i) - '0'; 
		}
		if (eneg) exp = -exp;
		res = res * powOf10(exp);
		if (neg) res = -res;
		return res;
	}

	/**
	 * Returns the {@code double} value of this
	 * {@code Double} object.
	 *
	 * @return the {@code double} value represented by this object
	 */
	public double doubleValue() {
		return value;
	}

	@Override
	public byte byteValue() {
		return (byte) value;
	}

	@Override
	public float floatValue() {
		return (float) value;
	}

	@Override
	public int intValue() {
		return (int) value;
	}
	
    @Override
    public long longValue() {
        return (long) value;
    }
    
    @Override
    public short shortValue() {
        return (short) value;
    }

	@Override
	public int hashCode() {
		int low = lowPartToIntBits(value);
		int high = highPartToIntBits(value);
		return low ^ high;
	}

    @Override
    public String toString() {
        return Double.toString(value);
    }

    private static char[] str1 = new char[64];
    /**
     * Returns a string containing a concise, human-readable description of the
     * specified double value.
     *
     * @param d
     *             the double to convert to a string.
     * @return a printable representation of {@code d}.
     */
    public static String toString(double d) {
    	doubleToChars(d, 15, str1);
        return new String(str1);
    }

	/**
	 * Compares this object to the specified double object to determine their
	 * relative order. There are two special cases:
	 * <ul>
	 * <li>{@code Double.NaN} is equal to {@code Double.NaN} and it is greater
	 * than any other double value, including {@code Double.POSITIVE_INFINITY};</li>
	 * <li>+0.0d is greater than -0.0d</li>
	 * </ul>
	 *
	 * @param object
	 *            the double object to compare this object to.
	 * @return a negative value if the value of this double is less than the
	 *         value of {@code object}; 0 if the value of this double and the
	 *         value of {@code object} are equal; a positive value if the value
	 *         of this double is greater than the value of {@code object}.
	 * @throws NullPointerException
	 *             if {@code object} is {@code null}.
	 * @see java.lang.Comparable
	 * @since 1.2
	 */
	public int compareTo(Double object) {
		return compare(value, object.value);
	}

    /**
     * Compares the two specified double values. There are two special cases:
     * <ul>
     * <li>{@code Double.NaN} is equal to {@code Double.NaN} and it is greater
     * than any other double value, including {@code Double.POSITIVE_INFINITY};</li>
     * <li>+0.0d is greater than -0.0d</li>
     * </ul>
     *
     * @param double1
     *            the first value to compare.
     * @param double2
     *            the second value to compare.
     * @return a negative value if {@code double1} is less than {@code double2};
     *         0 if {@code double1} and {@code double2} are equal; a positive
     *         value if {@code double1} is greater than {@code double2}.
     */
    public static int compare(double double1, double double2) {
        // Non-zero, non-NaN checking.
        if (double1 > double2) {
            return 1;
        }
        if (double2 > double1) {
            return -1;
        }
        if (double1 == double2 && 0.0d != double1) {
            return 0;
        }

        // NaNs are equal to other NaNs and larger than any other double
        if (isNaN(double1)) {
            if (isNaN(double2)) {
                return 0;
            }
            return 1;
        } else if (isNaN(double2)) {
            return -1;
        }

        // Deal with +0.0 and -0.0
        long d1 = highPartToIntBits(double1);
        long d2 = highPartToIntBits(double2);
        // The below expression is equivalent to:
        // (d1 == d2) ? 0 : (d1 < d2) ? -1 : 1
        return (int) ((d1 >> 31) - (d2 >> 31));
    }

	/**
	 * Tests this double for equality with {@code object}.
	 * To be equal, {@code object} must be an instance of {@code Double} and
	 * {@code doubleToLongBits} must give the same value for both objects.
	 *
	 * <p>Note that, unlike {@code ==}, {@code -0.0} and {@code +0.0} compare
	 * unequal, and {@code NaN}s compare equal by this method.
	 *
	 * @param object
	 *            the object to compare this double with.
	 * @return {@code true} if the specified object is equal to this
	 *         {@code Double}; {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object object) {
		return (object instanceof Double) &&
				(highPartToIntBits(this.value) == highPartToIntBits(((Double) object).value)) &&
						(lowPartToIntBits(this.value) == lowPartToIntBits(((Double) object).value));
	}

	/**
	 * Indicates whether this object represents an infinite value.
	 *
	 * @return {@code true} if the value of this double is positive or negative
	 *         infinity; {@code false} otherwise.
	 */
	public boolean isInfinite() {
		return isInfinite(value);
	}

	/**
	 * Indicates whether the specified double represents an infinite value.
	 *
	 * @param d
	 *            the double to check.
	 * @return {@code true} if the value of {@code d} is positive or negative
	 *         infinity; {@code false} otherwise.
	 */
	public static boolean isInfinite(double d) {
		return (d == POSITIVE_INFINITY) || (d == NEGATIVE_INFINITY);
	}

	/**
	 * Indicates whether this object is a <em>Not-a-Number (NaN)</em> value.
	 *
	 * @return {@code true} if this double is <em>Not-a-Number</em>;
	 *         {@code false} if it is a (potentially infinite) double number.
	 */
	public boolean isNaN() {
		return isNaN(value);
	}

	/**
	 * Indicates whether the specified double is a <em>Not-a-Number (NaN)</em>
	 * value.
	 *
	 * @param d
	 *            the double value to check.
	 * @return {@code true} if {@code d} is <em>Not-a-Number</em>;
	 *         {@code false} if it is a (potentially infinite) double number.
	 */
	public static boolean isNaN(double d) {
		return d != d;
	}

}
