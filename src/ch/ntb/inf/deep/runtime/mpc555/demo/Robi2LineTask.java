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

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;

/**
 * Demo application for the NTB Robi2. Let the Robi following a line on the
 * surface.
 * 
 * This class initializes the task as needed and carries the driving control. In
 * order to use it, build a class creating an instance of this class and install
 * it using Task.install
 */
public class Robi2LineTask extends Task {
	public Robi2LineTask() {
		// Setup the demo task
		this.period = 0;

		// Initialize some LEDs
		for (int i = 0; i < 4; i++) {
			Robi2.setPatternLED(i, 1, true);
		}
	}

	private boolean sensors() {
		boolean isSensor = false;
		if (Robi2.getDistSensorValue(2) > 250) {
			isSensor = true;
		}
		for (short i = 4; i < 16; i++) {
			if (Robi2.getDistSensorValue(i) > 250) {
				isSensor = true;
			}
		}
		return isSensor;
	}

	private boolean leftFloorSensor() {
		return (Robi2.getDistSensorValue(0) > 550);
	}

	private boolean rightFloorSensor() {
		return (Robi2.getDistSensorValue(1) > 550);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action() {
		if (sensors()) {
			Robi2.stop();
		} else if (leftFloorSensor() && rightFloorSensor()) {
			Robi2.setRightDriveSpeed(50);
			Robi2.setLeftDriveSpeed(100);
		} else if (!rightFloorSensor() && !leftFloorSensor()) {
			Robi2.setRightDriveSpeed(-50);
			Robi2.setLeftDriveSpeed(-100);
		} else if (!leftFloorSensor() && rightFloorSensor()) {
			Robi2.setRightDriveSpeed(-50);
			Robi2.setLeftDriveSpeed(-100);
		} else {
			Robi2.drive(80);
		}
	}
}