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
 * 14.05.2013	NTB/Urs Graf	creation
 */
/**
* Driver to use the Mios Modulus Counter to count external events.
* Attention: Using this driver may lead to MDASM module to be no longer usable for double action function 
*/
public class MMCSM_COUNT implements IntbMpc555HB {
	int channel;

	/**
	 * Create a counter on a MMCSM pin.
	 * @param channel select module channel 11 or 13
	 */
	public MMCSM_COUNT(int channel) {
		this.channel = channel;
		if (channel == 11) {
			US.PUT2(MMCSM6CNT, 0);
			US.PUT2(MMCSM6ML, 0);
			US.PUT2(MMCSM6SCR, 0x0200);	
		} else if (channel == 13) {
			US.PUT2(MMCSM22CNT, 0);
			US.PUT2(MMCSM22ML, 0);
			US.PUT2(MMCSM22SCR, 0x0200);	
		}
	}
	
	
	/**
	 * Read the count value.
	 * @return the count value of the corresponding channel.
	 */
	public short getCount() {
		if (channel == 11) {
			return US.GET2(MMCSM6CNT);	
		} else if (channel == 13) {
			return US.GET2(MMCSM22CNT);	
		}
		return 0;
	}
}
