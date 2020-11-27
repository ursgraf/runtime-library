package org.deepjava.runtime.iMX6.test;
import java.io.PrintStream;

import org.deepjava.runtime.iMX6.driver.*;

public class UART3Test {
	static {
		UART3.start(115200, (short)0, (short)8);
		System.out = new PrintStream(UART3.out);
		System.out.println("UART3 Test");
	}

}
