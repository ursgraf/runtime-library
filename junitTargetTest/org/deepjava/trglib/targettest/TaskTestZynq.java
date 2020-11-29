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

package org.deepjava.trglib.targettest;

import org.deepjava.runtime.Kernel;
import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.util.Actionable;
import org.deepjava.runtime.zynq7000.*;

import ch.ntb.inf.junitTarget.*;

public class TaskTestZynq implements Izynq7000 {
	static int res;

	@Before
	public static void startTask() {
		res = 1;
		new TaskExtZynq();
		new ActionableZynq(2);
		CmdTransmitter.sendDone();
	}
	
	@Test
	public static void testTask() {
		Assert.assertEquals("Test1", 1, res);
		Assert.assertEquals("Test2", 5, TaskExtZynq.count);
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testActionable() {
		Assert.assertEquals("Test2", 5, ActionableZynq.count);
		CmdTransmitter.sendDone();
	}	
	
	@Test
	public static void testTaskTime() {
//		US.PUT2(TBSCR, 0); 	// stop timer
		long time = Kernel.timeUs();
		int timeMs = Task.time();	
		Assert.assertEquals("Test1", timeMs, time / 1000);
//		US.PUT2(TBSCR, 1); 	// restart timer
		CmdTransmitter.sendDone();
	}	
}

class TaskExtZynq extends Task {
	static int count;
	
	public void action() {
		count++;
		if (nofActivations == 5) Task.remove(this);
	}
	
	TaskExtZynq() {
		Task.install(this);
	}
}

class ActionableZynq implements Actionable {
	static int count;
	static Task t;
	
	public void action() {
		count++;
		if (count == 5) Task.remove(t);
	}
	
	public static void init() {
		t = new Task(new ActionableZynq(2));
		Task.install(t);
	}
	
	ActionableZynq(int x) {
		count = x;
		t = new Task(this);
		Task.install(t);
	}
}
