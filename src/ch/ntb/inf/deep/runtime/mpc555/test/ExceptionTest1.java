package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;

public class ExceptionTest1 {
	
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
//		} finally { 
//			a += 2;
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

	static void m31() {
		int[] a = new int[3];
		a[5] = 1000;
	}


	
//	static void m32() {
//		int[] a = new int[3];
//		int i = -2;
//		while (i < 2) {
////			System.out.println(i);
//			try {
////				System.out.println("try");
//				a[i++] = 10;
//			} catch (Exception e) {
////				System.out.println("catch");
//				i++;
//			}
//		}
//		System.out.println(a[0]);
//		System.out.println(a[1]);
//		System.out.println(i);
////		System.out.println(a[1]);
////		System.out.println(a[2]);
////		System.out.println(a[3]);
//	}
//
	static void m4x() {
		int[] a = null;
		a[5] = 1000;
	}
//
//	static void m5() {
//		int a = 0;
//		int b = 100 / a;
//	}
//
//	static void m6() {
//		Object a = new short[3];
//		((int[])a)[0] = 100;
//	}
//
//	static int a = 1;
//	static void m71() {
//		if (a == 2) while (true); else {int b = 3;}
//	}

//	static void m7() {
//		try {
//			m3();
//		} catch (Exception e) {
////			m5();
//	//		int a = e.addr;
//			System.err.print(e.message);
//			System.out.print(" at addr ");
////			System.out.printHexln(e.addr);
////			while (true);
//		}
//	}

//	static void m8(int x) {
//		int a = 100;
//		try {
//			int c = 20;
//			a++;
//			throw new MyException1();
//		} catch (MyException1 e){ 
//			a += e.x;
//			if (a == 200) a += x;
//		}
//		int b = a + 100;
//	}
//	
//	static void m9() {
////		int[] a = new int[-1]; 
//		try {
//			MyException1[] a1 = new MyException1[-100];
//		} catch (Exception e) {
////			String str = e.message;
//			int a = 101;
////			String str = e.message;
//			System.out.println(e.message);
//		}
//	}

//	static void m10() {
//		int[] a = new int[4];
//		for (int i = 0; i < 3; i++) a[i] = 100;
//	}

////	public static void main (String[] args) {
////		m1();
////	}
	
//	static int m19() {
//		int a = 10;
//		if (a > 10){
//			int b = a + 1;
//		}
//		return a;
//	}
	
	static {
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI1.out);
		System.err = System.out;
		System.out.println("Exception demo");
	}
}

class MyException1 extends Exception{
	int x = 10;
}