package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Treiber für den Analog-Digital Konverter.<br>
 * Der Treiber muss über die Methode <code>init(..)</code> gestartet werden.
 * Mittels dieser Methode kann bestimmt werden, ob die QADC-A oder QADC-B
 * benutzt werden soll.<br>
 * Es werden alle 16 analogen Eingänge innerhalb von 1 ms ausgewertet und können
 * einzeln über die Methode <code>read(..)</code> ausgelesen werden.<br>
 * Der Wertebereich der Eingänge beträgt: <code>0..3</code> und
 * <code>48..59</code>
 */
public class QADC implements ntbMpc555HB {


	private static final int ADDR_OFFSET = 32;

	private static final int CCW_INIT = 0x00C0;
	private static final int END_OF_QUEUE = 0x003F;

	/**
	 * Gibt den Wert zurück, welcher über den Analog-Digital Konverter
	 * eingelesen wurde.
	 * 
	 * @param qadcA
	 *            <code>true</code>: benutzen des QADC-A. <code>false</code>:
	 *            benutzen des QADC-B.
	 * @param channel
	 *            Kanal, dessen Wert eingelessen werden soll. Mögliche Werte
	 *            sind <code>0..3</code> und <code>48..59</code>.
	 * @return Konvertierter Wert des Analog-Digtal Konverters für den Kanal
	 *         <code>channel</code>.
	 */
	public static short read(boolean qadcA, int channel) {
		int channelOffset = getAddrForChn(channel);
		if (qadcA) {
			return US.GET2(RJURR_A + channelOffset + ADDR_OFFSET);
		} else {
			return US.GET2(RJURR_B + channelOffset + ADDR_OFFSET);
		}
	}
	
	/**
	 * Gibt die Adresse für den entsprechenden Kanal zurück.<br>
	 * Der zurückgegebene Wert entspricht der korrekten Adresse (Multiplikation mit 2)
	 * 
	 * @param channel
	 *            Kanal, dessen Adresse berechnet werden soll.
	 * @return Adresse des übergebenen Kanals.
	 */
	private static int getAddrForChn(int channel) {
		if (channel >= 48) {
			channel -= 44;
		}
		return channel * 2;
	}

	/**
	 * Initialisert den Analog-Digital Konverter.<br>
	 * Mittels dem Parameter <code>qadcA</code> kann bestimmt werden, welcher
	 * Konverter benutzt werden soll.
	 * 
	 * @param qadcA
	 *            <code>true</code>: benutzen des QADC-A. <code>false</code>:
	 *            benutzen des QADC-B.
	 */
	public static void init(boolean qadcA) {
		if (qadcA) {
			// user access
			US.PUT2(QADC64MCR_A, 0);
			
			// internal multiplexing, use ETRIG1 for queue1, QCLK = 40 MHz / (11+1 + 7+1) = 2 MHz
			US.PUT2(QACR0_A, 0x00B7);
			
			// queue2:
			// Periodic timer continuous-scan mode:
			// period = QCLK period x 2^11
			// Resume execution with the aborted CCW
			// queue2 begins at CCW + 2*16 (32 = ADDR_OFFSET)
			// This offset is used because of the DistSense driver
			US.PUT2(QACR2_A, 0x1890);

			// CCW for AN0 - AN3, max sample time
			// ADDR_OFFSET: Using queue2
			for (int i = 0; i <= 3; i++) {
				int addr = i * 2;
				US.PUT2(CCW_A + ADDR_OFFSET + addr, CCW_INIT + i);
			}
			
			// CCW for AN48 - AN59, max sample time
			// ADDR_OFFSET: Using queue2
			for (int i = 48; i <= 59; i++) {
				int addr = getAddrForChn(i);
				US.PUT2(CCW_A + ADDR_OFFSET + addr, CCW_INIT + i);
			}
			
			// end of queue
			US.PUT2(CCW_A + ADDR_OFFSET + 16 * 2, END_OF_QUEUE);
		} else {
			// user access
			US.PUT2(QADC64MCR_B, 0);
			
			// internal multiplexing, use ETRIG1 for queue1, QCLK = 40 MHz / (11+1 + 7+1) = 2 MHz
			US.PUT2(QACR0_B, 0x00B7);
			
			// queue2:
			// Periodic timer continuous-scan mode:
			// period = QCLK period x 2^11
			// Resume execution with the aborted CCW
			// queue2 begins at CCW + 2*16 (32 = ADDR_OFFSET)
			// This offset is used because of the DistSense driver
			US.PUT2(QACR2_B, 0x1890);

			// CCW for AN0 - AN3, max sample time
			// ADDR_OFFSET: Using queue2
			for (int i = 0; i <= 3; i++) {
				int addr = i * 2;
				US.PUT2(CCW_B + ADDR_OFFSET + addr, CCW_INIT + i);
			}
			
			// CCW for AN48 - AN59, max sample time
			// ADDR_OFFSET: Using queue2
			for (int i = 48; i <= 59; i++) {
				int addr = getAddrForChn(i);
				US.PUT2(CCW_B + ADDR_OFFSET + addr, CCW_INIT + i);
			}
			
			// end of queue
			US.PUT2(CCW_B + ADDR_OFFSET + 16 * 2, END_OF_QUEUE);
		}

	}

}