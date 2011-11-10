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

package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.BlueRSCmdInt;

/* CHANGES:
 * 09.03.11 NTB/Roger Millischer	adapted to the new deep environment
 */

/**
 * Simple application for demonstrating the usage of the
 * BlueRSCmdInt driver with a Stollmann BlueRS+I module.
 */
public class BlueRSCmdIntDemo extends Task {
	private final static String partner = "008025003E46";
	private static final int resetPin = 15;
	private static int cmd = 1;

	public void action() { // Print status changes and received commands
		int status = BlueRSCmdInt.getStatus();
		printStatus(status);
		if (status == BlueRSCmdInt.getStatus()) {
			int rxCmd = BlueRSCmdInt.getReceivedCmd();
			if (rxCmd > 0) {
				System.out.print("Cmd received -> ");
				System.out.println(rxCmd);
			}
		}
	}

	public static void connect() { // Connect to the partner module
		if (BlueRSCmdInt.getStatus() == BlueRSCmdInt.disconnected)
			BlueRSCmdInt.connect(partner);
		else 
			System.out.println("Wrong mode");
	}

	public static void disconnect() {// Disconnect from the partner module
		if (BlueRSCmdInt.getStatus() == BlueRSCmdInt.connected)
			BlueRSCmdInt.disconnect();
		else
			System.out.println("Wrong mode");
	}

	public static void sendCmd() {// Send a command
		if (BlueRSCmdInt.getStatus() == BlueRSCmdInt.connected)
			BlueRSCmdInt.sendCommand(cmd++);
		else
			System.out.println("Wrong mode");
	}

	private static int lastStatus = -1;

	private void printStatus(int status) {
		if (status != lastStatus) {
			lastStatus = status;
			switch (status) {
			case BlueRSCmdInt.disconnected:
				System.out.println("BlueRS -> Disconnected");
				break;
			case BlueRSCmdInt.connecting:
				System.out.println("BlueRS -> Connecting");
				break;
			case BlueRSCmdInt.connected:
				System.out.println("BlueRS -> Connected");
				break;
			case BlueRSCmdInt.disconnecting:
				System.out.println("BlueRS -> Disconnecting");
				break;
			}
		}
	}

	static {
		//initialize SCI1
		SCI1.start(9600, SCI1.NO_PARITY, (short) 8);
		//hook SCI1 to System.out
		System.out = new PrintStream(SCI1.out);

		MPIOSM_DIO.init(resetPin, true); // Init Mpiosm
		MPIOSM_DIO.set(resetPin, false); // Reset BlueRS
		Task t = new BlueRSCmdIntDemo();
		t.period = 100;
		Task.install(t);
		MPIOSM_DIO.set(resetPin, true);
	}
}