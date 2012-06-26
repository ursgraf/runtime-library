package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;

public class ArrayTest5 {
	void print() {
		Foo f = new ArrayTest5.Foo();

		double sum = 0;
		for (int n = 0; n < 2; n++) {
			for (int m = 0; m < 2; m++)	{
				sum += f.A[n][m];
			}
		}
		System.out.println(sum);
	}

	static {
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI1.out);
		System.out.println("start");
		new ArrayTest5().print();
	}


	public class Foo {
		double[][] A = new double[][] {{ 0, 1},{ 2, 3}};
	}
}
