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