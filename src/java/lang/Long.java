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
 */
public class Long {
    /**
     * A constant holding the minimum value a <code>long</code> can
     * have, -2<sup>63</sup>.
     */
    public static final long MIN_VALUE = 0x8000000000000000L;

    /**
     * A constant holding the maximum value a <code>long</code> can
     * have, 2<sup>63</sup>-1.
     */
    public static final long MAX_VALUE = 0x7fffffffffffffffL;
	
    /**
     * Returns a <tt>Long</tt> instance representing the specified
     * <tt>long</tt> value.
     * If a new <tt>Long</tt> instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Long(long)}, as this method is likely to yield
     * significantly better space and time performance by caching
     * frequently requested values.
     *
     * @param  l a long value.
     * @return a <tt>Long</tt> instance representing <tt>l</tt>.
     */
    public static Long valueOf(long l) {
    	return new Long(l);
    }

    /**
     * The value of the <code>Long</code>.
     *
     * @serial
     */
    private final long value;

    /**
     * Constructs a newly allocated <code>Long</code> object that
     * represents the specified <code>long</code> argument.
     *
     * @param   value   the value to be represented by the 
     *          <code>Long</code> object.
     */
    public Long(long value) {
    	this.value = value;
    }


    /**
     * Returns the value of this <code>Long</code> as a
     * <code>byte</code>.
     */
    public byte byteValue() {
    	return (byte)value;
    }

    /**
     * Returns the value of this <code>Long</code> as a
     * <code>short</code>.
     */
    public short shortValue() {
    	return (short)value;
    }

    /**
     * Returns the value of this <code>Long</code> as an
     * <code>int</code>.
     */
    public int intValue() {
    	return (int)value;
    }

    /**
     * Returns the value of this <code>Long</code> as a
     * <code>long</code> value.
     */
    public long longValue() {
    	return (long)value;
    }

    /**
     * Returns the value of this <code>Long</code> as a
     * <code>float</code>.
     */
    public float floatValue() {
    	return (float)value;
    }

    /**
     * Returns the value of this <code>Long</code> as a
     * <code>double</code>.
     */
    public double doubleValue() {
    	return (double)value;
    }
}