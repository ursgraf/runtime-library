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

import ch.ntb.inf.deep.runtime.zynq7000.Decrementer;
import ch.ntb.inf.deep.runtime.zynq7000.driver.UART;

/* changes:
 * 31.10.2019	NTB/Urs Graf	creation
 */

/**
 * Simple demo application how to use the <code>Decrementer</code>.
 * This application simply outputs the character 'x' once
 * per second over the SCI2.
 */
public class DecrementerDemo extends Decrementer {
	static DecrementerDemo decTest; 
	
	/**
	 * Outputs 'x' once a second.
	 */
	public void action () {
		System.out.print('x');
	}
	
	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		System.out.print("System.out demo (UART)");
		
		// Create and install the Decrementer demo
		decTest = new DecrementerDemo(); 
		Decrementer.install(decTest, (int)1e9);
	}
}