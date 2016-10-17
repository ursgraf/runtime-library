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

package ch.ntb.inf.deep.runtime.iMX6;
import ch.ntb.inf.deep.runtime.IdeepCompilerConstants;
import ch.ntb.inf.deep.runtime.arm32.Iarm32;
import ch.ntb.inf.deep.runtime.arm32.ARMException;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 13.05.16	NTB/Urs Graf	creation
 */
/**
 * The class for the ARM reset exception.
 * The stack pointer will be initialized and the program counter will be
 * set to the beginning of the class initializer of the kernel.
 * 
 * @author Urs Graf
 */
class Reset extends ARMException implements Iarm32, IiMX6, Icolibri_iMX6, IdeepCompilerConstants {
	
	static void reset() {
//		US.ASM("setend BE"); // data memory organized in big endian format

		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
		int stackBase = US.GET4(sysTabBaseAddr + stackOffset + 4);
		int stackSize = US.GET4(sysTabBaseAddr + stackOffset + 8);
		US.PUTGPR(SP, stackBase + stackSize - 4);	// set stack pointer

		int kernelClinitAddr = US.GET4(sysTabBaseAddr + stKernelClinitAddr);
		US.PUTGPR(PC, kernelClinitAddr);	// never come back

//		US.ASM("b -8"); // stop here
	}
	
}
