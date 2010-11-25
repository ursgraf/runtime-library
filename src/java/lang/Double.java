package java.lang;

import ch.ntb.inf.deep.unsafe.SYS;

public class Double {
	private static final boolean _$compileForTarget = true; // compile for Target | Host
	private static final boolean _$bigEndian = true; // big-endian | little-endian

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
	 * <code>Double.longBitsToDouble(1L << 52)</code>.
	 */
	public static final double MIN_VALUE_NORM = MIN_VALUE * (double) (1L << 52);

	/**
	 * The number of bits used to represent a <tt>double</tt> value.
	 */
	public static final int SIZE = 64;

	private static final int expOffset = 0x3ff;

	private static double twoPow52 = 1L << 52; // 2^52

	public static final double INF_EXPONENT = expOffset + 1;

	private static final double[] tene = { // exact powers of 10
	1E0, 1E1, 1E2, 1E3, 1E4, 1E5, 1E6, 1E7, 1E8, 1E9, 1E10, 1E11, 1E12, 1E13,
			1E14, 1E15, 1E16, 1E17, 1E18, 1E19, 1E20, 1E21, 1E22 };

	private static final double[] ten = { // rounded powers of 10
	1E-307, 1E-284, 1E-261, 1E-238, 1E-215, 1E-192, 1E-169, 1E-146, 1E-123,
			1E-100, 1E-77, 1E-54, 1E-31, 1E-8, 1E15, 1E38, 1E61, 1E84, 1E107,
			1E130, 1E153, 1E176, 1E199, 1E222, 1E245, 1E268, 1E291 };

	private static final int[] eq = { 0x96810239, // eq[ 0] = {0, 3..5, 9, 16,
			// 23, 25, 26, 28, 31}
			0xFBBEFF64, // eq[ 1] = {2, 5, 6, 8..15, 17..21, 23..25, 27..31}
			0x1FFFFFFF, // eq[ 2] = {0..28}
			0xF85FCBEF, // eq[ 3] = {0..3, 5..9, 11, 14..20, 22, 27..31}
			0xFFFCFCC1, // eq[ 4] = {0, 6, 7, 10..15, 18..31}
			0xFFFBFFE3, // eq[ 5] = {0, 1, 5..17, 19..31}
			0xF7B5C5B3, // eq[ 6] = {0, 1, 4, 5, 7, 8, 10, 14..16, 18, 20, 21,
			// 23..26, 28..31}
			0xF58F7FFB, // eq[ 7] = {0, 1, 3..14, 16..19, 23, 24, 26, 28..31}
			0x273F4F7F, // eq[ 8] = {0..6, 8..11, 14, 16..21, 24..26, 29}
			0xFFFFFE56, // eq[ 9] = {1, 2, 4, 6, 9..31}
			0x7FFFFFFF, // eq[ 10] = {0..30}
			0x78F9F5FF, // eq[ 11] = {0..8, 10, 12..16, 19..23, 27..30}
			0xECBFD7BF, // eq[ 12] = {0..5, 7..10, 12, 14..21, 23, 26, 27,
			// 29..31}
			0xF9B7EEFF, // eq[ 13] = {0..7, 9..11, 13..18, 20, 21, 23, 24,
			// 27..31}
			0xFFFFFFCF, // eq[ 14] = {0..3, 6..31}
			0x17FFBBFF, // eq[ 15] = {0..9, 11..13, 15..26, 28}
			0xFF4F2816, // eq[ 16] = {1, 2, 4, 11, 13, 16..19, 22, 24..31}
			0xBEBCCBFE, // eq[ 17] = {1..9, 11, 14, 15, 18..21, 23, 25..29, 31}
			0x3DDB7B75, // eq[ 18] = {0, 2, 4..6, 8, 9, 11..14, 16, 17, 19, 20,
			// 22..24, 26..29}
			0x000000FC, // eq[ 19] = {2..7}
	};

	private static final int[] gr = { 0x69000000, // gr[ 0] = {24, 27, 29, 30}
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
			0x00000000 // gr[ 19] = {}
	};

