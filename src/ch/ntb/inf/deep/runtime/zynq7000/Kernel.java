/*
 * Copyright 2011 - 2015 NTB University of Applied Sciences in Technology
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

package ch.ntb.inf.deep.runtime.zynq7000;

import ch.ntb.inf.deep.runtime.IdeepCompilerConstants;
import ch.ntb.inf.deep.runtime.arm32.Heap;
import ch.ntb.inf.deep.runtime.arm32.Iarm32;
import ch.ntb.inf.deep.unsafe.arm.US;

/* changes:
 * 6.10.2015	NTB/Urs Graf	creation
 */

/**
 *  This is the kernel class. It provides basic functionalities and does the booting-up. 
 */
public class Kernel implements Iarm32, Izybo7000, IdeepCompilerConstants {
	final static int stackEndPattern = 0xee22dd33;
	/** Clock frequency of the processor. */
	public static final int clockFrequency = 400000000; // Hz
	static int loopAddr;
	static int cmdAddr;
	static long t = 0x1122;
	
	@SuppressWarnings("unused")
	private static void loop() {	// endless loop
		US.PUT4(SLCR_UNLOCK, 0xdf0d);
		US.PUT4(SLCR_MIO_PIN_07, 0x600);
		US.PUT4(GPIO_DIR0, 0x80);
		while (true) {
//			try {
				if (cmdAddr != -1) {
					US.PUTGPR(0, cmdAddr);
					US.ASM("mov r14, r15");	// copy PC to LR
					US.ASM("mov r15, r0");
					cmdAddr = -1;
				}
//			} catch (Exception e) {
//				cmdAddr = -1;	// stop trying to run the same method
//				e.printStackTrace();
//				Kernel.blink(2);
//			}
				US.PUT4(GPIO_DATA0, US.GET4(GPIO_DATA0) ^ 0x80);
//				t = time();
//				US.ASM("b -8");
				for (int i = 2000000; i > 0; i--); 
		}
	}
	
	/** 
	 * Reads the system time.
	 * 
	 * @return System time in \u00b5s
	 */
	public static long time() {
		int high1, high2, low;
		do {
			high1 = US.GET4(GTCR_U); 
			low = US.GET4(GTCR_L);
			high2 = US.GET4(GTCR_U); 
		} while (high1 != high2);
		long time = ((long)high1 << 32) | ((long)low & 0xffffffffL);
		return time;
	}
	
	/** 
	 * Blinks LED on MIO7 pin, nTimes with approx. 100us high time and 100us low time, blocks for 1s
	 * 
	 * @param nTimes Number of times the led blinks.
	 */
	public static void blink(int nTimes) { 
		US.PUT4(SLCR_UNLOCK, 0xdf0d);
		US.PUT4(SLCR_MIO_PIN_07, 0x600);
		US.PUT4(GPIO_DIR0, 0x80);
		int delay = 1000000;
		for (int i = 0; i < nTimes; i++) {
			US.PUT4(GPIO_DATA0, US.GET4(GPIO_DATA0) | 0x80);
			for (int k = 0; k < delay; k++);
			US.PUT4(GPIO_DATA0, US.GET4(GPIO_DATA0) ^ 0x80);
			for (int k = 0; k < delay; k++);
		}
		for (int k = 0; k < (10 * delay + nTimes * 2 * delay); k++);
	}

	/**
	 * Enables interrupts globally. 
	 * Individual interrupts for peripheral components must be enabled locally.
	 */
	public static void enableInterrupts() {
	}

	/** 
	 * Blinks LED on MPIOSM pin 15 if stack end was overwritten
	 */
	public static void checkStack() { 
		boot();
//		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
//		int stackBase = US.GET4(sysTabBaseAddr + stackOffset + 4);
//		if (US.GET4(stackBase) != stackEndPattern) while (true) blink(3);
	}

