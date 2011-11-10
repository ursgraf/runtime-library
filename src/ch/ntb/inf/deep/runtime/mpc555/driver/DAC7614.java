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

/* Changes:
 * 15.05.07	NTB/SP	porting from component pascal to java
 *         	NTB/UG	creation
 */

/**
 * Driver for the digital analog converter DAC7614.
 * PCS0 and PCS1 are used.
 */
public class DAC7614 implements ntbMpc555HB {

	/**
	 * Initialize the QSPI Port and set the output values of the DAC's to zero.
	 */
	public static void init(){
		US.PUT2(SPCR1, 0x0); 	//disable QSPI 
		US.PUT1(PQSPAR, 0x01B); // use PCS0, PCS1, MOSI, MISO for QSPI 
		US.PUT1(DDRQS, 0x01E); 	//SCK, MOSI, PCS's outputs; MISO is input 
		US.PUT2(PORTQS, 0x0FF); 	//all Pins, in case QSPI disabled, are high 
		US.PUT2(SPCR0, 0x08314); // QSPI is master, 16 bits per transfer, inactive state of SCLK is high (CPOL=1), data changed on leading edge (CPHA=1), clock = 1 MHz 
		US.PUT2(SPCR2, 0x4700); 	// no interrupts, wraparound mode, NEWQP=0, ENDQP=7 
		
		for(int i=0; i<4; i++) US.PUT1(COMDRAM + i,0x6E); //disable chip select after transfer, use bits in SPCR0, use PCS0 
		for(int i=4; i<8; i++) US.PUT1(COMDRAM + i, 0x6D); 		//disable chip select after transfer, use bits in SPCR0, use PCS1 
		for(int i=0; i<8; i++) US.PUT2(TRANRAM + 2 * i, (i % 4) * 0x4000 + 2048);
		
		US.PUT2(SPCR1, 0x08010);	//enable QSPI, delay 13us after transfer
	}

	/**
	 * Write the output value <code>val</code> to the channel <code>chn<code>
	 * @param ch Channel
	 * @param val Value
	 * 
	 */
	public static void write(int ch, int val){
		US.PUT2(TRANRAM + 2 * ch, (ch % 4) * 0x4000 + val);
	}
}
