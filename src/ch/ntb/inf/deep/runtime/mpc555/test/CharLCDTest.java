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

import ch.ntb.inf.deep.runtime.mpc555.driver.HD44780U;
import ch.ntb.inf.deep.runtime.ppc32.Task;
/* changes:
 * 06.05.2011 Roger Millischer adapted to deep
 * 08.02.06	NTB/HS	creation
 * 
*/
/**
* Testprogramm für <code>mpc555.CharLCD</code>.<br>
* Über die unten aufgeführten Methoden können die verschiedenen Funktionen des
* Displays getestet werden.
*/
public class CharLCDTest {
	
	static HD44780U disp;
	
	public static void dispOff() {
		disp.onOff(false, true, true);
	}

	public static void dispOn() {
		disp.onOff(true, true, true);
	}

	public static void writeT() {
		disp.writeChar('T');
	}

	public static void writeInt() {
		disp.writeInt(8635, 6);
	}

	public static void writeLn() {
		disp.writeLn();
	}

	public static void clearDisplay() {
		disp.clearDisplay();
	}

	public static void setCursor() {
		disp.setCursor(2, 4);
	}

	static { 
		disp = HD44780U.getInstance();
		disp.init(2);
	}

}
