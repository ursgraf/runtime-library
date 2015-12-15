package ch.ntb.inf.deep.runtime.mpc555.driver;

public interface DigitalOutput extends DigitalInput {

	/**
	 * Set the TTL signal at the given pin.
	 * 
	 * @param val		Value to set. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public abstract void set(boolean val);

}