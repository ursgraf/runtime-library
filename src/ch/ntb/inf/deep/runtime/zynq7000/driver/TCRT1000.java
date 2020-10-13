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

package ch.ntb.inf.deep.runtime.zynq7000.driver;

import ch.ntb.inf.deep.flink.core.FlinkDefinitions;
import ch.ntb.inf.deep.flink.core.FlinkDevice;
import ch.ntb.inf.deep.flink.subdevices.FlinkADC;
import ch.ntb.inf.deep.flink.subdevices.FlinkGPIO;
import ch.ntb.inf.deep.runtime.Kernel;
import ch.ntb.inf.deep.runtime.arm32.Task;
import ch.ntb.inf.deep.runtime.zynq7000.Izynq7000;

/* CHANGES:
 * 28.04.2020 NTB/UG	creation
 */

/**
 * Driver for up to 16 pulsed and multiplexed TCRT1000 or HLC1393 reflection distance
 * sensors.<br>
 * The driver needs 5 digital outputs and a single analog input of an AD7476.
 * 4 of the digital outputs are used as address channels and the fifth
 * one is the trigger signal. The analog input is used to read the sensor
 * values.<br>
 * All the sensors are repetitively sampled within 16ms, regardless of the number of sensors.<br>  
 */
public class TCRT1000 extends Task implements Izynq7000, FlinkDefinitions {

	private static final byte maxNofSensors = 16;
	private static TCRT1000 thisSngTask; // Singleton DistSense Task
	private int nofSensors; // number of connected sensors
	private int trigPin; // trigger pin
	private int addrPin0, addrPin1, addrPin2, addrPin3; // address pins
	private int sensAdr; // sensor address
	private static FlinkGPIO gpio;
	private static FlinkADC adc;
	private static State state;
	private static long time;
	private short[] resultVal = new short[maxNofSensors];

	/**
	 * Returns an instance of <i>TCRT1000</i> 
	 * @return Instance of TCRT1000
	 */
	public static TCRT1000 getInstance() {
		if (thisSngTask == null) {
			thisSngTask = new TCRT1000();
		}
		return thisSngTask;
	}
	
	private TCRT1000() {}

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
		switch (state) {
		case Start:
			time = Kernel.timeUs();
			if (sensAdr % 2 == 0) gpio.setValue(addrPin0, false);
			else gpio.setValue(addrPin0, true);
			if (sensAdr / 2 % 2 == 0) gpio.setValue(addrPin1, false);
			else gpio.setValue(addrPin1, true);
			if (sensAdr / 4 % 2 == 0) gpio.setValue(addrPin2, false);
			else gpio.setValue(addrPin2, true);
			if (sensAdr % 8 == 0) gpio.setValue(addrPin3, false);
			else gpio.setValue(addrPin3, true);
			gpio.setValue(trigPin, true);
			gpio.setValue(trigPin, false);
			state = State.Sample;
			break;
		case Sample:
			if (Kernel.timeUs() - time > 100) {
				resultVal[sensAdr] = (short) adc.getValue(0);
				state = State.Wait;
			}
			break;
		case Wait:
			if (Kernel.timeUs() - time > 1000) {
				sensAdr = (sensAdr + 1) % maxNofSensors;
				if (sensAdr < nofSensors) state = State.Start;
				else {
					state = State.Idle;
					time = Kernel.timeUs();
				}
			}
			break;
		case Idle:
			if (Kernel.timeUs() - time > 1000) {
				sensAdr = (sensAdr + 1) % maxNofSensors;
				time = Kernel.timeUs();
				if (sensAdr == 0) state = State.Start;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Initialize sensors.
	 * 
	 * @param nofSensors	Number of connected sensors: 0 &lt; numberOfSensors &lt;= 16
	 * @param addr3Pin		Highest order address pin
	 * @param addr2Pin		Address pin
	 * @param addr1Pin		Address pin
	 * @param addr0Pin		Lowest order address pin
	 * @param trigPin		Trigger pin
	 */
	public void init(int nofSensors, int addr3Pin, int addr2Pin, int addr1Pin, int addr0Pin, int trigPin) {
		gpio = FlinkDevice.getGPIO();
		adc = FlinkDevice.getAD7476();
		if (nofSensors > maxNofSensors) this.nofSensors = maxNofSensors;
		else if (nofSensors < 1) this.nofSensors = 1;
		else this.nofSensors = nofSensors;
		gpio.setDir(trigPin, true);
		gpio.setDir(addr3Pin, true);
		gpio.setDir(addr2Pin, true);
		gpio.setDir(addr1Pin, true);
		gpio.setDir(addr0Pin, true);
		this.trigPin = trigPin;
		this.addrPin3 = addr3Pin;
		this.addrPin2 = addr2Pin;
		this.addrPin1 = addr1Pin;
		this.addrPin0 = addr0Pin;
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
		state = State.Start;
		sensAdr = 0;
		Task.install(thisSngTask);
	}

}

enum State {
	Start, Sample, Wait, Idle
}