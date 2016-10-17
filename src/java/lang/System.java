/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang;

import java.io.InputStream;
import java.io.PrintStream;

import ch.ntb.inf.deep.marker.Modified;
import ch.ntb.inf.deep.runtime.Kernel;
import ch.ntb.inf.deep.runtime.util.DummyInputStream;
import ch.ntb.inf.deep.runtime.util.DummyOutputStream;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Provides access to system-related information and resources including
 * standard input and output. All methods of this class are accessed in a static way and the
 * class itself can not be instantiated.
 *
 */
/* Changes:
 * 27.5.2014	Urs Graf	initial import and modified
 */
public final class System implements Modified {
	public static PrintStream err;
	public static PrintStream out;
	public static InputStream in;

	private System() { }
	
	/**
	 * copies length elements from the array source, beginning with the element at sourcePosition, 
	 * to the array destination starting at destinationPosition. 
	 * The destination array must already exist when System.arraycopy() is called. 
	 * The method does not create it. The source and destination arrays must be of the same type
	 * @param src Source array.
	 * @param srcPos Start element in source array.
	 * @param dest Destination array.
	 * @param destPos Start element in destination array.
	 * @param length Number of elements to be copied.
	 */
	public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
		int tag = US.GET4(US.REF(src) - 4);
		int compSize = US.GET2(tag + 2);
		switch (compSize) {
		case 0:	// type boolean
			int srcStart = US.REF(src) + srcPos;
			int dstStart = US.REF(dest) + destPos;
			for (int i = 0; i < length; i++) {
				US.PUT1(dstStart + i, US.GET1(srcStart + i));
			}
			break;
		case 1:
			srcStart = US.REF(src) + srcPos;
			dstStart = US.REF(dest) + destPos;
			for (int i = 0; i < length; i++) {
				US.PUT1(dstStart + i, US.GET1(srcStart + i));
			}
			break;
		case 2:
			srcStart = US.REF(src) + srcPos*2;
			dstStart = US.REF(dest) + destPos*2;
			for (int i = 0; i < length; i++) {
				US.PUT2(dstStart + i*2, US.GET2(srcStart + i*2));
			}
			break;
		case 4:
			srcStart = US.REF(src) + srcPos*4;
			dstStart = US.REF(dest) + destPos*4;
			for (int i = 0; i < length; i++) {
				US.PUT4(dstStart + i*4, US.GET4(srcStart + i*4));
			}
			break;
		case 8:
			srcStart = US.REF(src) + srcPos*8;
			dstStart = US.REF(dest) + destPos*8;
			for (int i = 0; i < length; i++) {
				US.PUT8(dstStart + i*8, US.GET8(srcStart + i*8));
			}
			break;
		default: 
			break;
		}
	}

	public static void setErr(PrintStream err){
		System.err = err;
	}
	public static void setIn(InputStream in){
		System.in = in;
	}
	public static void setOut(PrintStream out){
		System.out = out;
	}
	
	static{
		out = new PrintStream(new DummyOutputStream());
		err = new PrintStream(new DummyOutputStream());
		in = new DummyInputStream();
		
	}

	public static long currentTimeMillis() {
		return Kernel.time();
	}
}
