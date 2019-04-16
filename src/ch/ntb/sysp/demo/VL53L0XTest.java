package ch.ntb.sysp.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.mpc555.driver.VL53L0X;
import ch.ntb.inf.deep.runtime.ppc32.Task;


public class VL53L0XTest extends Task {
	static VL53L0X vs;
	short[] data;
	boolean invalid = false;
	int count = 0;
	
	public void action(){
		data = vs.read();
		invalid = false;
		
		if (!vs.resetDone())
		{
			System.out.println("waiting until reset is done");
			return;
		}
		
		
		for (short dist : data)
		{
			System.out.print(dist);
			System.out.print(" ");
			if (dist <= 0)
			{
				// checking validity is optional
				invalid = true;
			}
		}
		System.out.println();
		
		// this is optional
		if (invalid)
		{
			vs.reset();
		}
	}
	
	
	static{
		SCI sci1 = SCI.getInstance(SCI.pSCI1);
		sci1.start(9600, SCI.NO_PARITY, (short)8);

		System.out = new PrintStream(sci1.out);
		System.err = System.out;
		
		vs = new VL53L0X(4);
		
		Task s = new VL53L0XTest();
		s.period = 500;
		
		Task.install(s);
	}
}
