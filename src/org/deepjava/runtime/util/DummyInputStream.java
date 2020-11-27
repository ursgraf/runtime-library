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

package org.deepjava.runtime.util;

import java.io.InputStream;
import java.lang.Override;

/**
 * A readable source of bytes.
 * 
 * This is a simple helper class. <code>System.in</code> will open such a <code>DummyInputStream</code> as a default. 
 * A user will later redirect <code>System.in</code> to a physically available device.
 *
 */
public class DummyInputStream extends InputStream {

	@Override
	public int available() {
		return 0;
	}

	@Override
	public int read() {
		return 0;
	}

}
