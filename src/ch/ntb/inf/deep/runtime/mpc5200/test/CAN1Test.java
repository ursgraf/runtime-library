package ch.ntb.inf.deep.runtime.mpc5200.test;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc5200.Task;
import ch.ntb.inf.deep.runtime.mpc5200.driver.UART3;
import ch.ntb.inf.deep.runtime.mpc5200.driver.can.CAN1;

public class CAN1Test extends Task {
	
	public void action() {
		CAN1.sampleNodes();
		if (nofActivations % 2000 == 0) {
			for (int i = 0; i < CAN1.nodeData.length; i++) {
				System.out.print(CAN1.nodeData[i].forceX);
				System.out.print('.');
				System.out.print(CAN1.nodeData[i].forceY);
				System.out.print('.');
				System.out.print(CAN1.nodeData[i].forceZ);
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	

	static {	
		UART3.start(9600, UART3.NO_PARITY, (short)8);
		System.out = new PrintStream(UART3.out);
		System.out.println("start");
		CAN1.init();
		Task t = new CAN1Test();	
		t.period = 1;
		Task.install(t);
	}
}
