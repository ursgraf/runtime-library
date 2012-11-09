package ch.ntb.inf.deep.runtime;

public interface IdeepCompilerConstants {

	// Compiler specific constants
	public static final int cblkCodeBaseOffset = 0x4;
	public static final int cblkCodeSizeOffset = 0x8;
	public static final int cblkVarBaseOffset = 0xc;
	public static final int cblkVarSizeOffset = 0x10;
	public static final int cblkClinitAddrOffset = 0x14;
	public static final int cblkNofPtrsOffset = 0x18;
	public static final int stClassConstOffset = 0x0;
	public static final int stStackOffset = 0x4;
	public static final int stHeapOffset = 0x8;
	public static final int stKernelClinitAddr = 0xc;
	public static final int stResetOffset = 0x10;

}