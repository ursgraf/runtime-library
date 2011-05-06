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
