package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.Decrementer;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/* changes:
 * 22.02.11 NTB/Martin Züger	OutT replaced by System.out
 * 11.11.10	NTB/Urs Graf		creation
 */

public class DecrementerTest extends Decrementer {
	static DecrementerTest decTest; 
	
	public void action () {
		System.out.print('x');
	}
	
	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		decTest = new DecrementerTest(); 
		decTest.decPeriodUs = 1000000;
		Decrementer.install(decTest);
	}
}