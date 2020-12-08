package org.deepjava.flink.subdevices;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;

/**
 * The flink watchdog subdevice realizes a watchdog function within a flink device.
 * It offers several channels. Each channel drives a single pin.
 * 
 * @author Urs Graf 
 */
public class FlinkWatchdog implements FlinkDefinitions{
	private static int BASE_CLOCK_ADDRESS = 0;
	private static int STATUS_CONF_ADDRESS = BASE_CLOCK_ADDRESS + REGISTER_WIDTH;
	private static int COUNTER_ADDRESS = STATUS_CONF_ADDRESS + REGISTER_WIDTH;
	private static int STATUS_BIT_MASK = 0x1;
	private static int REARM_BIT_MASK = 0x2;
	
	/** Handle to the subdevice within our flink device */
	public FlinkSubDevice dev;
	
	/**
	 * Creates a watchdog subdevice.
	 * @param dev handle to the subdevice
	 */
	public FlinkWatchdog(FlinkSubDevice dev){
		this.dev = dev;
	}
	
	/**
	 * Returns the base clock of the underlying hardware counter.
	 * @returnthe base clock in Hz
	 */
	public int getBaseClock(){
		return dev.read(BASE_CLOCK_ADDRESS);
	}
	
	/**
	 * Returns the actual counter value.
	 * @return	counter value
	 */
	public int getCounterValue(){
		return dev.read(COUNTER_ADDRESS);
	}
	
	/**
	 * Sets the actual counter value.
	 * @param value counter value
	 */
	public void setCounterValue(int value){
			dev.write(COUNTER_ADDRESS,value);
	}
	
	/**
	 * Arms the watchdog. If it has timed out, you have to arm again 
	 * before it can time out again.
	 */
	public void rearm() {
			int regValue = dev.read(STATUS_CONF_ADDRESS);
			regValue = regValue | REARM_BIT_MASK;
			dev.write(STATUS_CONF_ADDRESS,regValue);
	}
	
	/**
	 * Reads the status register and returns the state of the status bit within.
	 * @return true, if status bit set
	 */
	public boolean getStatus(){
		int regValue = dev.read(STATUS_CONF_ADDRESS);
		if ((regValue & STATUS_BIT_MASK) == STATUS_BIT_MASK){
			return true;
		} else {
			return false;
		}
	}
	
}
