package ch.ntb.inf.deep.runtime.ppc32;

import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 12.3.2014	NTB/Urs Graf	creation
 */

public class ProgramExc extends PPCException implements Ippc32 {
	public static int nofProgExceptions;
	public static int a;
	static int nextOpCode;

	static void programExc(Exception e) {
		nofProgExceptions++;
		int addr = US.GETSPR(SRR0);
		int instr = US.GET4(addr);
		int opCode = instr >> 21;
		if (opCode == 0x3ff) {	// tw, TOalways -> user defined exception
//			if (e.message == null) e.message = "custom";
			a = 10;
		} else if (opCode == 0x3e5) {	// tw, TOifgeU -> ArrayIndexOutOfBounds
			e = new ArrayIndexOutOfBoundsException("ArrayIndexOutOfBoundsException");
			a = 20;
		} else if (opCode == 0x64) {	// twi, TOifequal
			e = bla(addr);
		} else if (opCode == 0x3f8) {	// tw, TOifnequal
			e = new ClassCastException("ClassCastException");
			a = 50;
		} else {	
			e = new ClassCastException("UnknownException");
			a = 100;
		}
		e.addr = addr;

		US.PUTGPR(R2, US.REF(e));	// copy to volatile register
		US.PUTGPR(R3, addr);	// copy to volatile register
	}

	private static Exception bla(int addr) {
		Exception e;
		int nextInstr = US.GET4(addr + 4);
		nextOpCode = nextInstr;
		if ((nextInstr & 0xfc0003fe) == 0x7c0003d6) {	// divw -> ArithmeticException
			e = new ArithmeticException("ArithmeticException");
			a = 30;
//			US.ASM("bc always, 0,  0");
		} else {	// NullPointer
			e = new NullPointerException("NullPointerException");
			a = 40;
		}
		return e;
	}

}
