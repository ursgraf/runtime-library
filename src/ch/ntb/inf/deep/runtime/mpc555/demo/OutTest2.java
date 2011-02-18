package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class OutTest2 {
	
	static void run() {
		while (true) {
			SCI2.write((byte)'x');
			for (int i = 0; i < 1000000; i++);
			int a = 3;
			switch (a) {
			case 0: 
				SCI2.write((byte)'0');
				break;
			case 1: 
				SCI2.write((byte)'1');
				break;
			default:
				SCI2.write((byte)'y');
			}	
		}
	}
	
	static {
		SCI2.start(9600, (byte)0, (short)8);
		SCI2.write((byte)'9');
		run();
	}
}
