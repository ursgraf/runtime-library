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

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 * 22.1.11	Urs Graf		newstring added
 * 1.1.11	Urs Graf		two- and three dimensional arrays supported
 * 1.5.12	Urs Graf		GC added
 */

public class Heap implements ntbMpc555HB {
	private static final boolean dbg = true;
	private static final int minBlockSize = 16;	// smallest block size to allocate
	private static final int nofFreeLists = 8;	// free blocks are kept in multiple lists
	
	private static int nofMarkedObjs;	// total number of marked objects, objects still in use
	private static int nofMarkedRefArrays;	// number of marked arrays of references
	private static int nofMarkedPrimArrays;	// number of marked arrays of primitive type
	private static int nofMarkedRegObjs;	// number of marked regular objects (no arrays)
	private static int heapBase;	// address of start of heap
	private static int heapSize;	// size of heap in bytes
	private static int heapEnd;		// address of end of heap
	static private int freeHeap;	// size of free heap in bytes
	static private int heapPtr;		// used for allocation before Heap class is initialized
	static private int nofRoots;	// number of roots in all classes
	static private int[] roots;		// addresses of roots in all classes
	static private int[] freeBlocks;	// free blocks have size minBlockSize, 2*minBlockSize, 
										// 3*minBlockSize .. 6*minBlockSize, greater than 7*minBlockSize
	static private int[] nofFreeBlocks;	// counter for debugging purposes
	
	static int nofSweepFreeBlock, nofSweepMarkedBlock, nofSweepCollBlock;
	static int currBlock;
	
	// called by new	
	private static int newObject(int ref) {	
		int size = US.GET4(ref) + 8;
		int blockAddr = getBlock(size);
		US.PUT4(blockAddr, 0x80000000 | size);	// set mark bit and size
		US.PUT4(blockAddr + 4, ref);	// write tag
		ref = blockAddr + 8;
		int i = ref;
		while (i < blockAddr + size) {US.PUT4(i, 0); i += 4;}
		return ref;
	}	

	// called by newarray	
	private static int newPrimTypeArray(int nofElements, int type, int ref) {
		int elementSize;
		if (type == 7 || type == 11) elementSize = 8;
		else if (type == 6 || type == 10) elementSize = 4;
		else if (type == 5 || type == 9) elementSize = 2;
		else elementSize = 1;
		int size = nofElements * elementSize + 8;
		int blockAddr = getBlock(size);
		US.PUT4(blockAddr, 0x80810000 | nofElements);	// set mark and array bit, set primitive array bit, write length
		US.PUT4(blockAddr + 4, ref);	// write tag
		ref = blockAddr + 8;
		int i = ref;
		while (i < blockAddr + size) {US.PUT4(i, 0); i += 4;}
		return ref;
	}
	
	// called by anewarray	
	private static int newRefArray(int nofElements, int ref) {
		int size = nofElements * 4 + 8;
		int blockAddr = getBlock(size);
		US.PUT4(blockAddr, 0x80800000 | nofElements);	// set mark and array bit, write length
		US.PUT4(blockAddr + 4, ref);	// write tag
		ref = blockAddr + 8;
		int i = ref;
		while (i < blockAddr + size) {US.PUT4(i, 0); i += 4;}
		return ref;
	}
	
