package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Mpiosm;

public class SimpleBlinkerDemo extends Task {
	
	
	public void action(){
		Mpiosm.out(12, !Mpiosm.in(12));
	}
	
	static{
		Mpiosm.init(12, true);
		Mpiosm.out(12, false);
		SimpleBlinkerDemo t = new SimpleBlinkerDemo();
		t.period = 500;
		Task.install(t);
	}

}
