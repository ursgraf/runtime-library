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

package ch.ntb.inf.deep.runtime.mpc555.test;
import ch.ntb.inf.deep.runtime.mpc555.*;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */
class BlinkerApplication {
	static int res;
	static Blinker blinker14, blinker13, blinker12, blinker11;
	static Task task1;
	
	static void getNofBlinkers () {
		res = Blinker.getNofBlinkers();
	}
	
	static void changePeriod14to100 () {
		Task.remove(blinker14);
		blinker14.changePeriod(100);
		Task.install(blinker14);
	}
	static void changePeriod14to1000 () {
		Task.remove(blinker14);
		blinker14.changePeriod(1000);
		Task.install(blinker14);
	}
	
	static void changePeriod13 () {
		if (task1 instanceof Blinker) ((Blinker)task1).changePeriod(2000);
	}
	
	static {
		blinker14 = new Blinker(14, 500); 
		blinker12 = new Blinker(12, 1000, 5); 
		blinker13 = new Blinker(13, 100, 20); 
		blinker11 = new Blinker(11, 500, 30); 
		task1 = blinker13;
	}
}