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
import org.deepjava.runtime.ppc32.Heap;
import org.deepjava.runtime.ppc32.Task;

/*changes:
 * 10.04.12	NTB/GRAU	creation
 */

public class HeapTest2 extends Task {
	HeapTest2 next;
	static HeapTest2 head;
	static Task t;
	
	public void action() {
		@SuppressWarnings("unused")
		int[] a = new int[100];
		head = new HeapTest2();
		HeapTest2 tail = head;
		for (int i = 0; i < 100; i++) {HeapTest2 obj = new HeapTest2(); tail.next = obj; tail = obj;} 
		if (this.nofActivations % 100 == 0) {
			System.out.print("freeHeap = ");
			System.out.printHexln(Heap.getFreeHeap());
		}
	}
	
	static void runOnce(){
		t.action();
	}
	
	static {
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = new PrintStream(sci.out);
		System.out.println("HeapTest2 started");
		t = new HeapTest2();
		t.period = 10;
		Task.install(t);
	}
}
