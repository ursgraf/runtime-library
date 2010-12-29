package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2Plain;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class OutTest1 {
	static short[] a123;
	
	static void run() {
		while (true) {
			SCI2Plain.write((byte)'x');
			for (int i = 0; i < 1000000; i++);
		}
	}
	
	static {
		SCI2Plain.start(9600, (byte)0, (short)8);
		SCI2Plain.write((byte)'y');
		a123 = new short[4];
		a123[0] = 65;
		a123[1] = 66;
		a123[2] = 67;
		a123[3] = 68;
		short b = a123[1];
		SCI2Plain.write((byte)a123[3]);
		run();
	}
}
