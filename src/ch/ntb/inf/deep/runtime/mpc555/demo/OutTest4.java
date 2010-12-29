package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.*;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class OutTest4 {
	static {
		SCI2Plain.start(9600, (byte)0, (short)8);
		OutT.switchToSCI2();
		SCI2Plain.write((byte)'f');
		OutT.println('w');
		OutT.println(123);
		SCI2Plain.write((byte)'1');
	}
}
