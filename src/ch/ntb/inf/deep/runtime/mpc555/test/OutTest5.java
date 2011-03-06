package ch.ntb.inf.deep.runtime.mpc555.test;
import java.io.PrintStream;

import ch.ntb.inf.deep.lowLevel.LL;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 18.2.11	NTB/Urs Graf	creation
 */

public class OutTest5 {
	static double d1 = 2.5e17;
	static long l1;
	static long l2 = 0x4004000000000000L;
	static double d2, d3;
	static int exp;
	static int bits;
	
	static {
		l1 = LL.doubleToBits(d1);
		d2 = LL.bitsToDouble(l2);
		bits = Double.highPartToIntBits(d1);
		exp = Double.getExponent(d1);
		d3 = Double.setExponent(d2, 10);
//		US.HALT(30);
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		System.out.println("float test");
		System.out.println(2.5);
		System.out.println(2.345678);
		System.out.println(2.345678f);
		System.out.println(-2.34);
		System.out.printHexln(255);
		System.out.printHexln(-1);
		System.out.printHexln(0x973bc3af);
		System.out.printHexln(0xaf973bc3af456789L);
		System.out.printHexln(-1L);
		System.out.println("done");
	}
}
