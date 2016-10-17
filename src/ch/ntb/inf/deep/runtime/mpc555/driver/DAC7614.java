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

package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/* Changes:
 * 15.05.07	NTB/SP	porting from component pascal to java
 *         	NTB/UG	creation
 */

/**
 * Driver for the digital analog converter DAC7614.
 * PCS0 and PCS1 are used.
 */
public class DAC7614 implements IntbMpc555HB {

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
	 * Write the output value <code>val</code> to the channel <code>ch</code>
	 * @param ch Channel
	 * @param val Value
	 * 
	 */
	public static void write(int ch, int val){
		US.PUT2(TRANRAM + 2 * ch, (ch % 4) * 0x4000 + val);
	}
}
