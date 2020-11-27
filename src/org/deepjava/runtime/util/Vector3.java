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
 * This class implements a 3-dimensional vector.
 */
public class Vector3 extends Vector {
	
	/**
	 * Creates a new <code>Vector</code> with 3 dimensions.
	 */
	public Vector3() {super(3);}
	
	/**
	 * Creates a new <code>Vector</code> with 3 dimensions and initializes it.
	 * 
	 * @param x	Value of first dimension
	 * @param y Value of second dimension
	 * @param z Value of third dimension
	 */
	public Vector3(double x, double y, double z) {
		super(3);
		set(x, y, z);
	}
	
	/**
	 * Sets the values of the vector.
	 * 
	 * @param x	Value of first dimension
	 * @param y Value of second dimension
	 * @param z Value of third dimension
	 */
	public void set(double x, double y, double z) {
		set(1, x);
		set(2, y);
		set(3, z);
	}
}
