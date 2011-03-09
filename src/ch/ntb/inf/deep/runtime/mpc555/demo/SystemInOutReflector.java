package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;


/**
 * Demo for System.out and System.in using SCI2.
 */
public class SystemInOutReflector extends Task {
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action() {
		// reflect input on stdin to stdout
		if (SCI2.availToRead() > 0)
			SCI2.write((byte)SCI2.read());
	}

	static {
		// Initialize SCI2 (9600 8N1)
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		SCI2.write((byte)'x');
		SCI2.write((byte)'1');
		
		// Create and install the demo task
		Task t = new SystemInOutReflector();
		t.period = 0;
		Task.install(t);
	}
}
