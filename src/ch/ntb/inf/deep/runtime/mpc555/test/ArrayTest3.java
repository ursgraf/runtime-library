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

/*changes:
 * 10.11.2011	NTB/Urs Graf	creation
 */

public class ArrayTest3 {
	static short[][] a1 = new short[4][3];
	static short[][] a2 = {{1,2},{3,4}};
	static int[][][] a3 = new int[3][2][4];
	static int[][][] a4 = {
							{{1,2,3,4},{11,12,13,14}},
							{{101,102,103,104},{111,112,113,114}},
							{{201,202,203,204},{211,212,213,214}}
						  };
	static Object a5;

	static {
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = new PrintStream(sci.out);
		
		System.out.println("array test");
		a1[0][0] = 100;
		a1[0][1] = 101;
		a1[0][2] = 102;
		a1[1][0] = 200;
		a1[1][1] = 201;
		a1[1][2] = 202;
		a1[2][0] = 300;
		a1[2][1] = 301;
		a1[2][2] = 302;
		a1[3][0] = 400;
		a1[3][1] = 401;
		a1[3][2] = 402;
	}
}

