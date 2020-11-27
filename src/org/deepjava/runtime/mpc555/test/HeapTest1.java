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
import org.deepjava.unsafe.US;

/*changes:
 * 10.04.12	NTB/GRAU	creation
 */

public class HeapTest1 {
	static Object obj1;
	
	public static void allocObj1(){
		obj1 = new HeapTest1();
	}
	
	public static void allocArr1(){
		obj1 = new long[1024];
	}
	
	public static void allocArr2(){
		obj1 = new byte[0x4000];
	}
	
	// such a block size is too big to be repetively allocated
	// it takes to mark&sweep phases to add the blocks to the free list
	// during that time, space on the heap would have run out
	public static void allocArr3(){
		obj1 = new byte[0xf000];
	}

	// must throw a runtime exception
	//the size of the array is limited to 16 bit
	public static void allocArr4(){
		obj1 = new byte[0x20000];
	}

	public static void deleteObj1(){
		obj1 = null;
	}
	
	public static void mark() {
		Heap.mark();
		System.out.println("mark");
	}

	public static void sweep() {
		Heap.sweep();
		System.out.println("sweep");
	}

	public static void printRoots() {
		System.out.print("roots = ");
		System.out.println(Heap.getRoots().length);
		int[] roots = Heap.getRoots();
		for (int i = 0; i < Heap.getRoots().length; i++) {
			System.out.print("\troot at adr "); System.out.printHex(roots[i]);
			System.out.print(" val = "); System.out.printHex(US.GET4(roots[i])); System.out.println();
		}
	}

	public static void printHeapProperties() {
		System.out.print("Heap base = "); System.out.printHex(Heap.getHeapBase()); System.out.println();
		System.out.print("Heap size = "); System.out.printHex(Heap.getHeapSize()); System.out.println();
		System.out.print("Free heap = "); System.out.printHex(Heap.getFreeHeap()); System.out.println();
	}

	public static void printFreeBlocks() {
		System.out.println("nof free blocks: ");
		int[] freeBlocks = Heap.getFreeBlocks();
		int[] nofFreeBlocks = Heap.getNofFreeBlocks();
		for (int i = 0; i < freeBlocks.length; i++) {
			System.out.print("\tblock size = "); 
			System.out.print(16 * (i + 1));
			System.out.print("Bytes: nof free blocks = ");
			System.out.println(nofFreeBlocks[i]);
		}
	}

	public static void printFreeBlocksAddr() {
		System.out.println("free blocks: addr(size)");
		int[] freeBlocks = Heap.getFreeBlocks();
		for (int i = 0; i < freeBlocks.length; i++) {
			int addr = freeBlocks[i];
			System.out.print("\t"); 
			if (addr == 0) System.out.print("no blocks");
			while (addr != 0) {
				System.out.printHex(addr); System.out.print("("); 
				int size = US.GET4(addr) & 0xffffff; System.out.printHex(size);
				addr = US.GET4(addr + 4);
				System.out.print(")\t"); 

			}
			System.out.println();
		}
	}

	public static void printMarks() {
		System.out.print("nof marked objects = ");
		System.out.println(Heap.getNofMarkedObjs());
		System.out.print("nof marked regular objects = ");
		System.out.println(Heap.getNofMarkedRegObjs());
		System.out.print("nof marked array of references = ");
		System.out.println(Heap.getNofMarkedRefArrays());
		System.out.print("nof marked array of primitive type = ");
		System.out.println(Heap.getNofMarkedPrimArrays());
	}

	static {
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = new PrintStream(sci.out);
		System.out.println("HeapTest1 started");
	}
}
