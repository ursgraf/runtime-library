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

/*changes:
 * 15.2.2007 NTB/SP assigned to java
 * 18.05.06	NTB/HS	tpu selection added, ch => channel
 * 08.02.06	NTB/HS	stub creation
 */
/**
 * Decoding of quadrature coded signals <i>(FQD - Fast Quadrature Decoding)</i>
 * with TPU-A or TPU-B. Quadrature coded signals are generally used to measure 
 * the angular position of a motor axis. For this purpose a signal pair is used 
 * (signal A,B). 
 * 
 * The following operations can be used on all 16 channels of the TPU-A or TPU-B.
 * However, make sure to use two adjacent channels for the signal pair.
 */
public class TPU_FQD implements IntbMpc555HB{
	int channel;
	int diff;

	/**
	 * Creates a FQD function on two adjacent TPU pins. <br>
	 * <code>channel</code> and <code>channel+1</code>) will be used for FQD function.<br>
	 * The signal A of the encoder has to be connected to the TPU pin <code>channel</code>, 
	 * while signal B has to connected to the pin <code>channel+1</code>.
	 * 
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param channel	First TPU channel to initialize. Allowed values
	 * 					are 0..14. The second channel <code>channel+1</code> 
	 * 					will be initialized as well.
	 */
	public TPU_FQD(boolean tpuA, int channel) {
		this.channel = channel;
		if (tpuA) {diff = 0; TPUA.init();}
		else {diff = TPUMCR_B - TPUMCR_A; TPUB.init();}

		// initialize TPU for quadrature decode function code = 6;
		int shiftl = (channel % 4) * 4;	// first channel
		int reg = CFSR3_A + diff - (channel / 4) * 2;
		short s = US.GET2(reg);
		s &= ~(0xF << shiftl);
		s |= (0x6 << shiftl);
		US.PUT2(reg, s);
		shiftl = ((channel + 1) % 4) * 4;	// second channel
		reg = CFSR3_A + diff - ((channel + 1) / 4) * 2;
		s = US.GET2(reg);
		s &= ~(0xF << shiftl);
		s |= (0x6 << shiftl);
		US.PUT2(reg, s);

		// position count = 0
		US.PUT2(TPURAM0_A + diff + 0x10 * channel + 2, 0);
		// edge time LSB address of first channel
		US.PUT2(TPURAM0_A + diff + 0x10 * channel + 10, TPURAM0_A + diff + 0x10 * channel + 1);
		// edge time LSB address of second channel
		US.PUT2(TPURAM0_A + diff + 0x10 * (channel + 1) + 10, TPURAM0_A + diff + 0x10 * channel + 1);
		// corresponding pin state address of first channel
		US.PUT2(TPURAM0_A + diff + 0x10 * channel + 8, TPURAM0_A + diff + 0x10 * (channel + 1) + 6);
		// corresponding pin state address of second channel
		US.PUT2(TPURAM0_A + diff + 0x10 * (channel + 1) + 8, TPURAM0_A + diff + 0x10 * channel + 6);

		// host sequence register
		shiftl = (channel % 8) * 2;	// first channel
		reg = HSQR1_A + diff - (channel / 8) * 2;
		s = US.GET2(reg);
		s &= ~(0x3 << shiftl);
		US.PUT2(reg, s);
		shiftl = ((channel + 1) % 8) * 2;	// second channel
		reg = HSQR1_A + diff - ((channel + 1) / 8) * 2;
		s = US.GET2(reg);
		s &= ~(0x2 << shiftl);
		s |= (0x1 << shiftl);
		US.PUT2(reg, s);

		// initialize channel and channel + 1	 
		shiftl = (channel % 8) * 2;	// first channel
		reg = HSRR1_A + diff - (channel / 8) * 2;
		s = US.GET2(reg);
		s |= (0x3 << shiftl);
		US.PUT2(reg, s);
		shiftl = ((channel + 1) % 8) * 2;	// second channel
		reg = HSRR1_A + diff - ((channel + 1) / 8) * 2;
		s = US.GET2(reg);
		s |= (0x3 << shiftl);
		US.PUT2(reg, s);

		// set priority high
		shiftl = (channel % 8) * 2;	// first channel
		reg = CPR1_A + diff - (channel / 8) * 2;
		s = US.GET2(reg);
		s |= (0x3 << shiftl);
		US.PUT2(reg, s);
		shiftl = ((channel + 1) % 8) * 2;	// second channel
		reg = CPR1_A + diff - ((channel + 1) / 8) * 2;
		s = US.GET2(reg);
		s |= (0x3 << shiftl);
		US.PUT2(reg, s);
	}

	/**
	 * Reads the actual encoder position.
	 * 
	 * @return Actual encoder position.
	 */
	public short getPosition() {
		return US.GET2(TPURAM0_A + diff + 0x10 * channel + 2);
	}

	/**
	 * Sets the encoder position to a given value.
	 * 
	 * @param pos		New position to be set.
	 */
	public void setPosition(int pos) {
		US.PUT2(TPURAM0_A + diff + 0x10 * channel + 2, pos);
	}

}