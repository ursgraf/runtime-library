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
 * Jan. 2007	NTB/SP	creation
 */

/**
 * Non thread-save last in first out <code>byte</code> queue.
 * 
 */
public class ByteLiFo{
	byte[] data;
	int head;
	int count;

	/**
	 * @param size The size of the queue
	 */
	public ByteLiFo(int size){
		data = new byte[size + 1];
	}
	
	/**
	 * Inserts one <code>byte</code> into the LiFo.
	 * @param d <code>Byte</code> which will be inserted into the LiFo
	 */
	public void push(byte d){
			data[head] = d;
			head = (head + 1) % data.length;		
			if(count < data.length) count++;			
	}
	
	/**
	 * Removes one <code>byte</code> from the LiFo.
	 * @return The removed byte 
	 */
	public byte pop(){
		if(count > 0){
			count--;
			head--;
			if(head < 0) head += data.length;
			return data[head];			
		}
		return -1;
	}
	
	/**
	 * Clears the LiFo.
	 */
	public void clear(){
		head = 0;
		count = 0;
	}
	
	/**
	 * @return The available <code>bytes</code> to read.
	 */
	public int availToRead(){
		return count;
	}
	
	/**
	 * Compares the LiFo with a <code>byte array</code>.
	 * @param cData The byte array to compare with the LiFo
	 * @param lifoOffset The start offset of the LiFo
	 * @param length The length of <code>cData</code>
	 * @return true if <code>cData</code> and the LiFo are equal, false otherwise
	 */
	public boolean compare(byte[] cData, int lifoOffset, int length){
		if(length < 0) return false;
		if(length > cData.length) length = cData.length;
		if(length + lifoOffset > count) return false;
		int tempHead = (head - (length + lifoOffset));
		if(tempHead < 0) tempHead += this.data.length;
		for(int i=0; i<length; i++){
			if(this.data[tempHead] != cData[i]) return false;
			tempHead = (tempHead + 1) % data.length;
		}
		return true;	
	}
}