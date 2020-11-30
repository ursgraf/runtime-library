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

package org.deepjava.runtime.mpc555.driver;

import org.deepjava.runtime.mpc555.IntbMpc555HB;
import org.deepjava.unsafe.US;

/* Changes:
 * 01.03.2011	NTB/MZ	methods renamed: out -> set, in -> get
 * 22.02.2011	NTB/MZ	renamed and adapted to the new deep environment
 * 15.02.2007	NTB/SP	assigned to java
 * 09.02.2006	NTB/HS	creation
 */

/**
 * Driver to use pins on the MPIOSM (MIOS 16-bit parallel port I/O submodule)
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
public class MPIOSM_DIO implements IntbMpc555HB, DigitalOutput {

	public static final boolean OUTPUT = true;
	public static final boolean INPUT = false;
	
	int channel;
	
	/**
	 * Create a pin as in- or output.<br>
	 * 
	 * @param channel	MPIOSM pin to initialize. Allowed numbers are 0..15.
	 * @param out		Pin usage: <strong>true</strong> configures the pin as output, <strong>false</strong> as input. 
	 */
	public MPIOSM_DIO(int channel, boolean out) {
		this.channel = channel;
		short s = US.GET2(MPIOSMDDR);
		if(out) s |= (1 << channel);
		else s &= ~(1 << channel);
		US.PUT2(MPIOSMDDR,s);
	}

	/* (non-Javadoc)
	 * @see org.deepjava.runtime.mpc555.driver.DigitalInput#get()
	 */
	@Override
	public boolean get() {
		return (US.GET2(MPIOSMDR) & (1 << channel)) != 0;
	}

	/* (non-Javadoc)
	 * @see org.deepjava.runtime.mpc555.driver.DigitalOutput#set(boolean)
	 */
	@Override
	public void set(boolean val) {
		short s = US.GET2(MPIOSMDR);
		if(val) s |= (1 << channel);
		else s &= ~(1 << channel);
		US.PUT2(MPIOSMDR, s);
	}

	/**
	 * Sets the direction of a given pin to input or output.<br>
	 * 
	 * @param out		Pin usage: <strong>true</strong> configures the pin as output, <strong>false</strong> as input. 
	 */
	public void dir(boolean out) {
		short s = US.GET2(MPIOSMDDR);
		if(out) s |= (1 << channel);
		else s &= ~(1 << channel);
		US.PUT2(MPIOSMDDR,s);
	}

}