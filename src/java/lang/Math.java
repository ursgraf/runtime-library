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

import java.util.Random;
import ch.ntb.inf.deep.lowLevel.LL;
import ch.ntb.inf.deep.marker.Modified;

/**
 * Class Math provides basic math constants and operations such as trigonometric
 * functions, hyperbolic functions, exponential, logarithms, etc.
 */
/* Changes:
 * 2.6.2014	Urs Graf	initial import and modified
 */
public final class Math implements Modified {
	/**
	 * The double value closest to e, the base of the natural logarithm.
	 */
	public static final double E = 2.718281828459045;

	/**
	 * The double value closest to pi, the ratio of a circle's circumference to
	 * its diameter.
	 */
	public static final double PI = 3.141592653589793;

	private static Random random;

	/**
	 * Prevents this class from being instantiated.
	 */
	private Math() {
	}

	private static final double twoPI = 2 * PI;

	private static final double pio2 = PI / 2, pio4 = PI / 4;

	private static final int dExpOffset = 0x3ff;

	/**
	 * Calculation of power with exponent being an integer value
	 * 
	 * @param base base
	 * @param exp exponent
	 * @return power base<sup>exp</sup>.
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
	 * Returns the closest double approximation of the logarithm to a 
	 * certain base. The returned result is within 1 ulp (unit in the last place) of
	 * the real result.
	 *
	 * @param d
	 *            the value whose log has to be computed.
	 * @param base
	 *            the base to which the logarith is computed.
	 * @return the natural logarithm of the argument.
	 */
 	public static double log(double d, double base) {
		double sign = 1.0;
	
		if (d <= 1.0 || base <= 1.0) {
			if (d <= 0.0 || base <= 0.0 ) return Double.NaN;
	   		if (d < 1.0) {d = 1.0 / d; sign *= -1.0;}
	   		if (base < 1.0) {sign *= -1.0; base = 1.0 / base;}
	   		if (d == 1.0) {
	   			if (base != 1.0) return 0.0;
	   			return 1.0;
	   		}
	   	}
		double n = 0.0;
		while (d >= base) {d /= base; n++;}
		if (d == 1.0) return (sign * n);
		return sign * (n + (1.0 / log(base, d)));
	}

	/**
	 * Returns the closest double approximation of the natural logarithm of the
	 * argument. The returned result is within 1 ulp (unit in the last place) of
	 * the real result.
	 *
	 * @param d
	 *            the value whose log has to be computed.
	 * @return the natural logarithm of the argument.
	 */
 	public static double log(double d) {
		return log(d, E);
	}

    /**
     * Returns the closest double approximation of the base 10 logarithm of the
     * argument. The returned result is within 1 ulp (unit in the last place) of
     * the real result.
     *
     * @param d
     *            the value whose base 10 log has to be computed.
     * @return the natural logarithm of the argument.
     */
	public static double log10(double d) {
		return log(d, 10);
	}

 	/**
	 * Returns the absolute value of the argument.
	 * <p>
	 * Special cases:
	 * <ul>
	 * <li>{@code abs(-0.0) = +0.0}</li>
	 * <li>{@code abs(+infinity) = +infinity}</li>
	 * <li>{@code abs(-infinity) = +infinity}</li>
	 * <li>{@code abs(NaN) = NaN}</li>
	 * </ul>
	 * @param d Input value.
	 * @return Absolute value.
	 */
	public static double abs(double d) {
		return (d >= 0) ? d : -d;
	}

	/**
	 * Returns the absolute value of the argument.
	 * <p>
	 * Special cases:
	 * <ul>
	 * <li>{@code abs(-0.0) = +0.0}</li>
	 * <li>{@code abs(+infinity) = +infinity}</li>
	 * <li>{@code abs(-infinity) = +infinity}</li>
	 * <li>{@code abs(NaN) = NaN}</li>
	 * </ul>
	 * @param f Input value.
	 * @return Absolute value.
	 */
	public static float abs(float f) {
		return (f >= 0) ? f : -f;
	}
	
    /**
     * Returns the absolute value of the argument.
     * <p>
     * If the argument is {@code Integer.MIN_VALUE}, {@code Integer.MIN_VALUE}
     * is returned.
	 * @param i Input value.
	 * @return Absolute value.
     */
    public static int abs(int i) {
        return (i >= 0) ? i : -i;
    }

    /**
     * Returns the absolute value of the argument. If the argument is {@code
     * Long.MIN_VALUE}, {@code Long.MIN_VALUE} is returned.
	 * @param l Input value.
	 * @return Absolute value.
     */
    public static long abs(long l) {
        return (l >= 0) ? l : -l;
    }

    /**
     * Returns the most positive (closest to positive infinity) of the two
     * arguments.
	 * @param i1 First value.
	 * @param i2 Second value.
	 * @return Maximum of the two input values.
     */
    public static int max(int i1, int i2) {
        return i1 > i2 ? i1 : i2;
    }

    /**
     * Returns the most positive (closest to positive infinity) of the two
     * arguments.
	 * @param l1 First value.
	 * @param l2 Second value.
	 * @return Maximum of the two input values.
     */
    public static long max(long l1, long l2) {
        return l1 > l2 ? l1 : l2;
    }

	/**
	 * Returns maximum value<br>
	 * 
	 * @param a First value.
	 * @param b Second value.
	 * @return Maximum of the two input values.
	 * @return b if b &gt; a, else a
	 */
	public static double max(double a, double b) {
		if (b > a)
			a = b;
		return a;
	}

	/**
	 * Returns the most negative (closest to negative infinity) of the two
	 * arguments.
	 * @param i1 First value.
	 * @param i2 Second value.
	 * @return Minimum of the two input values.
	 */
	public static int min(int i1, int i2) {
		return i1 < i2 ? i1 : i2;
	}

