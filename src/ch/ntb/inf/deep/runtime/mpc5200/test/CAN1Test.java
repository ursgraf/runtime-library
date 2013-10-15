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

package ch.ntb.inf.deep.runtime.mpc5200.test;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc5200.Task;
import ch.ntb.inf.deep.runtime.mpc5200.driver.UART3;
import ch.ntb.inf.deep.runtime.mpc5200.driver.can.CAN1;

public class CAN1Test extends Task {
	
	public void action() {
		CAN1.sampleNodes();
		if (nofActivations % 2000 == 0) {
			for (int i = 0; i < CAN1.nodeData.length; i++) {
				System.out.print(CAN1.nodeData[i].forceX);
				System.out.print('.');
				System.out.print(CAN1.nodeData[i].forceY);
				System.out.print('.');
				System.out.print(CAN1.nodeData[i].forceZ);
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	

	static {	
		UART3.start(9600, UART3.NO_PARITY, (short)8);
		System.out = new PrintStream(UART3.out);
		System.out.println("start");
		CAN1.init();
		Task t = new CAN1Test();	
		t.period = 1;
		Task.install(t);
	}
}
