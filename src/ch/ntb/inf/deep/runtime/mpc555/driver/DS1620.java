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

/* Changes:
 * 12.03.08	NTB/UG	created
 */

/**
 *  Driver for the temperature sensor DS1620
 *  connected to MPIOSM-pins.
 */
public class DS1620 {
	byte rst, clk, dq;
	
	void outPattern (byte pat) {
		for (int i = 0; i < 8; i++) {
			MPIOSM_DIO.set(this.dq, (pat & (1 << i)) != 0);
//			for (int k = 0; k < 20; k++);
			MPIOSM_DIO.set(this.clk, false);
//			for (int k = 0; k < 20; k++);
			MPIOSM_DIO.set(this.clk, true);
//			for (int k = 0; k < 20; k++);
		}
	}
	
	/**
	 * start conversions, must be called once upon power-up
	 */
	public void startConvert () {
		MPIOSM_DIO.set(this.rst, true);
		this.outPattern((byte)0xee);
		MPIOSM_DIO.set(this.rst, false);
	}
			
	/**
	 * reads temperature, returns value in deg. centigrade times 2
	 */
	public short read () {
		MPIOSM_DIO.set(this.rst, true);
		this.outPattern((byte)0xaa);
		MPIOSM_DIO.init(this.dq, false);
		MPIOSM_DIO.set(this.clk, false);
		short data = 0;
		for (int i = 0; i < 9; i++) {
			if (MPIOSM_DIO.get(this.dq)) data |= 1 << i;
//			for (int k = 0; k < 10; k++);
			MPIOSM_DIO.set(this.clk, true);
//			for (int k = 0; k < 10; k++);
			MPIOSM_DIO.set(this.clk, false);
//			for (int k = 0; k < 10; k++);
		}
		MPIOSM_DIO.set(this.clk, true);
		MPIOSM_DIO.init(this.dq, true);
		MPIOSM_DIO.set(this.rst, false);
		return data;
	}

	/**
	 * configures the sensor for serial connection
	 * must be called only once, not for each power-cycle
	 */
	public void writeConfig () {
		MPIOSM_DIO.set(this.rst, true);
		this.outPattern((byte)0x0c);
		this.outPattern((byte)0x0a);
		MPIOSM_DIO.set(this.rst, false);
	}
	
	/**
	 * returns configuration data
	 */
	public byte readConfig () {
		MPIOSM_DIO.set(this.rst, true);
		this.outPattern((byte)0xac);
		MPIOSM_DIO.init(this.dq, false);
		MPIOSM_DIO.set(this.clk, false);
		byte data = 0;
		for (int i = 0; i < 8; i++) {
//			for (int k = 0; k < 20; k++);
			if (MPIOSM_DIO.get(this.dq)) data |= 1 << i;
//			for (int k = 0; k < 20; k++);
			MPIOSM_DIO.set(this.clk, true);
//			for (int k = 0; k < 20; k++);
			MPIOSM_DIO.set(this.clk, false);
		}
		MPIOSM_DIO.set(this.clk, true);
		MPIOSM_DIO.init(this.dq, true);
		MPIOSM_DIO.set(this.rst, false);
		return data;
	}
			
	/**
	 * creates new sensor
	 * @param rst
	 *            pin number (MPIOSM) for rst signal
	 * @param clk
	 *            pin number (MPIOSM) for clk signal
	 * @param dq
	 *            pin number (MPIOSM) for dq signal
	 */
	public DS1620 (byte rst, byte clk, byte dq) {
		this.rst = rst;
		this.clk = clk;
		this.dq = dq;
		MPIOSM_DIO.init(this.rst, true); MPIOSM_DIO.set(this.rst, false);
		MPIOSM_DIO.init(this.clk, true); MPIOSM_DIO.set(this.clk, true);
		MPIOSM_DIO.init(this.dq, true);
	}
}
			
			
