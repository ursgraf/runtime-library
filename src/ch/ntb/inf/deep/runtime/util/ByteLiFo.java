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
	 * Creates a new <code>ByteLifo</code> with <code>size</code> entries.
	 * 
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
	 * Reads the available entries in the queue.
	 * 
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