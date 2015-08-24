package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.ppc32.Task;

public class ExceptionTest2 extends Task {
	int i = -1;
	static int[] a = new int[5];
	
	public void action() {
		i++;
		if (i == 7) i = -1;
		System.out.println(i);
		a[i] = nofActivations;
	}

	static {
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = System.out;
		System.out.println("Exception demo");
		Task t = new ExceptionTest2();
		t.period = 1000;
		Task.install(t);
	}
}

