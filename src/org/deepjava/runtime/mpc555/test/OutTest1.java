/*
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

import org.deepjava.lowLevel.LL;
import org.deepjava.runtime.mpc555.driver.SCI;

/*changes:
 * 18.2.11	NTB/Urs Graf	creation
 */

public class OutTest1 {
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
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = new PrintStream(sci.out);
		
		System.out.print('B');
		System.out.println('A');
		System.out.println("hello world");
		System.out.println(123);
		System.out.println(-56);
		System.out.println(-8.5f);
		System.out.println(238.5234e-23);
		System.out.println(3452395879283579L);
		int a = -7463;
		System.out.println(a);
		System.out.println(true);

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
//		String str = null;	//test an NullPointerException
//		System.out.println(str);
		System.out.println("done");
	}
}
