package ch.ntb.inf.deep.runtime.mpc555.test;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 * Achtung: diese Testklasse funktioniert nicht mit dem interruptgesteuerten
 * SCI-Treiber, weil clinit hier nie terminiert und die Interrupts erst am 
 * Schluss der Klasseninitialisierung eingeschaltet werden
 */

public class OutTest1 {
	
	static void run() {
		while (true) {
			SCI2.write((byte)'x');
			for (int i = 0; i < 1000000; i++);
		}
	}
	
	static {
		SCI2.start(9600, (byte)0, (short)8);
		SCI2.write((byte)'y');
		run();
	}
}
