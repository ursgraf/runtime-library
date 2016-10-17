package ch.ntb.inf.deep.runtime.mpc5200.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc5200.driver.UART3;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/** 
 * Test class which periodically throws a <code>ArrayIndexOutOfBoundsException</code>.
 * 
 * @author Urs Graf
 *
 */
public class ExceptionDemo extends Task {
	static Task t;
	int i = -1;
	static int[] a = new int[5];

	public void action() {
		i++;
		if (i == 7) i = -1;
		System.out.println(i);
		a[i] = nofActivations;
	}

	static {
		UART3.start(9600, UART3.NO_PARITY, (short)8);
		System.out = new PrintStream(UART3.out);
		System.err = System.out;
		System.out.println("Exception demo");
		t = new ExceptionDemo();
		t.period = 2000;
		Task.install(t);
	}
}

