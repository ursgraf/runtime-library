package ch.ntb.inf.deep.runtime.mpc555;

import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class Heap {
//	static private int heapBase;
//	static private int heapPtr;

	// called by new	
	private static int newObject(int ref) {	
/*		int size = US.GET4(ref) + 8;
		int addr = heapPtr; 
		while (addr < heapPtr + size) US.PUT4(addr, 0);
		US.PUT4(heapPtr + 4, ref);	// write tag
		ref = heapPtr + 8;
		heapPtr += ((size + 15) >> 4) << 4;*/
		return ref;
	}
	
	// called by newarray	
	private static int newPrimTypeArray(int nofElements, int type) {
/*		int elementSize;
		if (type == 7 || type == 11) elementSize = 8;
		else if (type == 6 || type == 10) elementSize = 4;
		else if (type == 5 || type == 9) elementSize = 2;
		else elementSize = 1;
		int size = nofElements * elementSize + 8;
		int addr = heapPtr; 
		while (addr < heapPtr + size) US.PUT4(addr, 0);
		US.PUT4(heapPtr + 4, type);	// write tag
		US.PUT2(heapPtr + 2, nofElements);	// write length
		int ref = heapPtr + 8;
		heapPtr += ((size + 15) >> 4) << 4;*/
		int ref = 4;
		return ref;
	}
	
	// called by anewarray	
	private static Object newRefArray(Object ref, int nofElements) {
		return ref;
	}
	
	// called by multianewarray	
	private static Object newMultiDimArray(Object ref, int dim1, int dim2) {
//		ref = null;
		return ref;
	}
	
/*	static {
		heapBase = US.GET4(100);
		heapPtr = heapBase;
	}
	*/
}