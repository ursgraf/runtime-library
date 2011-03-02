package ch.ntb.inf.deep.runtime.mpc555.test;
import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.*;

/*changes:
 * 18.2.11	NTB/Urs Graf	creation
 */

public class OutTest5 {
	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		System.out.println("float test");
		System.out.println(2.5);
		System.out.println("done");
	}
}
