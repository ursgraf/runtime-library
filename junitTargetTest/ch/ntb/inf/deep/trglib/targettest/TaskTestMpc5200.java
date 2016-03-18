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

package ch.ntb.inf.deep.trglib.targettest;

import ch.ntb.inf.deep.runtime.mpc5200.*;
import ch.ntb.inf.deep.runtime.ppc32.Task;
import ch.ntb.inf.deep.runtime.util.Actionable;
import ch.ntb.inf.deep.unsafe.US;
import ch.ntb.inf.junitTarget.*;

public class TaskTestMpc5200 implements Impc5200 {
	static int res;

	@Before
	public static void startTask() {
		res = 1;
		new TaskExt1();
		new ActionableImpl1(2);
		CmdTransmitter.sendDone();
	}
	
	@Test
	public static void testTask() {
		Assert.assertEquals("Test1", 1, res);
		Assert.assertEquals("Test2", 5, TaskExt1.count);
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testActionable() {
		Assert.assertEquals("Test2", 5, ActionableImpl1.count);
		CmdTransmitter.sendDone();
	}	
	
	@Test
	public static void testTaskTime() {
		US.PUT4(XLBACR, 0); 	// stop timer
		long time = Kernel.time();
		int timeMs = Task.time();	
		Assert.assertEquals("Test1", timeMs, time / 1000);
		US.PUT4(XLBACR, 0x00002006); 	// restart timer
		CmdTransmitter.sendDone();
	}	
}

class TaskExt1 extends Task {
	static int count;
	
	public void action() {
		count++;
		if (nofActivations == 5) Task.remove(this);
	}
	
	TaskExt1() {
		Task.install(this);
	}
}

class ActionableImpl1 implements Actionable {
	static int count;
	static Task t;
	
	public void action() {
		count++;
		if (count == 5) Task.remove(t);
	}
	
	public static void init() {
		t = new Task(new ActionableImpl1(2));
		Task.install(t);
	}
	
	ActionableImpl1(int x) {
		count = x;
		t = new Task(this);
		Task.install(t);
	}
}
