package ch.ntb.inf.deep.runtime;

/**
 *  Mock kernel. The class file reader will replace this class by the proper kernel class, 
 *  which is set in the configuration. 
 */
public abstract class Kernel {
	
	/** 
	 * Address of the main loop. This address must be set by the kernel or a specific scheduler
	 * to a method containing an infinite loop. 
	 */
	public static int loopAddr;
	
	/**
	 * Address of a target command. A target command is a parameter-less method which can be 
	 * called by the host through a debugger. This method is inserted into the regular 
	 * schedule and run once.
	 */
	public static int cmdAddr;

	/**
	 * Reads the system time.
	 * 
	 * @return System time in \u00b5s
	 */
	public static long timeUs() {
		return -1;
	}

	/**
	 * Reads the system time.
	 * 
	 * @return System time in ns
	 */
	public static long timeNs() {
		return -1;
	}

	/**
	 * Blinks a led on a hardware pin a specified number of times.
	 * 
	 * @param i Number of times the led blinks.
	 */
	public static void blink(int i) { }

	/**
	 * Enables interrupts globally. 
	 * Individual interrupts for peripheral components must be enabled locally.
	 */
	public static void enableInterrupts() { }

}
