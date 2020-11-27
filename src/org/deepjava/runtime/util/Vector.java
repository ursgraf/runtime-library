/*
 * Copyright 2011 - 2014 NTB University of Applied Sciences in Technology
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

package org.deepjava.runtime.util;

/**
 * This class implements a n-dimensional vector.
 */
public class Vector extends Matrix {
	
	/**
	 * Creates a new <code>Vector</code> with <code>n</code> dimensions.
	 * Each entry is of type <code>double</code>.
	 * 
	 * @param n	Dimension
	 * @param row <code>true</code>: vector has <code>n</code> rows and one column,
	 * 		<code>false</code>: vector has <code>n</code> columns and one row.
	 */
	public Vector(int n, boolean row) {
		super(((row) ? n : 1), ((row) ? 1 : n));
	}
	
	/**
	 * Creates a new <code>Vector</code> with <code>n</code> dimensions.
	 * The <code>Vector</code> has one row and <code>n</code> columns.
	 * 
	 * @param n Dimension
	 */
	public Vector(int n) {this(n, false);}
	
	/**
	 * Returns the size of a <code>Vector</code>.
	 * 
	 * @return Dimension of the <code>Vector</code>.
	 */
	public int getSize() {return (rows==1) ? columns : rows; }
	
	/**
	 * Set one element in a <code>Vector</code>.
	 * 
	 * @param i	Element number (1..n)
	 * @param value Value to set
	 * @return <code>false</code> if specified position is not within the vector.
	 */
	public boolean set(int i, double value) {
		if (rows == 1) return set(1, i, value);
		return set(i, 1, value);
	}
	
	/**
	 * Read an entry in a <code>Vector</code>. Returns <code>Double.NaN</code> 
	 * if specified position is not within the <code>Vector</code>.
	 * 
	 * @param i	Element number (1..n)
	 * @return	Entry at <code>i</code> or <code>Double.NaN</code>
	 * 		if specified position is not within the vector.
	 */
	public double get(int i) {
		if (rows == 1) return get(1, i);
		else return get(i, 1);
	}

	/**
	 * Adds two vectors and stores the result in this instance. 
	 * Returns <code>false</code> if the dimensions of the involved vectors do not fit.
	 * 
	 * @param left First input vector
	 * @param right Second input vector
	 * @param ignoreOrientation If <code>true</code> the orientation (row vector - column vector)
	 * 			is not checked
	 * @return	<code>false</code> if dimensions of the involved vectors do not fit.
	 */
	public boolean add(Vector left, Vector right, boolean ignoreOrientation) {
		int size = this.getSize();
		if (left.getSize() != size || right.getSize() != size) return false;
		if (!ignoreOrientation && (rows != left.rows || rows != right.rows)) return false;
		for (int i = 1; i <= size; i++)	set(i, left.get(i) + right.get(i));		
		return true;
	}
	
	/**
	 * Calculates the dot products of a vector with this instance. 
	 * Returns <code>Double.NaN</code> if the dimensions of the involved vectors do not fit.
	 * 
	 * @param right Input vector
	 * @return Dot product or <code>Double.NaN</code>
	 * 		if dimensions of the involved vectors do not fit.
	 */
	public double dot(Vector right)	{
		int size = this.getSize();
		if (right.getSize() != size)
			return Double.NaN;
		
		double result = 0;
		for (int i = 1; i <= size; i++)
			result += this.get(i) * right.get(i);
		
		return result;
	}
}
