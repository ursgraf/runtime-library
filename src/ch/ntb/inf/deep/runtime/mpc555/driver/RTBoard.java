package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Dieser Treiber wird fuer den Regelungstechnik-Print verwendet.<br>
 * Dieser Print beinhaltet im Wesentlichen einen MPC555 als Prozessor.
 * Davon werden 2 normale analoge und 2 Power-Ausgaenge, 4 analoge Eingaenge und
 * 8 digitale Ein-/Ausgaenge auf Buchsen herausgefuehrt.
 * 
 * @author Graf Urs
 */

/* Changes:
 * 01.05.2011 Urs Graf: Fehler in DACinit korrigiert
 * 18.02.2010 M. Zueger: Treiber fuer DAC und ADC angepasst und direkt integriert
 */

public class RTBoard implements ntbMpc555HB {
  
  private static final int ADDR_OFFSET = 64;
  private static final int CCW_INIT = 0x0000;
  private static final int END_OF_QUEUE = 0x003F;

  /**
   * Gibt den analogen Wert eines Eingangs zurueck.<br>
   * Das analoge Signal wird vom gegebenen Kanal <code>channel</code>
   * eingelesen. Die Kanaele sind mit A-In0..3 bezeichnet. Der Wertebereich
   * fuer den zurueckgegebenen Wert betraegt -10..+10, welches dem Wert in
   * Volt entspricht. Die Aufloesung der ADC betraegt 10Bit.
   * 
   * @param channel
   *            Kanal, von welchem das analoge Signal eingelesen werden soll.
   * @return Analoges Signal in Volt [V], welches eingelesen wurde.
   */
  public static float analogIn(int channel) {
    return ((US.GET2(RJURR_A + ADDR_OFFSET + channel * 2)) - 511.5f) / 511.5f * 10f;
  }

  /**
   * Ausgabe eines analogen Signals auf einen normalen Ausgang.<br>
   * Es wird ein analoges Signal auf den gegebenen Kanal <code>channel</code>
   * ausgegeben. Die Kanaele sind mit A-Out0 und A-Out1 bezeichnet. Der
   * Wertebereich fuer <code>val</code> betraegt -10..+10 welches dem Wert in
   * Volt entspricht. Die Aufloesung der DAC betraegt 12Bit.
   * 
   * @param channel
   *            Kanal, auf welchem das analoge Signal ausgegeben werden soll.
   * @param val
   *            Wert in Volt [V], welcher auf dem analogen Kanal ausgegeben
   *            werden soll.
   */
  public static void analogOut(int channel, float val) {
    US.PUT2(TRANRAM + 2 * channel, (channel % 4) * 0x4000 + ((int)(val / 10 * 2047.5f + 2047.5f) & 0xfff));
}

	/**
	 * Ausgabe eines analogen Signals auf einen Power-Ausgang.<br>
	 * Es wird ein analoges Signal auf den gegebenen Kanal <code>channel</code>
	 * ausgegeben. Die Kanaele sind mit Power-Out0 und Power-Out1 bezeichnet.
	 * Der Wertebereich fuer <code>val</code> betraegt -10..+10, welches dem
	 * Wert in Volt entspricht. Die Aufloesung der DAC betraegt 12Bit.
	 * 
	 * @param channel
	 *            Kanal, auf welchem das analoge Signal ausgegeben werden soll.
	 * @param val
	 *            Wert in Volt [V], welcher auf dem analogen Kanal ausgegeben
	 *            werden soll.
	 */
  public static void analogPowerOut(int channel, float val) {
    channel += 2;
    US.PUT2(TRANRAM + 2 * channel, (channel % 4) * 0x4000 + ((int)(val / 10 * 2047.5f + 2047.5f) & 0xfff));
  }


	/**
	 * Initialisiert einen digitalen Kanal.<br>
	 * Es wird der mit <code>channel</code> angegebene Kanal als Ein- oder
	 * Ausgang initialisiert.
	 * 
	 * @param channel
	 *            Kanal, welcher initialisiert werden soll.
	 * @param out
	 *            Boolscher Wert, welcher <code>true</code> ist, wenn der Kanal
	 *            als Ausgang initialisiert werden soll. Ansonsten
	 *            <code>true</code>.
	 */
	public static void dioInit(int channel, boolean out) {
		TPU_DIO.init(true, channel, out);
	}


	/**
	 * Gibt das TTL-Signal am gewaehtlen digitalen Eingang zurueck.<br>
	 * Das digitale Signal wird vom gegebenen Kanal <code>channel</code>
	 * eingelesen. Die Kanaele sind mit 0..7 bezeichnet. Dabei entspricht der
	 * Wert <code>true</code> einem logischen Signal <code>1</code>.
	 * 
	 * @param channel
	 *            Kanal, von welchem das TTL-Signal eingelesen werden soll.
	 * @return Digitales Signal, welches vom gegebenen Kanal
	 *         <code>channel</code> eingelesen wird.
	 */
	public static boolean dioIn(int channel) {
		return TPU_DIO.get(true, channel);
	}


