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

import java.io.PrintStream;

import org.deepjava.runtime.mpc555.driver.SCI;
import org.deepjava.runtime.ppc32.Task;

public class EnumTest1 extends Task {

	static State state = State.STARTING;

	public void action() {
		switch (state) {
		case STARTING: state = State.RUNNING; break;
		case RUNNING: state = State.STOPPING; break;
		case STOPPING: state = State.STOPPED; break;
		case STOPPED: state = State.STARTING; break;
		}

		if (this.nofActivations % 101 == 0) {
			System.out.println(state.name());
		}
	}
	
	static void test1() {
		System.out.println(State.STOPPING.name());
		System.out.println(State.STOPPING.toString());
		System.out.println(State.valueOf("STOPPING").name());
		System.out.print("ordinal of STOPPING = "); System.out.println(State.STOPPING.ordinal());
	}

	static void test2() {
		int res = 0;
		State state = State.STOPPED;
		switch (state) {
		case STARTING: res = 1000; break;
		case RUNNING: res = 2000; break;
		case STOPPING: res = 3000; break;
		case STOPPED: res = 4000; break;
		}
		System.out.println(res);
	}

	static void test3() {
		int res = 0;
		Color state = Color.BLACK;
		switch (state) {
		case WHITE: res = 1000; break;
		case BLACK: res = 2000; break;
		case RED: res = 3000; break;
		}
		System.out.println(res);
	}

	static {	
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.out.println("test started");
		test1();
		test2();
		test3();
		System.out.println("test method done");
		Task t = new EnumTest1();	
		t.period = 50;
		Task.install(t);
	}
}

enum State {STARTING, RUNNING, STOPPING, STOPPED}
enum Color {WHITE, BLACK, RED}
