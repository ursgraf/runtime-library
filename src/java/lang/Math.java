package java.lang;

import ch.ntb.inf.deep.unsafe.US;

/*changes:
 04.09.09	NTB/MZ	ceil, floor, fix, class moved to java.lang
 27.10.06	NTB/ED	SYS.ADR(param)	==> SYS.ADR(locVar)
 25.08.06	NTB/ED	powOf10, sqrt
 14.08.04	NTB/ED	creation, powOf10, powIntExp
 */

public class Math {
	private static final boolean _$bigEndian = true; // big-endian

	/**
	 * Keine Instanzen zulassen.
	 */
	private Math() {
	}

	/**
	 * The <code>double</code> value that is closer than any other to <i>e</i>,
	 * the base of the natural logarithms.
	 */
	public static final double E = 2.7182818284590452354;

	/**
	 * The <code>double</code> value that is closer than any other to <i>pi</i>,
	 * the ratio of the circumference of a circle to its diameter.
	 */
	public static final double PI = 3.14159265358979323846;

	private static final double twoPI = 2 * PI;

	private static final double pio2 = PI / 2, pio4 = PI / 4;

	private static final int dNaN16MSBs = 0x7ff8;

	private static double twoPow52 = 1L << 52; // 2^52

	private static final int dExpOffset = 0x3ff;

	/*
	 * private static final int dExpINF = 0x7ff; private static final int
	 * dMaxNofFractionDigits = 16; private static double dMinValueNormalized =
	 * Double.MIN_VALUE * (double)(1L << 52);
	 */

	/**
	 * Potenzberechnung
	 * 
	 * @param base Basis
	 * @param exp Exponent
	 * @return Potenz base<sup>exp</sup>.
	 */
	public static double powIntExp(double base, int exp) {
		double p = 1.0;
		if (exp < 0) {
			base = 1.0 / base;
			exp = -exp;
		}
		while (exp != 0) {
			if ((exp & 1) != 0)
				p = p * base;
			exp = exp >>> 1;
			base = base * base;
		}
		return p;
	}

	/**
	 * Gibt den Betrag des Arguments zurück.<br>
	 * 
	 * @param a
	 * @return Betrag
	 */
	public static int abs(int a) {
		if (a < 0)
			a = -a;
		return a;
	}

	/**
	 * Gibt den Betrag des Arguments zurück.<br>
	 * 
	 * @param a
	 * @return Betrag
	 */
	public static double abs(double a) {
		if (a < 0)
			a = -a;
		return a;
	}

	/**
	 * Bestimmt den Maximalwert der beiden Argumente.<br>
	 * 
	 * @param a
	 * @param b
	 * @return b, falls b > a, sonst a
	 */
	public static int max(int a, int b) {
		if (b > a)
			a = b;
		return a;
	}

	/**
	 * Bestimmt den Maximalwert der beiden Argumente.<br>
	 * 
	 * @param a
	 * @param b
	 * @return b, falls b > a, sonst a
	 */
	public static double max(double a, double b) {
		if (b > a)
			a = b;
		return a;
	}

	/**
	 * Bestimmt den Minimalwert der beiden Argumente.<br>
	 * 
	 * @param a
	 * @param b
	 * @return b, falls b < a, sonst a
	 */
	public static int min(int a, int b) {
		if (b < a)
			a = b;
		return a;
	}

	/**
	 * Bestimmt den Minimalwert der beiden Argumente.<br>
	 * 
	 * @param a
	 * @param b
	 * @return b, falls b < a, sonst a
	 */
	public static double min(double a, double b) {
		if (b < a)
			a = b;
		return a;
	}

