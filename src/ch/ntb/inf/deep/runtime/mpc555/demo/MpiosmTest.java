package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.*;
import ch.ntb.inf.deep.runtime.mpc555.driver.Mpiosm;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2Plain;
import ch.ntb.inf.deep.unsafe.US;

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