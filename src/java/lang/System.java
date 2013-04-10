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

package java.lang;

import java.io.InputStream;
import java.io.PrintStream;

import ch.ntb.inf.deep.unsafe.US;

public final class System {
	public static PrintStream err;
	public static PrintStream out;
	public static InputStream in;

	private System() { }
	
	/**
	 * copies length elements from the array source, beginning with the element at sourcePosition, 
	 * to the array destination starting at destinationPosition. 
	 * The destination array must already exist when System.arraycopy() is called. 
	 * The method does not create it. The source and destination arrays must be of the same type
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
}
