package ch.ntb.inf.sts.mpc555.driver;

import ch.ntb.inf.sts.internal.SYS;
import ch.ntb.inf.sts.mpc555.Kernel;
import ch.ntb.inf.sts.mpc555.Task;

/*changes:
 * 29.04.08	NTB/ED	simplification and efficiency improvement
 * 22.06.06	NTB/HS	whole driver in java, new init method
 * 18.05.06	NTB/HS	ch => channel
 * 08.02.06	NTB/HS	stub creation
 */
/**
 * Treiber für bis zu 16 Distanzsensoren über die QADC A Schnittstelle.<br>
 * Die Ansteurung erfolgt insgesamt über 5 Mpiosm-Pins. Die ersten 4 Pins wirken
 * dabei als Adresskanäle. Der 5. Pin wird als Trigger für die Hardware benutzt.<br>
 * Der Wert des aktuell abgefragten Sensors wird standardmässig über den Pin AN59
 * eingelesen. Über die Methode <code>init(.., inputChannel)</code> kann
 * dieser definiert werden. Da den Kanälen AN0 - AN3 und AN48-AN51 ein RC-Filter
 * vorgeschaltet ist, sollten diese für den DistSens Treiber nicht benutzt werden.<br>
 * <b>Achtung:</b>Damit der Analoge Ground definiert ist, muss der Pin AGnd mit
 *  Gnd verbunden werden.<br>
 * Der Trigger-Puls für das Auslesen der Sensoren wird hardwaremässig erstellt.
 * Damit ist sicher gestellt, dass die Sensoren bei einem Absturz des
 * Microcontrollers nicht zerstört werden.<br>
 * Die Abfrage eines Sensors dauert 1 ms. Dementsprechend werden alle Sensoren
 * periodisch im Zeitraum von 16 ms abgefragt.<br>
 * <b>Wichtig:</b><br>
 * Es müssen alle in der <code>init(..)</code> Methode definierten
 * Adresskanäle angeschlossen sein. Ansonsten werden einzelne Sensoren in zu
 * kurzer Zeit nacheinander abgefragt. Dies kann dazu führen, dass die Sensoren
 * zerstört werden. Um das Auslesen der Sensoren zu starten, muss nach der 
 * Initialisierung die Methode <code>start()</code> aufgerufen werden.
 * 
 * Namenskonventionen:
 * <br>pinNr: 4-Bit (logical) pin number (4-Bit  Pin-Nummer: 0 <= pinNr <= 15)
 * <br>pinPat: 16-Bit  pin pattern (16-Bit  Pin-Bitmuster mit genau einem 1-Bit)
 * 
 * <br>adr: 4-Bit (logical)Address (4-Bit Adresse)
 * <br>adrPat: 16-Bit  address pattern (16-Bit  Adress-Bitmuster mit max vier 1-Bits)
 * <br>adrPatTab: 16-Bit  address pattern table (Tabelle mit den 16 möglichen Adress-Bitmustern)
 */
public class DistSensor extends Task {

	private static final int UIMB = Kernel.UIMB;

	private static final int QADCMCR_A = UIMB + 0x4800,
			QADCINT_A = UIMB + 0x4804, PORTQA_A = UIMB + 0x4806,
			PORTQA_B = UIMB + 0x4807, DDRQA_A = UIMB + 0x4808,
			QACR0_A = UIMB + 0x480A, QACR1_A = UIMB + 0x480C,
			QACR2_A = UIMB + 0x480E, QASR0_A = UIMB + 0x4810,
			QASR1_A = UIMB + 0x4812, CCW_A = UIMB + 0x4A00,
			RJURR_A = UIMB + 0x4A80, LJSRR_A = UIMB + 0x4B00,
			LJURR_A = UIMB + 0x4B80;

	public static final  byte maxNofSensors = 16, maxAnalogInPortNr = 59;

	private static final  DistSensor  thisSngTask; // Singleton DistSense Task

