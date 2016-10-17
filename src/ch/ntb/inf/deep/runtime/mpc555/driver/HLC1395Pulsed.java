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

package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.runtime.ppc32.Task;
import ch.ntb.inf.deep.unsafe.US;

/* CHANGES:
 * 27.09.16 NTB/UG	changed to singleton
 * 31.08.11 NTB/MZ	JavaDoc updated
 * 09.06.11 NTB/RM	TpuTimeUnit renamed to tpuTimeBase
 * 25.05.11 NTB/RM	conflict with QADC_AIN driver fixed
 * 22.02.11 NTB/MZ	renamed to HLC1395Pulsed
 * 08.02.11 NTB/MZ  adapted to the new deep environment
 * 29.04.08	NTB/ED	simplification and efficiency improvement
 * 22.06.06	NTB/HS	whole driver in java, new init method
 * 18.05.06	NTB/HS	ch => channel
 * 08.02.06	NTB/HS	stub creation
 */

/**
 * Driver for up to 16 pulsed and multiplexed HLC1393 reflection distance
 * sensors.<br>
 * The driver needs 5 digital outputs of the MPIOSM and a single analog input of
 * QADC-A. 4 of the digital outputs are used as address channels and the fifth
 * one is the trigger signal. The analog input is used to read the sensor
 * values. It is highly recommended to neither use the channels AN0...AN3 nor
 * the channels AN48...AN51 because these pins have a RC input filter on 
 * on some NTB MPC555 header boards.<br>
 * All the sensors are repetitively sampled within 16ms, regardless of the number of sensors.<br>  
 * Its possible to use this driver together with 
 * {@link ch.ntb.inf.deep.runtime.mpc555.driver.QADC_AIN} on the same QADC module.
 * 
 * <strong>IMPORTANT:</strong> Connect AGnd to Gnd!
 * 
 */
public class HLC1395Pulsed extends Task implements IntbMpc555HB {

	private static final byte maxNofSensors = 16, maxAnalogInPortNr = 59;
	private static HLC1395Pulsed thisSngTask; // Singleton DistSense Task
	private int nofSensors; // number of connected sensors
	private int trigPinPat; // trigger pin bit pattern
	private int outPinPat; // bit pattern for all address pins and the trigger pin
	private int sensAdr; // sensor address

	// Address pattern table, adrPatTab[s]: address bit pattern for sensor s
	private final short[] adrPatTab = new short[maxNofSensors];
	private short[] resultVal = new short[16];

	/**
	 * Returns an instance of <i>HLC1395Pulsed Driver</i> 
	 * @return Instance of HLC1395Pulsed Driver
	 */
	public static HLC1395Pulsed getInstance() {
		if (thisSngTask == null) {
			thisSngTask = new HLC1395Pulsed();
		}
		return thisSngTask;
	}
	private HLC1395Pulsed() {}

	/**
	 * Read the value of the given sensor number
	 * 
	 * @param channel	channel/sensor number
	 * @return converted value
	 */
	public short read(int channel) {
		return resultVal[channel];
	}

	/**
	 * Background task loop: Do not call this method!
	 */
	public void action() {
		resultVal[sensAdr] = (short) (US.GET2(RJURR_A + 2) - US.GET2(RJURR_A)); // get result, dark - bright
		sensAdr++;
		if (sensAdr >= nofSensors) sensAdr = 0;
		
		// fire sensor
		int dataReg = US.GET2(Kernel.MPIOSMDR);
		dataReg = dataReg & ~outPinPat; // clear output pins (address and
		// trigger pins)
		dataReg = dataReg | adrPatTab[sensAdr]; // set new address and
		// trigger pins

		US.PUT2(Kernel.MPIOSMDR, dataReg);
		// use queue1, no interrupts, enable single-scan, 
		// interval timer single-scan mode, 256 * QCLK
		US.PUT2(QACR1_A, 0x2500);
		// trig pulse must not be too short
		// small delay by last register access
		dataReg = dataReg & ~trigPinPat; // clear trigger pin
		US.PUT2(Kernel.MPIOSMDR, dataReg);
	}

	/**
	 * Initialize sensors.
	 * 
	 * @param numberOfSensors	Number of connected sensors: 0 &lt; numberOfSensors &lt;= 16
	 * @param pinNumbers		Pin numbers of the 4 address pins and of the trigger pin.
	 * 							Use 4 bits per pin in the following order: trgPin, adr3Pin,
	 * 							adr2Pin, adr1Pin, adr0Pin. Example: 0xF85AC means trgPin =
	 * 							MPIOB15, adr3Pin = MPIOB8, adr2Pin =  MPIOB5, adr1Pin =
	 * 							MPIOB10 and adr0Pin = MPIOB12.
	 * @param analogInChn		Pin number for the analog input channel (ANx).
	 */
	public void init(int numberOfSensors, int pinNumbers, int analogInChn) {
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
		US.PUT2(QADC64MCR_A, 0);
		// internal multiplexing, QCLK = 2 MHz
		US.PUT2(QACR0_A, 0x00B7);

		// setup queue1 at the beginning of the CCW
		// pause after conversion, max sample time, use input channel
		US.PUT2(CCW_A, 0x02C0 + analogInChn);
		// max sample time, use input channel
		US.PUT2(CCW_A + 2, 0x00C0 + analogInChn);
		// end of queue
		US.PUT2(CCW_A + 4, 0x003F);
	}

	/**
	 * Initialize sensors. Set unused address pins to -1.
	 * 
	 * @param addr3Pin		MPIOB pin for the address lane 3.
	 * @param addr2Pin		MPIOB pin for the address lane 2.
	 * @param addr1Pin		MPIOB pin for the address lane 1.
	 * @param addr0Pin		MPIOB pin for the address lane 0.
	 * @param trgPin		MPIOB pin for the trigger signal.
	 * @param analogInPin	ADC-A channel for the sensor signal.
	 */
	public void init(int addr3Pin, int addr2Pin, int addr1Pin,
			int addr0Pin, int trgPin, int analogInPin) {
		int val = getNofSensAndPinNumbers(addr3Pin, addr2Pin, addr1Pin,
				addr0Pin, trgPin);
		init(val >> 20, val, analogInPin); // nofSens = val >> 20;
	}
	
	/**
	 * Stop reading the sensors.
	 */
	public void stop() {
		Task.remove(thisSngTask);
	}

	/**
	 * Start reading the sensors.<br>
	 * This method must be called after the initialization
	 * or after a call of <code>stop()</code>.
	 */
	public void start() {
		sensAdr = 0;
		thisSngTask.period = maxNofSensors / nofSensors;
		Task.install(thisSngTask);
	}

	private int getNofSensAndPinNumbers(int adr3PinNr, int adr2PinNr,
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
//		if (nofSens == 16)
//			nofSens = 15;
		while (n > 0) {
			pinNumbers = pinNumbers << 4 | (adr3PinNr & 0xF);
			adr3PinNr = adr2PinNr;
			adr2PinNr = adr1PinNr;
			adr1PinNr = adr0PinNr;
			n = n >> 1;
		}
		return pinNumbers | (nofSens << 20);
	}

}