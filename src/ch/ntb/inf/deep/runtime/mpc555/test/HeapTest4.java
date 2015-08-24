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

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.ppc32.Heap;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/*changes:
 * 13.03.15	NTB/GRAU	creation
 */

public class HeapTest4 extends Task {
	
	@SuppressWarnings("unused")
	public void action() {
		//		byte[] a = new byte[10000]; // Wrong!!
		// such a block size is too big to be repetively allocated
		// it takes to mark&sweep phases to add the blocks to the free list
		// during that time, space on the heap would have run out
		// and anyway, the size of the array is limited to 16 bit
		byte[] a = new byte[0x3ff8];
		System.out.print("allocation successfull, "); System.out.printHexln(Heap.getFreeHeap());
	}

	@SuppressWarnings("unused")
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
	
	@SuppressWarnings("unused")
	private static void test2() {
		try {
			int[] a = new int[100000];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = new PrintStream(sci.out);
		System.out.println("HeapTest4 started");
		System.out.print("Total heap size = "); System.out.printHexln(Heap.getHeapSize());
//		test1();
//		test2();
		Task t = new HeapTest4();
		t.period = 1000;
		t.time = 1000;
		Task.install(t);
		System.out.println("HeapTest4 done");
	}

}