	private static int nofSensors; // Anzahl angeschlossener Sensoren
	private static int trigPinPat; // Trigger-Pin-Bitmuster
//	private static int adrPat; // Adress-Pin-Bitmuster
	private static int outPinPat; // Bitmuster mit allen Adress-Pins und Trigger-Pin
	private static int sensAdr;

	// Adress-Bitmuster-Tabelle, adrPatTab[s]: Adresspin-Muster für Sensor  s
	private static final  short[]  adrPatTab = new short[maxNofSensors];

//	private static short[] resultVal = new short[16], resultDark = new short[16];
	private static short[] resultVal = new short[16];

	private DistSensor() { }
	
	/**
	 * Gibt den von der QADC-Schnittstelle gewandelten Wert für den
	 * entsprechenden Kanal <code>channel</code> zurück.
	 * 
	 * @param channel
	 *            Kanal, welcher ausgelesen werden soll.
	 * @return Gewandelter Wert.
	 */
	public static short read(int channel) {
//		return (short) (resultDark[channel] - resultVal[channel]);
		return  resultVal[channel];
	}

	/**
	 * task activation method:  Do not call this method!
	 * <br>get value of previous sensor and fire next one
	 */
	public void Do() {
		if (sensAdr >= 0) {// get result
//			resultVal[sensAdr] = SYS.GET2(RJURR_A);
//			resultDark[sensAdr] = SYS.GET2(RJURR_A + 2);
			resultVal[sensAdr] = (short)(SYS.GET2(RJURR_A + 2) - SYS.GET2(RJURR_A)); // dark - val
		}
		sensAdr++;
		period = 1;
		if (sensAdr >= nofSensors) {
			if (sensAdr >= maxNofSensors) {
				sensAdr = 0;
			}else {
				period = maxNofSensors - sensAdr;
				sensAdr = -1;
			}
		}
		if (sensAdr >= 0) { // fire sensor
			int dataReg = SYS.GET2(Kernel.MPIOSMDR);
			dataReg = dataReg & ~outPinPat; // clear output pins (address and trigger pins)
			dataReg = dataReg | adrPatTab[sensAdr]; // set new address and trigger pins

			SYS.PUT2(Kernel.MPIOSMDR, dataReg);
			// no interrupts, enable single-scan, interval timer single-scan
			// mode, 256 * QCLK
			SYS.PUT2(QACR1_A, 0x2500);
			// trig pulse must not be too short
			dataReg = dataReg & ~trigPinPat; // clear trigger pin
			SYS.PUT2(Kernel.MPIOSMDR, dataReg);
		}
	}


