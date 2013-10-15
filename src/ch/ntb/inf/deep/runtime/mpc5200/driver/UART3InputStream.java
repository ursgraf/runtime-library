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

package ch.ntb.inf.deep.runtime.mpc5200.driver;

import java.io.InputStream;

/* Changes:
 * 12.02.2013	Martin Zueger	initial version
 */

/**
 *
 * Input Stream to read bytes form the UART3.
 * Don't forget to initialize the UART3 before using this class.
 * 
 */
public class UART3InputStream extends InputStream{

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	public int available() {
		return UART3.availToRead();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() {
		return UART3.read();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte b[]){
		return UART3.read(b);
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte b[], int off, int len){
		return UART3.read(b, off, len);
	}
	
}
