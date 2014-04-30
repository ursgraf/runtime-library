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

package ch.ntb.inf.deep.runtime.mpc555;
import ch.ntb.inf.deep.runtime.IdeepCompilerConstants;
import ch.ntb.inf.deep.runtime.ppc32.Heap;
import ch.ntb.inf.deep.runtime.ppc32.Ippc32;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 30.04.14	NTB/Urs Graf	catching of unchecked exception added
 * 23.11.11	NTB/Martin Züger	classConstOffset
 * 11.11.10	NTB/Urs Graf		creation
 */

public class Kernel implements Ippc32, IntbMpc555HB, IdeepCompilerConstants {
	final static int stackEndPattern = 0xee22dd33;
	public static final int clockFrequency = 40000000; // Hz
	static int loopAddr;
	static int cmdAddr;
	
	@SuppressWarnings("unused")
	private static void loop() {	// endless loop
		while (true) {
			try {
				if (cmdAddr != -1) {
					US.PUTSPR(LR, cmdAddr);	
					US.ASM("bclrl always, 0");
					cmdAddr = -1;
				}
			} catch (Exception e) {
				cmdAddr = -1;	// stop trying to run the same method
				System.err.print(e.message);
				System.err.print(" at addr ");
				System.err.printHexln(e.addr);
				Kernel.blink(10);
			}
		}
	}
	
	/** 
	 * @return system time in us
	 */
	public static long time() {
		int high1, high2, low;
		do {
			high1 = US.GETSPR(TBUread); 
			low = US.GETSPR(TBLread);
			high2 = US.GETSPR(TBUread); 
		} while (high1 != high2);
		long time = ((long)high1 << 32) | ((long)low & 0xffffffffL);
		return time;
	}
	
	/** 
	 * blinks LED on MPIOSM pin 15, nTimes with approx. 100us high time and 100us low time, blocks for 1s
	 */
	public static void blink(int nTimes) { 
		US.PUT2(MPIOSMDDR, US.GET2(MPIOSMDDR) | 0x8000);
		int delay = 200000;
		for (int i = 0; i < nTimes; i++) {
			US.PUT2(MPIOSMDR, US.GET2(MPIOSMDR) | 0x8000);
			for (int k = 0; k < delay; k++);
			US.PUT2(MPIOSMDR, US.GET2(MPIOSMDR) & ~0x8000);
			for (int k = 0; k < delay; k++);
		}
		for (int k = 0; k < (10 * delay + nTimes * 2 * delay); k++);
	}

	/** 
	 * blinks LED on MPIOSM pin 15 if stack end was overwritten
	 */
	public static void checkStack() { 
		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
		int stackBase = US.GET4(sysTabBaseAddr + stackOffset + 4);
		if (US.GET4(stackBase) != stackEndPattern) while (true) blink(3);
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
		return crc;
	}
	
	private static void boot() {
//		blink(1);
		US.PUT4(SIUMCR, 0x00040000);	// internal arb., no data show cycles, BDM operation, CS functions,
			// output FREEZE, no lock, use data & address bus, use as RSTCONF, no reserv. logic
		if (clockFrequency == 40000000)
			US.PUT4(PLPRCR, 0x00900000);	// MF = 9, 40MHz operation with 4MHz quarz
		int reg;
		do reg = US.GET4(PLPRCR); while ((reg & (1 << 16)) == 0);	// wait for PLL to lock 
		US.PUT4(UMCR, 0);	// enable IMB clock, no int. multiplexing, full speed
		US.PUTSPR(158, 0x7);	// ICTRL: take out of serialized mode
		US.PUTSPR(638, 0x800);	// IMMR: enable internal flash
		// configure CS for external Flash
		US.PUT4(BR0, 0x01000003);	// chip select base address reg external Flash,
		// base address = 1000000H, 32 bit port, no write protect, WE activ, no burst accesses, valid 
		US.PUT4(OR0, 0x0ffc00020);	// address mask = 4MB, adress type mask = 0,
		// CS normal timing, CS and addr. same timing, 2 wait states
		// configure CS for external RAM 
		US.PUT4(BR1, 0x00800003); 	// chip select base address reg external RAM,
		// base address = 800000H, 32 bit port, no write protect, WE activ, no burst accesses, valid
		US.PUT4(OR1, 0x0ffe00020);		//address mask = 2MB, adress type mask = 0,
		// CS normal timing, CS and addr. same timing, 2 wait states
		US.PUT2(PDMCR, 0); 	// configure pads, slow slew rate, enable pull-ups 
		US.PUT4(SCCR, 0x081210300); 	// enable clock out and engineering clock, EECLK = 10MHz 
		US.PUT2(TBSCR, 1); 	// time base, no interrupts, stop time base while freeze, enable
		short reset = US.GET2(RSR);
		if ((reset & (1<<5 | 1<<15)) != 0) {	// boot from flash
			US.PUT4(SYPCR, 0xffffff83);	// bus monitor time out, enable bus monitor, disable watchdog
			US.PUT4(DMBR, 0x1);			// dual mapping enable, map from address 0, use CS0 -> external Flash
			US.PUT4(DMOR, 0x7e000000);	// map 32k -> 0x0...0x8000
		}
		
//		set FPSCR;
		
		// mark stack end with specific pattern
		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
		int stackBase = US.GET4(sysTabBaseAddr + stackOffset + 4);
		US.PUT4(stackBase, stackEndPattern);

		int classConstOffset = US.GET4(sysTabBaseAddr);
//		int state = 0;
		int kernelClinitAddr = US.GET4(sysTabBaseAddr + stKernelClinitAddr); 
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
		while (true) {
			// get addresses of classes from system table
			int constBlkBase = US.GET4(sysTabBaseAddr + classConstOffset);
			if (constBlkBase == 0) break;

			// initialize classes
			int clinitAddr = US.GET4(constBlkBase + cblkClinitAddrOffset);
			if (clinitAddr != -1) {	
				if (clinitAddr != kernelClinitAddr) {	// skip kernel 
					US.PUTSPR(LR, clinitAddr);
					US.ASM("bclrl always, 0");
				} else {	// kernel
					loopAddr = US.ADR_OF_METHOD("ch/ntb/inf/deep/runtime/mpc555/Kernel/loop");
					US.ASM("mtspr EIE, r0");
				}
			}
			// the direct call to clinitAddr destroys volatile registers, hence make sure
			// the variable classConstOffset is forced into nonvolatile register
			// this is done by call to empty()
			empty();
			classConstOffset += 4;
		}
	}
	
	private static void empty() {
	}

	static {
		try {
			boot();
			cmdAddr = -1;	// must be after class variables are zeroed by boot
			US.PUTSPR(LR, loopAddr);
			US.ASM("bclrl always, 0");
		} catch (Exception e) {
			System.err.print(e.message);
			System.err.print(" at addr ");
			System.err.printHexln(e.addr);
			while (true) Kernel.blink(5);
		}
	}

}