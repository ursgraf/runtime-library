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
 * This driver configures one of the QADC modules (QADC-A or QADC-B) for 
 * analog input sampling operation.<br>
 * Both modules comprise of 16 input channels each. If one of the modules
 * is initialized with the method <code>init(...)</code> all its 16 channels 
 * ADC are repetitively sampled every 1 ms and could be read with the method
 * <code>read(...)</code>.<br>
 * The channels are grouped into two groups of 8 channels each, PortA and PortB.<br>
 * PortB: PQB0(AN0), PQB1(AN1), PQB2(AN2), PQB3(AN3), PQB4(AN48), PQB5(AN49), PQB6(AN50), PQB7(AN51)<br>
 * PortA: PQA0(AN52), PQA1(AN53), PQA2(AN54), PQA3(AN55), PQA4(AN56), PQA5(AN57), PQA6(AN58), PQA7(AN59)<br>
 * <br>
 * One or several of the channels can be configured for digital I/O with
 * {@link ch.ntb.inf.deep.runtime.mpc555.driver.QADC_DIO} while the remaining channels 
 * still being used for analog input sampling. However, the method
 * <code>read(...)</code> will not return meaningful data on the channels configured for
 * digital I/O.
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
			
			// internal multiplexing, QCLK = 40 MHz / (11+1 + 7+1) = 2 MHz
			US.PUT2(QACR0_A, 0x00B7);
			
			// use queue2
			// interval timer continuous-scan mode with period = QCLK period x 2^11
			// resume execution with the aborted CCW
			// queue2 begins at position 16 in the CCW
			// this offset allows for other functions to use queue1 at lower positions
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
			
			// internal multiplexing, QCLK = 40 MHz / (11+1 + 7+1) = 2 MHz
			US.PUT2(QACR0_B, 0x00B7);
			
			// use queue2
			// interval timer continuous-scan mode with period = QCLK period x 2^11
			// resume execution with the aborted CCW
			// queue2 begins at position 16 in the CCW
			// this offset allows for other functions to use queue1 at lower positions
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