	// called by multianewarray	
	private static int newMultiDimArray(int ref, int nofDim, int dim0, int dim1, int dim2, int dim3) {
		int addr;
		if (nofDim > 3 || nofDim < 2) US.HALT(20);
		if (nofDim == 2) {
			addr = newRefArray(dim0, ref);
			int arrayInfo = US.GET4(ref);
			if (arrayInfo < 0) {	// primitive type
				int elemSize = arrayInfo & 0xffff;	// mask array type bit and dimension
				int type;
				if (elemSize == 4) type = 6;
				else if (elemSize == 2) type = 5;
				else if (elemSize == 8) type = 7;
				else type = 4;
				int dim1Ref = US.GET4(ref + 12);
				for (int i = 0; i < dim0; i++) {
					int dim1Addr = newPrimTypeArray(dim1, type, dim1Ref);
					US.PUT4(addr + i * 4, dim1Addr);
				}
			} else {
				int dim1Ref = US.GET4(ref + 12);
				for (int i = 0; i < dim0; i++) {
					int dim1Addr = newRefArray(dim1, dim1Ref);
					US.PUT4(addr + i * 4, dim1Addr);
				}
			}
		} else {	// nofDim == 3
			addr = newRefArray(dim0, ref);
			int arrayInfo = US.GET4(ref);
			if (arrayInfo < 0) {	// primitive type
				int elemSize = arrayInfo & 0xffff;	// mask array type bit and dimension
				int type;
				if (elemSize == 4) type = 6;
				else if (elemSize == 2) type = 5;
				else if (elemSize == 8) type = 7;
				else type = 4;
				int dim1Ref = US.GET4(ref + 12);
				int dim2Ref = US.GET4(ref + 16);
				for (int i = 0; i < dim0; i++) {
					int dim1Addr = newRefArray(dim1, dim1Ref);
					US.PUT4(addr + i * 4, dim1Addr);
					for (int k = 0; k < dim1; k++) {
						int dim2Addr = newPrimTypeArray(dim2, type, dim2Ref);
						US.PUT4(dim1Addr + k * 4, dim2Addr);
					}
				}
			} else {
				int dim1Ref = US.GET4(ref + 12);
				int dim2Ref = US.GET4(ref + 16);
				for (int i = 0; i < dim0; i++) {
					int dim1Addr = newRefArray(dim1, dim1Ref);
					US.PUT4(addr + i * 4, dim1Addr);
					for (int k = 0; k < dim1; k++) {
						int dim2Addr = newRefArray(dim2, dim2Ref);
						US.PUT4(dim1Addr + k * 4, dim2Addr);
					}
				}
			}

//			int elemSize = US.GET4(ref) & 0xffff;	// mask array type bit and dimension
//			int dim1Size = 8 + dim1 * 4;	
//			int dim2Size = (8 + dim2 * elemSize + 3) >> 2 << 2;	
//			int size = 8 + dim0 * 4 + dim0 * dim1Size + dim0 * dim1 * dim2Size;
//			addr = heapPtr; 
//			while (addr < heapPtr + size) {US.PUT4(addr, 0); addr += 4;}
//			US.PUT4(heapPtr + 4, ref);	// write tag of dim0
//			US.PUT4(heapPtr, 0x800000 | dim0);	// write length of dim0 and array bit
//			int dim1Ref = US.GET4(ref + 12);
//			int dim2Ref = US.GET4(ref + 16);
//			ref = heapPtr + 8;
//			addr = ref;
//			for (int i = 0; i < dim0; i++) {
//				int elem1Addr = ref + 4 * dim0 + i * dim1Size + 8; 
//				US.PUT4(addr, elem1Addr);
//				US.PUT4(elem1Addr - 4, dim1Ref);	// write tag of dim1
//				US.PUT4(elem1Addr - 8, 0x800000 | dim1);	// write length of dim1 and array bit
//				for (int j = 0; j < dim1; j++) {
//					int elem2Addr = ref + 4 * dim0 + dim0 * dim1Size + (dim1 * i + j) * dim2Size + 8; 
//					US.PUT4(elem1Addr, elem2Addr);
//					US.PUT4(elem2Addr - 4, dim2Ref);	// write tag of dim2
//					US.PUT4(elem2Addr - 8, 0x800000 | dim2);	// write length of dim2 and array bit
//					elem1Addr += 4;
//				}
//				addr += 4;
//			}
//			heapPtr += ((size + 15) >> 4) << 4;
		}
		return addr;
	}
	
	// called by newstring in java/lang/String
	public static int newstring(int ref, int len) {
		int size = len + 8;
		int blockAddr = getBlock(size);
		US.PUT4(blockAddr, 0x80810000);	// set mark and array bit, set primitive array bit
		US.PUT4(blockAddr + 4, ref);	// write tag
		ref = blockAddr + 8;
		int i = ref;
		while (i < blockAddr + size) {US.PUT4(i, 0); i += 4;}
		return ref;
	}

