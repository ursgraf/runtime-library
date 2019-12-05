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

package ch.ntb.inf.deep.runtime.zynq7000.microzed.demo;

import ch.ntb.inf.deep.runtime.arm32.Task;
import ch.ntb.inf.deep.runtime.zynq7000.Izynq7000;
import ch.ntb.inf.deep.unsafe.arm.US;

/* changes:
 * 19.10.18	NTB/Urs Graf	creation
 */

public class Blinker extends Task implements Izynq7000 {
	static Blinker blinker;
	int times;

	public void changePeriod (int period) {	// instance method 
		this.period = period;
	}

	public void action () {	// instance method, overwritten
		US.PUT4(GPIO_OUT1, US.GET4(GPIO_OUT1) ^ 0x8000);
		if (this.nofActivations == this.times) Task.remove(this);
	}
	
	public Blinker (int period, int times) {	// base constructor
		this.times = times;
		US.PUT4(SLCR_UNLOCK, 0xdf0d);
		US.PUT4(MIO_PIN_47, 0x300);
		US.PUT4(SLCR_LOCK, 0x767b);
		US.PUT4(GPIO_DIR1, 0x8000);
		US.PUT4(GPIO_OUT_EN1, 0x8000);
		this.period = period;	
		Task.install(this);
	}
	
	public Blinker (int period) {	// second constructor
		this(period, 0);	// call to base constructor
	}

	static void changePeriod1to100 () {
		Task.remove(blinker);
		blinker.period = 100;
		Task.install(blinker);
	}
	static void changePeriod1to1000 () {
		Task.remove(blinker);
		blinker.period = 1000;
		Task.install(blinker);
	}

	static {	// class constructor
		blinker = new Blinker(1000);
	}
}