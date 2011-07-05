package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.runtime.mpc555.driver.AM29LV160;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 2.5.11	NTB/GRAU	creation
 */

public class FlashTest1 implements ntbMpc555HB {
	
	static final int flashAddr = extFlashBase + 0x20008;
	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		System.out.println("flash test");
		System.out.printHexln(US.GET4(flashAddr));
//		AM29LV160.programShort(flashAddr, (short)0xaaaa);
//		AM29LV160.programShort(flashAddr+2, (short)0x8888);
		AM29LV160.eraseSector(flashAddr);
		System.out.printHexln(US.GET4(flashAddr));
	}
}
