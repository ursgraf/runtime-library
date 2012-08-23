package ch.ntb.inf.deep.runtime.mpc5200.test;

import ch.ntb.inf.deep.runtime.mpc5200.Task;
import ch.ntb.inf.deep.runtime.mpc5200.phyCoreMpc5200tiny;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Simple blinker application demo.
 * Blinks the LED on the eval board. The LED will be toggled every half second.
 */
public class SimpleBlinkerDemo extends Task implements phyCoreMpc5200tiny{

	public void action(){
		US.PUT4(GPWOUT, US.GET4(GPWOUT) ^ 0x80000000);
	}
	
	static {
		US.PUT4(GPWER, US.GET4(GPWER) | 0x80000000);	// enable GPIO use
		US.PUT4(GPWDDR, US.GET4(GPWDDR) | 0x80000000);	// make output
		
		// Create and install the task
		SimpleBlinkerDemo t = new SimpleBlinkerDemo();
		t.period = 500;
		Task.install(t);
	}

}

