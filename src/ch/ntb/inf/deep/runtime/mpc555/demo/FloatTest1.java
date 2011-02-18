package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class FloatTest1 {
	static float f1;
	static float f2 = 1.5f;
	static float f3 = 4.2f * f2;
	
	static {
		float a = 1.0f;
//		f1 = a;
//		a = f3;
		long l1 = US.GETFPR(1);
//		double b = 1.0;
		US.ASM("b 0");
	}
}
