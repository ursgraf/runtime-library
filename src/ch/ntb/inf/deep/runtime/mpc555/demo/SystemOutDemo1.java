package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;


/**
 * Demo for System.out using SCI1.
 */
public class SystemOutDemo1 extends Task {
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action() {
		// Write a single character to the stdout
		System.out.print('.');
	}

	static {
		// Initialize SCI1 (9600 8N1)
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		
		// Use the SCI1 for stdout and stderr
		System.out = new PrintStream(SCI1.out);
		System.err = System.out;
		
		// Print a string to the stdout
		System.out.print("System.out demo (SCI1)");
		
		// Create and install the demo task
		Task t = new SystemOutDemo1();
		t.period = 500;
		Task.install(t);
	}
}
