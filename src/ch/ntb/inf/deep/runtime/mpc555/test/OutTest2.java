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

package ch.ntb.inf.deep.runtime.mpc555.test;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 * Achtung: diese Testklasse funktioniert nicht mit dem interruptgesteuerten
 * SCI-Treiber, weil clinit hier nie terminiert und die Interrupts erst am 
 * Schluss der Klasseninitialisierung eingeschaltet werden
 */

public class OutTest2 {
	
	static void run() {
		while (true) {
			SCI2.write((byte)'x');
			for (int i = 0; i < 1000000; i++);
			int a = 3;
			switch (a) {
			case 0: 
				SCI2.write((byte)'0');
				break;
			case 1: 
				SCI2.write((byte)'1');
				break;
			default:
				SCI2.write((byte)'y');
			}	
		}
	}
	
	static {
		SCI2.start(9600, (byte)0, (short)8);
		SCI2.write((byte)'9');
		run();
	}
}
