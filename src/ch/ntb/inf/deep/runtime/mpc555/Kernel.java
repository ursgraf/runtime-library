package ch.ntb.inf.deep.runtime.mpc555;
import ch.ntb.inf.deep.unsafe.*;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class Kernel implements Registers {
	
	public static final int MPIOSMDDR = 0;
	public static final int MPIOSMDR = 0;
	public static final int CextRomBase = 0;

	/** 
	 * @return system time in us
	 */
	public static long time() {
		long time = SYS.GETSPR(TBU) >> 32;
		time |= SYS.GETSPR(TBL);
		return time;
	}
	
	/** 
	 * blinks LED on MPIOSM pin 15 with aprrox. 5Hz
	 */
	public static void blink() {
		SYS.PUT2(MPIOSMDDR, SYS.GET2(MPIOSMDDR) | 0x8000);
		while (true) {
			for (int i = 0; i < 1000000; i++);
			SYS.PUT2(MPIOSMDR, SYS.GET2(MPIOSMDR) ^ 0x8000);
		}
	}
	
	private static void boot() {
		SYS.PUT4(SIUMCR, 0x00040000);	// internal arb., no data show cycles, BDM operation, CS functions,
			// output FREEZE, no lock, use data & address bus, use as RSTCONF, no reserv. logic
		SYS.PUT4(PLPRCR, 0x00900000);	// MF = 9, 40MHz operation with 4MHz quarz
		int reg;
		do reg = SYS.GET4(PLPRCR); while ((reg & (1 << 16)) != 0);	// wait for PLL to lock 
		SYS.PUT4(UMCR, 0);	// enable IMB clock, no int. multiplexing, full speed
		SYS.PUTSPR(158, 0x800);	// take out of serialized mode
		SYS.PUTSPR(638, 0x800);	// enable internal flash
		// configure CS for external Flash
		SYS.PUT4(BR0, 0x01000003);	// chip select base address reg external Flash,
		// base address = 1000000H, 32 bit port, no write protect, WE activ, no burst accesses, valid 
		SYS.PUT4(OR0, 0x0ffc00020);	// address mask = 4MB, adress type mask = 0,
		// CS normal timing, CS and addr. same timing, 2 wait states
		// configure CS for external RAM 
		SYS.PUT4(BR1, 0x00800003); 	// chip select base address reg external RAM,
		// base address = 800000H, 32 bit port, no write protect, WE activ, no burst accesses, valid
		SYS.PUT4(OR1, 0x0ffe00020);		//address mask = 2MB, adress type mask = 0,
		// CS normal timing, CS and addr. same timing, 2 wait states
		SYS.PUT2(PDMCR, 0); 	// configure pads, slow slew rate, enable pull-ups 
		SYS.PUT4(SCCR, 0x081210300); 	// enable clock out and engineering clock, EECLK = 10MHz 
		SYS.PUT2(TBSCR, 1); 	// time base, no interrupts, stop time base while freeze, enable
		short reset = SYS.GET2(RSR);
		if ((reset & (1<<5 | 1<<15)) != 0) {	// boot from flash
/*			SYS.PUT4(SYPCR, pSYPCR);
			SYS.PUT4(DMBR, pDMBRRom);
			SYS.PUT4(DMOR, pDMOR);
			SYS.GET(sysTabAdrRom + stoSysTabSize, sysTabSize);
			SYS.MOVE(sysTabAdrRom, sysTabAdr, sysTabSize)*/
		}
/*		
//		SetFPSCR;

		modTabPtr := sysTabAdr + stoModulesOffset;
		SYS.GET(modTabPtr, modTabPtr);
		INC(modTabPtr, sysTabAdr + 8); 	(*now modTabPtr points to the first module*)
		modNr := kernelModNr;

		state := 0;
		LOOP
		(*---- get addresses from system table *)
			SYS.GET(modTabPtr, constBlkBase);
			IF constBlkBase = 0 THEN	EXIT	END;
			SYS.GET(constBlkBase, nofPtrs);
			fixHdrBase := constBlkBase + (nofPtrs + 1) * 4;
			SYS.GET(fixHdrBase + cbfhoBodyAddr, bodyAdr);
			SYS.GET(fixHdrBase + cbfhoConstBlkSize, size);
			IF FCS(constBlkBase, constBlkBase + size) # 0 THEN	LOOP	Blink(8, 16)	END	END;

			SYS.GET(fixHdrBase + cbfhoCodeBlkBase, codeBlkBase);
			SYS.GET(fixHdrBase + cbfhoCodeBlkSize, size);
			IF FCS(codeBlkBase, codeBlkBase + size) # 0 THEN	LOOP	Blink(16, 8); Blink(4, 64)	END	END;

		(*---- copy constants to var block *)
			SYS.GET(fixHdrBase + cbfhoConstBase, constBase);
			SYS.GET(fixHdrBase + cbfhoConstSize, size);
			SYS.GET(fixHdrBase + cbfhoInitVarBlkBase, initVarBlkBase);
			SYS.MOVE(constBase, initVarBlkBase, size);

		(*---- global variables *)
			SYS.GET(fixHdrBase + cbfhoGlobVarBlkBase, globVarBlkBase); 	begin := globVarBlkBase;
			SYS.GET(fixHdrBase + cbfhoGlobVarBlkSize, end); 	INC(end, begin);
			WHILE begin < end DO	SYS.PUT4(begin, 0); 	INC(begin, 4)	END;

		(*---- init module *)
			IF modNr # kernelModNr THEN (* skip kernel *)
				SYS.PUT(SYS.ADR(body), bodyAdr); 	SYS.PUT(SYS.ADR(body) + 4, globVarBlkBase);
				body;
				IF modNr = exceptionModNr THEN (* enable ints after initialisation of module exceptions *)
					SYS.PUT4(SIEL, 0FFFF0000H); 	(* external ints are edge sensitive, exit low-power modes *)
					SYS.PUT4(SIPEND, 0FFFF0000H); 	(* reset all int requests *)
					SYS.PUT4(SIMASK, 0FFFF0000H); 	(* enable all interrupt levels *)
					SYS.GETREG(msr, anySet); 	INCL(anySet, msrEE); 	SYS.PUTREG(msr, anySet)
				END
			ELSE
				scheduler := Loop (* kernel *);
			END;
			INC(state); 	INC(modNr); 	INC(modTabPtr, 4);
		END;*/
	}
	
	static {
		boot();
		blink();
	}

}