	/**
	 * Die Mpiosm-Schnittstelle wird für das Auslesen der Sensoren
	 * initialisiert. Für das Auslesen sind bis zu 5 Mpiosm-Pins nötig. Die 4
	 * Adresskanäle werden durch <code>adr3PinNr</code>,
	 * <code>adr2PinNr</code>, <code>adr1PinNr</code> und
	 * <code>adr0PinNr</code> dargestellt.<br>
	 * Der Pin <code>trigChannel</code> wird als Trigger für die
	 * hardwaremässige Ansteuerung benötigt.<br>
	 * Für das Einlesen des Wertes kann mittels <code>inputChannel</code> der
	 * gewünschte Kanal gewählt werden.<br>
	 * Falls nicht alle 16 Sensoren benötigt werden ist es auch nicht nötig 4
	 * Adresskanäle zu benutzen. Nicht benutzte Adresspins müssen mit
	 * <code>-1</code> initialisiert werden.<br>
	 * Dabei müssen die Adresspins fortlaufend benutzt werden.<br>
	 * <i>Beispiel: Wenn nur 2 Pins benötigt werden, könnte die Initalisierung
	 * wie folgt aussehen:</i> <code>init(-1, -1, 3, 6, 1, 0)</code>
	 * 
	 * @param numberOfSensors
	 *            Anzahl Sensoren: 0 < numberOfSensors <= maxNofSensors
	 *            <br>Die Sensoren mit den Adressen  0..numberOfSensors-1  werden gezündet und abgefragt.
	 *            
	 * @param pinNumbers
	 *            Pin-Nummern der vier Adresspins und des Triggerpins, vier Bit pro PinNummer.
	 *            <br>Reihenfolge: trigger, adr3, adr2, adr1, adr0
	 *            <br>Beispiel: 0xF85AC bedeutet:
	 *            <br>trgPinNr=15, adr3PinNr=8, adr2PinNr=5, adr1PinNr=10, adr0PinNr=12
	 *            
	 * @param analogInChn
	 *            Pin-Nummer für den Analog-Input-Kanal (<code>ANx</code>).
	 *            <br>Achtung:</b> Den Pins AN0-AN3 und AN48-51 ist ein RC-Filter vorgeschaltet.
	 *			Aus diesem Grund sollten diese nicht für diesen Treiber verwendet werden.
	 */
	public static void init(int numberOfSensors, int pinNumbers, int analogInChn) {
		if (numberOfSensors > maxNofSensors) numberOfSensors = maxNofSensors;
		else if (numberOfSensors < 1) numberOfSensors = 1;
		nofSensors = numberOfSensors;

		pinNumbers = pinNumbers & 0xFFFFF;

		if (analogInChn > maxAnalogInPortNr)  analogInChn = maxAnalogInPortNr;
		else if (analogInChn < 0)  analogInChn = 0;

		trigPinPat = 1 << (pinNumbers >> 16);

		//-- set up adrPatTab and outPinPat
		int n = numberOfSensors;
		outPinPat = 0;
		while (n  > 0) {
			int pinPat = trigPinPat;
			n--;
			if ( (n&1) != 0) pinPat |= 1<<(pinNumbers & 0xF);
			if ( (n&2) != 0) pinPat |= 1<<(pinNumbers>> 4 & 0xF);
			if ( (n&4) != 0) pinPat |= 1<<(pinNumbers>> 8 & 0xF);
			if ( (n&8) != 0) pinPat |= 1<<(pinNumbers>>12 & 0xF);
			adrPatTab[n] = (short)pinPat;
			outPinPat |= pinPat;
		}

		// init output pins
		int val = SYS.GET2(Kernel.MPIOSMDDR);
		SYS.PUT2(Kernel.MPIOSMDDR, val | outPinPat);

		// user access
		SYS.PUT2(QADCMCR_A, 0);
		// internal multiplexing, use ETRIG1 for queue1, QCLK = 2 MHz
		SYS.PUT2(QACR0_A, 0x00B7);
		// disable queue2, queue 2 begins at 16
		SYS.PUT2(QACR2_A, 0x0010);

		// pause after conversion, max sample time, use inputChannel
		SYS.PUT2(CCW_A, 0x02C0 + analogInChn);
		// max sample time, use inputChannel
		SYS.PUT2(CCW_A + 2, 0x00C0 + analogInChn);
		// end of queue
		SYS.PUT2(CCW_A + 4, 0x003F);

		sensAdr = -1;
	}

	/**
	 * Unterbricht das Auslesen der Sensoren.
	 */
	public static void stop(){
		Task.remove(thisSngTask);
	}

	/**
	 * Startet das Auslesen der Sensoren.<br>
	 * Muss nach der Initialisierung oder nach 
	 * einem Aufruf von <code>stop()</code> aufgerufen werden.
	 */
	public static void start(){
		thisSngTask.period = 1;
		Task.install(thisSngTask);
	}

