package ch.ntb.inf.deep.runtime.mpc555;
import ch.ntb.inf.deep.unsafe.*;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

class Reset extends PPCException implements ntbMpc555HB {
	
	static void reset() {
		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
		int stackBase = US.GET4(sysTabBaseAddr + stackOffset * 4);
		int stackSize = US.GET4(sysTabBaseAddr + stackOffset * 4 + 4);
		US.PUTGPR(1, stackBase + stackSize - 4);	// set stack pointer
		int kernelClinitAddr = US.GET4(sysTabBaseAddr + stKernelClinitAddr);
		US.PUTSPR(SRR0, kernelClinitAddr);
		US.PUTSPR(SRR1, SRR1init);
		US.ASM("rfi");
	}
}
