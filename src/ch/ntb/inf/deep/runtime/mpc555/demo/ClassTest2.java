package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2Plain;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class ClassTest2 {
	static ClassTest2 test2;
	static ClassTest2[] a3 = new ClassTest2[3];
	byte b1 = 65;
	short s1;
	int i1;
	byte[] a1;
	ClassTest2[] a2;
	int i2 = 0x332211;
	
	static void run() {
		while (true) {
			SCI2Plain.write((byte)'x');
			test2.send();
			for (int i = 0; i < 1000000; i++);
			int a = 3;
			switch (a) {
			case 0: 
				SCI2Plain.write((byte)'0');
				break;
			case 1: 
				SCI2Plain.write((byte)'1');
				break;
			default:
				SCI2Plain.write((byte)'y');
			}	
		}
	}
	
	void send() {
		SCI2Plain.write((byte)'U');
	}
	
	public ClassTest2 () {
		s1 = 0x7788;
		a1 = new byte[5];
		a1[0] = (byte)0x44;
		a1[1] = (byte)0x55;
		a1[2] = (byte)(s1 >> 8);
		a1[3] = (byte)s1;
		a1[4] = 10;
		a2 = new ClassTest2[7];
	}
	
	static {
		SCI2Plain.start(9600, (byte)0, (short)8);
		SCI2Plain.write((byte)'0');
		test2 = new ClassTest2();
		test2.send();
		test2.a2[1] = new ClassTest2();
		a3[1] = new ClassTest2();
		a3[2] = test2.a2[1];
		SCI2Plain.write((byte)'1');
		SCI2Plain.write((byte)test2.a2[1].b1);
		run();
	}
}
