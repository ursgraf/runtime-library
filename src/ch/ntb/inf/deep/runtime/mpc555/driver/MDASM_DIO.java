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
