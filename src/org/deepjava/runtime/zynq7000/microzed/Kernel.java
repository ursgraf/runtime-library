/*
 * Copyright 2011 - 2015 NTB University of Applied Sciences in Technology
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

package org.deepjava.runtime.zynq7000.microzed;

import org.deepjava.runtime.IdeepCompilerConstants;
import org.deepjava.runtime.arm32.*;
import org.deepjava.unsafe.arm.US;

/* changes:
 * 6.10.2015	NTB/Urs Graf	creation
 * 28.8.2019	NTB/Urs Graf 	add exception handling
 */

/**
 *  This is the kernel class. It provides basic functionalities and does the booting-up. 
 */
public class Kernel implements IMicroZed, IdeepCompilerConstants {
	final static int stackEndPattern = 0xee22dd33;
	/** Clock frequency of the processor. */
	public static final int clockFrequency = 667000000; // Hz
	
	static int loopAddr;
	static int cmdAddr;
	
	@SuppressWarnings("unused")
	private static void loop() {	// endless loop
		while (true) {
			try {
				if (cmdAddr != -1) {
					US.PUTGPR(6, cmdAddr);	// use scratch register
					US.ASM("mov r14, r15");	// copy PC to LR 
					US.ASM("mov r15, r6");	// jump 
					cmdAddr = -1;
				}
			} catch (Exception e) {
				cmdAddr = -1;	// stop trying to run the same method
				e.printStackTrace();
				Kernel.blink(2);
			}
		}
	}
	
	/** 
	 * Reads the system time.
	 * 
	 * @return System time in \u00b5s
	 */
	public static long timeUs() {
		int high1, high2, low;
		do {
			high1 = US.GET4(GTCR_U); 
			low = US.GET4(GTCR_L);
			high2 = US.GET4(GTCR_U); 
		} while (high1 != high2);
		long time = ((long)high1 << 32) | ((long)low & 0xffffffffL);
		return time / 333;	// clock = 333MHz
	}
	
	/** 
	 * Reads the system time.
	 * 
	 * @return System time in ns
	 */
	public static long timeNs() {
		int high1, high2, low;
		do {
			high1 = US.GET4(GTCR_U); 
			low = US.GET4(GTCR_L);
			high2 = US.GET4(GTCR_U); 
		} while (high1 != high2);
		long time = ((long)high1 << 32) | ((long)low & 0xffffffffL);
		return time * 3;	// clock = 333MHz
	}
	
	/** 
	 * Blinks LED on MIO47 pin, nTimes with approx. 100us high time and 100us low time, blocks for 1s
	 * 
	 * @param nTimes Number of times the led blinks.
	 */
	public static void blink(int nTimes) { 
		US.PUT4(SLCR_UNLOCK, 0xdf0d);
		US.PUT4(MIO_PIN_47, 0x300);		// led, LVCMOS18, fast, GPIO 47, tristate disable
		US.PUT4(SLCR_LOCK, 0x767b);
		US.PUT4(GPIO_DIR1, US.GET4(GPIO_DIR1) | 0x8000);
		US.PUT4(GPIO_OUT_EN1, US.GET4(GPIO_OUT_EN1) | 0x8000);
		int delay = 1000000;
		for (int i = 0; i < nTimes; i++) {
			US.PUT4(GPIO_OUT1, US.GET4(GPIO_OUT1) | 0x8000);
			for (int k = 0; k < delay; k++);
			US.PUT4(GPIO_OUT1, US.GET4(GPIO_OUT1) ^ 0x8000);
			for (int k = 0; k < delay; k++);
		}
		for (int k = 0; k < (10 * delay + nTimes * 2 * delay); k++);
	}

	/**
	 * Enables interrupts globally. 
	 * Individual interrupts for peripheral components must be enabled locally.
	 */
	public static void enableInterrupts() {
		US.ASM("cpsie i");	// enable IRQ
	}

	/** 
	 * Blinks LED on GPIO pin 47 if stack end was overwritten
	 */
	public static void checkStack() { 
		int addr = sysTabBaseAddr;
		// sysTab is in flash when running out of flash
		if (US.BIT(REBOOT_STATUS, 22)) addr += 0x100000;
		int stackOffset = US.GET4(addr + stStackOffset);
		int stackBase = US.GET4(addr + stackOffset + 4);
		if (US.GET4(stackBase) != stackEndPattern) while (true) blink(3);
	}

	private static int FCS(int begin, int end) {
		int crc  = 0xffffffff;  // initial content
		final int poly = 0xedb88320;  // reverse polynomial 0x04c11db7
		int addr = begin;
		while (addr < end) {
			byte b = US.GET1(addr);
			int temp = (crc ^ b) & 0xff;
			for (int i = 0; i < 8; i++) { // read 8 bits one at a time
				if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly;
				else temp = (temp >>> 1);
			}
			crc = (crc >>> 8) ^ temp;
			addr++;
		}
		return crc;
	}
	
