package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;

public class ExceptionTest1 {
	
	@SuppressWarnings("unused")
	static void m1() {
		int a = 100;
		System.out.println("m1()");
		try {
			a++;
			if (a > 200) return;
			m2();
		} catch (MyException1 e){ 
			a += e.x;
			int c = a + 10;
			if (a > 200) return;
			System.out.println("catch1 in m1()");
		} catch (Exception e){
			int c = a + 20;
			if (a > 200) return;
			System.out.println("catch2 in m1()");
		} finally { 
			a += 2;
			System.out.print("a = "); System.out.println(a);
		}
	}

	static void m2() throws MyException1 {
		int a = 100;
		if (a > 10) throw new MyException1();
		a++;
	}
	
	static void m3() {
		try {
			m2();
		} catch (MyException1 e) {
			System.out.println(e.message);
		}
	}

	static void m4() {
		try {
			m2();
		} catch (MyException1 e) {
			System.out.println("catch in m3()");
		}
	}

	static void m5() {
		int[] a = new int[3];
		a[5] = 1000;
	}

	@SuppressWarnings("null")
	static void m6() {
		int[] a = null;
		a[5] = 1000;
	}

	@SuppressWarnings("unused")
	static void m7() {
//		int a = 0;
//		int b = 100 / a;
//		int b = 100 % a;
//		int c = a / 0; // must give ArithmeticException as well
		long m = 0;
//		long n = 100000000000L / m;	
		long n = 100000000000L % m;	
	}

	@SuppressWarnings("unused")
	static void m8() {
		int a = -1;
		int[] b = new int[a];
	}

	@SuppressWarnings("unused")
	static void m9() {
		int a = -1;
		MyException1[] b = new MyException1[a];
	}

	@SuppressWarnings("unused")
	static void m10() {
		Object obj = new MyException1();
		ExceptionTest1 obj2 = (ExceptionTest1) obj;
	}

	static void m11() {
		Object a = new short[3];
//		Object obj = new MyException1();
		((int[])a)[0] = 100;
	}

	static {
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI1.out);
		System.err = System.out;
		System.out.println("Exception demo");
	}
}

@SuppressWarnings("serial")
class MyException1 extends Exception {
	int x;
	
	public MyException1() {
		message = "MyException";
		x = 10;
	}
}