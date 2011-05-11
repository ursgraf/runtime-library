package ch.ntb.inf.deep.runtime.mpc555.demo;
import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;


public class Robi2SensorProximityDemo_Out extends Task {
	static final short NoOfSensors = Robi2SensorProximityDemo.NoOfSensors;

	private static int count;

	public void action( ) {
		for (short i = 0; i < NoOfSensors; i++) {
			int val = Robi2.getDistSensorValue(i);
			System.out.print(val);
			if (count < NoOfSensors-1) {
				System.out.print("\t");
				count++;
			} else {
				System.out.println();
				count = 0;
			}
		}
	}
	
	static { // Initialisierung
		// Initialize SCI1 (9600 8N1)
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		
		// Use the SCI1 for stdout
		System.out = new PrintStream(SCI1.out);
		count = 0;
	}
}