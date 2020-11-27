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

package org.deepjava.runtime.mpc555.demo;

import org.deepjava.runtime.mpc555.driver.Robi2;
import org.deepjava.runtime.ppc32.Task;

/**
 * This class is part of the Robi2SensorProximityDemo.
 */
public class Robi2SensorProximityDemo_Led extends Task {
	static final short NoOfSensors = Robi2SensorProximityDemo.NoOfSensors;
	
	static final short Limit = 100;

	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action( ) {
		boolean barrier;
		for (short i = 0; i < NoOfSensors; i++) {
			barrier = false;
			int val = Robi2.getDistSensorValue(i);
			if (val > Limit) {
				barrier= true;
			}
			if(i <12){
				Robi2.setPatternLED( i/3, i%3, barrier);
			}else{
				switch(i){
				case 12:
					Robi2.setHeadPosLED(barrier);
					break;
				case 13:
					Robi2.setLeftPosLED(barrier);
					break;
				case 14:
					Robi2.setRightPosLED(barrier);
					break;
				case 15:
					Robi2.setCenterLED(barrier);
					break;
				}
			}
		}
	}
	
}