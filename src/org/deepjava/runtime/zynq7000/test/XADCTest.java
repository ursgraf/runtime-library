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

package org.deepjava.runtime.zynq7000.test;

import java.io.PrintStream;
import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.zynq7000.driver.UART;
import org.deepjava.runtime.zynq7000.driver.XADC;

/* CHANGES:
 * 28.04.2020 NTB/UG	creation
 */

/**
 * Demo application for the TCRT1000 driver.
 * The application reads periodically the values of four sensors
 * and print them every second to the UART1.
 */
public class XADCTest extends Task {
	
	/* (non-Javadoc)
	 * @see org.deepjava.runtime.arm32.Task#action()
	 */
	public void action() {
//		for(int i = 0; i < 4; i++) {
//			System.out.print(sense.read(i));
//			System.out.print('\t');
//		}
		System.out.print(XADC.read(0));
		System.out.print('\t');
		System.out.print(XADC.read(1));
		System.out.print('\t');
		System.out.print(XADC.read(2));
		System.out.print('\t');
		System.out.println(XADC.read(3));
	}
	
	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		System.out.println("XADC test");
		
		// Initialize TCRT1000 driver for 4 sensors and start reading values
		
		// Create and install demo task
		Task t = new XADCTest();
		t.period = 1000;
		Task.install(t);
	}
}

