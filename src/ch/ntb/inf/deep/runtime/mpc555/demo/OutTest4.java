package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;

/*changes:
 * 11.11.10	NTB/Urs Graf	creation
 */

public class OutTest4 {
	static {
		OutT.switchToSCI2();
		OutT.print('B');
		OutT.println('A');
		OutT.println("hello world");
		OutT.println(123);
		OutT.println(-56);
//		int a = -7463;
//		OutT.println(a);
		OutT.println(true);
	}
}
