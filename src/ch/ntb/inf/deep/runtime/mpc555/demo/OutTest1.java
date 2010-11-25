package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1Plain;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class OutTest1 {
	
	static void run() {
		for (int i = 0; i < 10000000; i++);
		SCI1Plain.send((byte)65);
	}
	
	static {
		SCI1Plain.start();
		run();
	}
}
