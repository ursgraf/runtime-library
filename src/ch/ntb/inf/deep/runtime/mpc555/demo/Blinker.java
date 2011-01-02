package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.*;
import ch.ntb.inf.deep.runtime.mpc555.driver.*;

public class Blinker extends Task{
	static int count;	// Klassenvariable
	int pin;	// Objektvariable
	int times;	// Objektvariable

	public static int getNofBlinkers () { 	// Klassenmethode
		return count;
	}

	public void changePeriod (int period) {	// Objektmethode 
		Task.remove(this);
		this.period = period;
		Task.install(this);
	}

	public void action () {	// Objektmethode, überschrieben
		SCI2Plain.write((byte)'4');
		Mpiosm.out(this.pin, !Mpiosm.in(this.pin));
		if (this.nofActivations == this.times) Task.remove(this);
	}
	
	public Blinker (int pin, int period, int times) {	// Grundkonstruktor
		this.pin = pin;
		this.times = times;
		Mpiosm.init(pin, true);
		Mpiosm.out(pin, false);
		this.period = period;	
		Task.install(this);
		count++;
	}
	
	public Blinker (int pin, int period) {	// 2. Konstruktor
		this(pin, period, 0);	// Aufruf des Grundkonstruktors
	}

	static {	// Klassenkonstruktor
		count = 0;
	}
}