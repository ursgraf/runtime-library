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

package org.deepjava.runtime.mpc555.test;

import org.deepjava.runtime.mpc555.driver.*;
import org.deepjava.runtime.ppc32.Task;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */
public class Blinker extends Task{
	static int count;	// class variable
	MPIOSM_DIO out;	// instance variable
	int times;	// instance variable

	public static int getNofBlinkers () { 	// class method
		return count;
	}

	public void changePeriod (int period) {	// instance method 
		this.period = period;
	}

	public void action () {	// instance method, overwritten
		out.set(!out.get());
		if (this.nofActivations == this.times) Task.remove(this);
	}
	
	public Blinker (int pin, int period, int times) {	// base constructor
		this.times = times;
		out = new MPIOSM_DIO(pin, true);
		out.set(false);
		this.period = period;	
		Task.install(this);
		count++;
	}
	
	public Blinker (int pin, int period) {	// second constructor
		this(pin, period, 0);	// call to base constructor
	}

	static {	// class constructor
		count = 0;
	}
}