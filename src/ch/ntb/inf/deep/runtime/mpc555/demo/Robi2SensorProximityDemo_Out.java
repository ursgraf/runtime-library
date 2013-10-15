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

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;


/**
 * This class is part of the Robi2SensorProximityDemo.
 */
public class Robi2SensorProximityDemo_Out extends Task {
	static final short NoOfSensors = Robi2SensorProximityDemo.NoOfSensors;

	private static int count;

	public void action( ) {
		for (short i = 0; i < NoOfSensors; i++) {
			int val = Robi2.getDistSensorValue(i);
			System.out.print(val);
			if (count < NoOfSensors-1) {
				System.out.print("\t");
				count++;
			} else {
				System.out.println();
				count = 0;
			}
		}
	}
	
	static {
		// Initialize SCI1 (9600 8N1)
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		
		// Use the SCI1 for stdout
		System.out = new PrintStream(SCI1.out);
		count = 0;
	}
}