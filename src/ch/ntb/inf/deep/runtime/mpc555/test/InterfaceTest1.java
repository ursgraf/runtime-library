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

public class InterfaceTest1 implements IA {

	public int mA1() {
		return 100;
	}
	
	public int mA2() {
		return 200;
	}

	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.out.println("start");
		
//		InterfaceTest1 obj = new InterfaceTest1();
//		System.out.println(obj.m1());
		IA obj = new InterfaceTest1();
		System.out.println(obj.mA1());
		System.out.println(obj.mA2());
		System.out.println("test ok");

	}
}


interface IA {
	int mA1();
	int mA2();
}