	/**
	 * Returns the most negative (closest to negative infinity) of the two
	 * arguments.
	 * @param l1 First value.
	 * @param l2 Second value.
	 * @return Minimum of the two input values.
	 */
	public static long min(long l1, long l2) {
        return l1 < l2 ? l1 : l2;
    }

	/**
	 * Returns minimum value<br>
	 * 
	 * @param a First value.
	 * @param b Second value.
	 * @return Minimum of the two input values.
	 * @return b if b &lt; a, else a
	 */
	public static double min(double a, double b) {
		if (b < a)
			a = b;
		return a;
	}

	/**
	 * Calculates square root.<br>
	 * 
	 * @param arg Input value
	 * @return square root
	 */
	public static double sqrt(double arg) {
		double a = arg;
		long bits = LL.doubleToBits(a); 
		int e = (int)(bits >>> 52);
		if ((e & 0x800) == 0x800)
			return Double.NaN;
		if ((e & 0x7FF) == 0x7FF)
			return a;

		if (e < 0x0010)
			return 0.0; 

		int exp = e - dExpOffset; // exponent without offset
		int expDiv2 = exp >> 1;
		int expMod2 = exp & 1;
		exp = expMod2 + dExpOffset;
		bits = bits & 0xfffffffffffffL | ((long)(exp) << 52);
		a = LL.bitsToDouble(bits);

		double xa = (a + 1) / 2;
		double xn = (xa + a / xa) / 2;
		xa = (xn + a / xn) / 2; // 1
		xn = (xa + a / xa) / 2; // 2
		xa = (xn + a / xn) / 2; // 3
		xn = (xa + a / xa) / 2; // 4
		a = (xn + a / xn) / 2; // 5
		bits = LL.doubleToBits(a);
		bits += (long)expDiv2 << 52;
		a = LL.bitsToDouble(bits);
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
		if (Double.getExponent(arg) == (int)Double.INF_EXPONENT)
			return Double.NaN;

		if (arg < 0)
			arg = -arg;
		arg = arg % twoPI;

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
		if (Double.getExponent(arg) == (int)Double.INF_EXPONENT)
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
//		final double maxCot = 1.633123935319537E16;

		if (arg < 1.0E-16)
			return 1.633123935319537E16;

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
		if (Double.getExponent(arg) == (int)Double.INF_EXPONENT)
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
	 * Returns the arc tangent of an angle, in the range of -<i>pi</i>/2 through
	 * <i>pi</i>/2. <br>
	 * arctan(arg1/arg2) is calculated considering signs of the inputs<br>
	 * Special cases:<br>
	 * If both arguments are zero, NaN is returned
	 * @param arg1 Nominator	
	 * @param arg2 Denominator
	 * @return the arc tangent of the argument
	 */
	public static double atan2(double arg1, double arg2){
		if(arg1+arg2 == arg1) {
			if(arg1 > 0)
				return Math.PI/2;
			if(arg1 == 0)
				return Double.NaN;
			return -Math.PI/2;
		}
		arg1 = Math.atan(arg1/arg2);
		if(arg2 < 0){
			if(arg1 <= 0)
				return arg1 + Math.PI;
			return arg1 - Math.PI;
		}
		return arg1;
	}
	
	/**
	 * Returns the arc cosinus of an angle in radians
	 * @param x the value whose arc cosinus is to be returned
	 * @return the arc cosinus of the argument
	 */
	public static double acos(double x){
		if (x == 1){
			return 0;
		}
		if (x == 0){
			return Math.PI/2;
		}
		else{
			return Math.atan(-x / Math.sqrt(-x*x+1)) + Math.PI/2;
		}
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
	
    /**
     * Returns a pseudo-random double {@code n}, where {@code n >= 0.0 && n < 1.0}.
     * This method reuses a single instance of {@link java.util.Random}.
     * This method is thread-safe because access to the {@code Random} is synchronized,
     * but this harms scalability. Applications may find a performance benefit from
     * allocating a {@code Random} for each of their threads.
     *
     * @return a pseudo-random number.
     */
    public static synchronized double random() {
        if (random == null) {
            random = new Random();
        }
        return random.nextDouble();
    }

    /**
     * Returns the measure in radians of the supplied degree angle. The result
     * is {@code angdeg / 180 * pi}.
     * <p>
     * Special cases:
     * <ul>
     * <li>{@code toRadians(+0.0) = +0.0}</li>
     * <li>{@code toRadians(-0.0) = -0.0}</li>
     * <li>{@code toRadians(+infinity) = +infinity}</li>
     * <li>{@code toRadians(-infinity) = -infinity}</li>
     * <li>{@code toRadians(NaN) = NaN}</li>
     * </ul>
     *
     * @param angdeg
     *            an angle in degrees.
     * @return the radian measure of the angle.
     */
    public static double toRadians(double angdeg) {
        return angdeg / 180d * PI;
    }

    /**
     * Returns the measure in degrees of the supplied radian angle. The result
     * is {@code angrad * 180 / pi}.
     * <p>
     * Special cases:
     * <ul>
     * <li>{@code toDegrees(+0.0) = +0.0}</li>
     * <li>{@code toDegrees(-0.0) = -0.0}</li>
     * <li>{@code toDegrees(+infinity) = +infinity}</li>
     * <li>{@code toDegrees(-infinity) = -infinity}</li>
     * <li>{@code toDegrees(NaN) = NaN}</li>
     * </ul>
     *
     * @param angrad
     *            an angle in radians.
     * @return the degree measure of the angle.
     */
    public static double toDegrees(double angrad) {
        return angrad * 180d / PI;
    }

}