	private static int FCS(int begin, int end) {
		int crc  = 0xffffffff;  // initial content
		final int poly = 0xedb88320;  // reverse polynomial 0x04c11db7
		int addr = begin;
		while (addr < end) {
			byte b = US.GET1(addr);
			int temp = (crc ^ b) & 0xff;
			for (int i = 0; i < 8; i++) { // read 8 bits one at a time
				if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly;
				else temp = (temp >>> 1);
			}
			crc = (crc >>> 8) ^ temp;
			addr++;
		}
//		return crc;
		return 0;
	}
	
	private static void boot() {	// set to private later
		blink(2);
//		US.ASM("b -8"); // stop here
		
        // _ init VFP (FPU
        // _ _ enable coprocessor 10 and 11
		US.ASM("mrc p15, 0, r1, c1, c0, 2");
		US.ASM("orr r1, r1, #0xf00000");
        US.ASM("mcr p15, 0, r1, c1, c0, 2");
//		US.ASM("b -8");

        // _ _ enable vfp
		US.ASM("vmrs r1, FPEXC");	//US.ASM("fmrx r1, FPEXC");
        US.ASM("orr r1, r1, #0x40000000");      // FPEXC_EN bit"
        US.ASM("vmsr FPEXC, r1");	//US.ASM("fmxr FPEXC, r1");
        
        // fill the number 100 into d0
        US.PUTGPR(5, 0x40590000);
        US.PUTGPR(6, 0);
        US.ASM("vmov d0, r6, r5");

		// mark stack end with specific pattern
		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
		int stackBase = US.GET4(sysTabBaseAddr + stackOffset + 4);
		US.PUT4(stackBase, stackEndPattern);

		int classConstOffset = US.GET4(sysTabBaseAddr);
//		int state = 0;
		while (true) {
			// get addresses of classes from system table
			int constBlkBase = US.GET4(sysTabBaseAddr + classConstOffset);
			if (constBlkBase == 0) break;

			// check integrity of constant block for each class
			int constBlkSize = US.GET4(constBlkBase);
			if (FCS(constBlkBase, constBlkBase + constBlkSize) != 0) while(true) blink(1);

			// initialize class variables
			int varBase = US.GET4(constBlkBase + cblkVarBaseOffset);
			int varSize = US.GET4(constBlkBase + cblkVarSizeOffset);
			int begin = varBase;
			int end = varBase + varSize;
			while (begin < end) {US.PUT4(begin, 0); begin += 4;}
			
//			state++; 
			classConstOffset += 4;
		}
		classConstOffset = US.GET4(sysTabBaseAddr);
		Heap.sysTabBaseAddr = sysTabBaseAddr;
		int kernelClinitAddr = US.GET4(sysTabBaseAddr + stKernelClinitAddr); 
		while (true) {
			// get addresses of classes from system table
			int constBlkBase = US.GET4(sysTabBaseAddr + classConstOffset);
			if (constBlkBase == 0) break;

			// initialize classes
			int clinitAddr = US.GET4(constBlkBase + cblkClinitAddrOffset);
			if (clinitAddr != -1) {	
				if (clinitAddr != kernelClinitAddr) {	// skip kernel 
					US.PUTGPR(0, clinitAddr);
					US.ASM("mov r14, r15");	// copy PC to LR 
					US.ASM("mov r15, r0");
				} else {	// kernel
					loopAddr = US.ADR_OF_METHOD("ch/ntb/inf/deep/runtime/zynq7000/Kernel/loop");
				}
			}

			// the direct call to clinitAddr destroys volatile registers, hence make sure
			// the variable classConstOffset is forced into nonvolatile register
			// this is done by call to empty()
			empty();
			classConstOffset += 4;
		}
	}
	
	private static void empty() { }

	static {
//		try {
//		US.ASM("b -8"); // stop here
			boot();
			cmdAddr = -1;	// must be after class variables are zeroed by boot
//			blink(1);
			US.PUTGPR(0, loopAddr);
			US.ASM("mov r14, r15");	// copy PC to LR 
			US.ASM("mov r15, r0");	 
//		} catch (Exception e) {
//			e.printStackTrace();
//			while (true) Kernel.blink(5);
//		}
	}

}