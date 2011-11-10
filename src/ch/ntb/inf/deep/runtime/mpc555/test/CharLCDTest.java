/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.runtime.mpc555.test;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.HD44780U;
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
public class CharLCDTest extends Task {
	
	
	/**
	 * Schaltet das Display aus.
	 * 
	 */
	public static void DispOff() {
		HD44780U.onOff(false, true, true);
	}

	/**
	 * Schaltet das Display ein.
	 * 
	 */
	public static void DispOn() {
		HD44780U.onOff(true, true, true);
	}

	/**
	 * Schreibt das Zeichen <code>T</code> auf das Display.
	 * 
	 */
	public static void writeT() {
		HD44780U.wrChar('T');
	}

	/**
	 * Löscht das Display.
	 * 
	 */
	public static void clearDisplay() {
		HD44780U.clearDisplay();
	}

	/**
	 * Setzt den Cursor auf die 3. Zeile an der Position 4.
	 * 
	 */
	public static void setCursor() {
		HD44780U.setCursor(2, 4);
	}

	static { // Task Initialisierung
		HD44780U.init(2);
	}

}
