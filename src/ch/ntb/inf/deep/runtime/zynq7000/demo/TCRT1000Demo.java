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

import ch.ntb.inf.deep.runtime.zynq7000.driver.TCRT1000;
import ch.ntb.inf.deep.runtime.zynq7000.driver.UART;
import ch.ntb.inf.deep.runtime.arm32.Task;

/* CHANGES:
 * 28.04.2020 NTB/UG	creation
 */

/**
 * Demo application for the TCRT1000 driver.
 * The application reads periodically the values of four sensors
 * and print them every second to the UART1.
 */
public class TCRT1000Demo extends Task {
	
	static TCRT1000 sense;
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.arm32.Task#action()
	 */
	public void action() {
		for(int i = 0; i < 4; i++) {
			System.out.print(sense.read(i));
			System.out.print('\t');
		}
		System.out.println();
	}
	
	static {
		// Initialize TCRT1000 driver for 4 sensors and start reading values
		sense = TCRT1000.getInstance();
		sense.init(4, 4, 3, 2, 1, 0); // initialize 4 sensors
		sense.start();
		
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		System.out.print("TCRT1000 demo");
		
		// Create and install demo task
		Task demoTask = new TCRT1000Demo();
		demoTask.period = 1000;
		Task.install(demoTask);
	}
}
