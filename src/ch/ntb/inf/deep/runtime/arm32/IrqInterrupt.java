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

package ch.ntb.inf.deep.runtime.arm32;

import ch.ntb.inf.deep.runtime.Kernel;
import ch.ntb.inf.deep.runtime.zynq7000.Izynq7000;
import ch.ntb.inf.deep.unsafe.arm.US;

/*changes:
 * 08.05.2019	NTB/Urs Graf	creation
 */

/**
 * The class for the ARM irq interrupt.
 */
public class IrqInterrupt extends ARMException implements Izynq7000 {
	/**
	 * The number of times a irq interrupt was executed
	 */
	public static int nofIRQ;

	static void irqInterrupt() {
//		nofIRQ++;
//		int id = US.GET4(ICCIAR);
//		id = US.GET4(ICCIAR);
//		US.GET4(UART1_FIFO);
//		US.PUT4(ICCEOIR, id);
		while (true) {
//			Kernel.blink(1); Kernel.blink(2);
		}
	}

}
