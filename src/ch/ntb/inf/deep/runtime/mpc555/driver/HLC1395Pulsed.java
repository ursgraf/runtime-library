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

package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/* CHANGES:
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
 * sensors.
 * 
 * The driver needs 5 digital outputs of the MPIOSM and a single analog input of
 * QADC-A. 4 of the digital outputs are used as address channels and the fifth
 * one is the trigger signal. The analog input is used to read the sensor
 * values. It is highly recommended to neither use the channels AN0...AN3 nor
 * the channels AN48...AN51 because this pins have on some NTB MPC555-Headerboards
 * a RC input filter.
 * 
 * <strong>IMPORTANT:</strong> Connect AGnd to Gnd!
 * 
 */
public class HLC1395Pulsed extends Task implements ntbMpc555HB {

	private static final byte maxNofSensors = 16, maxAnalogInPortNr = 59;
	private static final HLC1395Pulsed thisSngTask; // Singleton DistSense Task
	private static int nofSensors; // Number of connected sensors
	private static int trigPinPat; // trigger pin bit pattern
	private static int outPinPat; // bit pattern for all address pins and the trigger pin
	private static int sensAdr; // sensor address

	// Address pattern table, adrPatTab[s]: address bit pattern for sensor s
	private static final short[] adrPatTab = new short[maxNofSensors];
	private static short[] resultVal = new short[16];

	private HLC1395Pulsed() {}

	/**
	 * Read the value of the given sensor number
	 * 
	 * @param channel	channel/sensor number
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

	/**
	 * Initialize sensors.
	 * 
	 * @param numberOfSensors	Number of connected sensors: 0 < numberOfSensors <= 16
	 * @param pinNumbers		Pin numbers of the 4 address pins and of the trigger pin.
	 * 							Use 4 bits per pin in the following order: trgPin, adr3Pin,
	 * 							adr2Pin, adr1Pin, adr0Pin. Example: 0xF85AC means trgPin =
	 * 							MPIOB15, adr3Pin = MPIOB8, adr2Pin =  MPIOB5, adr1Pin =
	 * 							MPIOB10 and adr0Pin = MPIOB12.
	 * @param analogInChn		Pin number for the analog input channel (ANx).
	 */
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
		US.PUT2(QADC64MCR_A, 0);
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
	 * Initialize sensors.
	 * 
	 * @param addr3Pin		MPIOB pin for the address lane 3.
	 * @param addr2Pin		MPIOB pin for the address lane 2.
	 * @param addr1Pin		MPIOB pin for the address lane 1.
	 * @param addr0Pin		MPIOB pin for the address lane 0.
	 * @param trgPin		MPIOB pin for the trigger signal.
	 * @param analogInPin	ADC-A channel for the sensor signal.
	 */
	public static void init(int addr3Pin, int addr2Pin, int addr1Pin,
			int addr0Pin, int trgPin, int analogInPin) {
		int val = getNofSensAndPinNumbers(addr3Pin, addr2Pin, addr1Pin,
				addr0Pin, trgPin);
		init(val >> 20, val, analogInPin); // nofSens = val >> 20;
	}
	
	/**
	 * Stop reading sensors.
	 */
	public static void stop() {
		Task.remove(thisSngTask);
	}

	/**
	 * Start reading sensors.<br>
	 * This method must be called after the initialization
	 * or after a call of <code>stop()</stop>.
	 */
	public static void start() {
		thisSngTask.period = 1;
		Task.install(thisSngTask);
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

	static {
		thisSngTask = new HLC1395Pulsed();
	}
	
}