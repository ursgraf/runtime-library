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

package ch.ntb.inf.deep.runtime.ppc32;

import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */

public class Decrementer extends PPCException implements Ippc32 {
	public static int nofDecExceptions;
	public static Decrementer dec = new Decrementer();
	public int decPeriodUs = -1; 	// use longest period per default
	
	public void action() {	
	}

	static void decrementer() {
		nofDecExceptions++;
		US.PUTSPR(DEC, dec.decPeriodUs);
		dec.action();
	}

	public static void install(Decrementer decrementer) {
		dec = decrementer;		
		US.PUTSPR(DEC, dec.decPeriodUs);
	}

}
