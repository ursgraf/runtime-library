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

import ch.ntb.inf.deep.lowLevel.LL;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 18.2.11	NTB/Urs Graf	creation
 */

public class OutTest5 {
	static double d1 = 2.5e17;
	static long l1;
	static long l2 = 0x4004000000000000L;
	static double d2, d3;
	static int exp;
	static int bits;
	
	static {
		l1 = LL.doubleToBits(d1);
		d2 = LL.bitsToDouble(l2);
		bits = Double.highPartToIntBits(d1);
		exp = Double.getExponent(d1);
		d3 = Double.setExponent(d2, 10);
//		US.HALT(30);
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		System.out.println("float test");
		System.out.println(2.5);
		System.out.println(2.345678);
		System.out.println(2.345678f);
		System.out.println(-2.34);
		System.out.printHexln(255);
		System.out.printHexln(-1);
		System.out.printHexln(0x973bc3af);
		System.out.printHexln(0xaf973bc3af456789L);
		System.out.printHexln(-1L);
		System.out.println("done");
	}
}
