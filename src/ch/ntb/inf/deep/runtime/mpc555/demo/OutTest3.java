package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class OutTest3 extends Task {
	static long time1;
	
	public void action() {
		SCI2.write((byte)'w');
	}
	
	static {
		SCI2.start(9600, (byte)0, (short)8);
		SCI2.write((byte)'a');
		time1 = Kernel.time();
		SCI2.write((byte)'b');
		Task t1 = new OutTest3();
		t1.period = 1000; 
		t1.action();
		Task.install(t1);
	}
}
