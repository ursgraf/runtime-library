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
 * Driver for the QADC Module. The module has two queued analog/digital converters
 * with 16 channels each: QADC-A and QADC-B. The ADC has to be initialized before used.
 * All 16 channels of an ADC were converted every 1 ms and could be read with the method
 * <code>read(...)</code>. Allowed channel numbers are 0..3 and 48..59.
 */
public class QADC_AIN implements IntbMpc555HB {

	private static final int ADDR_OFFSET = 32;
	private static final int CCW_INIT = 0x00C0;
	private static final int END_OF_QUEUE = 0x003F;
	
	/**
	 * Returns the converted value of the given ADC channel.
	 * @param qadcA		<i>true</i> means ADC-A and <i>false</i> means ADC-B.
	 * @param channel	Channel to read. Allowed channel numbers are 0..3 and 48..59.
	 * @return			the converted value of the given channel.
	 */
	public static short read(boolean qadcA, int channel) {
		int channelOffset = getAddrForChn(channel);
		if (qadcA) {
			return US.GET2(RJURR_A + channelOffset + ADDR_OFFSET);
		} else {
			return US.GET2(RJURR_B + channelOffset + ADDR_OFFSET);
		}
	}
	
	/**
	 * Returns the address of the given channel. The returned value is 
	 * the correct address (channel number multiplied by two).
	 * 
	 * @param channel	channel of which the address will be calculated.
	 * @return			address of the given channel.
	 */
	private static int getAddrForChn(int channel) {
		if (channel >= 48) {
			channel -= 44;
		}
		return channel * 2;
	}
	
	/**
	 * Initialize the analog/digital converter.
	 * 
	 * @param qadcA	<i>true</i> means ADC-A and <i>false</i> means ADC-B.
	 */
	public static void init(boolean qadcA) {
		if (qadcA) {
			// user access
			US.PUT2(QADC64MCR_A, 0);
			
			// internal multiplexing, use ETRIG1 for queue1, QCLK = 40 MHz / (11+1 + 7+1) = 2 MHz
			US.PUT2(QACR0_A, 0x00B7);
			
			// queue2:
			// Periodic timer continuous-scan mode:
			// period = QCLK period x 2^11
			// Resume execution with the aborted CCW
			// queue2 begins at CCW + 2*16 (32 = ADDR_OFFSET)
			// This offset is used because of the DistSense driver
			US.PUT2(QACR2_A, 0x1890);

			// CCW for AN0 - AN3, max sample time
			// ADDR_OFFSET: Using queue2
			for (int i = 0; i <= 3; i++) {
				int addr = i * 2;
				US.PUT2(CCW_A + ADDR_OFFSET + addr, CCW_INIT + i);
			}
			
			// CCW for AN48 - AN59, max sample time
			// ADDR_OFFSET: Using queue2
			for (int i = 48; i <= 59; i++) {
				int addr = getAddrForChn(i);
				US.PUT2(CCW_A + ADDR_OFFSET + addr, CCW_INIT + i);
			}
			
			// end of queue
			US.PUT2(CCW_A + ADDR_OFFSET + 16 * 2, END_OF_QUEUE);
		} else {
			// user access
			US.PUT2(QADC64MCR_B, 0);
			
			// internal multiplexing, use ETRIG1 for queue1, QCLK = 40 MHz / (11+1 + 7+1) = 2 MHz
			US.PUT2(QACR0_B, 0x00B7);
			
			// queue2:
			// Periodic timer continuous-scan mode:
			// period = QCLK period x 2^11
			// Resume execution with the aborted CCW
			// queue2 begins at CCW + 2*16 (32 = ADDR_OFFSET)
			// This offset is used because of the DistSense driver
			US.PUT2(QACR2_B, 0x1890);

			// CCW for AN0 - AN3, max sample time
			// ADDR_OFFSET: Using queue2
			for (int i = 0; i <= 3; i++) {
				int addr = i * 2;
				US.PUT2(CCW_B + ADDR_OFFSET + addr, CCW_INIT + i);
			}
			
			// CCW for AN48 - AN59, max sample time
			// ADDR_OFFSET: Using queue2
			for (int i = 48; i <= 59; i++) {
				int addr = getAddrForChn(i);
				US.PUT2(CCW_B + ADDR_OFFSET + addr, CCW_INIT + i);
			}
			
			// end of queue
			US.PUT2(CCW_B + ADDR_OFFSET + 16 * 2, END_OF_QUEUE);
		}

	}

}