	/**
	 * Berechnung der Quadratwurzel.<br>
	 * 
	 * @param arg
	 * @return Quadratwurzel
	 */
	public static double sqrt(double arg) {
		double a = arg;
		int argAdr = 0;
//		if (_$bigEndian)
//			argAdr = US.ADR(a);
//		else
//			argAdr = US.ADR(a) + 6;

		int high = US.GET2(argAdr); // high = Math.abs(a) >>> 32
		if (high < 0)
			return Double.NaN;
		if ((high & 0x7FF0) == 0x7FF0)
			return a;

		int seDiv2 = 0;
		/*
		 * allow denormalized numbers if (a < Double.MIN_VALUE_NORM) { //
		 * denormalized number a = a * twoPow52; high = SYS.GET2(argAdr); seDiv2
		 * = -26 << 4; }
		 */
		if (high < 0x0010)
			return 0.0; // a < Double.MIN_VALUE_NORM: denormalized number

		int sexp = high - (dExpOffset << 4); // sexp: shifted exponent without
												// offset
		seDiv2 += (sexp >> 1) & (-1 << 4);
		int seMod2 = sexp & (1 << 4);
		high = (high & ((1 << 4) - 1)) + seMod2 + (dExpOffset << 4);
		US.PUT2(argAdr, high);

		double ar = a; // load a in reg ar
		double xa = (ar + 1) / 2;
		double xn = (xa + ar / xa) / 2;
		xa = (xn + ar / xn) / 2; // 1
		xn = (xa + ar / xa) / 2; // 2
		xa = (xn + ar / xn) / 2; // 3
		xn = (xa + ar / xa) / 2; // 4
		a = (xn + ar / xn) / 2; // 5
		// high = SYS.GET2(argAdr); SYS.PUT2(argAdr, high + seDiv2);
		US.PUT2(argAdr, US.GET2(argAdr) + seDiv2);
		return a;
	}

	private static final double // cos polynomial coefficients
			cosC2 = -0.4999999963,
			cosC8 = 0.0000247609, cosC4 = 0.0416666418,
			cosC10 = -0.0000002605,
			cosC6 = -0.0013888397;

	/**
	 * Returns the trigonometric cosine of an angle. Special cases:
	 * <ul>
	 * <li>If the argument is NaN or an infinity, then the result is NaN.
	 * </ul>
	 * 
	 * <p>
	 * The computed result must be within 1 ulp of the exact result. Results
	 * must be semi-monotonic.
	 * 
	 * @param arg an angle, in radians.
	 * @return the cosine of the argument.
	 */
	public static double cos(double arg) {
		if (Double.getExponent(arg) == Double.INF_EXPONENT)
			return Double.NaN;

		if (arg < 0)
			arg = -arg;
		arg = arg % (2 * Math.PI);

		int quadrant = (int) (arg / pio2);
		switch (quadrant) {
		case 1:
			arg = PI - arg;
			break;
		case 2:
			arg = arg - PI;
			break;
		case 3:
			arg = twoPI - arg;
			break;
		}

		double arg2 = arg * arg;
		double res = ((((cosC10 * arg2 + cosC8) * arg2 + cosC6) * arg2 + cosC4)
				* arg2 + cosC2)
				* arg2 + 1.0;
		if (((quadrant + 1) & 2) != 0)
			res = -res;
		return res;
	}

	private static final double // sine polynomial coefficients
			sinC2 = -0.1666666664,
			sinC8 = 0.0000027526, sinC4 = 0.0083333315,
			sinC10 = -0.0000000239,
			sinC6 = -0.0001984090;

	/**
	 * Returns the trigonometric sine of an angle. Special cases:
	 * <ul>
	 * <li>If the argument is NaN or an infinity, then the result is NaN.
	 * <li>If the argument is zero, then the result is a zero with the same sign
	 * as the argument.
	 * </ul>
	 * 
	 * <p>
	 * The computed result must be within 1 ulp of the exact result. Results
	 * must be semi-monotonic.
	 * 
	 * @param arg an angle, in radians.
	 * @return the sine of the argument.
	 */
	public static double sin(double arg) {
		if (Double.getExponent(arg) == Double.INF_EXPONENT)
			return Double.NaN;

		arg = arg % twoPI;
		if (arg < 0)
			arg = arg + twoPI;

		int quadrant = (int) (arg / pio2);
		switch (quadrant) {
		case 1:
			arg = PI - arg;
			break;
		case 2:
			arg = arg - PI;
			break;
		case 3:
			arg = twoPI - arg;
			break;
		}
		if (arg == 0.0)
			return arg;

		double arg2 = arg * arg;
		double res = ((((sinC10 * arg2 + sinC8) * arg2 + sinC6) * arg2 + sinC4)
				* arg2 + sinC2)
				* arg2 * arg + arg;
		if (quadrant >= 2)
			res = -res;
		return res;
	}

	private static final double // tanInPio4 polynomial coefficients
			tanC2 = 0.3333314036,
			tanC8 = 0.0245650893, tanC4 = 0.1333923995,
			tanC10 = 0.0029005250,
			tanC6 = 0.0533740603, tanC12 = 0.0095168091;

	private static double tanInPio4(double arg) {
		double arg2 = arg * arg;
		double res = (((((tanC12 * arg2 + tanC10) * arg2 + tanC8) * arg2 + tanC6)
				* arg2 + tanC4)
				* arg2 + tanC2)
				* arg2 * arg + arg;
		return res;
	}

