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
import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.QADC_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.runtime.ppc32.Heap;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/*changes:
 * 13.03.15	NTB/GRAU	creation
 */

public class HeapTest4 extends Task {
	
	public void action() {
		int[] a = new int[10000];
		System.out.println("allocation successful");
	}
	
	private static void test1() {
		try {
			short[] a = new short[-2];
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			float[][] a = new float[2][-2];
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Task[] a = new Task[-2];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void test2() {
		try {
			int[] a = new int[100000];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.err = new PrintStream(SCI2.out);
		System.out.println("HeapTest4 started");
		test1();
		test2();
		Task t = new HeapTest4();
		t.period = 1000;
		t.time = 1000;
		Task.install(t);
		System.out.println("HeapTest4 done");
	}

}

