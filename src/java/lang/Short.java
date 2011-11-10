/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package java.lang;


/**
 * The <code>Short</code> class wraps a value of primitive type
 * <code>short</code> in an object.  An object of type
 * <code>Short</code> contains a single field whose type is
 * <code>short</code>.
 * 
 * changes:
 * 20.9.2011	NTB/Urs Graf	ported to deep
 *
 */
public class Short {

    /**
     * A constant holding the minimum value a <code>short</code> can
     * have, -2<sup>15</sup>.
     */
    public static final short   MIN_VALUE = -32768;

    /**
     * A constant holding the maximum value a <code>short</code> can
     * have, 2<sup>15</sup>-1.
     */
    public static final short   MAX_VALUE = 32767;
	
    /**
     * Returns a new <code>String</code> object representing the
     * specified <code>short</code>. The radix is assumed to be 10.
     *
     * @param s the <code>short</code> to be converted
     * @return the string representation of the specified <code>short</code>
     * @see java.lang.Integer#toString(int)
     */
    public static String toString(short s) {
    	return Integer.toString((int)s);
    }

	/**
	 * Returns a {@code Short} instance representing the specified
	 * {@code short} value.
	 *
	 * @param  sh a short value.
	 * @return a {@code Short} instance representing {@code sh}.
	 * @since  1.5
	 */
	public static Short valueOf(short sh) {
		return new Short(sh);
	}


	/**
	 * The value of the {@code Short}.
	 *
	 * @serial
	 */
	private final short value;

	/**
	 * Constructs a newly allocated {@code Short} object that
	 * represents the specified {@code short} value.
	 *
	 * @param   value   the value to be represented by the
	 *                  {@code Short} object.
	 */
	public Short(short value) {
		this.value = value;
	}

	/**
	 * Returns the value of this {@code Short} as a
	 * {@code short}.
	 */
	public short byteValue() {
		return (short)value;
	}

	/**
	 * Returns the value of this {@code Short} as a
	 * {@code short}.
	 */
	public short shortValue() {
		return (short)value;
	}

	/**
	 * Returns the value of this {@code Short} as an
	 * {@code int}.
	 */
	public int intValue() {
		return value;
	}

	/**
	 * Returns the value of this {@code Short} as a
	 * {@code long}.
	 */
	public long longValue() {
		return (long)value;
	}

	/**
	 * Returns the value of this {@code Short} as a
	 * {@code float}.
	 */
	public float floatValue() {
		return (float)value;
	}

	/**
	 * Returns the value of this {@code Short} as a
	 * {@code double}.
	 */
	public double doubleValue() {
		return (double)value;
	}

}
