package ch.ntb.inf.deep.unsafe;

/*changes:
 6.6.06,NTB/ED	creation
 */
/** Stub of Module SYSTEM */
public class SYS {
	/** get address */
	public static int ADR(int variable) {
		return 0;
	}

	public static int ADR(boolean variable) {
		return 0;
	}

	public static int ADR(double variable) {
		return 0;
	}

	public static int ADR(float variable) {
		return 0;
	}

	public static int ADR(Object object) {
		return 0;
	}

	/** put 1 byte: mem[address] = (byte)value */
	public static void PUT1(int address, int value) {
	}

	/** put 2 bytes: mem[address] = (short)value */
	public static void PUT2(int address, int value) {
	}

	/** put 4 bytes: mem[address] = value */
	public static void PUT4(int address, int value) {
	}

	/** get bit: return BIT(mem[address], bitNr) */
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

	/** get content of general purpose register */
	public static int GETGPR(int reg) {
		return 0;
	}

	/** get content of floating point register */
	public static int GETFPR(int reg) {
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
	public static void PUTFPR(int reg, int value) {
	}

	/** write to special purpose register */
	public static void PUTSPR(int reg, int value) {
	}

	/** insert machine code */
	public static void ASM(String instr) {
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