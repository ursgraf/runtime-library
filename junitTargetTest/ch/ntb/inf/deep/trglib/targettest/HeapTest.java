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

import ch.ntb.inf.deep.runtime.ppc32.Heap;
import ch.ntb.inf.junitTarget.Assert;
import ch.ntb.inf.junitTarget.CmdTransmitter;
import ch.ntb.inf.junitTarget.MaxErrors;
import ch.ntb.inf.junitTarget.Test;

/**
 * NTB 9.4.2013, Urs Graf
 * 
 */
@MaxErrors(100)
	@SuppressWarnings("unused")
public class HeapTest {
	
//	@Ignore
	@Test
	public static void test1() {
		int free = Heap.getFreeHeap();
		for (int i = 0; i < 100; i++);
		Assert.assertEquals("test1", free, Heap.getFreeHeap());
		@SuppressWarnings("unused")
		int[] a1 = {10, 20};
		Assert.assertEquals("test2", free - 16, Heap.getFreeHeap());
		free = Heap.getFreeHeap();
		a1 = new int[3];
		Assert.assertEquals("test3", free - 32, Heap.getFreeHeap());
		CmdTransmitter.sendDone();
	}	

	static int[] a1;
	@Test
	public static void test2() {
		Heap.mark(); Heap.sweep();
		Heap.mark(); Heap.sweep(); // run GC a second time to free all blocks
		int free = Heap.getFreeHeap();
		a1 = new int[]{10, 20};
		Assert.assertEquals("test1", free - 16, Heap.getFreeHeap());
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test2", free - 16, Heap.getFreeHeap());
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test3", free - 16, Heap.getFreeHeap());
		a1 = null;
		Assert.assertEquals("test4", free - 16, Heap.getFreeHeap());
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test5", free, Heap.getFreeHeap());
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test6", free, Heap.getFreeHeap());
		CmdTransmitter.sendDone();
	}	

	static String str1 = "abcd";
	static String str2;
	static char[] chrs = {'m','n','o'};
	
	@Test
	public static void test3() {
		Heap.mark(); Heap.sweep();
		Heap.mark(); Heap.sweep(); // run GC a second time to free all blocks
		int free = Heap.getFreeHeap();
		str1 = "xy";	// const string uses no heap
		Assert.assertEquals("test1", free, Heap.getFreeHeap());	
		Heap.mark(); Heap.sweep();
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test2", free, Heap.getFreeHeap());	
		str2 = new String(chrs);
		Assert.assertEquals("test3", free - 32, Heap.getFreeHeap());
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test4", free - 32, Heap.getFreeHeap());
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test5", free - 32, Heap.getFreeHeap());
		str1 = str2;
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test6", free - 32, Heap.getFreeHeap());
		str2 = null;
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test7", free - 32, Heap.getFreeHeap());
		str1 = null;
		Heap.mark(); Heap.sweep();
		Assert.assertEquals("test8", free, Heap.getFreeHeap());
		CmdTransmitter.sendDone();
	}	
//static {
//	test3();
//}
}
