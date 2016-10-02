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

/* changes:
 * 08.04.2011	NTB/MZ	methods in/out renamed to get/set
 * 15.02.2007	NTB/SP	adapted to java
 * 08.02.2006	NTB/HS	stub creation
 */
/**
 * Driver to use a channel of the TPU (A or B) as a GPIO.<br>
 * Each 16 channels of both time processing units can be used as
 * general purpose in- or output.
 */
public class TPU_DIO implements IntbMpc555HB, DigitalOutput {

	int channel;
	int diff;

	/**
	 * Create a general purpose in- our output on a given channel. 
	 * 
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param channel	TPU channel to initialize. Allowed values are 0..15.
	 * @param out		<code>true</code> initializes the channel as a digital output.
	 * 					<code>false</code> initializes the channel as a digital input.
	 */
	public TPU_DIO(boolean tpuA, int channel, boolean out) {
		this.channel = channel;
		if (tpuA) {diff = 0; TPUA.init();}
		else {diff = TPUMCR_B - TPUMCR_A; TPUB.init();}

		//function code (2) for DIO
		short s = US.GET2(CFSR3_A + diff - (channel / 4) * 2);
		int shiftl = (channel % 4) * 4;
		s &= ~(7 << shiftl);
		s |= (2 << shiftl);
		US.PUT2(CFSR3_A + diff - (channel / 4) * 2,s);

		//Update on transition for inputs, dosen't have any effect for outputs
		s = US.GET2(HSQR1_A + diff - (channel / 8) * 2);
		shiftl = (channel % 8) * 2;
		s &= ~(3 << shiftl);
		US.PUT2(HSQR1_A + diff -(channel / 8) * 2, s);

		if (out) {
			US.PUT2(TPURAM0_A + diff + 0x10 * channel, 0x3);
		} else {
			s = US.GET2(HSQR1_A + diff - (channel / 8) * 2);
			s &= ~(3 << shiftl);
			US.PUT2(HSQR1_A + diff -(channel / 8) * 2, s);
			US.PUT2(TPURAM0_A + diff + 0x10 * channel, 0xF);
		}

		//Request initialization
		s = US.GET2(HSRR1_A + diff -(channel / 8)* 2);
		s |= (3 <<shiftl);
		US.PUT2(HSRR1_A + diff -(channel / 8)* 2, s);

		//Set priority low
		s = US.GET2(CPR1_A + diff - (channel / 8)* 2);
		s &= ~(3 << shiftl);
		s |= (1 << shiftl);
		US.PUT2(CPR1_A + diff - (channel / 8) * 2,s);

	}

	/**
	 * Returns the current state of the TTL signal.
	 * 
	 * @return 			the current state of the TTL at the given pin. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public boolean get() {
		return (US.GET2(TPURAM0_A + diff + 0x10 * channel + 2) & (1 << 15)) != 0; 
	}

	/**
	 * Set the TTL signal.
	 * 
	 * @param val		Value to set. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public void set(boolean val) {
		//Disable all Interrupts
		short sh = US.GET2(CISR_A + diff);
		US.PUT2(CISR_A + diff,(short)0);

		short s = US.GET2(HSRR1_A + diff - ((channel / 8) * 2));
		int shiftl = (channel % 8) * 2;
		s &= ~(3 << shiftl);
		if(val) s |= (1 << shiftl);
		else s |= (2 << shiftl);
		US.PUT2(HSRR1_A + diff - ((channel / 8) * 2), s);

		//Restore Interrupts
		US.PUT2(CISR_A + diff, sh);
	}

	/**
	 * Sets the direction of a given channel to input or output.<br>
	 * 
	 * @param out		Pin usage: <strong>true</strong> configures the pin as output, <strong>false</strong> as input. 
	 */
	public void dir(boolean out) {
		int shiftl = (channel % 4) * 4;
		if (out) {
			US.PUT2(TPURAM0_A + diff + 0x10 * channel, 0x3);
		} else {
			short s = US.GET2(HSQR1_A + diff - (channel / 8) * 2);
			s &= ~(3 << shiftl);
			US.PUT2(HSQR1_A + diff -(channel / 8) * 2, s);
			US.PUT2(TPURAM0_A + diff + 0x10 * channel, 0xF);
		}

		//Request initialization
		short s = US.GET2(HSRR1_A + diff -(channel / 8)* 2);
		s |= (3 <<shiftl);
		US.PUT2(HSRR1_A + diff -(channel / 8)* 2, s);
	}


}