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
 * 22.02.2011	NTB/MZ	renamed and adapted to the new deep environment
 * 15.02.2007	NTB/SP	assigned to java
 * 09.02.2006	NTB/HS	creation
 */

/**
 * Driver to use the MPIOSM (MIOS 16-bit parallel port I/O submodule)
 * as digital in- and outputs. The MPIOSM provides 16 independent I/Os (0..15).
 * <br><br>
 * <strong>Additional informations for using this driver with the NTB MPC555
 * header board:</strong><br>
 * <ul>
 * <li>Pin numbers 13 and 14 of the MPIOSM are used for the CAN-Controller on the
 * header board and must not be used if the controller is assembled!</li>
 * <li>MPIOSM pin 15 is connected to the led D1 on the header board and can only be
 * used as an output!</li>
 * </ul>
 * For further informations please read the corresponding documentation on the <i>NTB
 * Infoportal</i>.
 */
public class MPIOSM_DIO implements IntbMpc555HB {

	public static final boolean OUTPUT = true;
	public static final boolean INPUT = false;
	
	/**
	 * Initialize a pin as in- or output.<br>
	 * You have to initialize a pin before you can use it!
	 * 
	 * @param channel	MPIOSM pin to initialize. Allowed numbers are 0..15.
	 * @param out		Pin usage: <strong>true</strong> configures the pin as output, <strong>false</strong> as input. 
	 */
	public static void init(int channel, boolean out) {
		short s = US.GET2(MPIOSMDDR);
		if(out) s |= (1 << channel);
		else s &= ~(1 << channel);
		US.PUT2(MPIOSMDDR,s);
	}

	/**
	 * Returns the current state of the TTL signal at the given pin.
	 * 
	 * @param channel	MPIOSM pin to capture. Allowed numbers are 0..15.
	 * @return the current state of the TTL at the given pin. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public static boolean get(int channel) {
		return (US.GET2(MPIOSMDR) & (1 << channel)) != 0;
	}

	/**
	 * Set the TTL signal at the given pin.
	 * 
	 * @param channel	MPIOSM pin to set. Allowed numbers are 0..15.
	 * @param val		Value to set. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public static void set(int channel, boolean val) {
		short s = US.GET2(MPIOSMDR);
		if(val) s |= (1 << channel);
		else s &= ~(1 << channel);
		US.PUT2(MPIOSMDR, s);
	}

}