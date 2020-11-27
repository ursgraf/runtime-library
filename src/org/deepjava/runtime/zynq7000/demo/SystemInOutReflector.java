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

package org.deepjava.runtime.zynq7000.demo;

import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.zynq7000.driver.*;


/**
 * Demo for InputStream and OutputStream using UART1.<br>
 * Received characters will be sent back immediately.
 * 
 * @author Urs Graf
 */
public class SystemInOutReflector extends Task {
	static UARTOutputStream out;
	static UARTInputStream in;
	
	/**
	 * Reflect input on in stream to out stream.
	 */
	public void action() {
		if (in.available() > 0)	out.write(in.read());
	}

	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short) 0, (short)8);
		out = uart.out;
		in = uart.in;
		out.write((byte)'x');
		
		Task t = new SystemInOutReflector();
		t.period = 0;
		Task.install(t);
	}
	
}
