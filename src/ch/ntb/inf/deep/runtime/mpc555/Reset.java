package ch.ntb.inf.deep.runtime.mpc555;
import ch.ntb.inf.deep.unsafe.*;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

class Reset extends PPCException implements Registers {
	
	static void reset() {
		int stackBase = HWD.GET4(sysTabBaseAddr + stackOffset + 4);
		int stackSize = HWD.GET4(sysTabBaseAddr + stackOffset + 8);
		HWD.PUTGPR(1, stackBase + stackSize - 4);	// set stack pointer
		int kernelConstBlkBase = HWD.GET4(HWD.GET4(sysTabBaseAddr) + 4);
		int clinitAddr = HWD.GET4(kernelConstBlkBase + clbkClinitAddrOffset);
		HWD.PUTSPR(SRR0, clinitAddr);
		HWD.PUTSPR(SRR1, SRR1init);
		HWD.ASM("rfi");
	}
}
