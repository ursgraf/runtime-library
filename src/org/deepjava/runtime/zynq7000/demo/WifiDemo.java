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

import java.io.PrintStream;

import org.deepjava.runtime.util.IntPacket;
import org.deepjava.runtime.zynq7000.driver.RN131;
import org.deepjava.runtime.zynq7000.driver.UART;
import org.deepjava.runtime.arm32.Task;

public class WifiDemo extends Task {
	private RN131 wifi;
	private static WifiDemo task;
	
	public WifiDemo() {
		period = 500;
		UART uart = UART.getInstance(UART.pUART0);
		uart.start(115200, (short) 0, (short)8);
		wifi = new RN131(uart.in , uart.out, 0);
	}
	
	public void action() {
		System.out.print(wifi.getState().toString());

		if (wifi.connected)
			System.out.print("\t(connected)\t");
		else
			System.out.print("\t(not connected)\t");

		IntPacket.Type type = wifi.intPacket.readInt();
		if (type == IntPacket.Type.Int) {
			System.out.print("int packet =");
			System.out.print(wifi.intPacket.getInt());
		} else if (type == IntPacket.Type.Unknown) {
			System.out.print("unknown(");
			System.out.print(wifi.intPacket.getHeader());
			System.out.print(")=");
			System.out.print(wifi.intPacket.getInt());
		}
		System.out.println();
		if (nofActivations % 20 == 0) wifi.intPacket.writeInt(nofActivations);
	}

	public static void reset() {
		task.wifi.reset();
	}
	
	public static void sendCmd() {
		if (task.wifi.connected) {
			task.wifi.intPacket.writeInt(123);
		}
	}
	
	public static void sendOther() {
		if (task.wifi.connected) {
			task.wifi.intPacket.writeInt((byte)0xab, 789);
		}
	}
		
	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		System.out.println("WifiDemo");
		
		task = new WifiDemo();
		Task.install(task);
	}
}
