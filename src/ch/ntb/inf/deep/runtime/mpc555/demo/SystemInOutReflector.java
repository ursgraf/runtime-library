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

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCIInputStream;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCIOutputStream;
import ch.ntb.inf.deep.runtime.ppc32.Task;


/**
 * Demo for InputStream and OutputStream using SCI2.
 */
public class SystemInOutReflector extends Task {
	static SCIOutputStream out;
	static SCIInputStream in;
	
	public void action() {
		// reflect input on stdin to stdout
		if (in.available() > 0)	out.write(in.read());
	}

	static {
		// Initialize SCI2 (9600 8N1)
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		out = new SCIOutputStream(SCIOutputStream.pSCI2);
		in = new SCIInputStream(SCIInputStream.pSCI2);
		out.write((byte)'x');
		
		Task t = new SystemInOutReflector();
		t.period = 0;
		Task.install(t);
	}
}
