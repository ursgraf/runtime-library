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

import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;
import ch.ntb.inf.deep.runtime.mpc555.driver.RN131WiFlyCmdInt;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/*
 * Changes:		28.10.2013		NTB/KALA		initial version
 */

/**
 * Application to demonstrate the usage of the RN131WiFlyCmdInt driver
 * with a Roving RN-131C WiFly Module
 */

public class RN131WiFlyCmdIntDemo extends Task{
	private static final String ipPartner = "169.254.1.2";	// partner IP Address
	private static final boolean createNet = true;
	private static final String ipAddr = "169.254.1.1";		// own IP Address
	private static final String ssid = "SysPNet_Team33";
	private static final int resetPin = 11;
	private static int cmd = 1;
	private static int lastState = -1;
	
	// Prints state changes of RN131WiFlyCmdInt driver and received commands
	public void action(){
		int state = RN131WiFlyCmdInt.getState();
		printState(state);
		if(state == RN131WiFlyCmdInt.connected){
			int rxCmd = RN131WiFlyCmdInt.getReceivedCmd();
			if(rxCmd >= 0){
				System.out.print("Cmd received -> ");
				System.out.println(rxCmd);
			}
		}
	}
	
	// print state changes
	private static void printState(int state){
		if (state != lastState) {
			lastState = state;
			switch (state) {
			case RN131WiFlyCmdInt.disconnected:
				System.out.println("RN131WiFi -> Disconnected");
				break;
			case RN131WiFlyCmdInt.connecting:
				System.out.println("RN131WiFly -> Connecting");
				break;
			case RN131WiFlyCmdInt.connected:
				System.out.println("RN131WiFi -> Connected");
				break;
			case RN131WiFlyCmdInt.configure:
				System.out.println("RN131WiFi -> Configure");
				break;
			}
		}
	}
	
	// Initialize the module
	public static void init(){
		if(RN131WiFlyCmdInt.getState() != RN131WiFlyCmdInt.connected){
			RN131WiFlyCmdInt.init(ssid, createNet, ipAddr);
		}
		else{
			System.out.println("Disconnect first");
		}
	}
	
	// Connect to the partner module
	public static void connect(){
		int res;
		if(RN131WiFlyCmdInt.getState() == RN131WiFlyCmdInt.disconnected){
			res = RN131WiFlyCmdInt.connect(ipPartner);
			if(res != RN131WiFlyCmdInt.success){
				System.out.print("Connect failed: ");
				System.out.println(res);
			}
		}
		else{
			System.out.println("Wrong mode");
		}
	}
	
	// Disconnects from the partner module
	public static void disconnect(){
		int res;
		if(RN131WiFlyCmdInt.getState() == RN131WiFlyCmdInt.connected){
			res = RN131WiFlyCmdInt.disconnect();
			if(res != RN131WiFlyCmdInt.success){
				System.out.print("Disconnect failed: ");
				System.out.println(res);
			}
		}
		else{
			System.out.println("Wrong mode");
		}
	}
	
	// Send a command
	public static void sendCmd(){
		int res;
		if(RN131WiFlyCmdInt.getState() == RN131WiFlyCmdInt.connected){
			res = RN131WiFlyCmdInt.sendCmd(cmd++);
			if(res != RN131WiFlyCmdInt.success){
				System.out.print("Send failed: ");
				System.out.println(res);
			}
		}
		else{
			System.out.println("Not connected");
		}
	}
	
	static{
		//initialize SCI1
		SCI1.start(19200, SCI1.NO_PARITY, (short) 8);
		//hook SCI1 to System.out
		System.out = new PrintStream(SCI1.out);
		
		MPIOSM_DIO.init(resetPin,true); //Init Mpiosm
		MPIOSM_DIO.set(resetPin,false); //Reset RN131C
		
		Task t = new RN131WiFlyCmdIntDemo();
		t.period = 100;
		Task.install(t);
		MPIOSM_DIO.set(resetPin, true); // release Reset of RN131C
		
	}

}
