package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.HLC1395Pulsed;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/* CHANGES:
 * 22.02.2011	NTB/Züger	OutT replaced by System.out
 * 09.02.2011	NTB/Züger	creation
 */

public class HLC1395Demo extends Task {
	
	public void action() {
		for(int i = 0; i < 4; i++) {
			System.out.print(HLC1395Pulsed.read(i));
			System.out.print('\t');
		}
		System.out.println();
	}
	
	static {
		HLC1395Pulsed.init(4, 0x50076, 59); // initialize 4 sensors (addrPin0 = MPIOB6, addrPin1 = MPIOB7, trgPin = MPIOB5, analogInPin = AN59)
		HLC1395Pulsed.start();
		
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		
		System.out.println("HLC1295-Demo");
		
		Task demoTask = new HLC1395Demo();
		demoTask.period = 1000;
		Task.install(demoTask);
	}
}
