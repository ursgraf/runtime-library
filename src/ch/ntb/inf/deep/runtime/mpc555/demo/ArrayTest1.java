package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.driver.OutT;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class ArrayTest1 {
//	static int[] a4 = {1,2,3,4};
//	static int[][] a23 = {{1,2,3},{4,5,6}};
//	static int[][][] a223 = {{{1,2,3},{4,5,6}},{{11,12,13},{14,15,16}}};
	static int[][] s1 = new int[2][3];
	
	static {
		OutT.switchToSCI2();
		OutT.println("array test");
//		OutT.println(a4[1]);
//		OutT.println(a23[1][2]);
//		OutT.println(a223[1][0][2]);
		s1[1][1] = 100;
		OutT.println(s1[1][1]);
	}
}
