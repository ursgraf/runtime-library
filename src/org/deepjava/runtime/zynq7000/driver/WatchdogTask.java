package org.deepjava.runtime.zynq7000.driver;

import org.deepjava.flink.core.FlinkDevice;
import org.deepjava.flink.subdevices.FlinkWatchdog;
import org.deepjava.runtime.arm32.Task;

/**
 * Task which periodically resets a flink watchdog subdevice, @see org.deepjava.flink.subdevices.FlinkWatchdog. 
 * Control Tasks which for instance run a motor controller may periodically set the variable <i>kick</i> to true.
 * If this does not happen within a predefined time or if this watchdog task itself does not run within the same 
 * time span, the watchdog will time out and disable a supervised drive.
 */
public class WatchdogTask extends Task {
	
	/** Periodically set to <i>true</i> */
	public boolean kick;
	
	private FlinkWatchdog dog;
	private int setValue;
	
	/**
	 * Creates a watchdog task with a given period. The watchdog period itself must be slightly 
	 * higher to prevent unwanted timeouts ion case of large timing jitter of the task 
	 * periodicity.
	 * @param period task period in ms
	 * @param periodWatchdog watchdog timeout in ms
	 */
	public WatchdogTask(int period, int periodWatchdog) {
		dog = FlinkDevice.getWatchdog();
		setValue = dog.getBaseClock() / 1000 * periodWatchdog; 
		dog.setCounterValue(setValue);
		dog.rearm();
		this.period = period;
		Task.install(this);
	}
	
	/* (non-Javadoc)
	 * @see org.deepjava.runtime.arm32.Task#action()
	 */
	public void action() {
		if (!dog.getStatus()) System.err.println("Watchdog has timed out");
		if (kick) {
			dog.setCounterValue(setValue);
			kick = false;
		}
	}

}
