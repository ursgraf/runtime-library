package ch.ntb.inf.sts.mpc555;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class Heap {

	// called by new	
	private static Object newObject(Object ref) {
		return ref;
	}
	
	// called by newarray	
	private static Object newArray(int nofElements, int type) {
		Object ref = null;
		return ref;
	}
	
	// called by anewarray	
	private static Object anewArray(int nofElements, Object ref) {
		return ref;
	}
	
	// called by multianewarray	
	private static Object multianewArray(int nofDimensions, int dim1, int dim2) {
		Object ref = null;
		return ref;
	}
	
}