	private static int getBlock(int size) {
		int addr;
		int i = size / minBlockSize;
		int blockSize = ((size + minBlockSize - 1) >> 4) << 4;
		if (i >= nofFreeLists) i = nofFreeLists - 1;
		// search free block in free block list
		if (freeBlocks == null) { // there is no free list at the very beginning
			addr = heapPtr;
			heapPtr += blockSize;
			freeHeap -= blockSize;
		} else {
			while (freeBlocks[i] == 0 && i < nofFreeLists - 1) i++;
			if (i < nofFreeLists - 1) {	
				addr = freeBlocks[i];	// unlink block from list
				freeBlocks[i] = US.GET4(addr + 4);	
				nofFreeBlocks[i]--;
				freeHeap -= blockSize;
				int restBlockSize = (i+1) * minBlockSize - blockSize;	// put rest into free list
				i = restBlockSize / minBlockSize;
				if (i > 0) {	// there is a rest block
					i--;
					int nextBlockAddr = addr + blockSize;
					US.PUT4(nextBlockAddr, (1 << 30) | restBlockSize);	// set free bit
					US.PUT4(nextBlockAddr + 4, freeBlocks[i]);
					freeBlocks[i] = nextBlockAddr;
					nofFreeBlocks[i]++;
					freeHeap += restBlockSize;
				}
			} else {	// get block from list with block size >= 128 Bytes
				addr = freeBlocks[nofFreeLists - 1];
				int freeBlockSize = US.GET4(addr) & 0xffffff;
				int last = addr;
				while (blockSize > freeBlockSize) {	// search block which is bit enough
					last = addr;
					addr = US.GET4(addr + 4);
					freeBlockSize = US.GET4(addr) & 0xffffff;
				}
				// unlink block
				if (last == addr) freeBlocks[nofFreeLists - 1] = US.GET4(addr + 4);	// first block in list
				else US.PUT4(last + 4, US.GET4(addr + 4));
				freeHeap -= freeBlockSize;
				nofFreeBlocks[nofFreeLists - 1]--;
				// put rest in free list
				int restBlockSize = freeBlockSize - blockSize;
				i = restBlockSize / minBlockSize;
				if (i > 0) {	// there is a rest block
					i--;
					if (i >= nofFreeLists) i = nofFreeLists - 1; 
					int nextBlockAddr = addr + blockSize;
					US.PUT4(nextBlockAddr, (1 << 30) | restBlockSize);	// set free bit
					US.PUT4(nextBlockAddr + 4, freeBlocks[i]);
					freeBlocks[i] = nextBlockAddr;
					nofFreeBlocks[i]++;
					freeHeap += restBlockSize;
				}
			}
		}
		return addr;
	}

	public static void mark() {
		if (dbg) {nofMarkedObjs = 0; nofMarkedRegObjs = 0; nofMarkedRefArrays = 0; nofMarkedPrimArrays = 0;}
		for (int i = 0; i < nofRoots; i++) {
			int obj = US.GET4(roots[i]);
			if (obj != 0) traverse(obj);
		}
	}
	
	private static void traverse(int obj) {
		int heapInfo = US.GET4(obj - 8);	
		if (heapInfo >= 0) {	// if not marked
			if (dbg) nofMarkedObjs++;
			US.PUT4(obj - 8, heapInfo | (1 << 31));	// mark
			if (heapInfo << 8 >= 0) {	// no array
				if (dbg) nofMarkedRegObjs++;
//				// read all ref field in typedesc
//				// traverse(ref);
			} else {	// array
				if (heapInfo << 15 >= 0) {	// array of references
					if (dbg) nofMarkedRefArrays++;
					int len = heapInfo & 0xffff;
					for (int i = 0; i < len; i++) {
						int ref = US.GET4(obj + i * 4);
						traverse(ref);
					}
				} else {	// array of primitives, don't follow
					if (dbg) nofMarkedPrimArrays++;
				}
			}
		}
	}

