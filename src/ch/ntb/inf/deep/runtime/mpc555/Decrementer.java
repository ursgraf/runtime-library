package ch.ntb.inf.deep.runtime.mpc555;
import ch.ntb.inf.deep.unsafe.*;

/*changes:
 * 11.11.10	NTB/Urs Graf	creation
 */

public class Decrementer extends PPCException implements ntbMpc555HB {
	public static int nofDecExceptions;
	public static Decrementer dec = new Decrementer();
	public int decPeriodUs = -1; 	// use longest period per default
	
	public void action() {	
	}

	static void decrementer() {
		nofDecExceptions++;
		US.PUTSPR(DEC, dec.decPeriodUs);
		dec.action();
	}

}
