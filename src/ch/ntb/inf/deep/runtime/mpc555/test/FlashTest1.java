/*
 * Copyright 2011 - 2013 NTB University of Applied Sciences in Technology
 * Buchs, Switzerland, http://www.ntb.ch/inf
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.mpc555.driver.ffs.AM29LV160;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 2.5.11	NTB/GRAU	creation
 */

public class FlashTest1 implements IntbMpc555HB {
	
	static final int flashAddr = extFlashBase + 0x20008;
	static {
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		
		System.out.println("flash test");
		System.out.printHexln(US.GET4(flashAddr));
//		AM29LV160.programShort(flashAddr, (short)0xaaaa);
//		AM29LV160.programShort(flashAddr+2, (short)0x8888);
		AM29LV160.eraseSector(flashAddr);
		System.out.printHexln(US.GET4(flashAddr));
	}
}
