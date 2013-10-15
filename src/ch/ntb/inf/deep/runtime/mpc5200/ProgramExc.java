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

package ch.ntb.inf.deep.runtime.mpc5200;

import ch.ntb.inf.deep.runtime.ppc32.PPCException;

/*changes:
 * 23.8.2012	NTB/Urs Graf	creation
 */

public class ProgramExc extends PPCException {
	public static int nofProgExceptions;

	static void programExc() {
		nofProgExceptions++;
		while (true) {
			Kernel.blink(1); Kernel.blink(2);
		}
	}

}
