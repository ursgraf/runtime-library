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


/* Changes:
 * 01.03.2011	NTB/MZ	methods renamed: out -> set, in -> get
 * 09.12.2009	NTB/SP	creation
 */
/**
* Driver to use the MPWM sub module as digital I/O.
* 
*/
public class MPWMSM_DIO implements IntbMpc555HB {

	int channel;
	
	/**
	 * Create a digital I/O on a MPWM pin.
	 * 
	 * @param channel Select module channel. Allowed values are 0..3, 6..9 or 0..3, 16-19.
	 * @param out Set I/O direction, <code>true</code> =&gt; output, <code>false</code> =&gt; input.
	 */
	public MPWMSM_DIO(int channel, boolean out) {
		if(channel >=6  && channel <= 9) channel += 10;
		this.channel = channel;
		if(out) US.PUT2(MPWMSM0SCR + channel * 8, 0x4000);
		else US.PUT2(MPWMSM0SCR + channel * 8, 0x0);
	}
	
	
	/**
	 * Set the TTL-Signal value <code>val</code> of the output.
	 * 
	 * @param val The TTL-Signal value
	 */
	public void set(boolean val) {
		if(val) US.PUT2(MPWMSM0SCR + channel * 8, 0x4800);
		else US.PUT2(MPWMSM0SCR + channel * 8, 0x4000);
	}
	
	/**
	 * 
	 * Read the TTL-Signal of the input.
	 * 
	 * @return The TTL value of the corresponding channel.
	 */
	public boolean get() {
		return (US.GET2(MPWMSM0SCR + channel * 8) & 0x8000) != 0;
	}
}