	static {
		thisSngTask = new DistSensor();
	}


//--- Deprecated Utilities:
	/** @Deprecated
	 * Konversionshilfe für altes init-Interface
	 */
	private static  int  getNofSensAndPinNumbers(int adr3PinNr, int adr2PinNr, int adr1PinNr, int adr0PinNr, int trigPinNr) {
		int pinNumbers = trigPinNr & 0xF;
		int nofSens = 16;
		while (adr3PinNr < 0) {
			pinNumbers = pinNumbers << 4;
			adr3PinNr = adr2PinNr;
			adr2PinNr = adr1PinNr;
			adr1PinNr = adr0PinNr;
			adr0PinNr = 0;
			nofSens = nofSens >> 1;
		}
		int n = nofSens >> 1;
		if (nofSens == 16) nofSens = 15;
		while (n > 0) {
			pinNumbers = pinNumbers << 4 | (adr3PinNr & 0xF);
			adr3PinNr = adr2PinNr;
			adr2PinNr = adr1PinNr;
			adr1PinNr = adr0PinNr;
			n = n >> 1;
		}
		return pinNumbers | (nofSens << 20);
	}
		
	/**@Deprecated
	 * Die Mpiosm-Schnittstelle wird für das Auslesen der Sensoren
	 * initialisiert. Für das Auslesen sind 5 Mpiosm-Pins nötig. Mit
	 * <code>adr3PinNr</code> wird der erste Pin definiert. Wobei die Pins
	 * <code>adr3PinNr</code>, <code>adr3PinNr+1</code>,
	 * <code>adr3PinNr+2</code>, <code>adr3PinNr+3</code> die
	 * Adresspins A3, A2, A1, A0 darstellen.<br>
	 * Über den Pin <code>adr3PinNr</code> wird das MSB (A3), über den Pin
	 * <code>adr3PinNr+3</code> das LSB (A0) ausgegeben.<br>
	 * Der Pin <code>adr3PinNr+4</code> wird als Trigger für die
	 * hardwaremässige Ansteuerung benötigt.<br>
	 * Da maximal 15 Mpisom-Pins zur Verfügung stehen, darf
	 * <code>adr3PinNr</code> maximal den Wert 11 haben.
	 * 
	 * @param adr3PinNr
	 *            Startpin, welcher für die Ausgabe des MSB der Adresse benutzt
	 *            wird. Diesem Pin folgen 4 weitere Pins bis
	 *            <code>adr3PinNr+4</code>, welche automatisch
	 *            initialisiert werden.
	 */
  	public static void init(int adr3PinNr) {
		if (adr3PinNr > 11) adr3PinNr = 11;
		int val = getNofSensAndPinNumbers(adr3PinNr, adr3PinNr+1, adr3PinNr+2, adr3PinNr+3, adr3PinNr+4);
		init(val >> 20, val , 59);//  nofSens = val >> 20;
	}

	/**@Deprecated
	 * Die Mpiosm-Schnittstelle wird für das Auslesen der Sensoren
	 * initialisiert. Für das Auslesen sind bis zu 5 Mpiosm-Pins nötig. Die 4
	 * Adresskanälen werden durch <code>adr3PinNr</code>,
	 * <code>adr2PinNr</code>, <code>adr1PinNr</code> und
	 * <code>adr0PinNr</code> dargestellt.<br>
	 * Der Pin <code>trigChannel</code> wird als Trigger für die
	 * hardwaremässige Ansteuerung benötigt.<br>
	 * Falls nicht alle 16 Sensoren benötigt werden ist es auch nicht nötig 4
	 * Adresskanäle zu benutzen. Nicht benutzte Adresspins müssen mit
	 * <code>-1</code> initialisiert werden.<br>
	 * Dabei müssen die Adresspins fortlaufend benutzt werden.<br>
	 * <i>Beispiel: Wenn nur 2 Pins benötigt werden, könnte die Initalisierung
	 * wie folgt aussehen:</i> <code>init(-1, -1, 3, 6, 1)</code>
	 * 
	 * @param adr3PinNr
	 *            Pin für den Adresskanal 3.
	 * @param adr2PinNr
	 *            Pin für den Adresskanal 2.
	 * @param adr1PinNr
	 *            Pin für den Adresskanal 1.
	 * @param adr0PinNr
	 *            Pin für den Adresskanal 0.
	 * @param trigChannel
	 *            Pin für den Trigger.
	 */
	 
