package ch.ntb.inf.deep.runtime.mpc555;
import ch.ntb.inf.deep.unsafe.*;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

class Reset extends PPCException implements ntbMpc555HB {
	
	static void reset() {
//		int stackBase = US.GET4(sysTabBaseAddr + stStackOffset);
//		int stackSize = US.GET4(sysTabBaseAddr + stStackOffset + 8);
		int stackBase = US.GET4(sysTabBaseAddr + 20);
		int stackSize = US.GET4(sysTabBaseAddr + 24);
		US.PUTGPR(1, stackBase + stackSize - 4);	// set stack pointer
//		int kernelClinitAddr = US.GET4(sysTabBaseAddr + stKernelClinitAddr);
		int kernelClinitAddr = US.GET4(sysTabBaseAddr + 12);
		US.PUTSPR(SRR0, kernelClinitAddr);
		US.PUTSPR(SRR1, SRR1init);
		US.ASM("rfi");
	}
}
