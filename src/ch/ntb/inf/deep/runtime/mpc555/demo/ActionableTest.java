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

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.runtime.ppc32.Task;
import ch.ntb.inf.deep.runtime.util.Actionable;

/**
 * This test class demonstrates the use of the <code>Actionable</code> interface.
 * <code>ActionableTest</code> is a subclass of <code>Test</code> and overrides its 
 * <code>print</code> method. <br>
 * <code>ActionableTest</code> also implements <code>Actionable</code> and defines its 
 * <code>action</code> method. When creating a <code>Task</code>, an instance of 
 * <code>ActionableTest</code> has to be passed as a parameter. <br>
 * This test class will printout "hello world" five times and then stops.
 * 
 * @author Urs Graf
 */
public class ActionableTest extends Test implements Actionable {
	
	static Task t;
	
	@Override
	public void action() {
		print();
		if (t.nofActivations == 5) Task.remove(t);
	}
	
	@Override
	public void print() {
		System.out.println("hello world");
	}
	
	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.out.println("Actionable test");
		t = new Task(new ActionableTest());
		t.period = 1000;
		Task.install(t);
	}
}

class Test {
	
	final String str = "hello";
	
	public void print() {
		System.out.println(str);
	}
}

