package ch.ntb.inf.deep.runtime;

/**
 *  Mock kernel. The class file reader will replace this class by the proper kernel class, 
 *  which is set in the configuration. 
 */
public abstract class Kernel {
	
	public static int loopAddr;
	public static int cmdAddr;

	public static long time() {
		return -1;
	}

	public static void blink(int i) {
	
	}

}
