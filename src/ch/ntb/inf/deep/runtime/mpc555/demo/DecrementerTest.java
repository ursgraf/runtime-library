package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.*;
import ch.ntb.inf.deep.runtime.mpc555.driver.OutT;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */

public class DecrementerTest extends Decrementer {
	
	public void action () {
		OutT.print('x');
	}
	
	static {
		DecrementerTest decTest = new DecrementerTest(); 
		decTest.decPeriodUs = 1000000;
		Decrementer.install(decTest);
	}
}