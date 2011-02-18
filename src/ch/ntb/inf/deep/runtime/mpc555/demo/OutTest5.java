package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;

/*changes:
 * 18.2.11	NTB/Urs Graf	creation
 */

public class OutTest5 {
	static {
		OutT.switchToSCI2();
		OutT.println("float test");
		OutT.println(2.5);
		OutT.println("done");
	}
}
