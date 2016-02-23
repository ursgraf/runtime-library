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

package ch.ntb.inf.deep.runtime.iMX6;
import ch.ntb.inf.deep.runtime.IdeepCompilerConstants;
import ch.ntb.inf.deep.runtime.arm32.Iarm32;
import ch.ntb.inf.deep.runtime.arm32.PPCException;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 11.11.10	NTB/GRAU	creation
 */
/**
 * The class for the PPC reset exception.
 * The stack pointer will be initialized and the program counter will be
 * set to the beginning of the class initializer of the kernel.
 * 
 * @author Urs Graf
 */
class Reset extends PPCException implements Iarm32, IiMX6, Icolibri_iMX6, IdeepCompilerConstants {
	
	static void reset() {
//		US.ASM("setend BE"); // data memory organized in big endian format
//		int stackOffset = US.GET4(sysTabBaseAddr + stStackOffset);
		
//		int a = US.GET4(0x18000000);
//		int c = a + b;
		
		int a = 0;
		US.PUT4(GPIO2_GDIR, 4);
		while (true) {
			a ^= -1;
//			a <<= 1;
			US.PUT4(GPIO2_DR, a);
//			if (a == 16) a = 1;
			for (int i = 1000000; i > 0; i--); 
//			US.PUT4(GPIO2_DR, 0);
//			for (int i = 10000000; i > 0; i--); 
//			US.PUT4(GPIO2_DR, 4);
//			for (int i = 10000000; i > 0; i--); 
//			US.PUT4(GPIO2_DR, 0);
//			for (int i = 10000000; i > 0; i--); 
		}
//		US.PUT4(GPIO2_DR, 0);
//		US.PUT4(GPIO2_DR, 4);
//		US.PUT4(GPIO2_DR, 0);
//		
//		while(true);
//		int b = 0x10;
//		while (b != 0) {
//			b--;
//			a += 0x11;
//		}
//		b++;
//		US.ASM("b -8"); 
//		int stackBase = US.GET4(sysTabBaseAddr + stackOffset + 4);
//		int stackSize = US.GET4(sysTabBaseAddr + stackOffset + 8);
//		US.PUTGPR(1, stackBase + stackSize - 4);	// set stack pointer
//		int kernelClinitAddr = US.GET4(sysTabBaseAddr + stKernelClinitAddr);
//		int c = 0x1122;
//		while (c != 0) c++;
//		int d = 0x3344;
//		while (true);
//		US.PUTSPR(SRR0, kernelClinitAddr);
//		US.PUTSPR(SRR1, SRR1init);
//		US.ASM("rfi");
	}
}
