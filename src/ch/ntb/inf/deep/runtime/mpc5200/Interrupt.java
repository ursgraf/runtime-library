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

package ch.ntb.inf.deep.runtime.mpc5200;

import ch.ntb.inf.deep.runtime.ppc32.PPCException;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 3.9.2012	NTB/GRAU	creation
 */
/**
 * The class for the PPC interrupt exception.<br>
 * Every interrupt handler is an instance of the class <code>Interrupt</code>.
 * 
 * @author Urs Graf
 */
public class Interrupt extends PPCException implements Impc5200 {

	/**
	 * Each interrupt request, which cannot be handled by a registered interrupt handler
	 * increments this counter
	 */
	public static int nofUnexpInterrupts = 0;
	
	static int nofInterrupts = 0;
	static Interrupt[] perInts = new PeripheralInterrupt[24]; 	// interrupt handlers for peripheral interrupts  
	
	/**
	 * This is the interrupt handler. Please make sure to overwrite this method for your 
	 * own interrupt handlers.
	 */
	public void action() {
		nofUnexpInterrupts++;
	}

	static void interrupt() {
		// read ICTLMISAR and determine main interrupt cause
		// currently only peripheral interrupts are supported, hence, this step can be omitted
		
		// if peripheral interrupt
		int status = US.GET4(ICTLPISAR);
		int perNr = 21 - Integer.numberOfTrailingZeros(status);
		perInts[perNr].action();
		
		nofInterrupts++;
	}

	/**
	 * Used to install user defined peripheral interrupt handlers.
	 * @param interrupt Instance of user defined peripheral interrupt handler
	 * @param peripheralNr Peripherals are numbered according to table 7-4 in 
	 * <a href="http://wiki.ntb.ch/infoportal/_media/embedded_systems/mpc555/mpc555_usermanual.pdf">mpc5200 User Manual</a>
	 */
	public static void installPeripheralInterrupt(Interrupt interrupt, int peripheralNr) {
		perInts[peripheralNr] = interrupt;
	}
	
	static {
		for (int i = 0; i < perInts.length; i++) perInts[i] = new PeripheralInterrupt(); 
	}

}
