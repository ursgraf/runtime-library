package ch.ntb.inf.deep.runtime.mpc555;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class Heap {

	// called by new	
	private static Object newObject(Object ref) {
		return ref;
	}
	
	// called by newarray	
	private static Object newPrimTypeArray(int nofElements, int type) {
		Object ref = null;
		return ref;
	}
	
	// called by anewarray	
	private static Object newRefArray(Object ref, int nofElements) {
		return ref;
	}
	
	// called by multianewarray	
	private static Object newMultiDimArray(Object ref, int dim1, int dim2) {
		ref = null;
		return ref;
	}
	
}