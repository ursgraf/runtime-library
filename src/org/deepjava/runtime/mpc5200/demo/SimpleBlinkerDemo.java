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

package org.deepjava.runtime.mpc5200.demo;

import org.deepjava.runtime.mpc5200.Impc5200;
import org.deepjava.runtime.ppc32.Task;
import org.deepjava.unsafe.US;

/**
 * Simple blinker application demo. Blinks LED on Phytec evaluation board every second.
 * 
 * @author Urs Graf
 */
public class SimpleBlinkerDemo extends Task implements Impc5200 {

	public void action(){
		US.PUT4(GPWOUT, US.GET4(GPWOUT) ^ 0x80000000);
	}
	
	static {
		US.PUT4(GPWER, US.GET4(GPWER) | 0x80000000);	// enable GPIO use
		US.PUT4(GPWDDR, US.GET4(GPWDDR) | 0x80000000);	// make output
		
		// Create and install the task
		SimpleBlinkerDemo t = new SimpleBlinkerDemo();
		t.period = 1000;
		Task.install(t);
	}

}

