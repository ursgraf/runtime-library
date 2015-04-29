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

package ch.ntb.inf.deep.runtime.util;

import java.io.PrintStream;
import java.lang.Math;

/**
 * This class implements matrices and their operations.
 */
public class Matrix {
	protected int rows, columns;
	protected double[][] value;
	
	private Matrix tmpTranspose = null;
	
	/**
	 * Creates a new <code>Matrix</code> from array of <code>double</code>.
	 * This does not copy the array but simply references the same memory range.
	 * 
	 * @param data Array of <code>double</code>. Rows of the matrix will
	 * be set to the first dimension of <code>data</code>. 
	 */
	public Matrix(double[][] data) {
		rows = data.length;
		
		if (rows == 0)
			columns = 0;
		else
			columns = data[0].length;
		
		value = data;
	}
	
	/**
	 * Creates a new copy of a <code>Matrix</code> from a given matrix <code>M</code>.
	 * 
	 * @param M Matrix to be copied from.
	 */
	public Matrix(Matrix M) {
		this(M.rows, M.columns);
		if (!copy(M)) {
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++)
					value[i][j] = 0;
		}
	}
	
	/** 
	 * Creates a new <code>Matrix</code> with <code>n</code> rows and <code>m</code> columns.
	 * 
	 * @param n Number of rows.
	 * @param m Number of columns.
	 */
	public Matrix(int n, int m) {
		rows = n;
		columns = m;
		value = new double[n][];
		for (int i = 0; i < n; i++)
			value[i] = new double[m];
	}
	
	/**
	 * Read an entry in a matrix. Returns <code>Double.NaN</code> if specified position is not within the matrix.
	 * 
	 * @param i	Row (1..n)
	 * @param j Column (1..m)
	 * @return	Entry at <code>i</code>,<code>j</code> or <code>Double.NaN</code>
	 * 		if specified position is not within the matrix
	 */
	public double get(int i, int j) {
		if (i < 1 || i > rows || j < 1 || j > columns) return Double.NaN;
		return value[i-1][j-1];
	}
	
	/**
	 * Writes an entry in a matrix. Returns <code>false</code> if specified position is not within the matrix.
	 * 
	 * @param i	Row (1..n)
	 * @param j Column (1..m)
	 * @param value Value to write.
	 * @return	<code>false</code> if specified position is not within the matrix.
	 */
	public boolean set(int i, int j, double value) {
		if (i < 1 || i > rows || j < 1 || j > columns)
			return false;
		
		this.value[i-1][j-1] = value;
		return true;
	}
	
	/**
	 * Reads number of rows in a matrix.
	 * 
	 * @return Number of rows.
	 */
	public int getRowCount() { return rows; }

	/**
	 * Reads number of columns in a matrix.
	 * 
	 * @return Number of columns.
	 */
	public int getColumnCount() { return columns; }

	/**
	 * Adds two matrices and stores the result in this instance. 
	 * Returns <code>false</code> if the dimensions of the involved matrices do not fit.
	 * 
	 * @param left First input matrix
	 * @param right Second input matrix
	 * @return	<code>false</code> if dimensions of the involved matrices do not fit.
	 */
	public boolean add(Matrix left, Matrix right) {
		int n = left.rows;
		int m = left.columns;
		
		if (right.rows != n || this.rows != n || right.columns != m || this.columns != m) return false;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				this.value[i][j] = left.value[i][j] + right.value[i][j];
			}
		}
		return true;
	}

	/**
	 * Multiplies two matrices and stores the result in this instance. 
	 * Returns <code>false</code> dimensions of the involved matrices do not fit.
	 * 
	 * @param left First input matrix
	 * @param right Second input matrix
	 * @return	<code>false</code> if the number of columns of <code>left</code> != number of rows of <code>right</code> or
	 * 	if the number of columns of <code>right</code> != number of columns of <code>this</code> or
	 *  if the number of rows of <code>left</code> != number of rows of <code>this</code> or.
	 */
	public boolean multiply(Matrix left, Matrix right) 	{
		int n = left.rows;
		int m = left.columns;
		int k = this.columns;
		
		if (right.rows != m || right.columns != k || this.rows != n) return false;
		
		for (int j = 0; j < k; j++) {
			for (int i = 0; i < n; i++) {
				this.value[i][j] = 0;
				for (int l = 0; l < m; l++) {
					this.value[i][j] += left.value[i][l] * right.value[l][j];
				}
			}
		}
		return true;
	}

	public boolean transpose(Matrix original) {
		if (this == original) {
			transpose();
			return true;
		}

		if (original.rows != columns || original.columns != rows)
			return false;
		
		for (int j = 0; j < columns; j++)
			for (int i = 0; i < rows; i++)
				value[i][j] = original.value[j][i];

		return true;
	}
	
	public void transpose() {
		if (tmpTranspose == null) tmpTranspose = this.clone();
		else tmpTranspose.copy(this);
		transpose(tmpTranspose);
	}

	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return A clone of this instance.
	 */
	public Matrix clone() {return new Matrix(this);}
	
	/**
	 * Copies the content of matrix <code>M</code> into this matrix. 
	 * 
	 * @param M The matrix to be copied from.
	 * @return <code>true</code> if successful, <code>false</code> if the two matrices have different dimensions.
	 */
	public boolean copy(Matrix M) {
		if (M.rows != rows || M.columns != columns)
			return false;
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				value[i][j] = M.value[i][j];

		return true;
	}
	
	/**
	 * Compares this object against the specified object. 
	 * 
	 * @param M The matrix to compare with.
	 * @return <code>true</code> if every value of this matrix is the same as the value of the compared matrix.
	 */
	public boolean isEqual(Matrix M) {
		if (M == this) return true;
		if (M.rows != rows || M.columns != columns)
			return false;

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				if (value[i][j] != M.value[i][j])
					return false;
		
		return true;
	}
	
	public static boolean rot3x(Matrix A, double angle) {
		if (A.getRowCount() != 3 || A.getColumnCount() != 3)
			return false;
		
		A.value[0][0] = 1;
		A.value[1][0] = 0;
		A.value[2][0] = 0;
		
		A.value[0][1] = 0;
		A.value[1][1] = Math.cos(angle);
		A.value[2][1] = Math.sin(angle);
		
		A.value[0][2] = 0;
		A.value[1][2] = -Math.sin(angle);
		A.value[2][2] = Math.cos(angle);
		
		return true;
	}

	public static Matrix rot3x(double angle) {
		Matrix R = new Matrix(3, 3);
		rot3x(R, angle);
		return R;
	}
	
	/**
	 * Prints a matrix onto a print stream.
	 * 
	 * @param out Print stream
	 */
	public void print(PrintStream out) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				out.print('\t');
				out.print((float)value[i][j]);
			}
			out.println();
		}
	}
	
}

