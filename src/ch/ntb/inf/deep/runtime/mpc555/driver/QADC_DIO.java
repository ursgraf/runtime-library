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
 * Driver to use the QADC-A or QADC-B as digital I/O.<br>
 * Please prefer the <code>Mpiosm</code> module for I/O applications, because
 * it's not possible to use the same QADC for ADC and I/0 applications at the same time! <br>
 * To access the module use the <code>channel</code> numbers 0..15. Whereby port numbers
 * PQA0..PQA7 correspondents with the <code>channel</code> numbers 8..15. Alternatively it's
 * also possible to use the ADC channel numbers AN0-AN3 and AN48-AN49 for accessing the ports.<br>
 * <strong>IMPORTANT</strong>: Don't initialize the Ports PQB (PQB0..PQB7 respectively
 * AN0..AN3 and AN48..AN51) as output, because these are input ports only!
 */
public class QADC_DIO implements ntbMpc555HB{

	private static int getChannel(int channel){
		if(channel>15) channel -= 44;
		return channel;
	}

	/**
	 * Initialize a pin of the QADC_A respectively QADC_B port as I/O.<br>
	 * @param qadcA select module QADC_A (<code>true</code>) or QADC_B (<code>false</code>)
	 * @param channel select module pin 0-15 respectively 0-3 and 48-59
	 * @param out set I/O direction,<code>true</code> => output, <code>false</code> => input
	 * @see "The general description of this driver"
	 */
	public static void init(boolean qadcA, int channel, boolean out){
		short oldState;
		channel=getChannel(channel);
		if(qadcA){
			oldState=US.GET2(DDRQA_A);
			if(out) US.PUT2(DDRQA_A,oldState | 1 << channel);
			else US.PUT2(DDRQA_A,oldState & ~(1 << channel));
		}else{
			oldState=US.GET2(DDRQA_B);
			if(out) US.PUT2(DDRQA_B,oldState | 1 << channel);
			else US.PUT2(DDRQA_B,oldState & ~(1 << channel));
		}
	}
	
	/**
	 * Read the TTL-Signal value of the corresponding <code>channel</code>
	 * 
	 * @param qadcA select module QADC_A (<code>true</code>) or QADC_B (<code>false</code>)
	 * @param channel select module pin 0-15 respectively 0-3 and 48-59
	 * @return the TTL-Signal value of <code>channel</code>
	 */
	public static boolean get(boolean qadcA, int channel){
		channel=getChannel(channel);
		if(qadcA){
			return (US.GET2(PORTQA_A) & (1<< channel)) != 0;
		}else{
			return (US.GET2(PORTQA_B) & (1<< channel)) != 0;
		}
	}
	
	/**
	 * Set the TTL-Signal value <code>val</code> to the corresponding <code>channel</code>
	 *  
	 * @param qadcA select module QADC_A (<code>true</code>) or QADC_B (<code>false</code>)
	 * @param channel select module pin 0-15 respectively 0-3 and 48-59
	 * @param val the TTL-Signal value
	 */
	public static void set(boolean qadcA, int channel, boolean val){
		channel=getChannel(channel);
		if(qadcA){
			if(val) US.PUT2(PORTQA_A, US.GET2(PORTQA_A) | (1 << channel));
			else US.PUT2(PORTQA_A, US.GET2(PORTQA_A) & ~(1 << channel));
		}else{
			if(val) US.PUT2(PORTQA_B, US.GET2(PORTQA_B) | (1 << channel));
			else US.PUT2(PORTQA_B, US.GET2(PORTQA_B) & ~(1 << channel));
		}
	}

}