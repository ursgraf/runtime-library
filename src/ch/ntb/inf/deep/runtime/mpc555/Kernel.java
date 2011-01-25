package ch.ntb.inf.deep.runtime.mpc555;
import ch.ntb.inf.deep.unsafe.*;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */

public class Kernel implements ntbMpc555HB {
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
		long time = (long)US.GETSPR(TBUread) << 32;
		time |= US.GETSPR(TBLread) & 0xffffffffL;
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
//		for (int k = 0; k < (2000000); k++);
	}


	private static short FCS(int begin, int end) {
		return 0;
	}
	
	private static void boot() {
		blink(1);
		US.PUT4(SIUMCR, 0x00040000);	// internal arb., no data show cycles, BDM operation, CS functions,
			// output FREEZE, no lock, use data & address bus, use as RSTCONF, no reserv. logic
		US.PUT4(PLPRCR, 0x00900000);	// MF = 9, 40MHz operation with 4MHz quarz
		int reg;
		do reg = US.GET4(PLPRCR); while ((reg & (1 << 16)) == 0);	// wait for PLL to lock 
		US.PUT4(UMCR, 0);	// enable IMB clock, no int. multiplexing, full speed
		US.PUTSPR(158, 0x800);	// take out of serialized mode
		US.PUTSPR(638, 0x800);	// enable internal flash
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
			US.PUT4(SYPCR, 0);	// noch korrigieren
/*			SYS.PUT4(DMBR, pDMBRRom);
			SYS.PUT4(DMOR, pDMOR);
			SYS.GET(sysTabAdrRom + stoSysTabSize, sysTabSize);
			SYS.MOVE(sysTabAdrRom, sysTabAdr, sysTabSize)*/
		}
		
//		SetFPSCR;
		int classConstOffset = US.GET4(sysTabBaseAddr) * 4 + 4;
		int state = 0;
		int kernelClinitAddr = US.GET4(sysTabBaseAddr + stKernelClinitAddr); 
		while (true) {
			// !!! Kernel needs to call at least one method in this loop, as the variables in boot
			// must go into nonvolatile registers

			// get addresses of classes from system table
			int constBlkBase = US.GET4(sysTabBaseAddr + classConstOffset);
			if (constBlkBase == 0) break;

			// check integrity of constant block for each class
			int constBlkSize = US.GET4(constBlkBase);
			if (FCS(constBlkBase, constBlkBase + constBlkSize) != 0) while(true) blink(1);

			// check integrity of code block for each class
			int codeBase = US.GET4(constBlkBase + cblkCodeBaseOffset);
			int codeSize = US.GET4(constBlkBase + cblkCodeSizeOffset);
			if (FCS(codeBase, codeBase + codeSize) != 0) while(true) blink(2);

			// initialize class variables
			int varBase = US.GET4(constBlkBase + cblkVarBaseOffset);
			int varSize = US.GET4(constBlkBase + cblkVarSizeOffset);
			int begin = varBase;
			int end = varBase + varSize;
			while (begin < end) {US.PUT4(begin, 0); begin += 4;}
			
			// initialize classes
			int clinitAddr = US.GET4(constBlkBase + cblkClinitAddrOffset);
			if (clinitAddr != -1) {	
				if (clinitAddr != kernelClinitAddr) {	// skip kernel 
					US.PUTSPR(LR, clinitAddr);
					US.ASM("bclrl always, 0");
				} else {	// kernel
					loopAddr = US.ADR_OF_METHOD("ch/ntb/inf/deep/runtime/mpc555/Kernel/loop");
				}
			}
			state++; 
			classConstOffset += 4;
		}
	}
	
	static {
		boot();
		cmdAddr = -1;	// must be after class variables are zeroed by boot
		US.ASM("mtspr EIE, r0");
		US.PUTSPR(LR, loopAddr);
		US.ASM("bclrl always, 0");
	}

}