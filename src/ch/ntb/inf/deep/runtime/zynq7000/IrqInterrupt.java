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

package ch.ntb.inf.deep.runtime.zynq7000;

import ch.ntb.inf.deep.runtime.arm32.ARMException;
import ch.ntb.inf.deep.unsafe.arm.US;

/* changes:
 * 08.05.2019	NTB/Urs Graf	creation
 */

/**
 * The class for the ARM irq interrupt.
 * Every interrupt handler is an instance of the class <code>IrqInterrupt</code>.
 */
public class IrqInterrupt extends ARMException implements Izynq7000 {

	/**
	 * Each interrupt request, which cannot be handled by a registered interrupt handler
	 * increments this counter
	 */
	public static int nofUnexpInterrupts = 0;

	/**
	 * The number of times a irq interrupt was executed
	 */
	public static int nofIRQ;

	static IrqInterrupt[] intTable = new IrqInterrupt[92]; 	// private and shared peripheral interrupts  

	/**
	 * This is the interrupt handler. Please make sure to overwrite this method for your 
	 * own interrupt handlers.
	 */
	public void action() {
		nofUnexpInterrupts++;
	}

	static void irqInterrupt() {
		nofIRQ++;
		int id = US.GET4(ICCIAR);	// get id of interrupt
		intTable[id].action();
		US.PUT4(ICCEOIR, id);	// clear interrupt
	}

	/**
	 * Used to install user defined interrupt handlers.
	 * @param interrupt Instance of user defined interrupt handler
	 * @param id ID of the private or shared peripheral interrupt
	 */
	public static void install(IrqInterrupt interrupt, int id) {
		intTable[id] = interrupt;
		int addr = ICDISER0 + id / 32 * 4;
		US.PUT4(addr, US.GET4(addr) | (1 << (id % 32)));	// interrupt set enable register
		addr = ICDIPTR0 + id / 4 * 4 + id % 4;
		US.PUT1(addr, 2);	// interrupts target register, targets CPU1
		//		addr = ICDICFR0 + id / 16 * 4;
		//		US.PUT4(addr, US.GET4(addr) | (2 << (id % 16) * 2));	// edge sensitive, might be necessary for certain types
	}

	static {
		for (int i = 0; i < intTable.length; i++) intTable[i] = new IrqInterrupt(); 
	}

}
