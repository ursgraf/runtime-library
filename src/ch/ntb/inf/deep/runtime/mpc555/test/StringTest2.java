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

/*changes:
 * 22.02.11 NTB/Martin Züger	OutT replaced by System.out
 * 11.02.11	NTB/Urs Graf		creation
 */

public class StringTest2 {
	static String strC1, strC2;
	String strI1, strI2;
		
	StringTest2() {
		System.out.println("class strings");
		
		strC1 = new String(new char[] {'a', 'b', 'c'});
		System.out.println(strC1);
		
		char[] a1 = new char[] {'1', '2', '3', '4', '5', '6'};
		strC1 = new String(a1, 2, 3);
		System.out.println(strC1);
		
		strC2 = new String(a1, 0, 6);
		char[] a2 = new char[strC2.length()];
		for (int i = 0; i < strC2.length(); i++) a2[i] = strC2.charAt(i);
		a2[0] = strC2.charAt(0);
		System.out.println(strC2);
		
		System.out.println("instance strings");
		
		strI1 = new String(new char[] {'d', 'e', 'f'});
		System.out.println(strI1);
		
		char[] a3 = new char[] {'9', '8', '7', '6', '5', '4', '3', '2', '1', '0'};
		strI1 = new String(a3, 2, 3);
		System.out.println(strI1);
		
		strI2 = new String(a3, 0, 6);
		char[] a4 = new char[strI2.length()];
		for (int i = 0; i < strI2.length(); i++) a4[i] = strI2.charAt(i);
		a4[0] = strI2.charAt(0);
		System.out.println(strI2);
		
	}
	
	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		System.out.println("StringTest2");
		
		new StringTest2();
		
		System.out.println("done");
	}
}
