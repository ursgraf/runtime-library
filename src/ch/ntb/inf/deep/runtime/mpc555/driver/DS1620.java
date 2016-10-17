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

/* Changes:
 * 12.03.08	NTB/UG	created
 */

/**
 *  Driver for the temperature sensor DS1620
 *  connected to pins of the MPIOSM.
 */
public class DS1620 {
	MPIOSM_DIO rst, clk, dq;
	
	void outPattern (byte pat) {
		for (int i = 0; i < 8; i++) {
			dq.set((pat & (1 << i)) != 0);
//			for (int k = 0; k < 20; k++);
			clk.set(false);
//			for (int k = 0; k < 20; k++);
			clk.set(true);
//			for (int k = 0; k < 20; k++);
		}
	}
	
	/**
	 * start conversions, must be called once upon power-up
	 */
	public void startConvert () {
		rst.set(true);
		this.outPattern((byte)0xee);
		rst.set(false);
	}
			
	/**
	 * Reads temperature, returns value in deg. centigrade times 2.
	 * @return Temperature in degrees centigrade times 2.
	 */
	public short read () {
		rst.set(true);
		this.outPattern((byte)0xaa);
		dq.dir(false);
		clk.set(false);
		short data = 0;
		for (int i = 0; i < 9; i++) {
			if (dq.get()) data |= 1 << i;
//			for (int k = 0; k < 10; k++);
			clk.set(true);
//			for (int k = 0; k < 10; k++);
			clk.set(false);
//			for (int k = 0; k < 10; k++);
		}
		clk.set(true);
		dq.dir(true);
		rst.set(false);
		return data;
	}

	/**
	 * Configures the sensor for serial connection
	 * must be called only once, not for each power-cycle.
	 */
	public void writeConfig () {
		rst.set(true);
		this.outPattern((byte)0x0c);
		this.outPattern((byte)0x0a);
		rst.set(false);
	}
	
	/**
	 * Returns configuration data
	 * @return Configuration word.
	 */
	public byte readConfig () {
		rst.set(true);
		this.outPattern((byte)0xac);
		dq.dir(false);
		clk.set(false);
		byte data = 0;
		for (int i = 0; i < 8; i++) {
//			for (int k = 0; k < 20; k++);
			if (dq.get()) data |= 1 << i;
//			for (int k = 0; k < 20; k++);
			clk.set(true);
//			for (int k = 0; k < 20; k++);
			clk.set(false);
		}
		clk.set(true);
		dq.dir(true);
		rst.set(false);
		return data;
	}
			
	/**
	 * Creates new sensor
	 * @param reset
	 *            pin number (MPIOSM) for rst signal
	 * @param clock
	 *            pin number (MPIOSM) for clk signal
	 * @param data
	 *            pin number (MPIOSM) for dq signal
	 */
	public DS1620 (byte reset, byte clock, byte data) {
		rst = new MPIOSM_DIO(reset, true); rst.set(false);
		clk = new MPIOSM_DIO(clock, true); clk.set(true);
		dq = new MPIOSM_DIO(data, true);
	}
}
			
			
