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

package org.deepjava.runtime.arm32;

/*changes:
 * 09.09.2020	OST/Urs Graf	creation
 */

/**
 * The class for the ARM undefined instruction exception.
 */
public class UndefinedInstruction extends ARMException implements Iarm32 {
	/**
	 * The number of times a undefined instruction exception was executed
	 */
	public static int nofUndefInstr;

	static void undefInstr(Exception e) {
		nofUndefInstr++;
		while (true);
	}

}
