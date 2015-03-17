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
 * Driver to using the TPU for generating pulse width modulated (PWM) signals.<br />
 * All 32 channels (2 x 16) can be used for this. All time data have to be a multiple of the
 * TPU time base (806 ns).
 */
public class TPU_PWM implements IntbMpc555HB{


	/** TPU time base in nano seconds [ns]. */
	public static final int tpuTimeBase = 806;

	/**
	 * Initialize a TPU channels for generating PWM signals.<br>
	 * Every channel has to be initialized before using it!
	 * Remember: <code>period</code> and <code>highTime</code> are
	 * 32 bit values!<br>
	 * The period time should be defined as an integer constant.
	 * Example for a period time of <i>T = 50 \u00b5s (f = 20 kHz)</i>:
	 * <code>private final int pwmPeriod = 50000 / TpuTimeUnit;</code>
	 * 
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param channel	TPU channel to initialize. Allowed values
	 * 					are 0..15.
	 * @param period	Period time as a multiple of the TPU time base 
	 * @param highTime	PWM signal high time as a multiple of the TPU
	 * 					time base. It has to be less or equal then
	 * 					the period time!
	 */
	public static void init(boolean tpuA, int channel, int period, int highTime) {
		int shift, tpuAdr, s;
		if(tpuA){
			TPUA.init();
			shift = (channel * 4) % 16;
			tpuAdr = CFSR3_A - (channel / 4) * 2;			
			s = US.GET2(tpuAdr);
			s &= ~(0xF << shift);
			s |= 3 << shift;
			US.PUT2(tpuAdr,(short) s);
			//Force pin hig, use TCR1
			tpuAdr = TPURAM0_A +0x10 * channel;
			US.PUT2(tpuAdr, 0x91 );
			//Define high time
			US.PUT2(tpuAdr + 4, highTime);
			//Define time of period
			US.PUT2(tpuAdr  + 6, period);
			//Request initialization
			tpuAdr = HSRR1_A - (channel / 8) * 2;
			shift = (channel * 2) % 16;
			s = US.GET2(tpuAdr);
			s &= ~(0x3 << shift);
			s |= 2 << shift;
			US.PUT2(tpuAdr,s);
			//set priority low
			tpuAdr = CPR1_A - (channel / 8) * 2;
			s = US.GET2(tpuAdr);
			s &= ~(0x3 << shift);
			s |= 1 << shift;
			US.PUT2(tpuAdr,s);
		}
		else {
			TPUB.init();
			shift = (channel * 4) % 16;
			tpuAdr = CFSR3_B - (channel / 4) * 2;			
			s = US.GET2(tpuAdr);
			s &= ~(0xF << shift);
			s |= 3 << shift;
			US.PUT2(tpuAdr,(short) s);
			//Force pin hig, use TCR1
			tpuAdr = TPURAM0_B +0x10 * channel;
			US.PUT2(tpuAdr, 0x91 );
			//Define high time
			US.PUT2(tpuAdr + 4, highTime);
			//Define time of period
			US.PUT2(tpuAdr  + 6, period);
			//Request initialization
			tpuAdr = HSRR1_B - (channel / 8) * 2;
			shift = (channel * 2) % 16;
			s = US.GET2(tpuAdr);
			s &= ~(0x3 << shift);
			s |= 2 << shift;
			US.PUT2(tpuAdr,s);
			//set priority low
			tpuAdr = CPR1_B - (channel / 8) * 2;
			s = US.GET2(tpuAdr);
			s &= ~(0x3 << shift);
			s |= 1 << shift;
			US.PUT2(tpuAdr,s);
		}
	}

	/**
	 * Update the parameters of a PWM signal at a TPU channel.<br>
	 * Every channel has to be initialized before using it!
	 * 
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param channel	TPU channel to initialize. Allowed values
	 * 					are 0..15.
	 * @param period	Period time as a multiple of the TPU time base 
	 * @param highTime	PWM signal high time as a multiple of the TPU
	 * 					time base. It has to be less or equal then
	 * 					the period time!
	 */
	public static void update(boolean tpuA, int channel, int period,
			int highTime) {
		int adr ;
		if(tpuA){
			//Define high time
			adr = TPURAM0_A + 0x10 * channel;
			US.PUT2(adr + 4, highTime);
			//Define time of period
			US.PUT2(adr + 6, period);
		}else{
			//Define high time
			adr = TPURAM0_B + 0x10 * channel;
			US.PUT2(adr + 4, highTime);
			//Define time of period
			US.PUT2(adr + 6, period);
		}
	}
}