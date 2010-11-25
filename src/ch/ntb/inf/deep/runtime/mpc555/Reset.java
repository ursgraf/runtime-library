package ch.ntb.inf.deep.runtime.mpc555;
import ch.ntb.inf.deep.unsafe.*;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

class Reset extends PPCException {
	
	static void reset() {
		HWD.PUTGPR(1, 0x0);	// set stack pointer
		
	}
}
