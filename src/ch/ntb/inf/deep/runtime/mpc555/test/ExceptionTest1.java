package ch.ntb.inf.deep.runtime.mpc555.test;

public class ExceptionTest1 {
	
	static void m1() {
		int a = 100;
		try {
			a++;
			if (a > 200) return;
			m2();
		} catch (MyException1 e){ 
			a += e.x;
			int c = a + 10;
			if (a > 200) return;
			System.out.println("catch1");
		} catch (Exception e){
			int c = a + 10;
			if (a > 200) return;
			System.out.println("catch2");
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
		int[] a = new int[3];
		a[5] = 1000;
	}
	
	static void m4() {
		int[] a = null;
		a[5] = 1000;
	}

	static void m5() {
		int a = 0;
		int b = 100 / a;
	}

	static void m6() {
		Object a = new short[3];
		((int[])a)[0] = 100;
	}

	static int a = 1;
	static void m71() {
		if (a == 2) while (true); else {int b = 3;}
	}

	static void m7() {
		try {
			m3();
		} catch (Exception e) {
//			int a = 2;
			while (true);
		}
	}

	static void m8() {
		int a = 100;
		try {
			a++;
			throw new MyException1();
		} catch (MyException1 e){ 
			a += e.x;
			while (true);
//			System.out.println("catch1");
		}
	}
	
	static void m9() {
		int a = 100;
		a += 5;
		while (true);
	}

	static void m10() {
		m3();
	}

//	public static void main (String[] args) {
//		m1();
//	}
}

class MyException1 extends Exception{
	int x = 10;
}