package ch.ntb.inf.deep.runtime.iMX6;

// Auto generated file (2015-10-06 07:18:50)

public interface IiMX6 {

	// System constants of CPU iMX6
	public static final int SRR1init = 0x3802;
	public static final int stackSize = 0x2000;
	public static final int sysTabBaseAddr = 0x4000;
	public static final int excpCodeSize = 0x4000;
	public static final int excpCodeBase = 0x0;
	public static final int CMFB_Size = 0x30000;
	public static final int CMFB_BaseAddr = 0x40000;
	public static final int CMFA_Size = 0x40000;
	public static final int CMFA_BaseAddr = 0x0;
	public static final int SRAMB_Size = 0x4000;
	public static final int SRAMB_BaseAddr = 0x3fc000;
	public static final int SRAMA_Size = 0x2800;
	public static final int SRAMA_BaseAddr = 0x3f9800;
	public static final int IMB = 0x0;

	// Specific registers of CPU iMX6
	public static final int SPR80 = 0x50;
	public static final int EIE = 0x50;
	public static final int SIUMCR = 0x2fc000;
	public static final int SYPCR = 0x2fc004;
}