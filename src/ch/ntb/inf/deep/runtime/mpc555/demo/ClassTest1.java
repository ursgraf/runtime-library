package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class ClassTest1 {
	static int x = 0x223344;
	static short s1;
	static byte b1 = -3;
	static boolean bool1 = true;
	static short[] a123;
	static int i = 0;
	static OutTest1 out = null;
	
	static void run() {
		while (true) {
			SCI2.write((byte)'x');
			SCI2.write((byte)a123[i]);
			i++;
			for (int i = 0; i < 1000000; i++);
		}
	}
	
	static {
		SCI2.start(9600, (byte)0, (short)8);
		SCI2.write((byte)'y');
		a123 = new short[4];
		a123[0] = 65;
		a123[1] = 66;
		a123[2] = 67;
		a123[3] = 68;
		s1 = (short)(2 + a123[3]);
		short s2 = a123[1] ;
		SCI2.write((byte)a123[3]);
		run();
	}
}
