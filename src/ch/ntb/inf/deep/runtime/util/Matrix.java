// TODO @Adam License, JavaDoc
package ch.ntb.inf.deep.runtime.util;

import java.io.PrintStream;
import java.lang.Math;

public class Matrix
{
	protected int rows, columns;
	protected double[][] value;
	
	private Matrix tmpTranspose = null;
	
	public Matrix(double[][] data)
	{
		rows = data.length;
		
		if (rows == 0)
			columns = 0;
		else
			columns = data[0].length;
		
		value = data;
	}
	
	public Matrix(Matrix M)
	{
		this(M.rows, M.columns);
		if (!copy(M))
		{
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++)
					value[i][j] = 0;
		}
	}
	
	public Matrix(int n, int m)
	{
		rows = n;
		columns = m;
		value = new double[n][];
		for (int i = 0; i < n; i++)
			value[i] = new double[m];
	}
	
	public double get(int i, int j)
	{
		if (i < 1 || i > rows || j < 1 || j > columns)
			return Double.NaN;
		
		return value[i-1][j-1];
	}
	
	public boolean set(int i, int j, double value)
	{
		if (i < 1 || i > rows || j < 1 || j > columns)
			return false;
		
		this.value[i-1][j-1] = value;
		return true;
	}
	public int getRowCount() { return rows; }
	public int getColumnCount() { return columns; }

	public boolean multiply(Matrix right) { return multiply(this,right); }

	public boolean multiply(Matrix left, Matrix right)
	{
		int n = left.rows;
		int m = left.columns;
		int k = this.columns;
		
		if (right.rows != m || right.columns != k || this.rows != n)
			return false;
		
		for (int j = 0; j < k; j++)
		{
			for (int i = 0; i < n; i++)
			{
				this.value[i][j] = 0;
				for (int l = 0; l < m; l++)
				{
					this.value[i][j] += left.value[i][l] * right.value[l][j];
				}
			}
		}
		
		return true;
	}

	public boolean transpose(Matrix original)
	{
		if (this == original)
		{
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
	
	public void transpose()
	{
		if (tmpTranspose == null)
			tmpTranspose = this.clone();
		else
			tmpTranspose.copy(this);
		transpose(tmpTranspose);
	}

	public Matrix clone() { return new Matrix(this); }
	
	public boolean copy(Matrix M)
	{
		if (M.rows != rows || M.columns != columns)
			return false;
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				value[i][j] = M.value[i][j];

		return true;
	}
	
	public boolean isEqual(Matrix M)
	{
		if (M == this) return true;
		if (M.rows != rows || M.columns != columns)
			return false;

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				if (value[i][j] != M.value[i][j])
					return false;
		
		return true;
	}
	
	public void print(PrintStream out)
	{
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < columns; j++)
			{
				out.print('\t');
				out.print((float)value[i][j]);
			}
			out.println();
		}
	}
	
	public static Matrix Rot3x(double angle)
	{
		Matrix R = new Matrix(3, 3);
		Rot3x(R, angle);
		return R;
	}
	
	public static boolean Rot3x(Matrix A, double angle)
	{
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
}

