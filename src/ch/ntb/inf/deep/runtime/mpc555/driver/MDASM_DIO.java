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
 * 08.05.2013	NTB/Urs Graf	creation
 */
/**
* Driver to use the MDASM sub module as digital I/O.
* 
*/
public class MDASM_DIO implements IntbMpc555HB {

	/**
	 * Initialize an MDASM pin as digital I/O.
	 * @param channel select module channel 11-15 or 27-31
	 * @param out set I/O direction, <code>true</code> => output, <code>false</code> => input.
	 */
	public static void init(int channel, boolean out) {
		channel -= 11;
		if (out) US.PUT2(MDASM11SCR + channel * 8, 8);
		else US.PUT2(MDASM11SCR + channel * 8, 3);
	}
	
	
	/**
	 * Set the TTL-Signal value <code>val</code> to the corresponding channel.
	 * @param channel channel select module channel 11-15 or 27-31
	 * @param val the TTL-Signal value
	 */
	public static void set(int channel, boolean val) {
		channel -= 11;
		if (val) US.PUT2(MDASM11SCR + channel * 8, 0x408);
		else US.PUT2(MDASM11SCR + channel * 8, 0x208);
	}
	
	/**
	 * 
	 * Read the TTL-Signal of the corresponding channel.
	 * @param channel channel select module channel 11-15 or 27-31
	 * @return the TTL value of the corresponding channel.
	 */
	public static boolean get(int channel) {
		channel -= 11;
		return (US.GET2(MDASM11SCR + channel * 8) & 0x8000) != 0;
	}
}
