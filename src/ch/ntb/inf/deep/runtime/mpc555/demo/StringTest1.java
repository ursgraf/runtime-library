package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 11.2.11	NTB/Urs Graf	creation
 */

public class StringTest1 {
	static String str1;
	static {
		OutT.switchToSCI2();
		OutT.println("hello world");
		str1 = new String(new char[] {'a', 'b', 'c'});
		OutT.println(str1);
		char[] a1 = new char[] {'1', '2', '3', '4', '5', '6'};
		str1 = new String(a1, 2, 3);
		OutT.println(str1);
		OutT.println("done");
	}
}
