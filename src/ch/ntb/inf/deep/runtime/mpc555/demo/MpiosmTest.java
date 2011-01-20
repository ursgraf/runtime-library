package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */
class MpiosmTest {
	
	static void pin14on () {
		Mpiosm.out(14, true);
	}

	static void pin14off () {
		Mpiosm.out(14, false);
	}
	
	static {
		Mpiosm.init(14, true);
	}
}