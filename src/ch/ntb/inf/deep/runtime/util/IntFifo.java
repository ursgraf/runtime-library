/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.runtime.util;

/* Changes:
 * 08.12.2008	NTB/SP	creation
 */

/**
 * First in first out <code>Integer</code> queue.
 * The size of the queue should be a multiple of 2 minus one (size = 2^x - 1).
 * 
 */
public class IntFifo{
	
	public static final int NO_DATA = -1; 
	
	int[] data;
	int head, tail;
	int size;

	/**
	 * @param size The size of the queue (size = 2^x - 1).
	 */
	public IntFifo(int size){
		data = new int[size + 1];
		this.size = size;
	}
	
	/**
	 * Inserts one <code>Integer</code> into the queue.
	 * @param data <code>Integer</code> which will be inserted into the queue
	 */
	public void enqueue(int data){
		if(availToWrite() > 0){
			this.data[tail] = data;
			tail = (tail + 1) % this.data.length;
		}
	}
	
	/**
	 * Removes one <code>Integer</code> from the queue.
	 * @return The removed Integer or @see {@link #NO_DATA} if no data is present
	 */
	public int dequeue(){
		if(head != tail){
			int c= data[head];
			head = (head + 1) % data.length;
			return c;
		}
		return NO_DATA;
	}
	
	
	/**
	 * Clears the queue.
	 */
	public void clear(){
		head = tail;
	}
	
	/**
	 * @return The available <code>Integers</code> to read.
	 */
	public int availToRead(){
		int len = tail - head;
		if(len < 0) return data.length + len;
		return len;
	}
	
	/**
	 * @return The available queue space.
	 */
	public int availToWrite(){
		int len = tail - head;
		if(len < 0) len = data.length + len;
		return size - len;
	}
	
	/**
	 * @return The size of the queue.
	 */
	public int getSize(){
		return size;
	}
}