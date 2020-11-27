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

package org.deepjava.runtime.mpc555.test;

import java.io.IOException;
import java.io.PrintStream;

import org.deepjava.runtime.mpc555.driver.MPIOSM_DIO;
import org.deepjava.runtime.mpc555.driver.MPWMSM_DIO;
import org.deepjava.runtime.mpc555.driver.QADC_DIO;
import org.deepjava.runtime.mpc555.driver.SCI;
import org.deepjava.runtime.mpc555.driver.TPU_DIO;
import org.deepjava.runtime.ppc32.Task;

/* CHANGES:
 * 12.10.2011	NTB/MZ	QADC test added
 * 04.10.2011	NTB/MZ	MPWM test added
 * 26.09.2011	NTB/MZ	Created (based on HLC1395Demo)
 */

public class DioTest extends Task {
	private static final int print = 0, waitForInput = 1, inputTest = 2, outputTest = 3;
	private static final boolean IN = false, OUT = true;
	@SuppressWarnings("unused")
	private static final boolean ON = true, OFF = false;
	private static final boolean TPUA = true, TPUB = false, QADCA = true, QADCB = false;
	private static final String HI = "HI";
	private static final String LO = "LO";
	private static final int periodTime = 250;
	private static MPIOSM_DIO[] dioMPIOSM;
	private static MPWMSM_DIO[] dioMPWMSM;
	private static TPU_DIO[] dioTPUA, dioTPUB;
	
	private static int action = print;
	private static int timeToToggle = 0;
	private static int counter = 0;
	private static boolean ledStat = OFF;
	
	private static String getState(boolean b) {
		if(b) return HI;
		return LO;
	}
	
	private static void initializeIOs(boolean out) {
		// MPIOSM
		dioMPIOSM = new MPIOSM_DIO[16];
		for(int i = 0; i < 16; i++) {
			dioMPIOSM[i] = new MPIOSM_DIO(i, out);
		}
		
		// TPU-A
		dioTPUA = new TPU_DIO[16];
		for(int i = 0; i < 16; i++) {
			dioTPUA[i] = new TPU_DIO(TPUA, i, out);
		}
		
		// TPU-B
		dioTPUB = new TPU_DIO[16];
		for(int i = 0; i < 16; i++) {
			dioTPUB[i] = new TPU_DIO(TPUB, i, out);
		}
		
		// MPWMSM
		dioMPWMSM = new MPWMSM_DIO[20];
		for (int i = 0; i < 4; i++) {
			dioMPWMSM[i] = new MPWMSM_DIO(i, out);
			dioMPWMSM[i + 16] = new MPWMSM_DIO(i + 16, out);
		}
		
		// QADC-A
		for(int i = 0; i < 16; i++) {
			QADC_DIO.init(QADCA, i, out);
		}
		
		// QADC-B
		for(int i = 0; i < 16; i++) {
			QADC_DIO.init(QADCB, i, out);
		}
	}
	
	private static void toggleLEDs() {
		// MPIOSM
		for(int i = 0; i < 16; i++) {
			dioMPIOSM[i].set(!ledStat);
		}
		
		// TPU-A
		for(int i = 0; i < 16; i++) {
			dioTPUA[i].set(!ledStat);
		}
		
		// TPU-B
		for(int i = 0; i < 16; i++) {
			dioTPUB[i].set(!ledStat);
		}
		
		// MPWMSM
		for(int i = 0; i < 4; i++) {
			dioMPWMSM[i].set(!ledStat);
			dioMPWMSM[i + 16].set(!ledStat);
		}
		
		// QADC-A
		for(int i = 0; i < 16; i++) {
			QADC_DIO.set(QADCA, i, !ledStat);
		}
		
		// QADC-B
		for(int i = 0; i < 16; i++) {
			QADC_DIO.set(QADCB, i, !ledStat);
		}
		
		ledStat = !ledStat;
	}
	
	private static void readInputs() {
		// MPIOSM
		System.out.println("MPIOSM:");
		for(int i = 0; i < 16; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(dioMPIOSM[i].get()));
			System.out.print('\t');
		}
		System.out.println();
		System.out.println();
		
		// TPU-A
		System.out.println("TPU-A:");
		for(int i = 0; i < 16; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(dioTPUA[i].get()));
			System.out.print('\t');
		}
		System.out.println();
		System.out.println();
		
		// TPU-B
		System.out.println("TPU-B:");
		for(int i = 0; i < 16; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(dioTPUB[i].get()));
			System.out.print('\t');
		}
		System.out.println();
		System.out.println();
		
		// MPWMSM
		System.out.println("MPWMSM:");
		for(int i = 0; i < 4; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(dioMPWMSM[i].get()));
			System.out.print('\t');
		}
		for(int i = 16; i < 20; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(dioMPWMSM[i].get()));
			System.out.print('\t');
		}
		
		// QADC-A
		System.out.println("QADC-A:");
		for(int i = 0; i < 16; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(QADC_DIO.get(QADCA, i)));
			System.out.print('\t');
		}
		
		// QADC-B
		System.out.println("QADC-B:");
		for(int i = 0; i < 16; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(QADC_DIO.get(QADCB, i)));
			System.out.print('\t');
		}
	}
	
	private static void inputTest() {
		readInputs();
		action = print;
	}
	
	private static void outputTest() {
		if(Task.time() > timeToToggle) {
			toggleLEDs();
			counter++;
			timeToToggle = Task.time() + periodTime;
		}
		if(counter > 10) {
			counter = 0;
			action = print;
		}
	}
	
	private DioTest() {}
	
	public void action() {
		switch (action) {
		case inputTest:
			inputTest();
			break;
		case outputTest:
			outputTest();
			break;
		case print:
			System.out.println("  Press one of the following keys to start a test:");
			System.out.println("    I for testing inputs");
			System.out.println("    O for testing outputs");
			System.out.println();
			action = waitForInput;
			break;
		case waitForInput:
			char c;
			try {
				c = (char)System.in.read();
			} catch (IOException e) {break;}
			if(c == 'i') {
				System.out.println("  [I] Running input test...");
				initializeIOs(IN);
				action = inputTest;
			}
			else if(c == 'o') {
				System.out.println("  [O] Running output test...");
				initializeIOs(OUT);
				action = outputTest;
			}
			break;
		default:
			System.out.println("ERROR");
			action = print;
			
		}
	}
	
	static {
		// Initialize SCI2 and set stdout to SCI2
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = new PrintStream(sci.out);
		System.in = sci.in;
		
		System.out.println("DIO-Test");
		System.out.println();
		System.out.println("  Usage:");
		
		// Create and install task
		Task t = new DioTest();
		t.period = 100;
		Task.install(t);
	}
}
