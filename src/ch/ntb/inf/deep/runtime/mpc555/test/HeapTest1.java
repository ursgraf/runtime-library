/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.runtime.mpc555.test;
import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.runtime.ppc.Heap;
import ch.ntb.inf.deep.unsafe.US;

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
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.out.println("HeapTest1 started");
	}
}