	private static void boot() {
//		US.ASM("b -8"); // stop here
		US.PUT4(SLCR_UNLOCK, 0xdf0d);
		
		US.PUT4(ARM_PLL_CFG, 0x0fa220);	// configure ARM PLL for 1333MHZ with 33.33MHz quartz
		US.PUT4(ARM_PLL_CTRL, 0x28011);	// divider = 40, bypass, reset
		US.PUT4(ARM_PLL_CTRL, US.GET4(ARM_PLL_CTRL) & ~1);	// deassert reset
		while (!US.BIT(PLL_STATUS, 0));	// wait to lock
		US.PUT4(ARM_PLL_CTRL, US.GET4(ARM_PLL_CTRL) & ~0x10);	// no bypass
		US.PUT4(ARM_CLK_CTRL, 0x1f000200);	// use ARM PLL for CPU, divisor = 2 -> processor frequency = 667MHz
		// CPU_6x4x = 667MHz, CPU_3x2x = 333MHz, CPU_2x = 222MHz, CPU_1x = 111MHz
		
		// never configure the DDR PLL, will be set by host when loading through JTAG
		// or will be set by FSBL after POR
		// notably, the DDR PLL must not be set when loading through JTAG and code code resides in DDR
//			US.PUT4(DDR_PLL_CFG, 0x12c220);	// configure DDR PLL for 1067MHZ with 33.33MHz quartz
//			US.PUT4(DDR_PLL_CTRL, 0x20011);	// divider = 32, bypass, reset
//			US.PUT4(DDR_PLL_CTRL, US.GET4(DDR_PLL_CTRL) & ~1);	// deassert reset
//			while (!US.BIT(PLL_STATUS, 1));	// wait to lock
//			US.PUT4(DDR_PLL_CTRL, US.GET4(DDR_PLL_CTRL) & ~0x10);	// no bypass
//			US.PUT4(DDR_CLK_CTRL, 0xc200003);	// 2x-divisor = 3, 3x-divisor = 2
		
		// configure PLL if loading from JTAG, in case of POR, the PLL will be setup by FSBL
		if (!US.BIT(REBOOT_STATUS, 22)) { // is not a power-on reset
			US.PUT4(IO_PLL_CFG, 0x1f42c0);	// configure IO PLL for 1000MHZ with 33.33MHz quartz
			US.PUT4(IO_PLL_CTRL, 0x1e011);	// divider = 30, bypass, reset
			US.PUT4(IO_PLL_CTRL, US.GET4(IO_PLL_CTRL) & ~1);	// deassert reset
			while (!US.BIT(PLL_STATUS, 2));	// wait to lock
			US.PUT4(IO_PLL_CTRL, US.GET4(IO_PLL_CTRL) & ~0x10);	// no bypass
		}

		US.PUT4(CPU_RST_CTRL, 0x100);	// assert peripheral reset, resets the GIC and timers
		US.PUT4(CPU_RST_CTRL, 0);	// deassert peripheral reset
		US.PUT4(UART_CLK_CTRL, 0xa03);	// UART clock, divisor = 10 -> 100MHz, select IO PLL, clock enable for UART0/1
		US.PUT4(APER_CLK_CTRL, 0x01ffcccd);	// enable clocks to access register of all peripherials
		US.PUT4(FPGA0_CLK_CTRL, 0x00200500); // PL clock 0, divisor1 = 2, divisor0 = 5, select IO PLL -> 100MHz
        US.PUT4(GTCR, 0x1);	// enable global timer, prescaler = 1 -> 333MHz
		
		US.PUT4(MIO_PIN_47, 0x300);		// led, LVCMOS18, fast, GPIO 47, tristate disable
		US.PUT4(MIO_PIN_51, 0x300);		// led, LVCMOS18, fast, GPIO 51, tristate disable
		US.PUT4(MIO_PIN_00, 0x300);		// led, LVCMOS18, fast, GPIO 0, tristate disable
		US.PUT4(MIO_PIN_09, 0x300);		// led, LVCMOS18, fast, GPIO 9, tristate disable
		US.PUT4(MIO_PIN_10, 0x300);		// led, LVCMOS18, fast, GPIO 10, tristate disable
		US.PUT4(MIO_PIN_11, 0x300);		// led, LVCMOS18, fast, GPIO 11, tristate disable
		US.PUT4(MIO_PIN_12, 0x300);		// led, LVCMOS18, fast, GPIO 12, tristate disable
		US.PUT4(MIO_PIN_13, 0x300);		// led, LVCMOS18, fast, GPIO 13, tristate disable
		US.PUT4(MIO_PIN_14, 0x12e1);	// UART0 rx
		US.PUT4(MIO_PIN_15, 0x12e0);	// UART0 tx
		US.PUT4(MIO_PIN_48, 0x12e0);	// UART1 tx
		US.PUT4(MIO_PIN_49, 0x12e1);	// UART1 rx

		US.PUT4(LVL_SHFTR_EN, 0xf);	// enable all level shifters between PS and PL
		US.PUT4(FPGA_RST_CTRL, 0);	// deassert FPGA reset
		US.PUT4(UART_RST_CTRL, 0xa);	// assert UART1 reset, must be reset when already having been setup by FSBL
		US.PUT4(UART_RST_CTRL, 0);	// deassert UART1 reset
		US.PUT4(SLCR_LOCK, 0x767b);

        // enable coprocessor 10 and 11
		int val = US.GETCPR(15, 1, 0, 0, 2);
		val |= 0xf00000;
		US.PUTCPR(15, 1, 0, 0, 2, val);

        // enable enable floating point extensions
		US.ASM("vmrs r6, FPEXC");
        US.ASM("orr r6, r6, #0x40000000");
        US.ASM("vmsr FPEXC, r6");
        
        int addr = sysTabBaseAddr;
//		if (US.BIT(REBOOT_STATUS, 22)) { // is a power-on reset
//			// sysTab is in flash when running out of flash
//			addr += 0x100000;
//			// copy vector table from start of DDR to address 0
//			for (int i = 0; i < 8; i++) US.PUT4(i * 4, US.GET4(0x100050 + i * 4));
//		}
		
 		// mark stack end with specific pattern
		int stackOffset = US.GET4(addr + stStackOffset);
		int stackBase = US.GET4(addr + stackOffset + 4);
		US.PUT4(stackBase, stackEndPattern);

		// setup generic interrupt controller	
		US.PUT4(ICCPMR, 0xff);	// set mask, the last 3 bits are read as 0
		US.PUT4(ICCICR, 1);	// use irq, global interrupt enable
		US.PUT4(ICDDCR, 1);	// enable distributor

		int classConstOffset = US.GET4(addr);
//		int state = 0;
		while (true) {
			// get addresses of classes from system table
			int constBlkBase = US.GET4(addr + classConstOffset);
			if (constBlkBase == 0) break;

			// check integrity of constant block for each class
			int constBlkSize = US.GET4(constBlkBase);
			if (FCS(constBlkBase, constBlkBase + constBlkSize) != 0) while(true) blink(1);

			// initialize class variables
			int varBase = US.GET4(constBlkBase + cblkVarBaseOffset);
			int varSize = US.GET4(constBlkBase + cblkVarSizeOffset);
			int begin = varBase;
			int end = varBase + varSize;
			while (begin < end) {US.PUT4(begin, 0); begin += 4;}
			
//			state++; 
			classConstOffset += 4;
		}
		classConstOffset = US.GET4(addr);
		Heap.sysTabBaseAddr = addr;
		int kernelClinitAddr = US.GET4(addr + stKernelClinitAddr); 
		while (true) {
			// get addresses of classes from system table
			int constBlkBase = US.GET4(addr + classConstOffset);
			if (constBlkBase == 0) break;

			// initialize classes
			int clinitAddr = US.GET4(constBlkBase + cblkClinitAddrOffset);
			if (clinitAddr != -1) {	
				if (clinitAddr != kernelClinitAddr) {	// skip kernel 
					US.PUTGPR(0, clinitAddr);
					US.ASM("mov r14, r15");	// copy PC to LR 
					US.ASM("mov r15, r0");
				} else {	// kernel
					loopAddr = US.ADR_OF_METHOD("org/deepjava/runtime/zynq7000/microzed/Kernel/loop");
					US.ASM("cpsie i");	// enable IRQ
				}
			}

			// the direct call to clinitAddr destroys volatile registers, hence make sure
			// the variable classConstOffset is forced into nonvolatile register
			// this is done by call to empty()
			empty();
			classConstOffset += 4;
		}
	}
	
	private static void empty() { }

	static {
		try {
			boot();
			cmdAddr = -1;	// must be after class variables are zeroed by boot
			// load PC
			US.PUTGPR(6, loopAddr);	// use scratch register
			US.ASM("mov r14, r15");	// copy PC to LR 
			US.ASM("mov r15, r6");	// jump 
		} catch (Exception e) {
			e.printStackTrace();
			while (true) Kernel.blink(5);
		}
	}

}