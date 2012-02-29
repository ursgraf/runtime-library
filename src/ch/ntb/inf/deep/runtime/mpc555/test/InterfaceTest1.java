package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

public class InterfaceTest1 implements IA {

	public int mA1() {
		return 100;
	}
	
	public int mA2() {
		return 200;
	}

	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.out.println("start");
		
//		InterfaceTest1 obj = new InterfaceTest1();
//		System.out.println(obj.m1());
		IA obj = new InterfaceTest1();
		System.out.println(obj.mA1());
		System.out.println(obj.mA2());
		System.out.println("test ok");

	}
}


interface IA {
	int mA1();
	int mA2();
}


