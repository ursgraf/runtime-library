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

package org.deepjava.runtime.mpc555;
import org.deepjava.runtime.IdeepCompilerConstants;
import org.deepjava.runtime.ppc32.Ippc32;
import org.deepjava.runtime.ppc32.PPCException;
import org.deepjava.unsafe.ppc.US;

/* changes:
 * 11.11.10	NTB/GRAU	creation
 */
/**
 * The class for the PPC reset exception.
 * The stack pointer will be initialized and the program counter will be
 * set to the beginning of the class initializer of the kernel.
 * 
 * @author Urs Graf
 */
class Reset extends PPCException implements Ippc32, IntbMpc555HB, IdeepCompilerConstants {
	
	static void reset() {
		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
		int stackBase = US.GET4(sysTabBaseAddr + stackOffset + 4);
		int stackSize = US.GET4(sysTabBaseAddr + stackOffset + 8);
		US.PUTGPR(1, stackBase + stackSize - 4);	// set stack pointer
		int kernelClinitAddr = US.GET4(sysTabBaseAddr + stKernelClinitAddr);
		US.PUTSPR(SRR0, kernelClinitAddr);
		US.PUTSPR(SRR1, SRR1init);
		US.ASM("rfi");
	}
}
