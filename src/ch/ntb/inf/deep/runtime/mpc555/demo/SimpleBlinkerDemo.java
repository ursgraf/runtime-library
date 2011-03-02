package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;

/* CHANGES:
 * 24.02.2011	M. Züger	creation
 */

/**
 * Simple blinker application demo.
 * Connect an LED to pin MPIOSM12. The LED will be toggled every half second.
 */
public class SimpleBlinkerDemo extends Task {
	
	
	public void action(){
		MPIOSM_DIO.set(12, !MPIOSM_DIO.get(12));
	}
	
	static{
		MPIOSM_DIO.init(12, true);
		MPIOSM_DIO.set(12, false);
		SimpleBlinkerDemo t = new SimpleBlinkerDemo();
		t.period = 500;
		Task.install(t);
	}

}
