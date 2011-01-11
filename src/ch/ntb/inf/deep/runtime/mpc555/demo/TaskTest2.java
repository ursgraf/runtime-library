package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.*;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 10.1.11	NTB/GRAU	creation
 */

public class TaskTest2 extends Task {
	long startTime;
	int pin;
	static TaskTest2 t1;
	
	public void action() {
		SCI2Plain.write((byte)'.');;
		if (Kernel.time() > startTime + 100000) {
			Mpiosm.out(pin, !Mpiosm.in(pin));
			startTime = Kernel.time();
		}
	}
	
	public TaskTest2(int pin) {
		SCI2Plain.write((byte)'a');
		this.startTime = Kernel.time();
		this.pin = pin;
		Mpiosm.init(pin, true);
		period = 500;
		time = 50;
		Task.install(this);
		SCI2Plain.write((byte)'b');
	}
	
	static {
		SCI2Plain.start(9600, (byte)0, (short)8);
		SCI2Plain.write((byte)'0');
//		OutT.switchToSCI2();
//		OutT.print((byte)'0');
//		OutT.println((byte)'1');
		t1 = new TaskTest2(9);
//		TaskTest2 t2 = new TaskTest2(10);
	}
}
