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

package java.util;

import java.io.Serializable;

import org.deepjava.marker.Modified;

/**
 * This class provides methods that return pseudo-random values.
 *
 * <p>It is dangerous to seed {@code Random} with the current time because
 * that value is more predictable to an attacker than the default seed.
 *
 */
/* Changes:
 * 2.6.2014	Urs Graf	initial import and modified
 */
public class Random implements Serializable, Modified {

    private static final long serialVersionUID = 3905348978240129619L;

    private static final long multiplier = 0x5deece66dL;

    /**
     * The boolean value indicating if the second Gaussian number is available.
     *
     * @serial
     */
    private boolean haveNextNextGaussian;

    /**
     * @serial It is associated with the internal state of this generator.
     */
    private long seed;

    /**
     * The second Gaussian generated number.
     *
     * @serial
     */
    private double nextNextGaussian;

    /**
     * Constructs a random generator with an initial state that is
     * unlikely to be duplicated by a subsequent instantiation.
     *
     * <p>The initial state (that is, the seed) is <i>partially</i> based
     * on the current time of day in milliseconds.
     */
    public Random() {
        // Note: Using identityHashCode() to be hermetic wrt subclasses.
//        setSeed(System.currentTimeMillis() + System.identityHashCode(this));
        setSeed(System.currentTimeMillis());
    }

    /**
     * Construct a random generator with the given {@code seed} as the
     * initial state. Equivalent to {@code Random r = new Random(); r.setSeed(seed);}.
     *
     * <p>This constructor is mainly useful for <i>predictability</i> in tests.
     * The default constructor is likely to provide better randomness.
     * @param seed Seed value.
     */
    public Random(long seed) {
        setSeed(seed);
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code int} value of
     * the number of bits specified by the argument {@code bits} as
     * described by Donald E. Knuth in <i>The Art of Computer Programming,
     * Volume 2: Seminumerical Algorithms</i>, section 3.2.1.
     *
     * <p>Most applications will want to use one of this class' convenience methods instead.
     * @param bits Number of bits.
     * @return Random value.
     */
    protected synchronized int next(int bits) {
        seed = (seed * multiplier + 0xbL) & ((1L << 48) - 1);
        return (int) (seed >>> (48 - bits));
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code boolean}.
     * @return Random value.
     */
    public boolean nextBoolean() {
        return next(1) != 0;
    }

    /**
     * Fills {@code buf} with random bytes.
     * @param buf Buffer to fill.
     */
    public void nextBytes(byte[] buf) {
        int rand = 0, count = 0, loop = 0;
        while (count < buf.length) {
            if (loop == 0) {
                rand = nextInt();
                loop = 3;
            } else {
                loop--;
            }
            buf[count++] = (byte) rand;
            rand >>= 8;
        }
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code double}
     * in the half-open range [0.0, 1.0).
     * @return Random value.
     */
    public double nextDouble() {
        return ((((long) next(26) << 27) + next(27)) / (double) (1L << 53));
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code float}
     * in the half-open range [0.0, 1.0).
     * @return Random value.
     */
    public float nextFloat() {
        return (next(24) / 16777216f);
    }

    /**
     * Returns a pseudo-random (approximately) normally distributed
     * {@code double} with mean 0.0 and standard deviation 1.0.
     * This method uses the <i>polar method</i> of G. E. P. Box, M.
     * E. Muller, and G. Marsaglia, as described by Donald E. Knuth in <i>The
     * Art of Computer Programming, Volume 2: Seminumerical Algorithms</i>,
     * section 3.4.1, subsection C, algorithm P.
     * @return Random value.
     */
    public synchronized double nextGaussian() {
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false;
            return nextNextGaussian;
        }

        double v1, v2, s;
        do {
            v1 = 2 * nextDouble() - 1;
            v2 = 2 * nextDouble() - 1;
            s = v1 * v1 + v2 * v2;
        } while (s >= 1 || s == 0);

        // The specification says this uses StrictMath, however we use Math
//        double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
        double multiplier = Math.sqrt(-2 * Math.log(s) / s);
        nextNextGaussian = v2 * multiplier;
        haveNextNextGaussian = true;
        return v1 * multiplier;
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code int}.
     * @return Random value.
     */
    public int nextInt() {
        return next(32);
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code int}
     * in the half-open range [0, n).
     * @param n Upper limit.
     * @return Random value.
     */
    public int nextInt(int n) {
        if (n <= 0) {
//            throw new IllegalArgumentException("n <= 0: " + n);
            throw new IllegalArgumentException("nextInt in Random");
        }
        if ((n & -n) == n) {
            return (int) ((n * (long) next(31)) >> 31);
        }
        int bits, val;
        do {
            bits = next(31);
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }

    /**
     * Returns a pseudo-random uniformly distributed {@code long}.
     * @return Random value.
     */
    public long nextLong() {
        return ((long) next(32) << 32) + next(32);
    }

    /**
     * Modifies the seed using a linear congruential formula presented in <i>The
     * Art of Computer Programming, Volume 2</i>, Section 3.2.1.
     * @param seed Seed value.
     */
    public synchronized void setSeed(long seed) {
        this.seed = (seed ^ multiplier) & ((1L << 48) - 1);
        haveNextNextGaussian = false;
    }
}
