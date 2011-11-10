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
	 * Creates a new ByteArrrayFifo.
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
	 * @return The available <code>byte<code> arrays to read.
	 */
	public int availToRead(){
		int len = tail - head;
		if(len < 0) return arrays.length + len;
		return len;
	}
	
	/**
	 * @return The available queue space.
	 */
	public int availToWrite(){
		int len = tail - head;
		if(len < 0) len = arrays.length + len;
		return size - len;
	}
	
	/**
	 * @return The size of the queue.
	 */
	public int getSize(){
		return size;
	}
}