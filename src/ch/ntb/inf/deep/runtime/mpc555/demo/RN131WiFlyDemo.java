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

import ch.ntb.inf.deep.runtime.mpc555.driver.RN131WiFly;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;

public class RN131WiFlyDemo extends Task{
	private static final String ssidNet = "SysPNet_Team33";	//Adhoc net name
	private static final boolean createNet = true;
	private static final String ip_adr = "169.254.1.3";		//IP Address
	private static final int resetPin = 11;
	private static int num = 1;
	private static int err=0, lastErr=0;
	
	static{
		SCI1.start(19200, SCI1.NO_PARITY,(short)8);
		System.out = new PrintStream(SCI1.out);
		MPIOSM_DIO.init(resetPin,true); //Init Mpiosm
		MPIOSM_DIO.set(resetPin,false); //Reset RN131C
		Task t = new RN131WiFlyDemo();
		t.period = 20;
		Task.install(t);
		MPIOSM_DIO.set(resetPin, true); // release Reset RN131C
		RN131WiFly.clear();
	}
	
	public void action(){
		if(RN131WiFly.availToRead() > 0){
			System.out.print((char)RN131WiFly.in.read());
		}
		err = RN131WiFly.getErrStatus();
		if(err != 0 && err!=lastErr){
			printError(err);
			lastErr = err;			
		}
	}
	
	public static void printError(int error){
		switch(error){
			case 0:
				System.out.print("RN_OK\r\n");
				break;
			case 1:
				System.out.print("Error occured: NO_ADHOC\r\n");
				break;
			case 2:
				System.out.print("Error occured: CONNECT_FAILED\r\n");
				break;
			case 3:
				System.out.print("Error occured: CMD_ERR_OCCURED\r\n");
				break;
			case 4:
				System.out.print("Error occured: IN_CMD_MODE\r\n");
				break;
			case 5:
				System.out.print("Error occured: ERR_CLOSE_CON\r\n");
				break;
			case 6:
				System.out.print("Error occured: TCP_CON_CLOSED\r\n");
				break;
		}
	}
	
	public static void configWiFi(){
		RN131WiFly.init(ssidNet,createNet,ip_adr);
	}
	
	public static void sendString(){
		if(RN131WiFly.tcpConnectionOpen()){
			RN131WiFly.out.write("Hello World!".getBytes());
		}
		else{
			System.out.print("Open TCP Connection before calling this function.\r\n");
		}
	}
	
	public static void sendData(){
		if(RN131WiFly.tcpConnectionOpen()){
			RN131WiFly.out.write((byte)num++);
		}
		else{
			System.out.print("Open TCP Connection before calling this function.\r\n");
		}
	}
	
	public static void sendChar(){
		if(RN131WiFly.tcpConnectionOpen()){
			RN131WiFly.out.write((byte)'c');
		}
		else{
			System.out.print("Open TCP Connection before calling this function.\r\n");
		}
	}
	
	public static void sendDollar(){
		if(RN131WiFly.tcpConnectionOpen()){
			RN131WiFly.out.write("$$$".getBytes());
		}
	}
	
	public static void connect(){
		RN131WiFly.openTcpConnection( "169.254.1.2", "2000", "0");//"5");
	}
	
	public static void disconnect(){
		RN131WiFly.closeConnection();
	}
		
	public static void restart(){
		MPIOSM_DIO.set(resetPin,false); //Reset RN131C
		
		MPIOSM_DIO.set(resetPin, true); // release Reset RN131C
	}
}
