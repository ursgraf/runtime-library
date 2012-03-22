package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

public class ActionableTest2 extends Task {
	public void action() {
		System.out.println(ActionableImpl.count);
		if (nofActivations == 5) Task.remove(this);
	}
	
	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.out.println("Actionable test");
		new ActionableImpl(2);
		System.out.println(ActionableImpl.count);
		Task t = new ActionableTest2();
		t.period = 1000;
		Task.install(t);
	}
}



class ActionableImpl implements Actionable {
	static int count;
	static Task t;
	
	public void action() {
		count++;
		if (t.nofActivations == 50) Task.remove(t);
	}

	public ActionableImpl(int x) {
		count = x;
		t = new Task(this);
		Task.install(t);
	}
}
