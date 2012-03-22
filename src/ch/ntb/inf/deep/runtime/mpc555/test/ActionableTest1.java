package ch.ntb.inf.deep.runtime.mpc555.test;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;

public class ActionableTest1 implements Actionable {
	int pin;

	@Override
	public void action() {
		MPIOSM_DIO.set(this.pin, !MPIOSM_DIO.get(this.pin));
	}
	
	public ActionableTest1(int pin) {
		this.pin = pin;
		MPIOSM_DIO.init(pin, true);
		MPIOSM_DIO.set(pin, false);
	}

	static {
		Task t = new Task(new ActionableTest1(15));
		t.period = 500;
		Task.install(t);
	}
}
