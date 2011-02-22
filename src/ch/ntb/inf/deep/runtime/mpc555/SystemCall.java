package ch.ntb.inf.deep.runtime.mpc555;

/*changes:
 * 11.11.10	NTB/Urs Graf	creation
 */

public class SystemCall extends PPCException {
	public static int nofScExceptions;

	static void systemCall() {
		nofScExceptions++;
		while (true) Kernel.blink(3);
	}

}
