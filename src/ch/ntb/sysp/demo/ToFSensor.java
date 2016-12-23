package ch.ntb.sysp.spezausb.tof;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.ppc32.Task;

public class ToFSensor extends Task{
	static VL6180X_SC18IS600 vs;
	
	public void action(){
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
		byte val = vs.getSingleRangeValue(1);
		System.out.print("RESULT__RANGE_VAL:\tSens0: ");
		System.out.printHex((val&0xFF));
		System.out.print("\t");
		if(val < 0) val+=256;
		System.out.println((val));
		System.out.println("-------------------------------------------");
		val = vs.getSingleRangeValue(2);
		System.out.print("RESULT__RANGE_VAL:\tSens1: ");
		System.out.printHex((val&0xFF));
		System.out.print("\t");
		if(val < 0) val+=256;
		System.out.println((val));
		System.out.println("-------------------------------------------");
		val = vs.getSingleRangeValue(3);
		System.out.print("RESULT__RANGE_VAL:\tSens2: ");
		System.out.printHex((val&0xFF));
		System.out.print("\t");
		if(val < 0) val+=256;
		System.out.println((val));
		System.out.println("-------------------------------------------");
		val = vs.getSingleRangeValue(4);
		System.out.print("RESULT__RANGE_VAL:\tSens3: ");
		System.out.printHex((val&0xFF));
		System.out.print("\t");
		if(val < 0) val+=256;
		System.out.println((val));
		System.out.println("-------------------------------------------");
	}
	
	public static void startCont(){					// für Beispiel 2
		vs.startRangeContMode(1);
		vs.startRangeContMode(2);
		vs.startRangeContMode(3);
		vs.startRangeContMode(4);
	}
	
	public static void getCont(){					// für Beispiel 2
		System.out.print("Sensor0: ");
		System.out.printHex(vs.getRangeContValue(1));
		System.out.print(" mm\nSensor1: ");
		System.out.printHex(vs.getRangeContValue(2));
		System.out.print(" mm\nSensor2: ");
		System.out.printHex(vs.getRangeContValue(3));
		System.out.print(" mm\nSensor3: ");
		System.out.printHex(vs.getRangeContValue(4));
		System.out.println(" mm");
	}
	
	static{
		SCI sci2 = SCI.getInstance(SCI.pSCI2);
		sci2.start(19200, SCI.NO_PARITY, (short)8);

		System.out = new PrintStream(sci2.out);
		
		vs = new VL6180X_SC18IS600(4);
		
		Task s = new ToFSensor();
		s.period = 1000;
		
		Task.install(s);
	}
}
