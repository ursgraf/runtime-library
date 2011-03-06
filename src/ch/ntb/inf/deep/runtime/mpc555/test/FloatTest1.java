package ch.ntb.inf.deep.runtime.mpc555.test;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class FloatTest1 {
	static float f1;
	static float f2 = 1.5f;
	static float f3 = 4.2f * f2;
	
	static {
		int i1 = 5;
		double d1 = i1;
		US.ASM("b 0");
	}
}
