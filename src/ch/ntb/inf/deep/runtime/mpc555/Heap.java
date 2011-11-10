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

package ch.ntb.inf.deep.runtime.mpc555;

import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 * 22.1.11	Urs Graf		newstring added
 */

public class Heap implements ntbMpc555HB {
	static private int heapBase;
	static private int heapPtr;

	// called by new	
	private static int newObject(int ref) {	
		int size = US.GET4(ref) + 8;
		int addr = heapPtr; 
		while (addr < heapPtr + size) {US.PUT4(addr, 0); addr += 4;}
		US.PUT4(heapPtr + 4, ref);	// write tag
		ref = heapPtr + 8;
		heapPtr += ((size + 15) >> 4) << 4;
		return ref;
	}	

	//TODO tag auf Basistyp setzen, size of Object dazunehmen
	// called by newarray	
	private static int newPrimTypeArray(int nofElements, int type, int ref) {
		int elementSize;
		if (type == 7 || type == 11) elementSize = 8;
		else if (type == 6 || type == 10) elementSize = 4;
		else if (type == 5 || type == 9) elementSize = 2;
		else elementSize = 1;
		int size = nofElements * elementSize + 8;
		int addr = heapPtr; 
		while (addr < heapPtr + size) {US.PUT4(addr, 0); addr += 4;}
		US.PUT4(heapPtr + 4, ref);	// write tag
		US.PUT2(heapPtr + 2, nofElements);	// write length
		ref = heapPtr + 8;
		heapPtr += ((size + 15) >> 4) << 4;
		return ref;
	}
	
	//TODO size of Object dazunehmen
	// called by anewarray	
	private static int newRefArray(int nofElements, int ref) {
		int size = nofElements * 4 + 8;
		int addr = heapPtr; 
		while (addr < heapPtr + size) {US.PUT4(addr, 0); addr += 4;}
		US.PUT4(heapPtr + 4, ref);	// write tag
		US.PUT2(heapPtr + 2, nofElements);	// write length
		ref = heapPtr + 8;
		heapPtr += ((size + 15) >> 4) << 4;
		return ref;
	}
	
	//TODO tag auf Basistyp setzen, size of Object dazunehmen
	// called by multianewarray	
	private static int newMultiDimArray(int ref, int nofDim, int dim0, int dim1, int dim2, int dim3, int dim4) {
		if (nofDim > 3 || nofDim < 2) US.HALT(20);
		if (nofDim == 2) {
			int elemSize = US.GET4(ref);
			int dim1Size = (8 + dim1 * elemSize + 3) >> 2 << 2;	
			int size = 8 + dim0 * 4 + dim0 * dim1Size;
			int addr = heapPtr; 
			while (addr < heapPtr + size) {US.PUT4(addr, 0); addr += 4;}
			US.PUT4(heapPtr + 4, ref);	// write tag
			US.PUT2(heapPtr + 2, dim0);	// write length of dim0
			ref = heapPtr + 8;
			addr = ref;
			for (int i = 0; i < dim0; i++) {
				int elemAddr = ref + 4 * dim0 + 8 + i * dim1Size; 
				US.PUT4(addr, elemAddr);
				US.PUT4(elemAddr - 4, ref);	// write tag
				US.PUT2(elemAddr - 6, dim1);	// write length of dim0
				addr += 4;
			}
			heapPtr += ((size + 15) >> 4) << 4;
		}
		return ref;
	}
	
	//TODO tag auf Basistyp setzen, size of String dazunehmen
	// called by newstring in java/lang/String
	public static int newstring(int ref, int len) {
		int size = len + 8;
		int addr = heapPtr; 
		while (addr < heapPtr + size) {US.PUT4(addr, 0); addr += 4;}
		US.PUT4(heapPtr + 4, ref);	// write tag
		ref = heapPtr + 8;
		heapPtr += ((size + 15) >> 4) << 4;
		return ref;
	}
	
	static {
		int heapOffset = US.GET4(sysTabBaseAddr + stHeapOffset);
		heapBase = US.GET4(sysTabBaseAddr + heapOffset * 4 + 4);
		heapPtr = heapBase;
	}

}