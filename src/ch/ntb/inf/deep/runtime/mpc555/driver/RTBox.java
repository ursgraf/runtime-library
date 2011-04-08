package ch.ntb.inf.deep.runtime.mpc555.driver;

/*changes:
 * 18.05.06	NTB/HS	chn => channel
 * 24.3.05,ED	Emulationsanweisungen gelöscht
 */

/**
 * Dieser Treiber wird für die Regelungstechnik-Box verwendet.<br>
 * Diese "Black-Box" beinhaltet im Wesentlichen einen MPC555 als Prozessor.
 * Davon werden je 8 analoge und digitale Ein- und Ausgänge auf Buchsen
 * herausgeführt.<br>
 * Ebenso gibt es zwei Anschlüsse für serielle Schnittestellen (RS232) sowie
 * eine parallele Schnittstelle für das Debugging (BDM).<br>
 * Weitere Informationen sind auf dem Infoportal erhältlich.
 */
public class RTBox {
	
	static final int FQDChannel=6;

	
	

	/**
	 * Ausgabe eines TTL-Signals auf dem digitalen Ausgang.<br>
	 * Es wird auf dem gewählten Kanal <code>channel</code> der in
	 * <code>level</code> übergebene Wert ausgegeben. Die Kanäle sind mit 0..7
	 * bezeichnet. Dabei entspricht der Wert <code>true</code> einem logischen
	 * Signal <code>1</code>.
	 * 
	 * @param channel
	 *            Kanal, auf welchem das TTL Signal ausgegeben werden soll.
	 * @param level
	 *            TTL-Signal, welches ausgegeben werden soll. <code>true</code>
	 *            entspricht einem logischen Signal <code>1</code>.
	 */
	public static void dioOut(int channel, boolean level) {
		TPU_DIO.set(true,channel+8,level);
	}

	/**
	 * Gibt das TTL-Signal am gewähtlen digitalen Eingang zurück.<br>
	 * Das digitale Signal wird vom gegebenen Kanal <code>channel</code>
	 * eingelesen. Die Kanäle sind mit 0..7 bezeichnet. Dabei entspricht der
	 * Wert <code>true</code> einem logischen Signal <code>1</code>.
	 * 
	 * @param channel
	 *            Kanal, von welchem das TTL-Signal eingelesen werden soll.
	 * @return Digitales Signal, welches vom gegebenen Kanal <code>channel</code>
	 *         eingelesen wird.
	 */
	public static boolean dioIn(int channel) {
		return TPU_DIO.get(true,channel);
	}

	/**
	 * Ausgabe eines analogen Signals.<br>
	 * Es wird ein analoges Signal auf den gegebenen Kanal <code>channel</code>
	 * ausgegeben. Die Kanäle sind mit 0..7 bezeichnet. Der Wertebereich für
	 * <code>val</code> beträgt -2048..2047. Dabei entspricht -2048 einem Wert
	 * von -10 V und 2047 einem Wert von +10 V.
	 * 
	 * @param channel
	 *            Kanal, auf welchem das analoge Signal ausgegeben werden soll.
	 * @param val
	 *            Wert, welcher auf dem analogen Kanal ausgegeben werden soll.
	 */
	public static void analogOut(int channel, int val) {
		DAC7614.write(channel,val+2048);
	}

	/**
	 * Gibt den analogen Wert eines Eingangs zurück.<br>
	 * Das analoge Signal wird vom gegebenen Kanal <code>channel</code>
	 * eingelesen. Die Kanäle sind mit 0..7 bezeichnet. Der Wertebereich für den
	 * zurückgegebenen Wert beträgt -512..511. Dabei entspricht -512 einem
	 * Wert von -10 V und 511 einem Wert von +10 V.
	 * 
	 * @param channel
	 *            Kanal, von welchem das analoge Signal eingelesen werden soll.
	 * @return Analoges Signal, welches eingelesen wurde.
	 */
	public static int analogIn(int channel) {
		return QADC_AIN.read(true,channel+8)-512;
	}

	/**
	 * Gibt den digitalen Zählerwert für einen Encoder zurück.<br>
	 * Die digitalen Kanäle 6 und 7 haben eine spezielle Verwendung. Sie können
	 * als Zählereingänge für einen Encoder verwendet werden.
	 * 
	 * @return Ausgelesener Zählerwert.
	 */
	public static int getEncCount() {
		return TPU_FQD.getPosition(true,FQDChannel);
	}

	/**
	 * Setzt den digitalen Zählerwert für einen Encoder.<br>
	 * Die digitalen Kanäle 6 und 7 haben eine spezielle Verwendung. Sie können
	 * als Zählereingänge für einen Encoder verwendet werden.<br>
	 * Der Zählerwert wird mit dem Wert <code>pos</code> gesetzt.
	 * 
	 * @param pos
	 *            Wert, mit welchem der Encoder-Zählerwert gesetzt werden soll.
	 */
	public static void setEncCount(int pos) {
		TPU_FQD.setPosition(true,FQDChannel,pos);
	}
	
	static{
		DAC7614.init();
		for(int i=0; i<8; i++){
			TPU_DIO.init(true,i,false);
			TPU_DIO.init(true,i+8,true);
		}
		TPU_FQD.init(true,FQDChannel);
		TPU_FQD.setPosition(true,FQDChannel,0);
		QADC_AIN.init(true);
	}
}
