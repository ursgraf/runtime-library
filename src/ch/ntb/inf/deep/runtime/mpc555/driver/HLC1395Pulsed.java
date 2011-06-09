package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.unsafe.US;

/* CHANGES:
 * 22.02.11 NTB/MZ	renamed to HLC1395Pulsed
 * 08.02.11 NTB/MZ  adapted to the new deep environment
 * 29.04.08	NTB/ED	simplification and efficiency improvement
 * 22.06.06	NTB/HS	whole driver in java, new init method
 * 18.05.06	NTB/HS	ch => channel
 * 08.02.06	NTB/HS	stub creation
 */

/**
 * Driver for up to 16 pulsed and multiplexed HLC1393 reflection distance
 * sensors.
 * 
 * The driver needs 5 digital outputs of the MPIOSM and a single analog input of
 * QADC-A. 4 of the digital outputs are used as address channels and the fifth
 * one is the trigger signal. The analog input is used to read the sensor
 * values. It is highly recommended to neither use the channels AN0...AN3 nor
 * the channels AN48...AN51 because this pins have a RC input filter.
 * 
 * <strong>IMPORTANT:</strong> Connect AGnd to Gnd!
 * 
 */
public class HLC1395Pulsed extends Task {

	private static final int USIU = IMB + 0x2FC000;
	private static final int UIMB = USIU + 0x4000;

	private static final int QADCMCR_A = UIMB + 0x4800,
			QADCINT_A = UIMB + 0x4804, PORTQA_A = UIMB + 0x4806,
			PORTQA_B = UIMB + 0x4807, DDRQA_A = UIMB + 0x4808,
			QACR0_A = UIMB + 0x480A, QACR1_A = UIMB + 0x480C,
			QACR2_A = UIMB + 0x480E, QASR0_A = UIMB + 0x4810,
			QASR1_A = UIMB + 0x4812, CCW_A = UIMB + 0x4A00,
			RJURR_A = UIMB + 0x4A80, LJSRR_A = UIMB + 0x4B00,
			LJURR_A = UIMB + 0x4B80;

	public static final byte maxNofSensors = 16, maxAnalogInPortNr = 59;

	private static final HLC1395Pulsed thisSngTask; // Singleton DistSense Task

	private static int nofSensors; // Anzahl angeschlossener Sensoren
	private static int trigPinPat; // Trigger-Pin-Bitmuster
	private static int outPinPat; // Bitmuster mit allen Adress-Pins und
									// Trigger-Pin
	private static int sensAdr;

	// Adress-Bitmuster-Tabelle, adrPatTab[s]: Adresspin-Muster für Sensor s
	private static final short[] adrPatTab = new short[maxNofSensors];
	private static short[] resultVal = new short[16];

	private HLC1395Pulsed() {
	}

	/**
	 * Returns the converted value of the given sensor number
	 * 
	 * @param channel
	 *            channel/sensor number
	 * @return converted value
	 */
	public static short read(int channel) {
		return resultVal[channel];
	}

	/**
	 * Background task loop: Do not call this method!
	 */
	public void action() {
		if (sensAdr >= 0) {// get result
			resultVal[sensAdr] = (short) (US.GET2(RJURR_A + 2) - US.GET2(RJURR_A)); // dark - val
		}
		sensAdr++;
		period = 1;
		if (sensAdr >= nofSensors) {
			if (sensAdr >= maxNofSensors) {
				sensAdr = 0;
			} else {
				period = maxNofSensors - sensAdr;
				sensAdr = -1;
			}
		}
		if (sensAdr >= 0) { // fire sensor
			int dataReg = US.GET2(Kernel.MPIOSMDR);
			dataReg = dataReg & ~outPinPat; // clear output pins (address and
											// trigger pins)
			dataReg = dataReg | adrPatTab[sensAdr]; // set new address and
													// trigger pins

			US.PUT2(Kernel.MPIOSMDR, dataReg);
			// no interrupts, enable single-scan, interval timer single-scan
			// mode, 256 * QCLK
			US.PUT2(QACR1_A, 0x2500);
			// trig pulse must not be too short
			dataReg = dataReg & ~trigPinPat; // clear trigger pin
			US.PUT2(Kernel.MPIOSMDR, dataReg);
		}
	}

	public static void init(int numberOfSensors, int pinNumbers, int analogInChn) {
		if (numberOfSensors > maxNofSensors)
			numberOfSensors = maxNofSensors;
		else if (numberOfSensors < 1)
			numberOfSensors = 1;
		nofSensors = numberOfSensors;

		pinNumbers = pinNumbers & 0xFFFFF;

		if (analogInChn > maxAnalogInPortNr)
			analogInChn = maxAnalogInPortNr;
		else if (analogInChn < 0)
			analogInChn = 0;

		trigPinPat = 1 << (pinNumbers >> 16);

		// -- set up adrPatTab and outPinPat
		int n = numberOfSensors;
		outPinPat = 0;
		while (n > 0) {
			int pinPat = trigPinPat;
			n--;
			if ((n & 1) != 0)
				pinPat |= 1 << (pinNumbers & 0xF);
			if ((n & 2) != 0)
				pinPat |= 1 << (pinNumbers >> 4 & 0xF);
			if ((n & 4) != 0)
				pinPat |= 1 << (pinNumbers >> 8 & 0xF);
			if ((n & 8) != 0)
				pinPat |= 1 << (pinNumbers >> 12 & 0xF);
			adrPatTab[n] = (short) pinPat;
			outPinPat |= pinPat;
		}

		// init output pins
		int val = US.GET2(Kernel.MPIOSMDDR);
		US.PUT2(Kernel.MPIOSMDDR, val | outPinPat);

		// user access
		US.PUT2(QADCMCR_A, 0);
		// internal multiplexing, use ETRIG1 for queue1, QCLK = 2 MHz
		US.PUT2(QACR0_A, 0x00B7);

		// pause after conversion, max sample time, use inputChannel
		US.PUT2(CCW_A, 0x02C0 + analogInChn);
		// max sample time, use inputChannel
		US.PUT2(CCW_A + 2, 0x00C0 + analogInChn);
		// end of queue
		US.PUT2(CCW_A + 4, 0x003F);

		sensAdr = -1;
	}

	/**
	 * Unterbricht das Auslesen der Sensoren.
	 */
	public static void stop() {
		Task.remove(thisSngTask);
	}

	/**
	 * Startet das Auslesen der Sensoren.<br>
	 * Muss nach der Initialisierung oder nach einem Aufruf von
	 * <code>stop()</code> aufgerufen werden.
	 */
	public static void start() {
		thisSngTask.period = 1;
		Task.install(thisSngTask);
	}

	static {
		thisSngTask = new HLC1395Pulsed();
	}

	private static int getNofSensAndPinNumbers(int adr3PinNr, int adr2PinNr,
			int adr1PinNr, int adr0PinNr, int trigPinNr) {
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
		if (nofSens == 16)
			nofSens = 15;
		while (n > 0) {
			pinNumbers = pinNumbers << 4 | (adr3PinNr & 0xF);
			adr3PinNr = adr2PinNr;
			adr2PinNr = adr1PinNr;
			adr1PinNr = adr0PinNr;
			n = n >> 1;
		}
		return pinNumbers | (nofSens << 20);
	}

	public static void init(int addr3Pin, int addr2Pin, int addr1Pin,
			int addr0Pin, int trgPin, int analogInPin) {
		int val = getNofSensAndPinNumbers(addr3Pin, addr2Pin, addr1Pin,
				addr0Pin, trgPin);
		init(val >> 20, val, analogInPin); // nofSens = val >> 20;
	}
}