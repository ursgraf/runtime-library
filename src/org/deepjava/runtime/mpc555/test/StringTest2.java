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
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		
		System.out.println("StringTest2");
		
		new StringTest2();
		
		System.out.println("done");
	}
}
