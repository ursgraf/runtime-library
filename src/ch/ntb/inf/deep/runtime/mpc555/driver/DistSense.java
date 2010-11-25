package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.unsafe.HWD;

/*changes:
 * 22.06.06	NTB/HS	whole driver in java, new init method
 * 18.05.06	NTB/HS	ch => channel
 * 08.02.06	NTB/HS	stub creation
 */

/**
 *@deprecated
 * * Treiber für bis zu 16 Distanzsensoren über die QADC A Schnittstelle.<br>
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
 * zerstört werden.
 */

public class DistSense extends Task {

	private static final int UIMB = Kernel.UIMB;

	private static final int QADCMCR_A = UIMB + 0x4800,
			QADCINT_A = UIMB + 0x4804, PORTQA_A = UIMB + 0x4806,
			PORTQA_B = UIMB + 0x4807, DDRQA_A = UIMB + 0x4808,
			QACR0_A = UIMB + 0x480A, QACR1_A = UIMB + 0x480C,
			QACR2_A = UIMB + 0x480E, QASR0_A = UIMB + 0x4810,
			QASR1_A = UIMB + 0x4812, CCW_A = UIMB + 0x4A00,
			RJURR_A = UIMB + 0x4A80, LJSRR_A = UIMB + 0x4B00,
			LJURR_A = UIMB + 0x4B80;

	static DistSense task;

	private static int addr = 0;

	private static int trigPin, trigPinNeg;

	private static int maxNoOfSens;

	private static int[] addrPin = new int[] { -1, -1, -1, -1 },
			addrPinNeg = new int[] { -1, -1, -1, -1 };

	private static short[] resultVal = new short[16],
			resultDark = new short[16];

	/**
	 * Gibt den von der QADC-Schnittstelle gewandelten Wert für den
	 * entsprechenden Kanal <code>channel</code> zurück.
	 * 
	 * @param channel
	 *            Kanal, welcher ausgelesen werden soll.
	 * @return Gewandelter Wert.
	 */
	public static short read(int channel) {
		return (short) (resultDark[channel] - resultVal[channel]);
	}

	/**
	 * Receive Task<br>
	 * <b>Do not call this method!</b>
	 */
	public void Do() {
		resultVal[addr] = HWD.GET2(RJURR_A);
		resultDark[addr] = HWD.GET2(RJURR_A + 2);
		addr = (addr + 1) % 16;
		int val = HWD.GET2(Kernel.MPIOSMDR);
		for (int i = 0; i < addrPinNeg.length; i++) {
			if (addrPinNeg[i] != -1)
				val &= addrPinNeg[i];
		}
		// val &= trigPinNeg;
		int v = getAddrPin(addr);
		val = val | v;
		if (addr <= maxNoOfSens) {
			val |= trigPin;
		}
		HWD.PUT2(Kernel.MPIOSMDR, val);
		// no interrupts, enable single-scan, interval timer single-scan
		// mode, 256 * QCLK
		HWD.PUT2(QACR1_A, 0x2500);
		// trig pulse must not be too short
		val &= trigPinNeg;
		HWD.PUT2(Kernel.MPIOSMDR, val);
	}

	private static int getAddrPin(int ad) {
		int value = 0;
		for (int i = 0; i < addrPin.length; i++) {
			int bit = ad % 2;
			if (bit == 1 & addrPin[i] > -1) {
				value |= addrPin[i];
			}
			ad /= 2;
		}
		return value;
	}

	/**
	 * Die Mpiosm-Schnittstelle wird für das Auslesen der Sensoren
	 * initialisiert. Für das Auslesen sind 5 Mpiosm-Pins nötig. Mit
	 * <code>startChannel</code> wird der erste Pin definiert. Wobei die Pins
	 * <code>startChannel</code>, <code>startChannel+1</code>,
	 * <code>startChannel+2</code>, <code>startChannel+3</code> die
	 * Adresskanäle darstellen.<br>
	 * Über den Pin <code>startChannel</code> wird das MSB, über den Pin
	 * <code>startChannel+3</code> das LSB ausgegeben.<br>
	 * Der Pin <code>startChannel+4</code> wird als Trigger für die
	 * hardwaremässige Ansteuerung benötigt.<br>
	 * Da maximal 15 Mpisom-Pins zur Verfügung stehen, darf
	 * <code>startChannel</code> maximal den Wert 11 haben.
	 * 
	 * @param startChannel
	 *            Startpin, welcher für die Ausgabe des MSB der Adresse benutzt
	 *            wird. Diesem Pin folgen 4 weitere Pins bis
	 *            <code>startChannel+4</code>, welche automatisch
	 *            initialisiert werden.
	 */
	public static void init(int startChannel) {
		if (startChannel > 11) {
			startChannel = 11;
		}

		init(startChannel, startChannel + 1, startChannel + 2,
				startChannel + 3, startChannel + 4);
	}

