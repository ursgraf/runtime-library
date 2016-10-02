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
 * 08.05.2013	NTB/Urs Graf	creation
 */
/**
* Driver to use the MDASM sub module as digital I/O.
* 
*/
public class MDASM_DIO implements IntbMpc555HB {

	int channel;

	/**
	 * Create a digital I/O on a MDASM pin.
	 * @param channel select module channel 11-15 or 27-31
	 * @param out set I/O direction, <code>true</code> =&gt; output, <code>false</code> =&gt; input.
	 */
	public MDASM_DIO(int channel, boolean out) {
		this.channel = channel - 11;
		if (out) US.PUT2(MDASM11SCR + this.channel * 8, 8);
		else US.PUT2(MDASM11SCR + this.channel * 8, 3);
	}
	
	
	/**
	 * Set the TTL signal value <code>val</code> of this digital I/O.
	 * @param val the TTL-Signal value
	 */
	public void set(boolean val) {
		if (val) US.PUT2(MDASM11SCR + channel * 8, 0x408);
		else US.PUT2(MDASM11SCR + channel * 8, 0x208);
	}
	
	/**
	 * 
	 * Read the TTL signal of this digital I/O.
	 * @return the TTL value of the corresponding channel.
	 */
	public boolean get() {
		return (US.GET2(MDASM11SCR + channel * 8) & 0x8000) != 0;
	}
}
