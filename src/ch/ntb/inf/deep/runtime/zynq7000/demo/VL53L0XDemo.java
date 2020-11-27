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

import ch.ntb.inf.deep.runtime.arm32.Task;
import ch.ntb.inf.deep.runtime.zynq7000.driver.UART;
import ch.ntb.inf.deep.runtime.zynq7000.driver.VL53L0X;

/**
 * Demo application for the VL53L0X driver.
 * The application reads periodically the values of four sensors
 * and print them every 50 ms to the UART1.
 */
public class VL53L0XDemo extends Task{
	
	static VL53L0X sensor;
	int count = 0;
	int values[];
	
	public void action() {
		values = sensor.read();
		for (int i = 0; i < values.length; i++) {
			System.out.print(values[i]);
			System.out.print(",");
		}
		System.out.println();
	}

	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		// Initialize 4 Sensors
		sensor = new VL53L0X(4);
		Task t = new VL53L0XDemo();
		t.period = 50;
		Task.install(t);
	}

}
