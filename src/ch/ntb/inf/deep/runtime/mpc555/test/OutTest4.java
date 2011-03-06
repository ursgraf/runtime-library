package ch.ntb.inf.deep.runtime.mpc555.test;
import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.*;

/*changes:
 * 22.02.11 NTB/Martin Züger	OutT replaced by System.out
 * 11.11.10	NTB/Urs Graf		creation
 */

public class OutTest4 {
	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		System.out.print('B');
		System.out.println('A');
		System.out.println("hello world");
		System.out.println(123);
		System.out.println(-56);
		System.out.println(3452395879283579L);
//		int a = -7463;
//		System.out.println(a);
		System.out.println(true);
	}
}
