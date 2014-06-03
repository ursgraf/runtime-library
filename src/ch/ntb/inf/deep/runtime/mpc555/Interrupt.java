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

package ch.ntb.inf.deep.runtime.mpc555;

import ch.ntb.inf.deep.runtime.ppc32.PPCException;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 25.11.10	NTB/GRAU	creation
 */
public class Interrupt extends PPCException implements IntbMpc555HB {

	/**
	 * Each interrupt request, which cannot be handled by a registered interrupt handler
	 * increments this counter
	 */
	public static int nofUnexpInterrupts = 0;
	
	static int nofInterrupts = 0;
	static Interrupt[] interrupts = new Interrupt[16]; 	// ext. and int. interrupts  
	
	public int enableRegAdr;
	public int enBit;
	public int flagRegAdr;
	public int flag;
	private Interrupt next;
	
	public void action() {
		nofUnexpInterrupts++;
	}

	static void interrupt() {
		nofInterrupts++;
		int pendInt = US.GET2(SIPEND);
		int i = 0;	// find highest bit
		while (pendInt != 0) {
	      i = (pendInt & -pendInt); // grab the lowest bit
	      pendInt &= ~i;            // clear the lowest bit
	    }
		int bitNr = 0;
		while (i > 1) {bitNr++; i >>=1;}
		Interrupt currInt = interrupts[15 - bitNr];
		if ((bitNr & 1) == 1) {		// external interrupt
			currInt.action();
			US.PUT2(SIPEND, 1 << bitNr);		// clear pending bit
		} else {	// internal interrupt
			handleInternalInt(currInt);
		}
	}

	private static void handleInternalInt(Interrupt currInt) {
		boolean done = false;
		while (currInt.next != null && !done) {
			short sh = US.GET2(currInt.enableRegAdr);
			if ((sh & (1 << currInt.enBit)) != 0) {
				sh = US.GET2(currInt.flagRegAdr);
				if ((sh & (1 << currInt.flag)) != 0) {
					currInt.action();
					done = true;
				}
			}
			currInt = currInt.next;
		}
		if (currInt.next == null && !done)
			currInt.action();	// default handler
	}

	public static void install(Interrupt interrupt, int level, boolean internal) {
		if (internal) {
			interrupt.next = interrupts[2 * level + 1]; 
			interrupts[2 * level + 1] = interrupt;
		} else {
			interrupt.next = interrupts[2 * level]; 
			interrupts[2 * level] = interrupt;
		}
	}
	
	static {
		for (int i = 0; i < 16; i++) interrupts[i] = new Interrupt(); 
		US.PUT4(SIEL, 0xffff0000);	// external ints are edge sensitive, exit low-power modes 
		US.PUT4(SIPEND, 0xffff0000);	// reset all int requests
		US.PUT4(SIMASK, 0x7fff0000);	// enable all interrupt levels, except NMI
	}

}
