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

package ch.ntb.inf.deep.lowLevel;

/**
 * The methods of this class will be translated by inserting machine code instructions
 * directly in the code without a method call. They offer maximum efficiency by using 
 * special optimization.
 */

public class LL {

	/** 
	 * Get bits of floating point value (64bit)
	 * @param d Double value.
	 * @return Bit pattern.
	 */
	public static long doubleToBits(double d) {
		return 0;
	}

	/** 
	 * Set double value directly from bits (64bit) 
	 * @param val Bit pattern.
	 * @return Double value.
	 */
	public static double bitsToDouble(long val) {
		return 0;
	}

	/** 
	 * Get bits of floating point value (32bit)
	 * @param f Double value.
	 * @return Bit pattern.
	 */
	public static int floatToBits(float f) {
		return 0;
	}

	/** 
	 * Set float value directly from bits (32bit) 
	 * @param val Bit pattern.
	 * @return Float value.
	 */
	public static float bitsToFloat(int val) {
		return 0;
	}
}
