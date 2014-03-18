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


//	public static void main (String[] args) {
//		m1();
//	}
}

class MyException1 extends Throwable{
	int x = 10;
}