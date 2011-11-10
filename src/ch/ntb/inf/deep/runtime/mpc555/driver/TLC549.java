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

package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;
//© NTB/UG 
/*changes:
*	21.07.07	NTB/SP	porting to java
*	08.02.06	NTB/HS	stub creation
*
*/
/**
 * Treiber für den AD-Wandler TLC549.<br>
 * Der AD-Wandler ist wie folgt anzuschliessen:<br>
 * MPC555 => TLC549:<br>
 * PCS0 => CS<br>
 * MISO => DATA OUT<br>
 * SCK => I/O CLOCK<br>
 * Für den Anschluss des AD-Wandlers wird die QSPI-Schnittstelle verwendet. Es
 * darf keine weitere extrene Peripherie angeschlossen werden, welche ebenfalls
 * die QSPI benötigt.
 */
public class TLC549 implements ntbMpc555HB {



	/**
	 * Initialisiert die QSPI für den Betrieb des TLC549.
	 */
	public static void init() {
		US.PUT2(SPCR1, 0x0); 	// disable QSPI 
		US.PUT1(PQSPAR, 0xB); 	//use PCS0, MOSI, MISO for QSPI //
		US.PUT1(DDRQS, 0xE); 	//SCK, MOSI, PCS0 outputs; MISO is input
		US.PUT2(PORTQS, 0xFF); 	//all Pins, in case QSPI disabled, are high 
		US.PUT2(SPCR0, 0xA028); 	//QSPI is master, 8 bits per transfer, inactive state of 
															//SCLK is LOW (CPOL=0), data captured on leading edge (CPHA=0), clock = 0.5 MHz 
		US.PUT2(SPCR2, 0x4000); 	// no interrupts, wraparound mode, NEWQP=0, ENDQP=0
		US.PUT1(COMDRAM, 0x3E); 	//disable chip select after transfer, 8 bits,  DT=1, DSCK=1, use PCS0 
		US.PUT2(SPCR1, 0xB816);	// enable QSPI, 1.4usec delay from PCS to SCK,
															//wait 17 usec for conversion after transfer 
	}

	/**
	 * Liest den gewandelten Wert aus dem AD-Wandler.
	 * 
	 * @return Gewandelter Wert.
	 */
	public static short read() {
			return US.GET2(RECRAM);
	}
	
	static{
		init();
	}
}