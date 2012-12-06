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
 * Demo application for the NTB Robi2. The Robi is driving around and avoids
 * collisions with obstacles.
 * 
 * This class initializes the task as needed and carries the driving control. In
 * order to use it, build a class creating an instance of this class and install
 * it using Task.install
 */
public class Robi2ObstacleTask extends Task {
	private static final short threshold = 70;
	private static final short turnSpeed = 100;
	private static final short driveSpeed = 60;
	short state = 0;
	boolean turnLeft = false;

	public Robi2ObstacleTask() {
		// Task initialization
		this.period = 0;

		// Enable some LEDs
		for (int i = 0; i < 4; i++) {
			Robi2.setPatternLED(i, 0, true);
			Robi2.setPatternLED(i, 2, true);
		}
		Robi2.setPatternLED(0, 1, true);
		Robi2.setPatternLED(3, 1, true);
	}

	private boolean hasBarrier() {
		boolean hasBarrier = false;
		for (int i = 4; i < 7; i++) {
			if (Robi2.getDistSensorValue(i) > threshold) {
				hasBarrier = true;
				turnLeft = false;
			}
		}
		for (short i = 10; i < 13; i++) {
			if (Robi2.getDistSensorValue(i) > threshold) {
				hasBarrier = true;
				turnLeft = true;
			}
		}
		if (Robi2.getDistSensorValue(2) > threshold) {
			hasBarrier = true;
			turnLeft = true;
		}
		return hasBarrier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action() {
		switch (state) {
		case 0: {
			if (hasBarrier()) {
				state = 1;
			}
			break;
		}
		case 1: // barrier ahead, turn roboter
			if (hasBarrier()) {
				state = 2;
				Robi2.stop();
				if (turnLeft) {
					Robi2.setRightDriveSpeed(-turnSpeed);
					Robi2.setLeftDriveSpeed(-turnSpeed);
				} else {
					Robi2.setRightDriveSpeed(turnSpeed);
					Robi2.setLeftDriveSpeed(turnSpeed);
				}
			}
			break;
		case 2: // noc barrier ahead, go straigt forward
			if (!hasBarrier()) {
				state = 1;
				Robi2.stop();
				Robi2.setRightDriveSpeed(-driveSpeed);
				Robi2.setLeftDriveSpeed(driveSpeed);
			}
			break;
		default:
			// should never be reachead
			state = 0;
			break;
		}
	}
}