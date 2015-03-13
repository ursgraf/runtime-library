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

public class Vector extends Matrix
{
	public Vector(int n) { this(n, false); }
	public Vector(int n, boolean row)
	{
		super(((row) ? n : 1), ((row) ? 1 : n));
	}

	public int getSize() { return (rows==1) ? columns : rows; }
	
	public boolean set(int i, double value)
	{
		if (rows == 1)
		{
			if (i < 1 || i > columns) return false;
			this.value[0][i-1] = value;
		}
		else
		{
			if (i < 1 || i > rows) return false;
			this.value[i-1][0] = value;
		}
		return true;
	}
	
	public double get(int i)
	{
		if (rows == 1)
		{
			if (i < 1 || i > columns)
				return Double.NaN;
			else
				return value[0][i-1];
		}
		else
		{
			if (i < 1 || i > rows)
				return Double.NaN;
			else
				return value[i-1][0];
		}
	}

	public boolean add(Vector right) { return add(this, right); }
	
	public boolean add(Vector right, boolean ignoreOrientation) { return add(this, right, ignoreOrientation); }
	
	public boolean add(Vector left, Vector right) { return add(left, right, true); }
	
	public boolean add(Vector left, Vector right, boolean ignoreOrientation)
	{
		int size = this.getSize();
		
		if (left.getSize() != size || right.getSize() != size)
			return false;
		
		if (!ignoreOrientation && (rows != left.rows || rows != right.rows))
			return false;
		
		for (int i = 1; i <= size; i++)
			set(i, left.get(i) + right.get(i));
		
		return true;
	}

	public double dot(Vector right)
	{
		int size = this.getSize();
		if (right.getSize() != size)
			return Double.NaN;
		
		double result = 0;
		for (int i = 1; i <= size; i++)
			result += (this.get(i) + right.get(i));
		
		return result;
	}
}
