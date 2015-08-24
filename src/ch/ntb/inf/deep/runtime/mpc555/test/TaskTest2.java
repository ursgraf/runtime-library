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
import java.io.IOException;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/*changes:
 * 10.1.11	NTB/GRAU	creation
 */

public class TaskTest2 extends Task {
	long startTime;
	static TaskTest2 t1;
	static MPIOSM_DIO out;
	static SCI sci;
	
	public void action() {
		try {
			sci.write((byte)'.');
		} catch (IOException e) {};
		if (Kernel.time() > startTime + 100000) {
			out.set(!out.get());
			startTime = Kernel.time();
		}
	}
	
	public TaskTest2(int pin) {
		try {
			sci.write((byte)'a');
		} catch (IOException e) {}
		this.startTime = Kernel.time();
		out = new MPIOSM_DIO(pin, true);
		period = 500;
		time = 50;
		Task.install(this);
		try {
			sci.write((byte)'b');
		} catch (IOException e) {}
	}
	
	static {
		sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, (byte)0, (short)8);
		try {
			sci.write((byte)'0');
		} catch (IOException e) {}
		t1 = new TaskTest2(9);
//		TaskTest2 t2 = new TaskTest2(10);
	}
}
