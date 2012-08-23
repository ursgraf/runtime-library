/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.runtime.mpc5200;
import ch.ntb.inf.deep.runtime.IdeepCompilerConstants;
import ch.ntb.inf.deep.unsafe.*;

/* changes:
 * 21.6.12	NTB/Urs Graf		creation
 */

public class Kernel implements phyCoreMpc5200tiny, IdeepCompilerConstants {
	final static int stackEndPattern = 0xee22dd33;
	static int loopAddr;
	static int cmdAddr;
	
	private static void loop() {	// endless loop
		while (true) {
			if (cmdAddr != -1) {
				US.PUTSPR(LR, cmdAddr);	
				US.ASM("bclrl always, 0");
				cmdAddr = -1;
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
		return time / 33;
	}
	
	/** 
	 * blinks LED on GPIO_WKUP_7, nTimes with approx. 100us high time and 100us low time, blocks for 1s
	 */
	public static void blink(int nTimes) { 
		US.PUT4(GPWER, US.GET4(GPWER) | 0x80000000);	// enable GPIO use
		US.PUT4(GPWDDR, US.GET4(GPWDDR) | 0x80000000);	// make output
		int delay = 200000;
		for (int i = 0; i < nTimes; i++) {
			US.PUT4(GPWOUT, US.GET4(GPWOUT) & ~0x80000000);
			for (int k = 0; k < delay; k++);
			US.PUT4(GPWOUT, US.GET4(GPWOUT) | 0x80000000);
			for (int k = 0; k < delay; k++);
		}
		for (int k = 0; k < (10 * delay + nTimes * 2 * delay); k++);
	}

	/** 
	 * blinks LED on GPIO_WKUP_7 if stack end was overwritten
	 */
	public static void checkStack() { 
		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
		int stackBase = US.GET4(sysTabBaseAddr + stackOffset);
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
		blink(4);
//		US.PUT4(MBAR, MemBaseAddr >> 16);	// switch memory base address
		US.PUT4(XLBACR, 0x00002006);	// time base enable, data and address timeout enable
//		US.PUT4(SIUMCR, 0x00040000);	// internal arb., no data show cycles, BDM operation, CS functions,
//			// output FREEZE, no lock, use data & address bus, use as RSTCONF, no reserv. logic
//		US.PUT4(PLPRCR, 0x00900000);	// MF = 9, 40MHz operation with 4MHz quarz
//		int reg;
//		do reg = US.GET4(PLPRCR); while ((reg & (1 << 16)) == 0);	// wait for PLL to lock 
//		US.PUT4(UMCR, 0);	// enable IMB clock, no int. multiplexing, full speed

		// configure memory base address
//		US.PUT4(MBAR, 0x0000f000);	// base address is now 0xf0000000
//		// configure CS0 for boot flash 
//		US.PUT4(CS0START, 0x0000ff00);	// start address = 0xff000000
//		US.PUT4(CS0STOP, 0x0000ffff); 	// stop address = 0xffffffff, size = 16MB
//		US.PUT4(CS0CR, 0x0008fd00);	// 8 wait states, multiplexed, ack, enabled, 25 addr. lines, 16 bit data, rw
//		US.PUT4(IPBI, 0x00010001);	// enable CS0, disable CSboot, enable wait states
//		US.PUT4(CSCR, 0x01000000);	// CS master enable
//		
//		// configure CS for SDRAM 
//		US.PUT4(SDRAMCS0, 0x0000001a);	// 128MB, start at 0
//		
//		// configure SDRAM controller for DDR 133MHz 
//		US.PUT4(SDRAMCONFIG1, 0x73722930);	// config 1	
//		US.PUT4(SDRAMCONFIG2, 0x47770000);	// config 2
//		US.PUT4(SDRAMCONTROL, 0xe15f0f02);	
	
		//		short reset = US.GET2(RSR);
//		if ((reset & (1<<5 | 1<<15)) != 0) {	// boot from flash
//			US.PUT4(SYPCR, 0xffffff83);	// bus monitor time out, enable bus monitor, disable watchdog
//			US.PUT4(DMBR, 0x1);			// dual mapping enable, map from address 0, use CS0 -> external Flash
//			US.PUT4(DMOR, 0x7e000000);	// map 32k -> 0x0...0x8000
//		}
		
//		set FPSCR;
		
		// mark stack end with specific pattern
		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
		int stackBase = US.GET4(sysTabBaseAddr + stackOffset);
		US.PUT4(stackBase, stackEndPattern);

		int classConstOffset = US.GET4(sysTabBaseAddr);
		int state = 0;
		int kernelClinitAddr = US.GET4(sysTabBaseAddr + stKernelClinitAddr); 
		while (true) {
//			blink(state);
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
			
			state++; 
			classConstOffset += 4;
		}
		classConstOffset = US.GET4(sysTabBaseAddr);
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
					loopAddr = US.ADR_OF_METHOD("ch/ntb/inf/deep/runtime/mpc5200/Kernel/loop");
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
		boot();
		cmdAddr = -1;	// must be after class variables are zeroed by boot
//		US.ASM("mtspr EIE, r0");	// not available on the 5200
		US.PUTSPR(LR, loopAddr);
		US.ASM("bclrl always, 0");
	}

}