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

