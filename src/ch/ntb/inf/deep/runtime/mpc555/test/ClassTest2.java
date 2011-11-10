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

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class ClassTest2 {
	static ClassTest2 test2;
	static ClassTest2[] a3 = new ClassTest2[3];
	byte b1 = 65;
	short s1;
	int i1;
	byte[] a1;
	ClassTest2[] a2;
	int i2 = 0x332211;
	
	static void run() {
		while (true) {
			SCI2.write((byte)'x');
			test2.send();
			for (int i = 0; i < 1000000; i++);
			int a = 3;
			switch (a) {
			case 0: 
				SCI2.write((byte)'0');
				break;
			case 1: 
				SCI2.write((byte)'1');
				break;
			default:
				SCI2.write((byte)'y');
			}	
		}
	}
	
	void send() {
		SCI2.write((byte)'U');
	}
	
	public ClassTest2 () {
		s1 = 0x7788;
		a1 = new byte[5];
		a1[0] = (byte)0x44;
		a1[1] = (byte)0x55;
		a1[2] = (byte)(s1 >> 8);
		a1[3] = (byte)s1;
		a1[4] = 10;
		a2 = new ClassTest2[7];
	}
	
	static {
		SCI2.start(9600, (byte)0, (short)8);
		SCI2.write((byte)'0');
		test2 = new ClassTest2();
		test2.send();
		test2.a2[1] = new ClassTest2();
		a3[1] = new ClassTest2();
		a3[2] = test2.a2[1];
		SCI2.write((byte)'1');
		SCI2.write((byte)test2.a2[1].b1);
		run();
	}
}
