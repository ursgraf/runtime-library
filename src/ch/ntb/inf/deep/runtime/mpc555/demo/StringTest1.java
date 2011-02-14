package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;

/*changes:
 * 11.2.11	NTB/Urs Graf	creation
 */

public class StringTest1 {
	static String str1;
	
	static {
		OutT.switchToSCI2();
		OutT.println("hello world");
		char[] a1 = new char[3];
		a1[0] = 'a'; a1[1] = 'b'; a1[2] = 'c'; 
		str1 = new String(a1);
//		OutT.print(str1);
	}
}
