package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.HLC1395P16;
import ch.ntb.inf.deep.runtime.mpc555.driver.OutT;

/* CHANGES:
 * 09.02.2011	NTB/Züger	creation
 */

public class HLC1395Demo extends Task {
	
	
	public void action() {
		for(int i = 0; i < 4; i++) {
			OutT.print(HLC1395P16.read(i));
		//	OutT.print(i);
			OutT.printTab();
		}
		OutT.println();
	}
	
	static {
		HLC1395P16.init(4, 0x50076, 59); // initialize 4 sensors (addrPin0 = MPIOB6, addrPin1 = MPIOB7, trgPin = MPIOB5, analogInPin = AN59)
		HLC1395P16.start();
		
		OutT.switchToSCI2();
		OutT.print('H');
		OutT.print('L');
		OutT.print('C');
		OutT.print('1');
		OutT.print('3');
		OutT.print('9');
		OutT.print('5');
		OutT.print('-');
		OutT.print('D');
		OutT.print('e');
		OutT.print('m');
		OutT.print('o');
		OutT.println();
		
		Task demoTask = new HLC1395Demo();
		demoTask.period = 1000;
		Task.install(demoTask);
	}
}
