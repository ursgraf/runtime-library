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

package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Interrupt;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;

/* changes:
 * 09.03.2011	NTB/Urs Graf	creation
 */

/**
 * Demonstrates the usage of some external interrupts
 */
public class ExtInterruptDemo extends Interrupt {
	MPIOSM_DIO out;
	static int count;

	public void action() {
		out.set(true);
		count++;
		System.out.print("interrupt");
		for (int i = 0; i < 50000; i++);
		out.set(false);
	}

	public ExtInterruptDemo(int pin) {
		out = new MPIOSM_DIO(pin, true);
		out.set(false);
	}

	static {
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = System.out;
		System.out.println("start");
		
		Interrupt int5 = new ExtInterruptDemo(5); 
		Interrupt int6 = new ExtInterruptDemo(6); 
		Interrupt.install(int5, 5, false);
		Interrupt.install(int6, 6, false);
	}
}
	

