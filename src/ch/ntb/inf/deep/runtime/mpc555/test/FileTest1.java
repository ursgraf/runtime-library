package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.runtime.mpc555.driver.AM29LV160;
import ch.ntb.inf.deep.runtime.mpc555.driver.FFS;
import ch.ntb.inf.deep.runtime.mpc555.driver.File;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 3.5.11	NTB/GRAU	creation
 */

public class FileTest1 implements ntbMpc555HB {
	
	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.out.println("file test");
		FFS.init();
		outDir();
	}

	private static void outDir() {
		System.out.println("output directory");

		
	}
}
