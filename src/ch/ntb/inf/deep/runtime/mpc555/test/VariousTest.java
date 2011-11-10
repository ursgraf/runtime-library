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

public class VariousTest {
	
	static int variousParams1(int a, int b, int c, int d) {
		return a + b + c + d;
	}
	
	static int variousParams2(int a, long b, byte c, float d, double e, long f, float g) {
		return (int)(a + b + c + d + e + f + g);
	}
	
	static {
		// Initialize SCI2 (9600 8N1)
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		
		// Use the SCI1 for stdout and stderr
		System.out = new PrintStream(SCI2.out);
		System.err = System.out;
		
		// Print a string to the stdout
		System.out.println("VariousTest");
		
		long b = -3;
		int a = 5;
		float c = 2.5f;
		System.out.print("Test 1: ");
		System.out.println(variousParams2(-2, 8L, (byte)-6, 2.5f, -3.5, -4, 3.8f));
		
		System.out.print("Test 2: ");
		System.out.println(variousParams2(a, b, (byte)b, c, c, b, c));
	}

}

