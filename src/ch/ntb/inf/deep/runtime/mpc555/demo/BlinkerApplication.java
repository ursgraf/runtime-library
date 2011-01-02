package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.*;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2Plain;

class BlinkerApplication {
	static int res;
	static Blinker blinker14, blinker13, blinker12;
	static Task task1;
	
	static void getNofBlinkers () {
		res = Blinker.getNofBlinkers();
	}
	
	static void changePeriod14 () {
		blinker14.changePeriod(100);
	}
	
	static void changePeriod13 () {
		if (task1 instanceof Blinker) ((Blinker)task1).changePeriod(100);
	}
	
	static {
		SCI2Plain.start(9600, (byte)0, (short)8);
		SCI2Plain.write((byte)'0');
//		blinker14 = new Blinker(14, 500); 
//		blinker13 = new Blinker(13, 100, 150); 
		blinker12 = new Blinker(12, 1000, 15); 
		SCI2Plain.write((byte)'1');
//		task1 = blinker13;
	}
}