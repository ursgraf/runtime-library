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

import ch.ntb.inf.deep.runtime.ppc32.PPCException;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 3.9.2012	NTB/GRAU	creation
 */
public class Interrupt extends PPCException implements IphyCoreMpc5200tiny {

	/**
	 * Each interrupt request, which cannot be handled by a registered interrupt handler
	 * increments this counter
	 */
	public static int nofUnexpInterrupts = 0;
	
	static int nofInterrupts = 0;
	static Interrupt[] perInts = new Interrupt[24]; 	// interrupt handlers for peripheral interrupts  
	
//	public int enableRegAdr;
//	public int enBit;
//	public int flagRegAdr;
//	public int flag;
//	private Interrupt next;
	
//	public static int data;
	
	public void action() {
		nofUnexpInterrupts++;
	}

	static void interrupt() {
//		int status = US.GET4(ICTLPISAR);
		US.GET4(ICTLPISAR);
//		int intNr = status;	// get the
		perInts[17].action();
		nofInterrupts++;
//		int pendInt = US.GET2(SIPEND);
//		int i = 0;	// find highest bit
//		while (pendInt != 0) {
//	      i = (pendInt & -pendInt); // grab the lowest bit
//	      pendInt &= ~i;            // clear the lowest bit
//	    }
//		int bitNr = 0;
//		while (i > 1) {bitNr++; i >>=1;}
//		Interrupt currInt = interrupts[15 - bitNr];
//		if ((bitNr & 1) == 1) {		// external interrupt
//			currInt.action();
//			US.PUT2(SIPEND, 1 << bitNr);		// clear pending bit
//		} else {	// internal interrupt
//			boolean done = false;
//			while (currInt.next != null && !done) {
//				short sh = US.GET2(currInt.enableRegAdr);
//				if ((sh & (1 << currInt.enBit)) != 0) {
//					sh = US.GET2(currInt.flagRegAdr);
//					if ((sh & (1 << currInt.flag)) != 0) {
//						currInt.action();
//						done = true;
//					}
//				}
//				currInt = currInt.next;
//			}
//			if (currInt.next == null && !done)
//				currInt.action();	// default handler
//		}
	}

	public static void install(Interrupt interrupt, int peripheralNr) {
//		interrupt.next = interrupts[2 * level + 1]; 
		perInts[peripheralNr] = interrupt;
	}
	
	static {
		for (int i = 0; i < perInts.length; i++) perInts[i] = new Interrupt(); 
//		US.PUT4(SIEL, 0xffff0000);	// external ints are edge sensitive, exit low-power modes 
//		US.PUT4(SIPEND, 0xffff0000);	// reset all int requests
//		US.PUT4(SIMASK, 0x7fff0000);	// enable all interrupt levels, except NMI
	}

}
