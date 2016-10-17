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

package ch.ntb.inf.deep.runtime.ppc32;

import ch.ntb.inf.deep.runtime.IdeepCompilerConstants;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 * 22.1.11	Urs Graf		newstring added
 * 1.1.11	Urs Graf		two- and three dimensional arrays supported
 * 1.5.12	Urs Graf		GC added
 */

/**
 *  Heap manager with mark-sweep garbage collection.<br>
 *  As soon as the remaining heap space is lower than a third of the total available
 *  heap space, a garbage collection is called.  
 */
public class Heap implements IdeepCompilerConstants {
	private static final boolean dbg = false;
	private static final int minBlockSize = 16;	// smallest block size to allocate
	private static final int nofFreeLists = 8;	// free blocks are kept in multiple lists
	
	private static int nofMarkedObjs;	// total number of marked objects, objects still in use
	private static int nofMarkedRefArrays;	// number of marked arrays of references
	private static int nofMarkedPrimArrays;	// number of marked arrays of primitive type
	private static int nofMarkedRegObjs;	// number of marked regular objects (no arrays)
	private static int heapBase;	// address of start of heap
	private static int heapSize;	// size of heap in bytes
	private static int heapEnd;		// address of end of heap
	private static int freeHeap;	// size of free heap in bytes
	private static int heapPtr;		// used for allocation before Heap class is initialized
	private static int threshold;	// limit, when gc is started 
	@SuppressWarnings("unused")
	private static boolean mark;	// phase of gc
	private static int nofRoots;	// number of roots in all classes
	private static int[] roots;		// addresses of roots in all classes
	private static int[] freeBlocks;	// free blocks have size minBlockSize, 2*minBlockSize, 
										// 3*minBlockSize .. 6*minBlockSize, greater than 7*minBlockSize
	private static int[] nofFreeBlocks;	// counter for debugging purposes
	
	private static int nofSweepFreeBlock, nofSweepMarkedBlock, nofSweepCollBlock;
	private static int currBlock;
	static boolean runGC;
	/**
	 * Base address of the system table. Must be set by the boot method of the kernel.
	 */
	public static int sysTabBaseAddr;
	
