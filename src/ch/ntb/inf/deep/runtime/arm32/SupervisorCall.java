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

package ch.ntb.inf.deep.runtime.arm32;

import ch.ntb.inf.deep.runtime.Kernel;

/*changes:
 * 17.10.18	NTB/Urs Graf	creation
 */

/**
 * The class for the ARM supervisor call.
 */
public class SupervisorCall extends ARMException {
	/**
	 * The number of times a supervisor call was executed
	 */
	public static int nofSvc;

	static void superVisorCall() {
		nofSvc++;
		while (true) {
			Kernel.blink(1); Kernel.blink(3);
		}
	}

}