	/**
	 * 10er-Potenzberechnung
	 * 
	 * @param e
	 *            Exponent
	 * @return Potenz 10<sup>exp</sup>.
	 */
	public static double powOf10(int e) {
		// Out.println(); Out.print("e0=" + e);
		double r;

		if (e < -307)
			return 0;
		else if (e > 308)
			return Double.NaN;
		e += 307;

		// assert e >= 0
		r = ten[e / 23] * tene[e % 23];
		/*
		 * int zh = Double.highPartToIntBits(r); int zl =
		 * Double.lowPartToIntBits(r); OutT.print('R'); OutT.print(zh);
		 * OutT.print('\t'); OutT.println(zl);
		 */
		// Out.println(", e2=" + e + ", (1 << e)=" + (1 << e) + ", (e >>> 5)=" +
		// (e >>> 5)+ ", eq[e >>> 5]=" + eq[e >>> 5]);
		if (((1 << e) & eq[e >>> 5]) != 0) { // e MOD 32 IN eq[e DIV 32]
			// Out.print(", e3=" + e);
			return r;
		}
		int E = Double.getExponent(r);
		r = Double.setExponent(r, 52);

		if (((1 << e) & gr[e >>> 5]) != 0) {
			// Out.print(" => r-1");
			r = r - 1;
		} else {
			// Out.print(" => r+1");
			r = r + 1; // IF e MOD 32 IN gr[e DIV 32] THEN
		}
		r = Double.setExponent(r, E);
		return r;
	}

	/*
	 * public static native long doubleToLongBits(double value); public static
	 * native double longBitsToDouble(long bits);
	 */

	public static double intBitsToDouble(int highBits, int lowBits) {
		double value = 0;
		if (_$bigEndian) {
			SYS.PUT4(SYS.ADR(value), highBits);
			SYS.PUT4(SYS.ADR(value) + 4, lowBits);
		} else { // little-endian
			SYS.PUT4(SYS.ADR(value) + 4, highBits);
			SYS.PUT4(SYS.ADR(value), lowBits);
		}
		return value;
	}

    public static  int  highPartToIntBits(double arg) {
		double value = arg;
		int	highBits;
		if (_$bigEndian) {
			highBits = SYS.GET4(SYS.ADR(value));
		} else { // little-endian
			highBits = SYS.GET4(SYS.ADR(value) + 4);
		}
		return highBits;
	}

    public static  int  lowPartToIntBits(double arg) {
		double value = arg;
		int	lowBits;
		if (_$bigEndian) {
			lowBits = SYS.GET4(SYS.ADR(value) + 4);
		} else { // little-endian
			lowBits = SYS.GET4(SYS.ADR(value));
		}
		return lowBits;
	}

	/**
	 * Bestimmt den Exponenten (zur Basis 2) des Argumtentes.
	 */
	public static  int  getExponent(double arg) {
		double x = arg;
		int  addr;
		if (_$bigEndian)	addr = SYS.ADR(x);	else	addr = SYS.ADR(x) + 6;
		return ((SYS.GET2(addr) >> 4) & 0x7FF) - expOffset;
	}

	/**
	 * Setzt den Exponenten (zur Basis 2) des Argumtentes neu und gibt den neuen
	 * Wert zurück.
	 */
	public static  double  setExponent(double arg, int newExp) {
		double x = arg;
		newExp += expOffset;
		int addr;
		if (_$bigEndian)
			addr = SYS.ADR(x);
		else
			addr = SYS.ADR(x) + 6;
		SYS.PUT2(addr, (SYS.GET2(addr) & 0x800F) | (newExp << 4));
		return x;
	}

	/**
	 * Bestimmt die Mantisse des Argumentes: 1.0 <= mantissa(x) < 2.0
	 * 
	 * @param arg
	 * @return die Mantisse des Argumentes
	 */
	public static  double  getMantissa(double arg) { // clear sign
		double x = arg;
		int  addr;
		if (_$bigEndian)	addr = SYS.ADR(x);	else	addr = SYS.ADR(x) + 6;
		int	highBits = SYS.GET2(addr);
		if ( (highBits & 0x7FF0)  == 0)	{ // denormalised or +-0
			x = 0;	highBits = 0;	// x *= twoPow52;	highBits = SYS.GET2(addr) & 0xFFFF; 	causes probles!
		}else {
			highBits = (highBits & 0x000F)  |  (expOffset << 4);	// 
			SYS.PUT2(addr, highBits);
		}
		return x;
	}

	private static final byte dMaxNofFractionDigits = 15;

	private static final char[] digits = new char[dMaxNofFractionDigits + 1];

	// private static int nofDigits;
	private static char[] gchars;

	private static int nofChars;

	private static final int highNaN = 0x7ff80000;

	private static final int highINF = 0x7ff00000;

	private static void putChar(char ch) {
		gchars[nofChars] = ch;
		nofChars++;
	}

