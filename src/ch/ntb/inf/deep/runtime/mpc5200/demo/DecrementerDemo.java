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

package ch.ntb.inf.deep.runtime.mpc5200.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc5200.driver.UART3;
import ch.ntb.inf.deep.runtime.ppc32.Decrementer;

/* changes:
 * 24.8.2012	NTB/Urs Graf		creation
 */

/**
 * Simple demo application how to use the Decrementer.
 * This application simply outputs the character 'x' 
 * over the UART3 for each decrementer exception.
 */
public class DecrementerDemo extends Decrementer {
	static DecrementerDemo decTest; 
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc5200.Decrementer#action()
	 */
	public void action () {
		System.out.print('x');
	}
	
	static {
		// Initialize the UART3 (9600 8N1) and use it for System.out
		UART3.start(9600, UART3.NO_PARITY, (short)8);
		System.out = new PrintStream(UART3.out);
		
		// Create and install the Decrementer demo
		System.out.println("decrementer started");
		decTest = new DecrementerDemo(); 
		decTest.decPeriodUs = 33000000;	// gives 1s with XLB clock = 132MHz
		Decrementer.install(decTest);
	}
}