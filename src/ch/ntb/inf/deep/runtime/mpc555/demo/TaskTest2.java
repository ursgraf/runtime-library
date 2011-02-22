package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/*changes:
 * 10.1.11	NTB/GRAU	creation
 */

public class TaskTest2 extends Task {
	long startTime;
	int pin;
	static TaskTest2 t1;
	
	public void action() {
		SCI2.write((byte)'.');;
		if (Kernel.time() > startTime + 100000) {
			MPIOSM_DIO.out(pin, !MPIOSM_DIO.in(pin));
			startTime = Kernel.time();
		}
	}
	
	public TaskTest2(int pin) {
		SCI2.write((byte)'a');
		this.startTime = Kernel.time();
		this.pin = pin;
		MPIOSM_DIO.init(pin, true);
		period = 500;
		time = 50;
		Task.install(this);
		SCI2.write((byte)'b');
	}
	
	static {
		SCI2.start(9600, (byte)0, (short)8);
		SCI2.write((byte)'0');
//		OutT.switchToSCI2();
//		OutT.print((byte)'0');
//		OutT.println((byte)'1');
		t1 = new TaskTest2(9);
//		TaskTest2 t2 = new TaskTest2(10);
	}
}