	/**
	 * Ausgabe eines TTL-Signals auf dem digitalen Ausgang.<br>
	 * Es wird auf dem gewaehlten Kanal <code>channel</code> der in
	 * <code>level</code> uebergebene Wert ausgegeben. Die Kanaele sind mit 0..7
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
		TPU_DIO.set(true, channel, level);
	}


	/**
	 * Setzt den Status auf einer LED.<br>
	 * Es wird auf dem gewaehlten Kanal in Form eines TTL-Signals die LED auf
	 * leuchtend oder dunkel gesetzt.
	 * 
	 * @param channel
	 *            Kanal, auf welcher LED das TTL Signal ausgegeben werden soll.
	 * @param level
	 *            TTL-Signal, welches ausgegeben werden soll. <code>true</code>
	 *            entspricht einem logischen Signal <code>1</code>, was heisst,
	 *            dass die LED leuchtet.
	 */
	public static void ledOut(int channel, boolean level) {
		TPU_DIO.set(false, 2 * channel + 1, !level);
	}


	/**
	 * Initialisiert den einen Encodereingang.<br>
	 * Es wird der mit <code>channel</code> angegebene Kanal, der mit einen
	 * ganzzahligen Wert zwischen 0..3 angegeben wird, als Encoder
	 * initialisiert.<br>
	 * <br>
	 * <b>Wichtig:</b><br>
	 * Da immer zwei digitale Eingaenge fuer einen Encodereingang benoetigt
	 * werden, wird immer der im Index naechstfolgende Eingang auch fuer den
	 * Encoder initialisiert. Zum Beispiel wenn channel den Wert 3 hat, bilden
	 * die digitalen Eingaenge 3 und 4 den Encodereingang.
	 * 
	 * @param channel
	 *            Kanal, auf dem der Encoder initialisiert werden soll
	 */
	public static void encInit(int channel) {
		TPU_FQD.init(true, channel);
		TPU_FQD.setPosition(true, channel, 0);
	}


	/**
	 * Gibt den digitalen Zaehlerwert fuer einen Encoder zurueck.<br>
	 * 
	 * @param channel
	 *            Kanal, von welchem der Encoder-Zaehlerwert eingelesen werden
	 *            soll.
	 * @return Ausgelesener Zaehlerwert.
	 */
	public static short getEncCount(int channel) {
		return TPU_FQD.getPosition(true, channel);
	}


	/**
	 * Setzt den digitalen Zaehlerwert fuer einen Encoder.<br>
	 * Es wird auf dem gewaehlten Kanal <code>channel</code> der in
	 * <code>pos</code> uebergebene Zaehlerwert gesetzt.<br>
	 * 
	 * @param channel
	 *            Kanal, auf welchem der Encoder-Zaehlerwert gesetzt werden
	 *            soll.
	 * @param pos
	 *            Wert, mit welchem der Encoder-Zaehlerwert gesetzt werden soll.
	 */
	public static void setEncCount(int channel, short pos) {
		TPU_FQD.setPosition(true, channel, pos);
	}


	private static void initDAC() {
		US.PUT2(SPCR1, 0x0);     //disable QSPI 
		US.PUT1(PQSPAR, 0x013);  // use PCS1, MOSI, MISO for QSPI 
		US.PUT1(DDRQS, 0x016);   //SCK, MOSI, PCS1 output; MISO is input 
		US.PUT2(PORTQS, 0x0FF);  //all Pins, in case QSPI disabled, are high 
		US.PUT2(SPCR0, 0x08302); // QSPI is master, 16 bits per transfer, inactive state of SCLK is high (CPOL=1), data changed on leading edge (CPHA=1), clock = 10MHz 
		US.PUT2(SPCR2, 0x4300);  // no interrupts, wraparound mode, NEWQP=0, ENDQP=03 
		for(int i=0; i<4; i++) US.PUT1(COMDRAM + i, 0x6D); //disable chip select after transfer, use bits in SPCR0, use PCS1 
		for(int i=0; i<4; i++) US.PUT2(TRANRAM + 2 * i, i * 0x4000 + 2048);
		US.PUT2(SPCR1, 0x08010);	//enable QSPI, delay 13us after transfer
	}


  private static void initADC() {
    // user access
    US.PUT2(QADC64MCR_A, 0);
    
    // internal multiplexing, use ETRIG1 for queue1, QCLK = 40 MHz / (11+1 + 7+1) = 2 MHz
    US.PUT2(QACR0_A, 0x00B7);
    
    // queue2:
    // Software triggered continuous-scan mode
    // Resume execution with the aborted CCW
    // queue2 begins at CCW + 2*32 = 64 ( = ADDR_OFFSET)
    US.PUT2(QACR2_A, 0x31A0);
    
    // CCW for AN48 - AN59, max sample time
    // ADDR_OFFSET: Using queue2
    for (int i = 52; i <= 58; i += 2) {
    	US.PUT2(CCW_A + ADDR_OFFSET + (i-52), CCW_INIT + i);
    }
    
    // end of queue
    US.PUT2(CCW_A + ADDR_OFFSET + 4 * 2, END_OF_QUEUE);
  }


  static {
	  /* 1) Initialize DAC */
	  initDAC();

	  /* 2) Initialize digital I/Os */
	  for (int i = 0; i < 4; i++) TPU_DIO.init(false, i * 2 + 1, true);

	  /* 3) Initialize ADC */
	  initADC();
  }
}
