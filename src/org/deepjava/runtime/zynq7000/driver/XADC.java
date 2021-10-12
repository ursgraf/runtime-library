/*
 * Copyright 2011 - 2021 NTB University of Applied Sciences in Technology
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

package org.deepjava.runtime.zynq7000.driver;

import org.deepjava.runtime.zynq7000.Izynq7000;
import org.deepjava.unsafe.US;

/* CHANGES:
 * 8.10.2021 OST/UG	creation
 */

/**
 * Driver for the internal analog to digital converter XADC. 
 * The XADC is configured for auto sequence mode for 4 channels. In this mode
 * all 4 channels are repetitively sampled within 12 bit accuracy.  
 */
public class XADC implements Izynq7000 {

	private static final int base = 0x7b000000; 	// Please consult the memory map of your PL configuration

	/**
	 * Read the value of the given channel number
	 * 
	 * @param channel	channel number
	 * @return converted value
	 */
	public static int read(int channel) {
		switch (channel) {
		case 0:
			return US.GET4(base + 0x240 + 4) >> 4;	// XADC channel 1
		case 1:
			return US.GET4(base + 0x240 + 8) >> 4;	// XADC channel 2
		case 2:
			return US.GET4(base + 0x240 + 12) >> 4;	// XADC channel 3
		case 3:
			return US.GET4(base + 0x240 + 32) >> 4;	// XADC channel 8
		default:
			return 0;
		}
	}

}