	public static void sweep() {
		int blockSize, collBlockAddr = 0, collBlockSize = 0;
		nofSweepFreeBlock = 0; nofSweepMarkedBlock = 0; nofSweepCollBlock = 0;
		currBlock = heapBase;
		while (currBlock < heapEnd) {
			int heapInfo = US.GET4(currBlock);
			if (heapInfo << 1 < 0) {	// block is free
				if (collBlockSize > 0) {	// close collected block till now and add to free list
					US.PUT4(collBlockAddr, (1 << 30) | collBlockSize); // set free bit
					int i = collBlockSize / minBlockSize - 1;
					if (i >= nofFreeLists) i = nofFreeLists - 1;
					US.PUT4(collBlockAddr + 4, freeBlocks[i]);
					freeBlocks[i] = collBlockAddr;
					nofFreeBlocks[i]++;
					freeHeap += collBlockSize;
					collBlockSize = 0;
					}
				blockSize = heapInfo & 0xffffff;
				currBlock += blockSize;
				nofSweepFreeBlock++;
			} else {	// block is marked as used or block to be collected
				if (heapInfo < 0) {	// object is marked
					if (collBlockSize > 0) {	// close collected block till now and add to free list
						US.PUT4(collBlockAddr, (1 << 30) | collBlockSize); // set free bit
						int i = collBlockSize / minBlockSize - 1;
						if (i >= nofFreeLists) i = nofFreeLists - 1;
						US.PUT4(collBlockAddr + 4, freeBlocks[i]);
						freeBlocks[i] = collBlockAddr;
						nofFreeBlocks[i]++;
						freeHeap += collBlockSize;
						collBlockSize = 0;
 					}
					US.PUT4(currBlock, heapInfo & ~(1 << 31));	// unmark
					nofSweepMarkedBlock++;
				}
				// find block size
				if (heapInfo << 8 >= 0) {	// no array
					int size = heapInfo & 0xffff;
					blockSize = ((size + minBlockSize - 1) >> 4) << 4; 
				} else {	// is array
					if (heapInfo << 15 >= 0) {	// array of references
						int nofElems = heapInfo & 0xffff;
						blockSize = ((nofElems * 4 + 8 + minBlockSize - 1) >> 4) << 4; 
					} else {	// array of primitives
						int tag = US.GET4(currBlock + 4);
						int arrayDesc = US.GET4(tag);
						int compSize = arrayDesc & 0xffff;
						int nofElems = heapInfo & 0xffff;
						blockSize = ((nofElems * compSize + 8 + minBlockSize - 1) >> 4) << 4; 
					}
				}
				if (heapInfo >= 0) {	// add to collected block
					if (collBlockSize == 0) collBlockAddr = currBlock;
					collBlockSize += blockSize;
					nofSweepCollBlock++;
				}
				currBlock += blockSize;
			} // end of block is marked or block to be collected
		}
	}
	
	static {
		int heapOffset = US.GET4(sysTabBaseAddr + stHeapOffset);
		heapBase = US.GET4(sysTabBaseAddr + heapOffset);
		heapPtr = heapBase;
		heapSize = US.GET4(sysTabBaseAddr + heapOffset + 4);
		heapEnd = heapBase + heapSize;
		freeHeap = heapSize;
		// read the roots of all classes into array
		int classConstOffset = US.GET4(sysTabBaseAddr);
		while (true) {
			// get addresses of classes from system table
			int constBlkBase = US.GET4(sysTabBaseAddr + classConstOffset);
			if (constBlkBase == 0) break;
			nofRoots += US.GET4(constBlkBase + cblkNofPtrsOffset);	// get nof roots
			classConstOffset += 4;
		}
		roots = new int[nofRoots]; 
		int n = 0;
		classConstOffset = US.GET4(sysTabBaseAddr);
		while (true) {
			// get addresses of classes from system table
			int constBlkBase = US.GET4(sysTabBaseAddr + classConstOffset);
			if (constBlkBase == 0) break;
			int nofPtrs = US.GET4(constBlkBase + cblkNofPtrsOffset);
			for (int i = 0; i < nofPtrs; i++) {
				roots[n] = US.GET4(constBlkBase + cblkNofPtrsOffset + 4 + i * 4);
				n++;
			}
			classConstOffset += 4;
		}
		nofFreeBlocks = new int[nofFreeLists];
		freeBlocks = new int[nofFreeLists];
		// whole heap is one big free block
		US.PUT4(heapPtr, (1 << 30) | freeHeap);	// set free bit
		US.PUT4(heapPtr + 4, 0);	// next field is null
		freeBlocks[nofFreeLists - 1] = heapPtr;
		nofFreeBlocks[nofFreeLists - 1] = 1;	
	}

	// debug methods
	public static int getHeapSize() {
		return heapSize;
	}
	
	public static int getHeapBase() {
		return heapBase;
	}
	
	public static int getFreeHeap() {
		return freeHeap;
	}
	
	public static int getNofRoots() {
		return nofRoots;
	}
	
	public static int[] getRoots() {
		return roots;
	}
	
	public static int[] getFreeBlocks() {
		return freeBlocks;
	}
	
	public static int[] getNofFreeBlocks() {
		return nofFreeBlocks;
	}
	
	public static int getNofMarkedObjs() {
		return nofMarkedObjs;
	}
	
	public static int getNofMarkedRefArrays() {
		return nofMarkedRefArrays;
	}
	
	public static int getNofMarkedPrimArrays() {
		return nofMarkedPrimArrays;
	}
	
	public static int getNofMarkedRegObjs() {
		return nofMarkedRegObjs;
	}
}