	public static void init(int adr3PinNr, int adr2PinNr, int adr1PinNr, int adr0PinNr, int trigPinNr) {
		int val = getNofSensAndPinNumbers(adr3PinNr, adr2PinNr, adr1PinNr, adr0PinNr, trigPinNr);
		init(val >> 20, val , 59);//  nofSens = val >> 20;
	}

		/**@Deprecated
		 * Die Mpiosm-Schnittstelle wird für das Auslesen der Sensoren
		 * initialisiert. Für das Auslesen sind bis zu 5 Mpiosm-Pins nötig. Die 4
		 * Adresskanälen werden durch <code>adr3PinNr</code>,
		 * <code>adr2PinNr</code>, <code>adr1PinNr</code> und
		 * <code>adr0PinNr</code> dargestellt.<br>
		 * Der Pin <code>trgPinNr</code> wird als Trigger für die
		 * hardwaremässige Ansteuerung benötigt.<br>
		 * Für das Einlesen des Wertes kann mittels <code>analogInPinNr</code> der
		 * gewünschte Kanal gewählt werden.<br>
		 * Falls nicht alle 16 Sensoren benötigt werden ist es auch nicht nötig 4
		 * Adresskanäle zu benutzen. Nicht benutzte Adresspins müssen mit
		 * <code>-1</code> initialisiert werden.<br>
		 * Dabei müssen die Adresspins fortlaufend benutzt werden.<br>
		 * <i>Beispiel: Wenn nur 2 Pins benötigt werden, könnte die Initalisierung
		 * wie folgt aussehen:</i> <code>init(-1, -1, 3, 6, 1, 0)</code>
		 * 
		 * @param adr3PinNr
		 *            Pin für den Adresskanal 3.
		 * @param adr2PinNr
		 *            Pin für den Adresskanal 2.
		 * @param adr1PinNr
		 *            Pin für den Adresskanal 1.
		 * @param adr0PinNr
		 *            Pin für den Adresskanal 0.
		 * @param trgPinNr
		 *            Pin für den Trigger.
		 * @param analogInPinNr
		 *            Pin für den Input (<code>ANx</code>).<br>
		 *			<b> Achtung:</b> Den Pins AN0-AN3 und AN48-51 ist ein RC-Filter vorgeschaltet.
						Aus diesem Grund sollten diese nicht für diesen Treiber verwendet werden.
		 */
	 
		public static void init(int adr3PinNr, int adr2PinNr, int adr1PinNr, int adr0PinNr, int trgPinNr, int analogInPinNr) {
			int val = getNofSensAndPinNumbers(adr3PinNr, adr2PinNr, adr1PinNr, adr0PinNr, trgPinNr);
			init(val >> 20, val , analogInPinNr);//  nofSens = val >> 20;
	 }
	//--- end of  deprecated Utilities:



	//------- debug, test utilities ----------------------------------------
//	public static void printState(String title) {
//		Out.println(title);
//		Out.println("\t nofSensors=" + nofSensors);
//		Out.printf("\t trigPat  =0x%1$1x", trigPinPat); Out.println();
//		Out.printf("\t outPinPat=0x%1$1x", outPinPat); Out.println();
//		Out.println("\t sensAdr=" + sensAdr);
//	}
//	public static void printAdrPatTab(String title) {
//		Out.println(title);
//		for (int a = 0; a < adrPatTab.length; a++) {
//			Out.printf("\t adrPatTab[%1$2d", a);
//			Out.printf("] = 0x%1$1x", (adrPatTab[a] & 0xFFFF)); Out.println();
//		}
//	}
//	public static void printResults(String title) {
//		Out.println(title);
//		for (int a = 0; a < resultVal.length; a++) {
//			Out.print("\t resultVal, Dark[" + a);
//			Out.printf("] = %1$4x", (resultVal[a] & 0xFFFF)); 
//			Out.printf(", %1$4x", (resultDark[a] & 0xFFFF)); 
//			Out.println();
//		}
//	}
	//------- end  debug utilities ---------------------------------------
}