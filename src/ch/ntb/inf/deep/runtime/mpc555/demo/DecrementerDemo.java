package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.Decrementer;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/* changes:
 * 22.02.11 NTB/Martin Züger	OutT replaced by System.out
 * 11.11.10	NTB/Urs Graf		creation
 */

/**
 * Simple demo application how to use the Decrementer.
 * This application simply outputs the character 'x' one time
 * per second over the SCI2.
 */
public class DecrementerDemo extends Decrementer {
	static DecrementerDemo decTest; 
	
	public void action () {
		System.out.print('x');
	}
	
	static {
		// Initialize the SCI2 (9600 8N1) and use it for System.out
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		// Create and install the Decrementer demo
		decTest = new DecrementerDemo(); 
		decTest.decPeriodUs = 1000000;
		Decrementer.install(decTest);
	}
}