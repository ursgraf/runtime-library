package ch.ntb.inf.deep.runtime.mpc555.test;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;

public class TaskWithInterfaceTest extends Task implements ntbMpc555HB {
	
	public void action() {
		MPIOSM_DIO.set(12, !MPIOSM_DIO.get(12));
	}
	
	static {
		MPIOSM_DIO.init(12, true);
		MPIOSM_DIO.set(12, false);
		Task t = new TaskWithInterfaceTest();
		t.period = 1000;
		Task.install(t);
	}

}
