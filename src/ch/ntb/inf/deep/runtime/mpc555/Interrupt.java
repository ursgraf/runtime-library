package ch.ntb.inf.deep.runtime.mpc555;

/*changes:
 * 25.11.10	NTB/GRAU	creation
 */

public class Interrupt extends PPCException {

	public int enableRegAdr;
	public int enBit;
	public int flagRegAdr;
	public int flag;
	private Interrupt next;

	static void interrupt() {
		
	}

}
