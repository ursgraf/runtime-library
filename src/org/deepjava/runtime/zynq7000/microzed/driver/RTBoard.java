package org.deepjava.runtime.zynq7000.microzed.driver;

import org.deepjava.flink.core.FlinkDevice;
import org.deepjava.flink.subdevices.FlinkADC;
import org.deepjava.flink.subdevices.FlinkDAC;
import org.deepjava.flink.subdevices.FlinkCounter;
import org.deepjava.runtime.zynq7000.microzed.IMicroZed;
import org.deepjava.unsafe.arm.US;

/*
 * Copyright 2011 - 2021 NTB University of Applied Sciences in Technology
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

/**
 * Driver for the Microzed board for control applications.
 * This board comprises two analog outputs with 1A current supply and 
 * two analog input channels. Further there are 4 digital input channels
 * which can be used to read two encoders.
 * The board incorporates 4 Leds and two push buttons.
 * The PL must be loaded with flink4 configuration.
 * 
 * @author Graf Urs
 */

/* Changes:
 * 23.11.2021 Urs Graf: creation
 */

public class RTBoard implements IMicroZed {

	private static final int adcRes;
	private static final int dacRes;
	private static FlinkADC adc;
	private static FlinkDAC dac;
	private static FlinkCounter fqd;

	/**
	 * Returns the value of an analog input channel.<br>
	 * The analog signal will be read from <code>channel</code>.
	 * The channel 0 carries the name <code>AIn1</code> and channel 1 carries the name <code>AIn2</code>. The range 
	 * of the return value is between -10..+10 corresponding to Volts.
	 * The resolution of the ADC is 12 bit.
	 * 
	 * @param channel
	 *            Channel with analog signal.
	 * @return Value in Volts (-10..+10).
	 */
	public static float analogIn(int channel) {
		return ((adc.getValue(channel)) - adcRes) * 10 / (float)adcRes;
	}

	/**
	 * Writes a value to an regular analog output <code>channel</code>
	 * The channel 0 is denoted with <code>AOut1</code> and channel 1 is denoted <code>AOut2</code>. 
	 * The range of <code>val</code> is between -10..+10 corresponding to Volts
	 * The resolution of the DAC is 12 bit.
	 * 
	 * @param channel
	 *            Channel with analog signal.
	 * @param val
	 *            Value in Volts (-10..+10).
	 */
	public static void analogOut(int channel, float val) {
		dac.setValue(channel, (int)(val / 10 * dacRes) + dacRes);
	}

	/**
	 * The state of the two push buttons can be read. 
	 * Channels are numbered 0 for switch <code>SW2</code> and 1 for switch <code>SW3</code>. 
	 * The value <code>true</code> corresponds to the logical signal <code>1</code>.
	 * 
	 * @param channel
	 *            Channel to be read.
	 * @return Digital signal at <code>channel</code>.
	 */
	public static boolean buttonIn(int channel) {
		int reg = US.GET4(GPIO_IN0);
		if (channel == 0) return (reg & 0x8000) != 0;
		else return (reg & 0x4000) != 0;
	}

	/**
	 * Write a digital output to a led.
	 * leds are numbered <code>0..3</code>. That corresponds to
	 * leds <code>LED2</code>, <code>LED3</code>, <code>LED4</code>, <code>LED5</code>.
	 * 
	 * @param channel
	 *            Led channel.
	 * @param level
	 *            <code>true</code> corresponds to the led lightening up.
	 */
	public static void ledOut(int channel, boolean level) {
		int reg = ~(1 << (16 + 10 + channel));
		if (!level) reg &= 0xffff0000;
		US.PUT4(GPIO_MASK_LSW0, reg);
	}

	/**
	 * Reads the encoder position.<br>
	 * 
	 * @param channel Channel of encoder input (0 or 1).
	 * @return Position in counter values.
	 */
	public static short getEncCount(int channel) {
		return fqd.getCount(channel);
	}

	/**
	 * Sets both encoder channels to zero.<br>
	 */
	public static void reset() {
		fqd.reset();
	}

	static {
		fqd = FlinkDevice.getCounter();
		adc = FlinkDevice.getADC128S102();
		dac = FlinkDevice.getAD5668();

		// init leds and buttons
		int reg = US.GET4(GPIO_DIR0);
		reg |= 0x3c00; 
		reg &= ~0xc000;
		US.PUT4(GPIO_DIR0, reg);
		reg = US.GET4(GPIO_OUT_EN0);
		reg |= 0x3c00; 
		reg &= ~0xc000;
		US.PUT4(GPIO_OUT_EN0, reg);
		reg = US.GET4(GPIO_OUT0);
		reg &= ~0x3c00; 
		US.PUT4(GPIO_OUT0, reg);
		
		adcRes = adc.getResolution() / 2;
		dacRes = dac.getResolution() / 2;
		analogOut(0, 0);
		analogOut(1, 0);
	}
}