	public static  int  doubleToRawBytes (double arg, char []  chars) {
		double val = arg;
		int	adr = SYS.ADR(val);
		int	nofBytes = 8;
		do {
			nofBytes--;
			chars[nofBytes] = (char) (SYS.GET1(adr + nofBytes) & 0xFF);
		} while (nofBytes > 0);
		/*
		 * chars[0] = (char)(SYS.GET1(adr + 0) & 0xFF); chars[1] =
		 * (char)(SYS.GET1(adr + 1) & 0xFF); chars[2] = (char)(SYS.GET1(adr + 2) &
		 * 0xFF); chars[3] = (char)(SYS.GET1(adr + 3) & 0xFF); chars[4] =
		 * (char)(SYS.GET1(adr + 4) & 0xFF); chars[5] = (char)(SYS.GET1(adr + 5) &
		 * 0xFF); chars[6] = (char)(SYS.GET1(adr + 6) & 0xFF); chars[7] =
		 * (char)(SYS.GET1(adr + 7) & 0xFF);
		 * 
		 * int patt = highPartToIntBits(val); chars[0] = (char)(patt >>> 24 &
		 * 0xFF); chars[1] = (char)(patt >>> 16 & 0xFF); chars[2] = (char)(patt
		 * >>> 8 & 0xFF); chars[3] = (char)(patt & 0xFF);
		 * 
		 * patt = lowPartToIntBits(val); chars[4] = (char)(patt >>> 24 & 0xFF);
		 * chars[5] = (char)(patt >>> 16 & 0xFF); chars[6] = (char)(patt >>> 8 &
		 * 0xFF); chars[7] = (char)(patt & 0xFF);
		 */
		return 8;
	}
	public static  int  floatToRawBytes (float arg, char []  chars) {
		float val = arg;
//		float fvalue = (float)val;
		int	adr = SYS.ADR(val);
		chars[0] = (char)(SYS.GET1(adr) & 0xFF);
		chars[1] = (char)(SYS.GET1(adr + 1) & 0xFF);
		chars[2] = (char)(SYS.GET1(adr + 2) & 0xFF);
		chars[3] = (char)(SYS.GET1(adr + 3) & 0xFF);
		return 4;
	}

	public static int doubleToChars(double val, int nofFractDigits, char[] chars) {
		gchars = chars;
		nofChars = 0;
		if (chars == null)
			return 0;

		int high = highPartToIntBits(val);
		// OutT.print('a'); OutT.println(high);

		if ((high & highINF) == highINF) {
			if ((high & highNaN) == highNaN) { // NaN
				putChar('N');
				putChar('a');
				putChar('N');
			} else { // INF
				if (high >= 0)
					putChar('+');
				else
					putChar('-');
				putChar('I');
				putChar('N');
				putChar('F');
			}
			putChar('\0');
			gchars = null;
			return nofChars;
		}

		// val != NaN && val != INF (ordinary value)
		int exp = (high & highINF) >> 20;
		if (exp != 0 && high < 0) {
			putChar('-');
			val = -val;
		}
		// OutT.print('e'); OutT.println(exp);

		int low;
		if (exp == 0) { // no denormals
			high = 0;
			low = 0;
		} else { // x > 2^-1023 ( x >
			// DoubleT.MIN_VALUE_NORM=2.2250738585072014E-308)
			if (nofFractDigits < 1)
				nofFractDigits = 1;
			else if (nofFractDigits > 15)
				nofFractDigits = 15;

			// exp = (exp - expOffset) * 301029 DIV 1000000; // ln(2)/ln(10) =
			// 0.301029996
			exp = (exp - expOffset) * 301029;
			// OutT.print('E'); OutT.println(exp);
			if (exp % 1000000 < 0)
				exp = exp / 1000000 - 1;
			else
				exp = exp / 1000000; // modulo division
			// OutT.print('E'); OutT.println(exp);

			double z = powOf10(exp + 1);
			/*
			 * int zh = highPartToIntBits(z); int zl = lowPartToIntBits(z);
			 * OutT.print('h'); OutT.print(zh); OutT.print('\t');
			 * OutT.println(zl);
			 */

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
		/*
		 * OutT.print('H'); OutT.print(high); OutT.print('\t');
		 * OutT.println(low);
		 */

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
		if (exp >= 0)
			putChar('+');
		else {
			putChar('-');
			exp = -exp;
		}
		/*
		 * OutT.print('E'); OutT.println(exp);
		 */
		putChar((char) (exp / 100 % 10 + '0'));
		putChar((char) (exp / 10 % 10 + '0'));
		putChar((char) (exp % 10 + '0'));

		putChar('\0');
		gchars = null;
		return nofChars;
	}
}
