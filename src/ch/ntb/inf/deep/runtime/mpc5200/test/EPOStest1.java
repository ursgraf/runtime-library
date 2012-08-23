package ch.ntb.inf.deep.runtime.mpc5200.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc5200.Task;
import ch.ntb.inf.deep.runtime.mpc5200.driver.UART3;
import ch.ntb.inf.deep.runtime.mpc5200.driver.can.CANopen;
import ch.ntb.inf.deep.runtime.mpc5200.driver.can.EPOS;

public class EPOStest1 extends Task {
	static EPOS drive1;
	static int pos = 0;
	static boolean toggle = true;

	public void action() {
		if (toggle) pos += 10; else pos -=10;
//		drive1.setPosition(pos);
		if (this.nofActivations % 100 == 0) {
			CANopen.dispMsgBuf2();
			drive1.sendSync(); 
		}
		if (this.nofActivations % 500 == 0) {
			if (toggle) drive1.setOutAC(); else drive1.setOutBD();
			toggle = !toggle;
		}
	}
	

	static {	
		UART3.start();
		System.out = new PrintStream(UART3.out);
		drive1 = new EPOS((byte)1);
		drive1.start();
		drive1.initOutABCD(); 
		drive1.setOutBD();
		drive1.setParams();
		drive1.setPDOtransmission();
		drive1.startNode();
		drive1.enablePower();
		Task t = new EPOStest1();	
		t.period = 10;
		Task.install(t);
	}
}
