package ch.ntb.inf.deep.unsafe;

/*changes:
 6.6.06,NTB/ED	creation
 */
/** Stub of Module SYSTEM */
public class US {

	/** put 1 byte: mem[address] = (byte)value */
	public static void PUT1(int address, int value) {
	}

	/** put 2 bytes: mem[address] = (short)value */
	public static void PUT2(int address, int value) {
	}

	/** put 4 bytes: mem[address] = value */
	public static void PUT4(int address, int value) {
	}

	/** put 8 bytes: mem[address] = value */
	public static void PUT8(int address, long value) {
	}

	/** get bit: return BIT(mem[address](byte), bitNr) */
	public static boolean BIT(int address, int bitNr) {
		return false;
	}

	/** get 1 byte: return mem[address] */
	public static byte GET1(int address) {
		return 0;
	}

	/** get 2 bytes: return mem[address] */
	public static short GET2(int address) {
		return 0;
	}

	/** get 4 bytes: return mem[address] */
	public static int GET4(int address) {
		return 0;
	}

	/** get 8 bytes: return mem[address] */
	public static long GET8(int address) {
		return 0;
	}

	/** get content of general purpose register */
	public static int GETGPR(int reg) {
		return 0;
	}

	/** get content of floating point register */
	public static double GETFPR(int reg) {
		return 0;
	}

	/** get content of special purpose register */
	public static int GETSPR(int reg) {
		return 0;
	}

	/** write to general purpose register */
	public static void PUTGPR(int reg, int value) {
	}

	/** write to floating point register */
	public static void PUTFPR(int reg, double value) {
	}

	/** write to special purpose register */
	public static void PUTSPR(int reg, int value) {
	}

	/** insert machine code */
	public static void ASM(String instr) {
	}

	/** get address of method */
	public static int ADR_OF_METHOD(String name) {
		return 0;
	}
	
	/** halt exception: program termination, 20 <= haltNr < 256 */
	public static void HALT(int haltNr) {
	}

	// ---- methods which must be called before any other statement of caller
	// (-method)

	/**
	 * saves FPSCR and all temporary FPRs (Floating Point Registers) and sets
	 * the FP flag in MSR (usingGPR0)
	 */
	public static void ENABLE_FLOATS() {
	}
}