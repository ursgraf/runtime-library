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
 * The {@code Byte} class wraps a value of primitive type 
 * {@code byte} in an object. An object of type {@code Byte}
 * contains a single field whose type is {@code byte}.
 * 
 * changes:
 * 20.9.2011	NTB/Urs Graf	ported to deep
 *
 */
public class Byte {

	/**
	 * A constant holding the minimum value a {@code byte} can
	 * have, -2<sup>7</sup>.
	 */
	public static final byte   MIN_VALUE = -128;

	/**
	 * A constant holding the maximum value a {@code byte} can
	 * have, 2<sup>7</sup>-1.
	 */
	public static final byte   MAX_VALUE = 127;
	
	/**
	 * Returns a new {@code String} object representing the
	 * specified {@code byte}. The radix is assumed to be 10.
	 *
	 * @param b the {@code byte} to be converted
	 * @return the string representation of the specified {@code byte}
	 * @see java.lang.Integer#toString(int)
	 */
	public static String toString(byte b) {
		return Integer.toString((int)b);
	}

	/**
	 * Returns a {@code Byte} instance representing the specified
	 * {@code byte} value.
	 *
	 * @param  b a byte value.
	 * @return a {@code Byte} instance representing {@code b}.
	 * @since  1.5
	 */
	public static Byte valueOf(byte b) {
		return new Byte(b);
	}


	/**
	 * The value of the {@code Byte}.
	 *
	 * @serial
	 */
	private final byte value;

	/**
	 * Constructs a newly allocated {@code Byte} object that
	 * represents the specified {@code byte} value.
	 *
	 * @param   value   the value to be represented by the
	 *                  {@code Byte} object.
	 */
	public Byte(byte value) {
		this.value = value;
	}

	/**
	 * Returns the value of this {@code Byte} as a
	 * {@code byte}.
	 */
	public byte byteValue() {
		return (byte)value;
	}

	/**
	 * Returns the value of this {@code Byte} as a
	 * {@code short}.
	 */
	public short shortValue() {
		return (short)value;
	}

	/**
	 * Returns the value of this {@code Byte} as an
	 * {@code int}.
	 */
	public int intValue() {
		return value;
	}

	/**
	 * Returns the value of this {@code Byte} as a
	 * {@code long}.
	 */
	public long longValue() {
		return (long)value;
	}

	/**
	 * Returns the value of this {@code Byte} as a
	 * {@code float}.
	 */
	public float floatValue() {
		return (float)value;
	}

	/**
	 * Returns the value of this {@code Byte} as a
	 * {@code double}.
	 */
	public double doubleValue() {
		return (double)value;
	}

}
