package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.*;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2Plain;
import ch.ntb.inf.deep.unsafe.US;

class BlinkerApplication {
	static int res;
	static Blinker blinker14, blinker13, blinker12, blinker11;
	static Task task1;
	
	static void getNofBlinkers () {
		res = Blinker.getNofBlinkers();
	}
	
	static void changePeriod14to100 () {
		Task.remove(blinker14);
		blinker14.changePeriod(100);
		Task.install(blinker14);
	}
	static void changePeriod14to1000 () {
		Task.remove(blinker14);
		blinker14.changePeriod(1000);
		Task.install(blinker14);
	}
	
	static void changePeriod13 () {
		if (task1 instanceof Blinker) ((Blinker)task1).changePeriod(2000);
	}
	
	static {
		SCI2Plain.start(9600, (byte)0, (short)8);
		SCI2Plain.write((byte)'0');
		blinker14 = new Blinker(14, 500); 
		blinker12 = new Blinker(12, 1000, 5); 
		blinker13 = new Blinker(13, 100, 20); 
		blinker11 = new Blinker(11, 500, 30); 
		SCI2Plain.write((byte)'1');
		task1 = blinker13;
	}
}