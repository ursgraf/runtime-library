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
 * The {@code Integer} class wraps a value of the primitive type
 * {@code int} in an object. An object of type {@code Integer}
 * contains a single field whose type is {@code int}.
 * 
 * changes:
 * 20.9.2011	NTB/Urs Graf	ported to deep
 *
 */
public class Integer {
    /**
     * A constant holding the minimum value an <code>int</code> can
     * have, -2<sup>31</sup>.
     */
    public static final int   MIN_VALUE = 0x80000000;

    /**
     * A constant holding the maximum value an <code>int</code> can
     * have, 2<sup>31</sup>-1.
     */
    public static final int   MAX_VALUE = 0x7fffffff;

    private static char str[] = new char[11];
	
	/**
	 * Converts a <code>integer</code> to a String.
	 * @param val the <code>integer</code> to convert.
	 * @return the converted <code>integer</code>
	 */
	public static String toString(int val){
		
		int i = str.length;
		boolean neg = false;
		if(val < 0){
			neg = true;
			val = -val;
		}
		do{
			str[--i] = (char) ((val % 10) + '0');
			val /= 10;
		}while(val > 0);
		if(neg) str[--i] = '-';
		return new String(str, i, str.length - i);
	}
	
	/**
	 * Converts <code>integer</code> to a char array.
	 * @param string the <code>char</code> array who represents the string.
	 * @param off the start position of the <code>char</code> array.
	 * @param val the <code>integer</code> to convert.
	 * @return the length of the string
	 */
	public static int toCharArray(char string[], int off, int val){
		int i = str.length;
		boolean neg = false;
		if(val < 0){
			neg = true;
			val = -val;
		}
		do{
			str[--i] = (char) ((val % 10) + '0');
			val /= 10;
		}while(val > 0);
		if(neg) str[--i] = '-';
		int len = str.length -i;
		for(int j = 0; j < len; j++){
			string[j + off] = str[j+i];
		}	
		return len + off;
	}

	/**
	 * Returns an {@code Integer} instance representing the specified
	 * {@code int} value.  
	 *
	 * @param  i an {@code int} value.
	 * @return an {@code Integer} instance representing {@code i}.
	 * @since  1.5
	 */
	public static Integer valueOf(int i) {
		return new Integer(i);
	}

	/**
	 * The value of the {@code Integer}.
	 *
	 * @serial
	 */
	private final int value;

	/**
	 * Constructs a newly allocated {@code Integer} object that
	 * represents the specified {@code int} value.
	 *
	 * @param   value   the value to be represented by the
	 *                  {@code Integer} object.
	 */
	public Integer(int value) {
		this.value = value;
	}

	/**
	 * Returns the value of this {@code Integer} as a
	 * {@code byte}.
	 */
	public byte byteValue() {
		return (byte)value;
	}

	/**
	 * Returns the value of this {@code Integer} as a
	 * {@code short}.
	 */
	public short shortValue() {
		return (short)value;
	}

	/**
	 * Returns the value of this {@code Integer} as an
	 * {@code int}.
	 */
	public int intValue() {
		return value;
	}

	/**
	 * Returns the value of this {@code Integer} as a
	 * {@code long}.
	 */
	public long longValue() {
		return (long)value;
	}

	/**
	 * Returns the value of this {@code Integer} as a
	 * {@code float}.
	 */
	public float floatValue() {
		return (float)value;
	}

	/**
	 * Returns the value of this {@code Integer} as a
	 * {@code double}.
	 */
	public double doubleValue() {
		return (double)value;
	}

}
