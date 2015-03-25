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

package ch.ntb.inf.deep.runtime.util;

/* Changes:
 * 08.01.2010	NTB/SP	creation
 */

/**
 * First in first out <code>byte</code> array queue.
 * The size of the queue should be a multiple of 2 minus one (size = 2^x - 1).
 * 
 */
/*
 * Changes:
 * 	
 */
public class ByteArrayFifo{
	byte[][] arrays;
	int head, tail;
	int size;

	/**
	 * Creates a new <code>ByteArray</code> with <code>size</code> entries.
	 * 
	 * @param size The size of the queue (size = 2^x - 1)
	 */
	public ByteArrayFifo(int size){
		arrays = new byte[size + 1][];
		this.size = size;
	}
	
	/**
	 * Inserts one byte array into the queue.
	 * @param data array which will be inserted into the queue
	 */
	public void enqueue(byte[] data){
		if(availToWrite() > 0){
			arrays[tail] = data;
			tail = (tail + 1) % arrays.length;
		}
	}
	
	/**
	 * Removes one <code>byte</code> array form the queue.
	 * @return The removed <code>byte</code> array.
	 */
	public byte[] dequeue(){
		if(head != tail){
			 byte[] c= arrays[head];
			head = (head + 1) % arrays.length;
			return c;
		}
		return null;
	}
	
	/**
	 * Clears the queue.
	 */
	public void reset(){
		head = tail;
	}
	
	/**
	 * Reads the available entries in the queue.
	 * 
	 * @return The available <code>byte</code> arrays to read.
	 */
	public int availToRead(){
		int len = tail - head;
		if(len < 0) return arrays.length + len;
		return len;
	}
	
	/**
	 * Reads the available space left in the queue.
	 * 
	 * @return The available queue space.
	 */
	public int availToWrite(){
		int len = tail - head;
		if(len < 0) len = arrays.length + len;
		return size - len;
	}
	
	/**
	 * Reads the maximum number of entries in the queue.
	 * 
	 * @return The size of the queue.
	 */
	public int getSize(){
		return size;
	}
}