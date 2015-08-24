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

package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCIInputStream;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCIOutputStream;
import ch.ntb.inf.deep.runtime.ppc32.Task;


/**
 * Demo for InputStream and OutputStream using SCI2.<br>
 * Received characters will be sent back immediately.
 * 
 * @author Urs Graf
 */
public class SystemInOutReflector extends Task {
	static SCIOutputStream out;
	static SCIInputStream in;
	
	/**
	 * Reflect input on in stream to out stream.
	 */
	public void action() {
		if (in.available() > 0)	out.write(in.read());
	}

	static {
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		out = sci.out;
		in = sci.in;
		out.write((byte)'x');
		
		Task t = new SystemInOutReflector();
		t.period = 0;
		Task.install(t);
	}
}
