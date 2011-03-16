package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Interrupt;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/** tests some external interrupts 
	changes:
	9.3.2011 NTB, Urs Graf, creation
*/

public class ExtInterruptDemo extends Interrupt {
	int pin;
	static int count;

	public void action() {
		MPIOSM_DIO.set(this.pin, true);
		count++;
		System.out.print(this.pin);
		for (int i = 0; i < 50000; i++);
		MPIOSM_DIO.set(this.pin, false);
	}

	public ExtInterruptDemo (int pin) {
		this.pin = pin;
		MPIOSM_DIO.init(this.pin, true);
		MPIOSM_DIO.set(this.pin, false);
	}

	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.out.println("start");
		
		Interrupt int5 = new ExtInterruptDemo(5); 
		Interrupt int6 = new ExtInterruptDemo(6); 
		Interrupt.install(int5, 5, false);
		Interrupt.install(int6, 6, false);
	}
}
	

