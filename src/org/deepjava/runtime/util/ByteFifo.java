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

import java.io.IOException;

/* Changes:
 * 04.06.2014	Urs Graf	exception added
 * 01.04.2008	NTB/SP	error states added
 * 28.09.2007	NTB/SP	creation
 */

/**
 * First in first out <code>byte</code> queue.
 * The size of the queue should be a multiple of 2 minus one (size = 2^x - 1).
 */
public class ByteFifo {
	
	byte[] data;
	int head, tail;
	int size;

	/**
	 * Creates a new <code>ByteFifo</code> with <code>size</code> entries.
	 * 
	 * @param size The size of the queue (size = 2^x - 1)
	 */
	public ByteFifo(int size) {
		data = new byte[size + 1];
		this.size = size;
	}
	
	/**
	 * Inserts one <code>byte</code> into the queue.
	 * @param data <code>Byte</code> which will be inserted into the queue
	 */
	public void enqueue(byte data) {
		if (availToWrite() > 0) {
			this.data[tail] = data;
			tail = (tail + 1) % this.data.length;
		}
	}

	/**
	 * Removes one <code>byte</code> from the queue.
	 * @return The removed byte.
	 * @throws IOException 
	 *            if reading from an empty queue.
	 */
	public byte dequeue() throws IOException {
		if (head != tail) {
			byte c= data[head];
			head = (head + 1) % data.length;
			return c;
		} else throw new IOException("IOException");
	}
	
	
	/**
	 * Clears the queue.
	 */
	public void clear() {
		head = tail;
	}
	
	/**
	 * Reads the available entries in the queue.
	 * 
	 * @return The available <code>bytes</code> to read.
	 */
	public int availToRead() {
		int len = tail - head;
		if(len < 0) return data.length + len;
		return len;
	}
	
	/**
	 * Reads the available space left in the queue.
	 * 
	 * @return The available queue space.
	 */
	public int availToWrite() {
		int len = tail - head;
		if(len < 0) len = data.length + len;
		return size - len;
	}
	
	/**
	 * Reads the maximum number of entries in the queue.
	 *
	 * @return The size of the queue.
	 */
	public int getSize() {
		return size;
	}
}