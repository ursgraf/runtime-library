package ch.ntb.inf.deep.runtime.mpc555;
import ch.ntb.inf.deep.unsafe.*;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

class Reset extends PPCException implements Registers {
	
	static void reset() {
		int stackBase = US.GET4(sysTabBaseAddr + stackOffset + 4);
		int stackSize = US.GET4(sysTabBaseAddr + stackOffset + 8);
		US.PUTGPR(1, stackBase + stackSize - 4);	// set stack pointer
		int kernelConstBlkBase = US.GET4(US.GET4(sysTabBaseAddr) + 4);
		int clinitAddr = US.GET4(kernelConstBlkBase + clbkClinitAddrOffset);
		US.PUTSPR(SRR0, clinitAddr);
		US.PUTSPR(SRR1, SRR1init);
		US.ASM("rfi");
	}
}
