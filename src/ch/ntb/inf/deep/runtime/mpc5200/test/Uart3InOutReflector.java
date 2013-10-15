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

package ch.ntb.inf.deep.runtime.mpc5200.test;

import ch.ntb.inf.deep.runtime.mpc5200.Task;
import ch.ntb.inf.deep.runtime.mpc5200.driver.UART3;


/**
 * Demo for Uart3.
 */
public class Uart3InOutReflector extends Task {
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action() {
		// reflect input on stdin to stdout
		if (UART3.availToRead() > 0)
			UART3.write(UART3.read());
	}

	static {
		// Initialize UART3 (9600 8N1)
		UART3.start(9600, UART3.NO_PARITY, (short)8);
		UART3.write((byte)'x');
		UART3.write((byte)'1');
		
		// Create and install the demo task
		Task t = new Uart3InOutReflector();
		t.period = 0;
		Task.install(t);
	}
}
