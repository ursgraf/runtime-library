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

package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.HLC1395Pulsed;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/* CHANGES:
 * 07.09.2011	NTB/MZ	Created (based on HLC1395Demo)
 */

/**
 * Test application for the HLC1395 experimental module.
 * The application reads periodically the values of four sensors
 * and print them every second to the SCI2.
 * Connecting diagram:
 * <pre>Trigger Pin    -- MPIOB5
 * Address Pin A  -- MPIOB6
 * Address Pin B  -- MPIOB7
 * Sensor out pin -- AN59</pre>
 * 
 * @author Martin Zueger
 */
public class HLC1395Test extends Task {
	
	public void action() {
		for(int i = 0; i < 4; i++) {
			System.out.print(HLC1395Pulsed.read(i));
			System.out.print('\t');
		}
		System.out.println();
	}
	
	static {
		// Initialize HLC1395Pulsed driver for 4 sensors and start reading values
		HLC1395Pulsed.init(4, 0x50076, 59); // initialize 4 sensors (addrPin0 = MPIOB6, addrPin1 = MPIOB7, trgPin = MPIOB5, analogInPin = AN59)
		HLC1395Pulsed.start();
		
		// Initialize SCI2 and set stdout to SCI1
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		System.out.println("HLC1295-Test");
		System.out.println();
		System.out.println("  Connections:");
		System.out.println("  Trigger Pin    -- MPIOB5");
		System.out.println("  Address Pin A  -- MPIOB6");
		System.out.println("  Address Pin B  -- MPIOB7");
		System.out.println("  Sensor out pin -- AN59");
		System.out.println();
		System.out.println("1:\t2:\t3:\t4:");
		
		// Create and install task
		Task t = new HLC1395Test();
		t.period = 1000;
		Task.install(t);
	}
}
