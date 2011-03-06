package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/*changes:
 * 3.3.2011	NTB/GRAU	creation
 */

public class ArrayTest2 {
	static double[] d = {0.5, 1.5, 2.5, 3.5, 4.5};
	static double d1;
	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		System.out.println("array test");
		d1 = d[0];
		System.out.println("done");
	}
}
