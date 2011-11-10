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

/* changes:
 * 08.04.2011	NTB/MZ	methods in/out renamed to get/set
 * 15.02.2007	NTB/SP	adapted to java
 * 08.02.2006	NTB/HS	stub creation
 */
/**
 * Driver to use a channel of the TPU (A or B) as a GPIO.<br/>
 * Each 16 channels of both time processing units can be used as
 * general purpose in- or output.
 */
public class TPU_DIO implements ntbMpc555HB {

	/**
	 * Initialize an channel as a general purpose in- our output. 
	 * 
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param channel	TPU channel to initialize. Allowed values are 0..15.
	 * @param out		<code>true</code> initializes the channel as a digital output.
	 * 					<code>false</code> initializes the channel as a digital input.
	 */
	public static void init(boolean tpuA, int channel, boolean out) {
		if(tpuA){
			//function code (2) for DIO
			short s = US.GET2(CFSR3_A - (channel / 4) * 2);
			int shiftl = (channel % 4) * 4;
			s &= ~(7 << shiftl);
			s |= (2 << shiftl);
			US.PUT2(CFSR3_A - (channel / 4) * 2,s);
			
			//Update on transition for inputs, dosen't have any effect for outputs
			s = US.GET2(HSQR1_A - (channel / 8) * 2);
			shiftl = (channel % 8) * 2;
			s &= ~(3 << shiftl);
			US.PUT2(HSQR1_A -(channel / 8) * 2, s);
			
			if(out){
				US.PUT2(TPURAM0_A + 0x10 * channel, 0x3);
			}else{
				s = US.GET2(HSQR1_A - (channel / 8) * 2);
				s &= ~(3 << shiftl);
				US.PUT2(HSQR1_A -(channel / 8) * 2, s);
				US.PUT2(TPURAM0_A + 0x10 * channel, 0xF);
			}
			
			//Request initialization
			s = US.GET2(HSRR1_A -(channel / 8)* 2);
			s |= (3 <<shiftl);
			US.PUT2(HSRR1_A -(channel / 8)* 2, s);
			
			//Set priority low
			s = US.GET2(CPR1_A - (channel / 8)* 2);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(CPR1_A - (channel / 8) * 2,s);
		}else{
			//function code (2) for DIO
			short s = US.GET2(CFSR3_B - (channel / 4) * 2);
			int shiftl = (channel % 4) * 4;
			s &= ~(7 << shiftl);
			s |= (2 << shiftl);
			US.PUT2(CFSR3_B - (channel / 4) * 2,s);
			
			//Update on transition for inputs, dosen't have any effect for outputs
			s = US.GET2(HSQR1_B - (channel / 8) * 2);
			shiftl = (channel % 8) * 2;
			s &= ~(3 << shiftl);
			US.PUT2(HSQR1_B -(channel / 8) * 2, s);
			
			if(out){
				US.PUT2(TPURAM0_B + 0x10 * channel, 0x3);
			}else{
				s = US.GET2(HSQR1_B - (channel / 8) * 2);
				s &= ~(3 << shiftl);
				US.PUT2(HSQR1_B -(channel / 8) * 2, s);
				US.PUT2(TPURAM0_B + 0x10 * channel, 0xF);
			}
			
			//Request initialization
			s = US.GET2(HSRR1_B -(channel / 8)* 2);
			s |= (3 <<shiftl);
			US.PUT2(HSRR1_B -(channel / 8)* 2, s);
			
			//Set priority low
			s = US.GET2(CPR1_B - (channel / 8)* 2);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(CPR1_B - (channel / 8) * 2,s);
		}
	}

	/**
	 * Returns the current state of the TTL signal at the given TPU channel.
	 * 
	 * @param channel	TPU pin to capture. Allowed numbers are 0..15.
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @return 			the current state of the TTL at the given pin. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public static boolean get(boolean tpuA, int channel) {
		if(tpuA){
			return (US.GET2(TPURAM0_A + 0x10 * channel + 2) & (1 << 15)) != 0; 
		}else{
			return (US.GET2(TPURAM0_B  + 0x10 * channel + 2) & (1 << 15)) != 0; 
		}
	}

	/**
	 * Set the TTL signal at the given pin.
	 * 
	 * @param channel	TPU pin to set. Allowed numbers are 0..15.
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param val		Value to set. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public static void set(boolean tpuA, int channel, boolean val) {
		if(tpuA){
			//Disable all Interrupts
			short sh = US.GET2(CISR_A);
			US.PUT2(CISR_A,(short)0);
			
			short s = US.GET2(HSRR1_A - ((channel / 8) * 2));
			int shiftl = (channel % 8) * 2;
			s &= ~(3 << shiftl);
			if(val) s |= (1 << shiftl);
			else s |= (2 << shiftl);
			US.PUT2(HSRR1_A - ((channel / 8) * 2), s);
			
			//Restore Interrupts
			US.PUT2(CISR_A, sh);
		}else{
			//Disable all Interrupts
			short sh = US.GET2(CISR_B);
			US.PUT2(CISR_B,(short)0);
			
			int shiftl = (channel % 8) * 2;
			short s = US.GET2(HSRR1_B - ((channel / 8) * 2));
			s &= ~(3 << shiftl);
			if(val) s |= (1 << shiftl);
			else s |= (2 << shiftl);
			US.PUT2(HSRR1_B - ((channel / 8) * 2), s);
			
			//Restore Interrupts
			US.PUT2(CISR_B, sh);
			
		}
	}

}