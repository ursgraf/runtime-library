/*
 * Copyright 2011 - 2019 NTB University of Applied Sciences in Technology
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

package org.deepjava.runtime.zynq7000;

import org.deepjava.unsafe.arm.US;

/* changes:
 * 31.10.2019	NTB/Urs Graf	creation
 */

/**
 * The class for the ARM private timer. This timer is used to count backwards. 
 * As soon as zero is reached the counter is reloaded with its initial value and an interrupt is generated.
 * The private timer is clocked with half of the CPU frequency (CPU_3x2x).
 * You can extend this class to define your own timer class, e.g. to run a control loop. 
 * Keep in mind, that the action method is called from the interrupt handler. If you use 
 * the floating point unit, you have to make sure that the floating point registers get saved 
 * upon entering the action method, see \ref US.ENABLE_FLOATS.
 */
public class Decrementer extends IrqInterrupt {
	
	/**
	 * This is the interrupt handler. Please make sure to overwrite this method for your 
	 * own interrupt handlers.
	 */
	@Override
	public void action() {
		nofUnexpInterrupts++;
	}


	/**
	 * Used to install user defined handler for decrementer exceptions.
	 * @param period Period in us, time between subsequent interrupts
	 */
	public Decrementer (int period) {
		US.PUT4(PTLR, period * 3);	
		US.PUT4(PTCOUNT, period * 3);
		US.PUT4(PTCR, (110 << 8) | 7);	// prescaler = 111, auto reload, enable interrupts
		IrqInterrupt.install(this, 29);
	}

}
