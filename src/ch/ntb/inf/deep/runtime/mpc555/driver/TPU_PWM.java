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
 * 26.05.2011	NTB/MZ,	JavaDoc updated
 * 07.12.2010	NTB/UG,	adapted to the new board interface file
 * 25.11.2010	NTB/UG,	ported to deep
 * 10.01.2008	NTB/SP,	to java ported
 * 08.02.2006	NTB/HS,	stub creation
 */

/**
 * Driver for generating pulse width modulated (PWM) signals on the TPU.<br>
 * All 16 channels of TPU-A or TPU-B can be used. All timing data has to be a multiple of the
 * TPU time base clock (806 ns).
 */
public class TPU_PWM implements IntbMpc555HB{

	int channel;
	int period;
	int diff;

	/** TPU time base in nanoseconds [ns]. */
	public static final int tpuTimeBase = 806;

	/**
	 * Create a TPU channel for the generation of PWM signals.<br>
	 * Remember: <code>period</code> and <code>highTime</code> have a resolution of 16 bit.
	 * However, the maximum value for both values is <code>0x8000</code>.<br>
	 * The period time should be defined as an integer constant.
	 * Example for a period time of <i>T = 50 \u00b5s (f = 20 kHz)</i>: <br>
	 * <code>private final int pwmPeriod = 50000 / TpuTimeBase;</code>
	 * 
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param channel	TPU channel to initialize. Allowed values
	 * 					are 0..15.
	 * @param period	Period time as a multiple of the TPU time base 
	 * @param highTime	PWM signal high time as a multiple of the TPU
	 * 					time base. It has to be less or equal then
	 * 					the period time.
	 */
	public TPU_PWM(boolean tpuA, int channel, int period, int highTime) {
		this.channel = channel;
		this.period = period;
		int shift, tpuAdr, s;

		if (tpuA) {diff = 0; TPUA.init();}
		else {diff = TPUMCR_B - TPUMCR_A; TPUB.init();}

		shift = (channel * 4) % 16;
		tpuAdr = CFSR3_A + diff - (channel / 4) * 2;			
		s = US.GET2(tpuAdr);
		s &= ~(0xF << shift);
		s |= 3 << shift;
		US.PUT2(tpuAdr,(short) s);
		//Force pin hig, use TCR1
		tpuAdr = TPURAM0_A + diff + 0x10 * channel;
		US.PUT2(tpuAdr, 0x91 );
		//Define high time
		US.PUT2(tpuAdr + 4, highTime);
		//Define time of period
		US.PUT2(tpuAdr + 6, period);
		//Request initialization
		tpuAdr = HSRR1_A + diff - (channel / 8) * 2;
		shift = (channel * 2) % 16;
		s = US.GET2(tpuAdr);
		s &= ~(0x3 << shift);
		s |= 2 << shift;
		US.PUT2(tpuAdr,s);
		//set priority low
		tpuAdr = CPR1_A + diff - (channel / 8) * 2;
		s = US.GET2(tpuAdr);
		s &= ~(0x3 << shift);
		s |= 1 << shift;
		US.PUT2(tpuAdr,s);
	}

	/**
	 * Update the parameters of a PWM signal at a TPU channel.<br>
	 * This method will simply update the period and high time registers without 
	 * initializing the channel. The maximum value for both values is <code>0x8000</code>.
	 * 
	 * @param period	Period time as a multiple of the TPU time base 
	 * @param highTime	PWM signal high time as a multiple of the TPU
	 * 					time base. It has to be less or equal then
	 * 					the period time!
	 */
	public void update(int period, int highTime) {
		// define high time and period
		US.PUT4(TPURAM0_A + diff + 0x10 * channel + 4, (highTime << 16) | period);
	}
	
	/**
	 * Update the parameters of a PWM signal at a TPU channel.<br>
	 * This method will simply update the period and high time registers without 
	 * initializing the channel. The maximum value for both values is <code>0x8000</code>.
	 * 
	 * @param highTime	PWM signal high time as a multiple of the TPU
	 * 					time base. It has to be less or equal then
	 * 					the period time!
	 */
	public void update(int highTime) {
		// define high time and period
		US.PUT4(TPURAM0_A + diff + 0x10 * channel + 4, (highTime << 16) | period);
	}

}