	/**
	 * Die Mpiosm-Schnittstelle wird für das Auslesen der Sensoren
	 * initialisiert. Für das Auslesen sind bis zu 5 Mpiosm-Pins nötig. Die 4
	 * Adresskanälen werden durch <code>addr3Channel</code>,
	 * <code>addr2Channel</code>, <code>addr1Channel</code> und
	 * <code>addr0Channel</code> dargestellt.<br>
	 * Der Pin <code>trigChannel</code> wird als Trigger für die
	 * hardwaremässige Ansteuerung benötigt.<br>
	 * Falls nicht alle 16 Sensoren benötigt werden ist es auch nicht nötig 4
	 * Adresskanäle zu benutzen. Nicht benutzte Adresspins müssen mit
	 * <code>-1</code> initialisiert werden.<br>
	 * Dabei müssen die Adresspins fortlaufend benutzt werden.<br>
	 * <i>Beispiel: Wenn nur 2 Pins benötigt werden, könnte die Initalisierung
	 * wie folgt aussehen:</i> <code>init(-1, -1, 3, 6, 1)</code>
	 * 
	 * @param addr3Channel
	 *            Pin für den Adresskanal 3.
	 * @param addr2Channel
	 *            Pin für den Adresskanal 2.
	 * @param addr1Channel
	 *            Pin für den Adresskanal 1.
	 * @param addr0Channel
	 *            Pin für den Adresskanal 0.
	 * @param trigChannel
	 *            Pin für den Trigger.
	 */
	public static void init(int addr3Channel, int addr2Channel,
			int addr1Channel, int addr0Channel, int trigChannel) {

		init(addr3Channel, addr2Channel, addr1Channel, addr0Channel,
				trigChannel, 59);
	}

	/**
	 * Die Mpiosm-Schnittstelle wird für das Auslesen der Sensoren
	 * initialisiert. Für das Auslesen sind bis zu 5 Mpiosm-Pins nötig. Die 4
	 * Adresskanälen werden durch <code>addr3Channel</code>,
	 * <code>addr2Channel</code>, <code>addr1Channel</code> und
	 * <code>addr0Channel</code> dargestellt.<br>
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
	 * @param addr3Channel
	 *            Pin für den Adresskanal 3.
	 * @param addr2Channel
	 *            Pin für den Adresskanal 2.
	 * @param addr1Channel
	 *            Pin für den Adresskanal 1.
	 * @param addr0Channel
	 *            Pin für den Adresskanal 0.
	 * @param trigChannel
	 *            Pin für den Trigger.
	 * @param inputChannel
	 *            Pin für den Input (<code>ANx</code>).<br>
	 *			<b> Achtung:</b> Den Pins AN0-AN3 und AN48-51 ist ein RC-Filter vorgeschaltet.
					Aus diesem Grund sollten diese nicht für diesen Treiber verwendet werden.
	 */
	public static void init(int addr3Channel, int addr2Channel,
			int addr1Channel, int addr0Channel, int trigChannel,
			int inputChannel) {
		maxNoOfSens = 15;
		if (addr3Channel > -1)
			addrPin[3] = 1 << addr3Channel;
		else
			maxNoOfSens = 7;
		if (addr2Channel > -1)
			addrPin[2] = 1 << addr2Channel;
		else
			maxNoOfSens = 3;
		if (addr1Channel > -1)
			addrPin[1] = 1 << addr1Channel;
		else
			maxNoOfSens = 1;
		if (addr0Channel > -1)
			addrPin[0] = 1 << addr0Channel;
		else
			maxNoOfSens = 0;

		trigPin = 1 << trigChannel;

		for (int i = 0; i < addrPin.length; i++) {
			if (addrPin[i] > -1)
				addrPinNeg[i] = 0xFFFFFFFF ^ addrPin[i];
		}
		trigPinNeg = 0xFFFFFFFF ^ trigPin;

		int val = HWD.GET2(Kernel.MPIOSMDDR);

		// Set pins as output
		for (int i = 0; i < addrPin.length; i++) {
			if (addrPin[i] > -1)
				val |= addrPin[i];
		}
		val |= trigPin;
		HWD.PUT2(Kernel.MPIOSMDDR, val);

		// user access
		HWD.PUT2(QADCMCR_A, 0);
		// internal multiplexing, use ETRIG1 for queue1, QCLK = 2 MHz
		HWD.PUT2(QACR0_A, 0x00B7);
		// disable queue2, queue 2 begins at 16
		HWD.PUT2(QACR2_A, 0x0010);

		if (inputChannel > 59)
			inputChannel = 59;
		if (inputChannel < 0)
			inputChannel = 0;
		// pause after conversion, max sample time, use inputChannel
		HWD.PUT2(CCW_A, 0x02C0 + inputChannel);
		// max sample time, use inputChannel
		HWD.PUT2(CCW_A + 2, 0x00C0 + inputChannel);
		// end of queue
		HWD.PUT2(CCW_A + 4, 0x003F);

		addr = 0;

		task = new DistSense();
		task.period = 1;
		Task.install(task);
	}
	
	
	
	/**
	 * Unterbricht das Auslesen der Sensoren.
	 */
	public static void stop(){
		Task.remove(task);
	}
	
	
	/**
	 * Startet das Auslesen der Sensoren nach einem Aufruf von <code>stop()</code> erneut.<br>
	 * Muss nach der Initialisierung <b>nicht</b> aufgerufen werden.
	 */
	public static void start(){
		Task.install(task);
	}

}