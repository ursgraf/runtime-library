package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;


/**
 * Demo for System.out using SCI2.
 */
public class SystemOutDemo2 extends Task {
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action() {
		// Write a single character to the stdout
		System.out.print('.');
	}

	static {
		// Initialize SCI2 (9600 8N1)
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		
		// Use the SCI2 for stdout and stderr
		System.out = new PrintStream(SCI2.out);
		System.err = System.out;
		
		// Print a string to the stdout
		System.out.print("System.out demo (SCI2)");
		
		// Create and install the demo task
		Task t = new SystemOutDemo2();
		t.period = 500;
		Task.install(t);
	}
}
