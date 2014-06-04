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
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class TaskTest1 extends Task {
	static long time1;
	static Task t1;
	
	public void action() {
		try {
			SCI2.write((byte)'c');
		} catch (IOException e) {}
		MPIOSM_DIO.set(12, !MPIOSM_DIO.get(12));
	}
	
	static {
		MPIOSM_DIO.init(12, true);
		SCI2.start(9600, (byte)0, (short)8);
		try {
			SCI2.write((byte)'a');
		} catch (IOException e) {}
		time1 = Kernel.time();
		t1 = new TaskTest1();
		t1.period = 1000; 
		t1.action();
		Task.install(t1);
		try {
			SCI2.write((byte)'b');
		} catch (IOException e) {}
	}
}
