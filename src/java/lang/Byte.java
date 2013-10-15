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
