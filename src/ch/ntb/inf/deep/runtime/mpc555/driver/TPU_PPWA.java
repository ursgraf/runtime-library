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

/**
 * Period and pulse width measurement (PPWA function) with the TPU-A or TPU-B.<br>
 * All 16 channels of TPU-A or TPU-B can be used.<br>
 */
public class TPU_PPWA implements IntbMpc555HB{

	int channel;
	boolean pulseWidth;
	int diff;

	/**
	 * Create a TPU channel for the measurement of period or pulse width.<br>
	 * 
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param channel	TPU channel to initialize. Allowed values
	 * 					are 0..15.
	 * @param pulseWidth
	 * 					<code>true</code>: pulse width measurement. 
	 * 					<code>false</code>: period measurement.
	 */
	public TPU_PPWA(boolean tpuA, int channel, boolean pulseWidth) {
		this.channel = channel;
		this.pulseWidth = pulseWidth;
		if (tpuA) {diff = 0; TPUA.init();}
		else {diff = TPUMCR_B - TPUMCR_A; TPUB.init();}

		// Disable interrupts for all channels
		int intChn = US.GET2(CIER_A + diff);
		US.PUT2(CIER_A + diff, 0);
		intChn &= (channel ^ 0xffffffff);

		// function code (5) for PPWA
		int low = (channel * 4) % 16;
		int value = US.GET2(CFSR3_A + diff - (channel / 4) * 2);
		value &= ((0xf << low) ^ 0xffffffff);
		value |= (0x5 << low);
		US.PUT2(CFSR3_A + diff - (channel / 4) * 2, value);

		// 24 bit pulse widths oder period, no links for channel (0b10)
		low = (channel * 2) % 16;
		value = US.GET2(HSQR1_A + diff - (channel / 8) * 2);
		value &= ((0x3 << low) ^ 0xffffffff);
		if (pulseWidth)
			value |= (0x2 << low);
		US.PUT2(HSQR1_A + diff - (channel / 8) * 2, value);

		// Channel control
		if (pulseWidth) {
			// Do not force any state, Detect falling edge
			US.PUT2(TPURAM0_A + diff + 0x10 * channel, 0x7);
		} else {
			// Do not force any state, Detect rising edge
			US.PUT2(TPURAM0_A + diff + 0x10 * channel, 0x0b);
		}
		// Max count
		US.PUT2(TPURAM0_A + diff + 0x10 * channel + 2, 0x0100);
		// Channel accum_rate = minimal
		US.PUT2(TPURAM0_A + diff + 0x10 * channel + 8, 0xff00);

		// Initialize
		low = (channel * 2) % 16;
		value = US.GET2(HSRR1_A + diff - (channel / 8) * 2);
		value &= ((0x3 << low) ^ 0xffffffff);
		value |= (0x2 << low);
		US.PUT2(HSRR1_A + diff - (channel / 8) * 2, value);

		// Set priority low
		low = (channel * 2) % 16;
		value = US.GET2(CPR1_A + diff - (channel / 8) * 2);
		value &= ((0x3 << low) ^ 0xffffffff);
		value |= (0x1 << low);
		US.PUT2(CPR1_A + diff - (channel / 8) * 2, value);

		// Enable interrupts for other channels
		US.PUT2(CIER_A + diff, intChn);
	}

	/**
	 * The TPU continuously samples the input data.
	 * This method reads the last sample.<br>
	 * The values are in \u00b5s. 
	 * 
	 * @return 			Last sample in \u00b5s.
	 */
	public int read() {
		int value = 0;
		int lowValue = US.GET2(TPURAM0_A + diff + 0x10 * channel + 0xA);
		if (diff == 0) value = lowValue * TPUA.getCycleTime() / 1000;
		else value = lowValue * TPUB.getCycleTime() / 1000;
		return value;
	}
}