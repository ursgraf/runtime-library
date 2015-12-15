package ch.ntb.inf.deep.runtime.mpc555.driver;

public interface DigitalInput {

	/**
	 * Returns the current state of the TTL signal on the given pin.
	 * 
	 * @return the current state of the TTL at the given pin. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public abstract boolean get();

}