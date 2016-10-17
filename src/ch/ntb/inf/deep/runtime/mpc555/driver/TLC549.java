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
//© NTB/UG 
/*changes:
*	21.07.07	NTB/SP	porting to java
*	08.02.06	NTB/HS	stub creation
*
*/
/**
 * Driver for a TLC549 8bit AD-converter connected to the QSPI.<br>
 * Connect the converter as follows:<br>
 * MPC555 =&gt; TLC549:<br>
 * PCS0 =&gt; CS<br>
 * MISO =&gt; DATA OUT<br>
 * SCK =&gt; I/O CLOCK<br>
 * Do not connect any other devices on the QSPI interface without adapting the 
 * settings of the control lines.
 */
public class TLC549 implements IntbMpc555HB {

	/**
	 * Initializes the QSPI for TLC549.
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
	 * Reads a digitized analog input value.
	 * 
	 * @return Input value (8bit, 0..255)
	 */
	public static short read() {
			return US.GET2(RECRAM);
	}
	
	static{
		init();
	}
}