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

import org.deepjava.runtime.mpc555.driver.MPIOSM_DIO;
import org.deepjava.runtime.ppc32.Task;
import org.deepjava.runtime.util.Actionable;

public class ActionableTest1 implements Actionable {
	MPIOSM_DIO out;

	@Override
	public void action() {
		out.set(!out.get());
	}
	
	public ActionableTest1(int pin) {
		out = new MPIOSM_DIO(pin, true);
		out.set(false);
	}

	static {
		Task t = new Task(new ActionableTest1(15));
		t.period = 100;
		Task.install(t);
	}
}
