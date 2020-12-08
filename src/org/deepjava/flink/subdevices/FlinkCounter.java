package org.deepjava.flink.subdevices;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;

/**
 * The flink counter subdevice realizes counter function within a flink device.
 * Notably, it can be used for quadrature decoding. It offers several channels.
 * 
 * @author Urs Graf 
 */
public class FlinkCounter implements FlinkDefinitions {

	/** Handle to the subdevice within our flink device */
	public FlinkSubDevice dev;
	private static final int COUNT_0_ADRESS = 0;
	
	/**
	 * Create a flink counter subdevice.
	 * @param dev subdevice in a flink device which implements the counter functionality
	 */
	public FlinkCounter(FlinkSubDevice dev) {
		this.dev = dev;
	}
	
	/** Returns the counter value.
	 * @param channel channel to read
	 * @return counter value
	 */
	public short getCount(int channel) {
		return (short) dev.read(COUNT_0_ADRESS + channel * REGISTER_WIDTH);
	}
	
	/**
	 * Resets the counter subdevice with all channels. After resetting the 
	 * counter it resumes its operation.
	 */
	public void reset() {
		int val = dev.getConfigReg();
		val = val | 0x1;
		dev.setConfigReg(val);
	}
}
