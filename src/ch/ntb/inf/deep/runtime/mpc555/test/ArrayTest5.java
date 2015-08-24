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

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;

public class ArrayTest5 {
	void print() {
		Foo f = new ArrayTest5.Foo();

		double sum = 0;
		for (int n = 0; n < 2; n++) {
			for (int m = 0; m < 2; m++)	{
				sum += f.A[n][m];
			}
		}
		System.out.println(sum);
	}

	static {
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = new PrintStream(sci.out);
		System.out.println("start");
		new ArrayTest5().print();
	}


	public class Foo {
		double[][] A = new double[][] {{ 0, 1},{ 2, 3}};
	}
}
