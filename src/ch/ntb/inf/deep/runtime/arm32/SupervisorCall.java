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

import ch.ntb.inf.deep.runtime.arm32.Iarm32;
import ch.ntb.inf.deep.unsafe.arm.US;

/*changes:
 * 17.10.18	NTB/Urs Graf	creation
 */

/**
 * The class for the ARM supervisor call.
 */
public class SupervisorCall extends ARMException implements Iarm32 {
	/**
	 * The number of times a supervisor call was executed
	 */
	public static int nofSvc;

	static void superVisorCall(Exception e) {
		int addr = US.GETGPR(LR);
		nofSvc++;
		int instr = US.GET4(addr - 4);
		int type = instr & 0xff;	// get exception type
		if (type == 1) {	// user defined exception
		} else if (type == 10) {	// ArrayIndexOutOfBounds
			e = new ArrayIndexOutOfBoundsException("ArrayIndexOutOfBoundsException");
		} else if (type == 20) {	// NullPointer
			e = new NullPointerException("NullPointerException");
		} else if (type == 30) {	// Arithmetic
			e = new ArithmeticException("ArithmeticException");
		} else if (type == 40) {	// ClassCast
			e = new ClassCastException("ClassCastException");
		} else {	
			e = new ClassCastException("UnknownException");
		}
		e.addr = addr;

		US.PUTGPR(R0, US.REF(e));	// copy to volatile register
		US.PUTGPR(R1, addr);	// copy to volatile register
//		US.ASM("b -8");
	}

}
