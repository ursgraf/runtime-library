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

/**
 * Driver for the MPWM module for generating PWM signals
 */
public class MPWMSM_PWM implements ntbMpc555HB {

	static public final int TIME_BASE = 400;	// time base in ns
	
	/** 
	 * Initialize a pin of the MPWM sub module for generating PWM signals.
	 * The period and high time have to be a multiple of the TIME_BASE ({@value TIME_BASE}ns)!
	 * @param channel	Module channel to initialize. Allowed channels are 0..3 and 6..9 or 0..3 and 16..19.
	 * @param period	Period of the PWM signal. Have to be a multiple of the MPWMSM time base.
	 * @param highTime	High time of the PWM signal. Have to be a multiple of the MPWMSM time base.
	 */
	public static void init(int channel, int period, int highTime){
		if(channel >=6  && channel <= 9) channel += 10;
		US.PUT2(MPWMSM0SCR + channel * 8, 0x04FC);	// enable, prescaler = 4 -> 400ns 
		US.PUT2(MPWMSM0PERR + channel * 8, period);	// set period 
		US.PUT2(MPWMSM0PULR + channel * 8, highTime);	//set pulse width 
	}
	
	/** 
	 * Update the PWM signals at the given channel.
	 * The period and high time have to be a multiple of the TIME_BASE ({@value TIME_BASE}ns)!
	 * @param channel	Module channel to update. Allowed channels are 0..3 and 6..9 or 0..3 and 16..19.
	 * @param period	Period of the PWM signal. Have to be a multiple of the MPWMSM time base!
	 * @param highTime	High time of the PWM signal. Have to be a multiple of the MPWMSM time base!
	 */
	public static void update(int channel, int period, int highTime){
		if(channel >=6  && channel <= 9) channel += 10;
		US.PUT2(MPWMSM0SCR + channel * 8, 0x04FC);	// enable, prescaler = 4 -> 400ns 
		US.PUT2(MPWMSM0PERR + channel * 8, period);	// set period 
		US.PUT2(MPWMSM0PULR + channel * 8, highTime);	//set pulse width 
	}

	static{
		US.PUT2(MIOS1MCR, 0);		// enable supervisor access 
		US.PUT2(MCPSMSCR, 0x8004);	// prescaler = 4, clock for MIOS = system clock / 4 = 10MHz
	}
}