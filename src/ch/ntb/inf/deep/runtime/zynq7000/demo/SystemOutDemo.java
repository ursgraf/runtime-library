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

package ch.ntb.inf.deep.runtime.zynq7000.demo;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.zynq7000.driver.UART;
import ch.ntb.inf.deep.runtime.arm32.Task;


/**
 * Demo for System.out using UART1.
 * This application simply outputs the character '.' once per second over the UART1.
 */
public class SystemOutDemo extends Task {
	static int count;
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.arm32.Task#action()
	 */
	public void action() {
		// Write a single character to the stdout
		count++;
		System.out.print('.');
	}

	static {
		// Initialize UART (115200 8N1)
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		
		// Use the UART for stdout and stderr
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		
		// Print a string to the stdout
		System.out.print("System.out demo (UART)");
		
		// Create and install the demo task
		Task t = new SystemOutDemo();
		t.period = 1000;
		Task.install(t);
	}
}
