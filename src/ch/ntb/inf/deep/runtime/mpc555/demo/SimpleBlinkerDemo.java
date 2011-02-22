package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;

public class SimpleBlinkerDemo extends Task {
	
	
	public void action(){
		MPIOSM_DIO.out(12, !MPIOSM_DIO.in(12));
	}
	
	static{
		MPIOSM_DIO.init(12, true);
		MPIOSM_DIO.out(12, false);
		SimpleBlinkerDemo t = new SimpleBlinkerDemo();
		t.period = 500;
		Task.install(t);
	}

}
