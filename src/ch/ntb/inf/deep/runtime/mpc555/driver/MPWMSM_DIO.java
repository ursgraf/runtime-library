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

import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;


/* Changes:
 * 01.03.2011	NTB/MZ	methods renamed: out -> set, in -> get
 * 09.12.2009	NTB/SP	creation
 */
/**
* Driver to use the MPWM sub module as digital I/O.
* 
*/
public class MPWMSM_DIO implements IntbMpc555HB {

	/**
	 * Initialize an MPWM pin as digital I/O.
	 * @param channel select module channel 0-3 and 6-9 or 0-3 and 16-19
	 * @param out set I/O direction, <code>true</code> => output, <code>false</code> => input.
	 */
	public static void init(int channel, boolean out){
		if(channel >=6  && channel <= 9) channel += 10;
		if(out) US.PUT2(MPWMSM0SCR + channel * 8, 0x4000);
		else US.PUT2(MPWMSM0SCR + channel * 8, 0x0);
	}
	
	
	/**
	 * Set the TTL-Signal value <code>val</code> to the corresponding channel.
	 * @param channel channel select module channel 0-3 and 6-9 or 0-3 and 16-19
	 * @param val the TTL-Signal value
	 */
	public static void set(int channel, boolean val){
		if(channel >=6  && channel <= 9) channel += 10;
		if(val) US.PUT2(MPWMSM0SCR + channel * 8, 0x4800);
		else US.PUT2(MPWMSM0SCR + channel * 8, 0x4000);
	}
	
	/**
	 * 
	 * Read the TTL-Signal of the corresponding channel.
	 * @param channel channel select module channel 0-3 and 6-9 or 0-3 and 16-19
	 * @return the TTL value of the corresponding channel.
	 */
	public static boolean get(int channel){
		if(channel >=6  && channel <= 9) channel += 10;
		return (US.GET2(MPWMSM0SCR + channel * 8) & 0x8000) != 0;
	}
}
