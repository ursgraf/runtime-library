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

package ch.ntb.inf.deep.runtime.zynq7000.demo;

import ch.ntb.inf.deep.runtime.arm32.Task;

/* changes:
 * 19.10.18	NTB/Urs Graf	creation
 */

class BlinkerApplication {
	static int res;
	static Blinker blinker1;
	static Task task1;
	
	static void getNofBlinkers () {
		res = Blinker.getNofBlinkers();
	}
	
	static void changePeriod1to100 () {
		Task.remove(blinker1);
		blinker1.changePeriod(100);
		Task.install(blinker1);
	}
	static void changePeriod1to1000 () {
		Task.remove(blinker1);
		blinker1.changePeriod(1000);
		Task.install(blinker1);
	}
	
	static {
		blinker1 = new Blinker(14, 100); 
		task1 = blinker1;
	}
}