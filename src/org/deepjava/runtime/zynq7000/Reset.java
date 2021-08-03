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

package org.deepjava.runtime.zynq7000;

import org.deepjava.runtime.IdeepCompilerConstants;
import org.deepjava.runtime.arm32.ARMException;
import org.deepjava.unsafe.arm.US;

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
class Reset extends ARMException implements Izynq7000, IdeepCompilerConstants {
	
	/** 
	 * Regular vector table, linker will place it to address 0.
	 * When booting from flash, the table will be loaded at start of DDR
	 * and will not be used there. 
	 */
	static void vectorTable() {
		US.ASM("b 0xf8"); // jump to reset method (0x100 - 8 - 0)
		US.ASM("b 0x7f4"); // undefined instruction (0x800 - 8 - 4)
		US.ASM("b 0x1f0"); // jump to supervisor call (0x200 - 8 - 8)
		US.ASM("b 0x8ec"); // prefetch abort, stop here (0x900 - 8 - 0xc)
		US.ASM("b 0x9e8"); // data abort, stop here (0xa00 - 8 - 0x10)
		US.ASM("b -8"); // not used, stop here
		US.ASM("b 0x3e0"); // jump to IRQ interrupt (0x400 - 8 - 0x18)
		US.ASM("b -8"); // FIQ, stop here
	}
	
	/** 
	 * Vector table used when booting from flash, linker will place it at address 0x50.
	 * The kernel will copy it to address 0
	 */
	static void vectorTableCopy() {
		US.ASM("b 0x1000f8"); // jump to reset method (0x100000 + 0x100 - 8 - 0)
		US.ASM("b 0x1007f4"); // undefined instruction (0x100000 + 0x800 - 8 - 4)
		US.ASM("b 0x1001f0"); // jump to supervisor call (0x100000 + 0x200 - 8 - 8)
		US.ASM("b 0x1008ec"); // prefetch abort, stop here (0x100000 + 0x900 - 8 - 0xc)
		US.ASM("b 0x1009e8"); // data abort, stop here (0x100000 + 0xa00 - 8 - 0x10)
		US.ASM("b -8"); // not used, stop here
		US.ASM("b 0x1003e0"); // jump to IRQ interrupt (0x100000 + 0x400 - 8 - 0x18)
		US.ASM("b -8"); // FIQ, stop here
	}
	
	static void reset() {
		US.ASM("cps #svc");	// change to supervisor mode
		int addr = sysTabBaseAddr;
		// sysTab is in flash when running out of flash
		if (US.BIT(REBOOT_STATUS, 22)) addr += 0x100000;
		// set SVC stack pointer
		int stackOffset = US.GET4(addr + stStackOffset);
		int stackBase = US.GET4(addr + stackOffset + 4);
		int stackSize = US.GET4(addr + stackOffset + 8);
		US.PUTGPR(SP, stackBase + stackSize - 4);
		// set IRQ stack pointer
		stackBase = US.GET4(addr + stackOffset + 12);
		stackSize = US.GET4(addr + stackOffset + 16);		
		US.ASM("cps #irq");	// change to IRQ mode  
		US.PUTGPR(SP, stackBase + stackSize - 4);	// set SP for IRQ
		US.ASM("cps #svc");	// change to supervisor mode

		int kernelClinitAddr = US.GET4(addr + stKernelClinitAddr);
		US.PUTGPR(PC, kernelClinitAddr);	// never come back
	}
	
}
