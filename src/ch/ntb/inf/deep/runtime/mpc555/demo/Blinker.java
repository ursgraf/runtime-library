package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.*;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */
public class Blinker extends Task{
	static int count;	// class variable
	int pin;	// instance variable
	int times;	// instance variable

	public static int getNofBlinkers () { 	// class method
		return count;
	}

	public void changePeriod (int period) {	// instance method 
		this.period = period;
	}

	public void action () {	// instance method, overwritten
		Mpiosm.out(this.pin, !Mpiosm.in(this.pin));
		if (this.nofActivations == this.times) Task.remove(this);
	}
	
	public Blinker (int pin, int period, int times) {	// base constructor
		this.pin = pin;
		this.times = times;
		Mpiosm.init(pin, true);
		Mpiosm.out(pin, false);
		this.period = period;	
		Task.install(this);
		count++;
	}
	
	public Blinker (int pin, int period) {	// second constructor
		this(pin, period, 0);	// call to base constructor
	}

	static {	// class constructor
		count = 0;
	}
}