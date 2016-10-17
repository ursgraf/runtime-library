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
 * Driver to use the QADC-A or QADC-B as digital I/O.<br>
 * Please prefer the {@link ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO} driver for I/O applications, because
 * it's not possible to use the same channel on a QADC for ADC and I/0 at the same time.<br>
 * The channels are grouped into two groups of 8 channels each, PortA and PortB.<br>
 * PortB: PQB0(AN0), PQB1(AN1), PQB2(AN2), PQB3(AN3), PQB4(AN48), PQB5(AN49), PQB6(AN50), PQB7(AN51)<br>
 * PortA: PQA0(AN52), PQA1(AN53), PQA2(AN54), PQA3(AN55), PQA4(AN56), PQA5(AN57), PQA6(AN58), PQA7(AN59)<br>
 * 
 * <strong>IMPORTANT</strong>: The pins on PortB work solely as digital inputs. 
 * The pins on PortA function as digital I/O. 
 */
public class QADC_DIO implements IntbMpc555HB{

	private static int getChannel(int channel){
		if(channel>15) channel -= 44;
		return channel;
	}

	/**
	 * Initialize a pin of the QADC port as I/O.
	 * 
	 * @param qadcA Select module QADC_A (<code>true</code>) or QADC_B (<code>false</code>)
	 * @param channel Select pin 0-15 (or 0-3 and 48-59)
	 * @param out Set I/O direction,<code>true</code> =&gt;output, <code>false</code> =&gt; input
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
	 * @param qadcA Select module QADC_A (<code>true</code>) or QADC_B (<code>false</code>)
	 * @param channel Select pin 0-15 (or 0-3 and 48-59)
	 * @return Logical level of <code>channel</code>
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
	 * Set the logical level <code>val</code> to the corresponding <code>channel</code>
	 *  
	 * @param qadcA Select module QADC_A (<code>true</code>) or QADC_B (<code>false</code>)
	 * @param channel Select pin 0-15 respectively 0-3 and 48-59
	 * @param val Logical level
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