	// called by new	
	@SuppressWarnings("unused")
	private static int newObject(int ref) throws RuntimeException {	
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
	private static int newPrimTypeArray(int nofElements, int type, int ref) throws NegativeArraySizeException {
		if (nofElements < 0) throw new NegativeArraySizeException("NegativeArraySizeException");
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
	private static int newRefArray(int nofElements, int ref) throws NegativeArraySizeException {
		if (nofElements < 0) throw new NegativeArraySizeException("NegativeArraySizeException");
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
	@SuppressWarnings("unused")
	private static int newMultiDimArray(int ref, int nofDim, int dim0, int dim1, int dim2, int dim3) throws NegativeArraySizeException {
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
		}
		return addr;
	}
	
	// called by newstring in java/lang/String
	@SuppressWarnings("unused")
	private static int newstring(int ref, int len) {
		int size = len + 8;
		int blockAddr = getBlock(size);
		US.PUT4(blockAddr, 0x80000000 | size);	// set mark bit and size, clear array bit and primitive array bit
		US.PUT4(blockAddr + 4, ref);	// write tag
		ref = blockAddr + 8;
		int i = ref;
		while (i < blockAddr + size) {US.PUT4(i, 0); i += 4;}
		return ref;
	}

	private static int getBlock(int size) throws RuntimeException {
		int addr;
		int blockSize = ((size + minBlockSize - 1) >> 4) << 4;
		if (blockSize >= 0x10000) throw new RuntimeException("Exception: Array block too big");	// array length must fit into 16 bit
		int i = blockSize / minBlockSize - 1;
		if (i >= nofFreeLists) i = nofFreeLists - 1;
		// search free block in free block list
		if (freeBlocks == null) { // there is no free list at the very beginning of the boot process
			addr = heapPtr;
			heapPtr += blockSize;
			freeHeap -= blockSize;
		} else {
			if (freeHeap < threshold) {
				runGC = true;
//				if (mark) mark(); else sweep();
//				mark = !mark;
			}
			while (freeBlocks[i] == 0 && i < nofFreeLists - 1) i++;
			if (i < nofFreeLists - 1) {	
				addr = freeBlocks[i];	// unlink block from list
				freeBlocks[i] = US.GET4(addr + 4);	
				nofFreeBlocks[i]--;
				int freeBlockSize = (i+1) * minBlockSize;
				freeHeap -= freeBlockSize;
				int restBlockSize = freeBlockSize - blockSize;	// put rest into free list
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
				if (addr == 0) throw new RuntimeException("Exception: Allocation in heap failed");	// no block in list 
				int freeBlockSize = US.GET4(addr) & 0xffffff;
				int prev = addr;
				while (blockSize > freeBlockSize) {	// search block which is big enough
					prev = addr;
					addr = US.GET4(addr + 4);
					if (addr == 0) throw new RuntimeException("Exception: Allocation in heap failed");	// no block left 
					freeBlockSize = US.GET4(addr) & 0xffffff;
				}
				// unlink block
				if (prev == addr) freeBlocks[nofFreeLists - 1] = US.GET4(addr + 4);	// first block in list
				else US.PUT4(prev + 4, US.GET4(addr + 4));
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

	/**
	 * Starts mark phase of garbage collection.
	 * This method should be solely used for test purposes. Never use it in application code! A garbage collection is automatically done
	 * when available heap space is low.
	 */
	public static void mark() {
//		System.out.println("mark");
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
				// follow references in the object
				int tag = US.GET4(obj - 4);
				int refsAddr = tag + US.GET4(tag + 8);
				int nofRefs = US.GET4(refsAddr);
				refsAddr += 4;
				for (int i = 0; i < nofRefs; i++) {
					int offset = US.GET4(refsAddr + i * 4);
					int ref = US.GET4(obj + offset);
					if (ref != 0) traverse(ref);
				}
			} else {	// array
				if (heapInfo << 15 >= 0) {	// array of references
					if (dbg) nofMarkedRefArrays++;
					int len = heapInfo & 0xffff;
					for (int i = 0; i < len; i++) {
						int ref = US.GET4(obj + i * 4);
						if (ref != 0) traverse(ref);
					}
				} else {	// array of primitives, don't follow
					if (dbg) nofMarkedPrimArrays++;
				}
			}
		}
	}

	/**
	 * Starts sweep phase of garbage collection.
	 * This method should be solely used for test purposes. Never use it in application code! A garbage collection is automatically done
	 * when available heap space is low.
	 */
	public static void sweep() {	// call to sweep only after marking
//		System.out.print("start sweep, free heap size = "); System.out.printHexln(Heap.getFreeHeap());
		int blockSize, collBlockAddr = 0, collBlockSize = 0;
		if (dbg) {nofSweepFreeBlock = 0; nofSweepMarkedBlock = 0; nofSweepCollBlock = 0;}
		currBlock = heapBase;
		while (currBlock < heapEnd) {
//			System.out.printHex(currBlock);
			int heapInfo = US.GET4(currBlock);
			if (heapInfo << 1 < 0) {	// block is free
//				System.out.println(" block is free");
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
				if (dbg) nofSweepFreeBlock++;
			} else {	// block is marked as used or block to be collected
				if (heapInfo < 0) {	// object is marked
//					System.out.print(" block is marked ");
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
					if (dbg) nofSweepMarkedBlock++;
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
//				System.out.print(" size="); System.out.printHexln(blockSize);
				if (heapInfo >= 0) {	// add to collected block
					if (collBlockSize == 0) collBlockAddr = currBlock;
					collBlockSize += blockSize;
					if (dbg) nofSweepCollBlock++;
				}
				currBlock += blockSize;
			} // end of block is marked or block to be collected
		}
//		System.out.print("end sweep, free heap size = "); System.out.printHexln(Heap.getFreeHeap());
	}
	
	static {
		int heapOffset = US.GET4(sysTabBaseAddr + stHeapOffset);
		heapBase = US.GET4(sysTabBaseAddr + heapOffset + 4);
		heapPtr = heapBase;
		heapSize = US.GET4(sysTabBaseAddr + heapOffset + 8);
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
		// int i = heapPtr + 8; while (i < heapEnd) {US.PUT4(i, 0); i += 4;} // initialize heap, nice for debugging
		threshold = heapSize / 3;
//		mark = true;
		freeBlocks[nofFreeLists - 1] = heapPtr;
		nofFreeBlocks[nofFreeLists - 1] = 1;	
	}

	/**
	 * Query total heap size.
	 * @return Total heap size in bytes.
	 */
	public static int getHeapSize() {
		return heapSize;
	}
	
	/**
	 * Query base address of heap.
	 * @return Base address of heap.
	 */
	public static int getHeapBase() {
		return heapBase;
	}
	
	/**
	 * Query free heap size.
	 * @return Free heap size in bytes.
	 */
	public static int getFreeHeap() {
		return freeHeap;
	}
	
	/**
	 * Used for debugging purposes.
	 * @return Number of roots.
	 */
	public static int getNofRoots() {
		return nofRoots;
	}
	
	/**
	 * Used for debugging purposes.
	 * @return Array of roots.
	 */
	public static int[] getRoots() {
		return roots;
	}
	
	/**
	 * Used for debugging purposes.
	 * @return Array of free blocks.
	 */
	public static int[] getFreeBlocks() {
		return freeBlocks;
	}
	
	/**
	 * Used for debugging purposes.
	 * @return Array of number of free blocks.
	 */
	public static int[] getNofFreeBlocks() {
		return nofFreeBlocks;
	}
	
	/**
	 * Used for debugging purposes.
	 * @return Number of marked objects.
	 */
	public static int getNofMarkedObjs() {
		return nofMarkedObjs;
	}
	
	/**
	 * Used for debugging purposes.
	 * @return Array of number of marked reference arrays.
	 */
	public static int getNofMarkedRefArrays() {
		return nofMarkedRefArrays;
	}
	
	/**
	 * Used for debugging purposes.
	 * @return Array of number of marked primitives arrays.
	 */
	public static int getNofMarkedPrimArrays() {
		return nofMarkedPrimArrays;
	}
	
	/**
	 * Used for debugging purposes.
	 * @return Array of number of marked regular objects arrays.
	 */
	public static int getNofMarkedRegObjs() {
		return nofMarkedRegObjs;
	}
}