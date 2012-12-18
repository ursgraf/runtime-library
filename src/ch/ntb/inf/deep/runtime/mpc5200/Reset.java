/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.runtime.mpc5200;
import ch.ntb.inf.deep.runtime.IdeepCompilerConstants;
import ch.ntb.inf.deep.runtime.ppc32.Ippc32;
import ch.ntb.inf.deep.runtime.ppc32.PPCException;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 21.6.12	NTB/GRAU	creation
 */

class Reset extends PPCException implements Ippc32, IphyCoreMpc5200tiny, IdeepCompilerConstants {
	
	static void reset() {
//		US.ASM("b 0");
		
		int sprg0 = US.GETSPR(272);
		int baseAddr;
		if (sprg0 == 0x55aa) {	// boot from ram
			baseAddr = extRamBase + sysTabBaseAddr;
		} else { // boot from flash
			baseAddr = extFlashBase + sysTabBaseAddr;
			US.PUT4(0x80000000, MemBaseAddr >> 16);	// switch memory base address
			
			// use led for debugging of reset
//			US.PUT4(GPWER, US.GET4(GPWER) | 0x80000000);	// enable GPIO use
//			US.PUT4(GPWDDR, US.GET4(GPWDDR) | 0x80000000);	// make output
//			US.PUT4(GPWOUT, 0x00000000);	// switch on led
//			US.PUT4(GPWOUT, 0x80000000);	// switch off led 1
			
			// configure CS0 for boot flash 
			US.PUT4(CS0START, 0x0000ff00);	// start address = 0xff000000
			US.PUT4(CS0STOP, 0x0000ffff); 	// stop address = 0xffffffff, size = 16MB
			US.PUT4(CS0CR, 0x0008fd00);	// 8 wait states, multiplexed, ack, enabled, 25 addr. lines, 16 bit data, rw
			US.PUT4(CSCR, 0x01000000);	// CS master enable
			US.PUT4(IPBICR, 0x00010001);	// enable CS0, disable CSboot, enable wait states
			
			// configure CS for SDRAM 
			US.PUT4(SDRAMCS0, 0x0000001a);	// 128MB, start at 0
			
			// configure SDRAM controller for DDR 133MHz 
			US.PUT4(SDRAMCR1, 0x73722930);	// config 1	
			US.PUT4(SDRAMCR2, 0x47770000);	// config 2
			US.PUT4(SDRAMCR, 0xe15f0f00);	
			US.PUT4(SDRAMCR, 0xe15f0f02);	
			US.PUT4(SDRAMMR, 0x40090000);	
			US.PUT4(SDRAMMR, 0x058d0000);	
			US.PUT4(SDRAMCR, 0xe15f0f02);	
			US.PUT4(SDRAMCR, 0xe15f0f04);	
			US.PUT4(SDRAMCR, 0xe15f0f04);	
			US.PUT4(SDRAMMR, 0x018d0000);				
			US.PUT4(SDRAMCR, 0x715f0f00);	
			
			// copy code and const from flash to ram
			int srcAddr = extFlashBase;
			int dstAddr = extRamBase;
			int size = US.GET4(baseAddr + stResetOffset + 4) / 4;
			for (int i = 0; i < size; i++) {
				US.PUT4(dstAddr, US.GET4(srcAddr));
				dstAddr += 4;
				srcAddr += 4;
			}
		}
		int stackOffset = US.GET4(baseAddr + stStackOffset);
		int stackBase = US.GET4(baseAddr + stackOffset + 4);
		int stackSize = US.GET4(baseAddr + stackOffset + 8);
		US.PUTGPR(1, stackBase + stackSize - 4);	// set stack pointer
		int kernelClinitAddr = US.GET4(baseAddr + stKernelClinitAddr);
		US.PUTSPR(SRR0, kernelClinitAddr);
		US.PUTSPR(SRR1, SRR1init);
		US.ASM("rfi");
	}
}
