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

import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;
import ch.ntb.inf.deep.runtime.ppc32.Task;

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