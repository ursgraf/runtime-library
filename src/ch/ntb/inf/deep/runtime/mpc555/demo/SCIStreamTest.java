package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

public class SCIStreamTest extends Task {
	
	public void action() {
		System.out.print('.');
		System.out.print(",");
	}

	static {

		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		
		System.out = new PrintStream(SCI2.out);
		System.err = System.out;
		System.out.print("System.out Test");
		Task t = new SCIStreamTest();
		t.period = 1000;
		Task.install(t);
	}
}