	private static final double // cotInPio4 polynomial coefficients
			cotC2 = -0.3333333410,
			cotC8 = -0.0002078504,
			cotC4 = -0.0222220287,
			cotC10 = -0.0000262619, cotC6 = -0.0021177168;

	private static double cotInPio4(double arg) {
		final double maxCot = 1.633123935319537E16;

		if (arg < 1.0E-16)
			return 1.633123935319537E16; // nach demo

		double arg2 = arg * arg;
		double res = ((((cotC10 * arg2 + cotC8) * arg2 + cotC6) * arg2 + cotC4)
				* arg2 + cotC2)
				* arg2 + 1.0;
		return res / arg;
	}

	private static final double tanMax = 1.633123935319537E16;
	private static final double tanMinArg = 1.0E-17;

	/**
	 * Returns the trigonometric tangent of an angle. Special cases:
	 * <ul>
	 * <li>If the argument is NaN or an infinity, then the result is NaN.
	 * <li>If the argument is zero, then the result is a zero with the same sign
	 *     as the argument.
	 * </ul>
	 * 
	 * <p>
	 * The computed result must be within 1 ulp of the exact result. Results
	 * must be semi-monotonic.
	 * 
	 * @param arg an angle, in radians.
	 * @return the tangent of the argument.
	 */
	public static double tan(double arg) {
		if (Double.getExponent(arg) == Double.INF_EXPONENT)
			return Double.NaN;

		arg = arg % PI;
		if (arg == 0.0)
			return arg;

		if (arg < 0) {
			arg = arg + PI;
		}
		int octant = (int) (arg / pio4);
		arg = arg % pio4;

		double res;
		if (arg <= tanMinArg) {
			switch (octant & 3) {
			case 0:
				res = arg;
				break;
			case 1:
				res = 1.0;
				break;
			case 2:
				res = -tanMax;
				break;
			case 3:
				res = -1.0;
				break;
			default:
				res = Double.NaN;
			}
		} else {
			switch (octant & 3) {
			case 0:
				res = tanInPio4(arg);
				break;
			case 1:
				res = cotInPio4(pio4 - arg);
				break;
			case 2:
				res = -cotInPio4(arg);
				break;
			case 3:
				res = -tanInPio4(pio4 - arg);
				break;
			default:
				res = Double.NaN;
			}
		}
		return res;
	}

	private static final double // atan polynomial coefficients
			atanC2 = -0.3333314528,
			atanC10 = -0.0752896400,
			atanC4 = 0.1999355085,
			atanC12 = 0.0429096138,
			atanC6 = -0.1420889944,
			atanC14 = -0.0161657367,
			atanC8 = 0.1065626393,
			atanC16 = 0.0028662257;

	/**
	 * Returns the arc tangent of an angle, in the range of -<i>pi</i>/2 through
	 * <i>pi</i>/2. Special cases:
	 * <ul>
	 * <li>If the argument is NaN, then the result is NaN.
	 * <li>If the argument is zero, then the result is a zero with the same sign
	 * as the argument.
	 * </ul>
	 * 
	 * <p>
	 * The computed result must be within 1 ulp of the exact result. Results
	 * must be semi-monotonic.
	 * 
	 * @param arg the value whose arc tangent is to be returned.
	 * @return the arc tangent of the argument.
	 */
	public static double atan(double arg) {
		boolean neg = false, octant1 = false;
		if (arg < 0) {
			neg = true;
			arg = -arg;
		}
		if (arg > 1.0) {
			octant1 = true;
			arg = 1.0 / arg;
		}

		double arg2 = arg * arg;
		double res = ((((((((atanC16 * arg2 + atanC14) * arg2 + atanC12) * arg2 + atanC10)
				* arg2 + atanC8)
				* arg2 + atanC6)
				* arg2 + atanC4)
				* arg2 + atanC2)
				* arg2 + 1.0)
				* arg;

		if (octant1)
			res = pio2 - res;
		if (neg)
			res = -res;
		return res;
	}

	/**
	 * Round toward zero
	 * 
	 * @param x value to round
	 * @return rounded value
	 */
	public static int fix(float x) {
		return (int) x;
	}

	/**
	 * Round toward positive infinity
	 * 
	 * @param x value to round
	 * @return rounded value
	 */
	public static int ceil(float x) {
		if (x > 0)
			x += 1f;
		return fix(x);
	}

	/**
	 * Round toward negative infinity
	 * 
	 * @param x value to round
	 * @return rounded value
	 */
	public static int floor(float x) {
		if (x < 0)
			x -= 1f;
		return fix(x);
	}
}
