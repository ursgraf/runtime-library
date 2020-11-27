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

package org.deepjava.runtime.mpc5200;

import org.deepjava.runtime.IdeepCompilerConstants;
import org.deepjava.runtime.ppc32.Ippc32;
import org.deepjava.runtime.ppc32.PPCException;
import org.deepjava.unsafe.ppc.US;

/* changes:
 * 21.6.12	NTB/GRAU	creation
 */
/**
 * The class for the PPC reset exception.<br>
 * The stack pointer will be initialized and the program counter will be
 * set to the beginning of the class initializer of the kernel.
 * This code assumes that uBoot already configured the external SDRAM.
 * The program has to be by uBoot to address 0x400000 in the SDRAM 
 * From there this code will copy itself to address 0 in the SDRAM.
 * 
 * @author Urs Graf
 */
class ResetPCMuBoot extends PPCException implements Ippc32, IphyCoreMpc5200tiny, IdeepCompilerConstants {
	
	static void reset() {		
		US.ASM("li r20,2");
		US.ASM("mtmsr r20");	// we have to reset the machine state, it was configured by uBoot
		US.PUTSPR(HID0, 0);		// switch of instruction cache, it was set by uBoot
//		US.ASM("b 0");

		// copy code and const from predefined address in SDRAM to start of SDRAM
		// uBoot code will be overwritten
		int baseAddr = 0x400000 + sysTabBaseAddr;
		int srcAddr = 0x400000 + US.GET4(baseAddr + stResetOffset);
		int dstAddr = extRamBase + US.GET4(baseAddr + stResetOffset);
		int size = US.GET4(baseAddr + stResetOffset + 4) / 4;
		for (int i = 0; i < size; i++) {
			US.PUT4(dstAddr, US.GET4(srcAddr));
			dstAddr += 4;
			srcAddr += 4;
		}

		int stackOffset = US.GET4(baseAddr + stStackOffset);
		int stackBase = US.GET4(baseAddr + stackOffset + 4);
		int stackSize = US.GET4(baseAddr + stackOffset + 8);
		US.PUTGPR(1, stackBase + stackSize - 4);	// set stack pointer

		// at this stage, the code has to be found in the SDRAM
		int kernelClinitAddr = US.GET4(baseAddr + stKernelClinitAddr);
		US.PUTSPR(SRR0, kernelClinitAddr);
		US.PUTSPR(SRR1, SRR1init);
		US.ASM("rfi");
	}		
}

