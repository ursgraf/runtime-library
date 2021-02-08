package org.deepjava.flink.subdevices;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;

/**
 * The flink PWM subdevice realizes a PWM (pulse with modulation)
 * function within a flink device.
 * It offers several channels. Each channel has its own period and duty cycle.
 * 
 * @author Urs Graf 
 */
public class FlinkPWM implements FlinkDefinitions {
	
	/** Handle to the subdevice within our flink device */
	public FlinkSubDevice dev;
	private static int BASE_CLOCK_ADDRESS = 0;
	private static int PERIOD_0_ADDRESS = BASE_CLOCK_ADDRESS + REGISTER_WIDTH;
	private int highTime0Address;
	
	/**
	 * Creates a PWM subdevice.
	 * @param dev handle to the subdevice
	 */
	public FlinkPWM(FlinkSubDevice dev) {
		this.dev = dev;
		this.highTime0Address = PERIOD_0_ADDRESS + dev.nofChannels * REGISTER_WIDTH;
	}
	
	/**
	 * Returns the base clock of the underlying hardware counter.
	 * @return	the base clock in Hz.
	 */
	public int getBaseClock() {
		return dev.read(BASE_CLOCK_ADDRESS);
	}
	
	/**
	 * Reads the period of a single channel. Channel number
	 * must be 0 &lt;= channel &lt; nof available channels. Period setting is
	 * in multiple of the base clock, @see getBaseClock().
	 * @param channel channel number
	 * @return multiple of base clock
	 */	
	public int getPeriod(int channel) {
		if (channel < dev.nofChannels) {
			return dev.read(PERIOD_0_ADDRESS + channel * REGISTER_WIDTH);
		} else {
			return 0;
		}
	}
	
	/**
	 * Reads the hightime of a single channel. Channel number
	 * must be 0 &lt;= channel &lt; nof available channels. Hightime setting is
	 * in multiple of the base clock, @see getBaseClock().
	 * @param channel channel number
	 * @return multiple of base clock
	 */	
	public int getHighTime(int channel) {
		if (channel < dev.nofChannels) {
			return dev.read(highTime0Address + channel * REGISTER_WIDTH);
		} else {
			return 0;
		}
	}
	
	/**
	 * Sets the period of a single channel. Channel number
	 * must be 0 &lt;= channel &lt; nof available channels. Period setting is
	 * in multiple of the base clock, @see getBaseClock().
	 * @param channel channel number
	 * @param period multiple of base clock
	 */
	public void setPeriod(int channel, int period) {
		if (channel < dev.nofChannels) {
			dev.write(PERIOD_0_ADDRESS + channel * REGISTER_WIDTH, period);
		}
	}
		
	/**
	 * Sets the hightime of a single channel. Channel number
	 * must be 0 &lt;= channel &lt; nof available channels. Hightime setting is
	 * in multiple of the base clock, @see getBaseClock().
	 * @param channel channel number
	 * @param highTime multiple of base clock
	 */
	public void setHighTime(int channel, int highTime) {
		if(channel < dev.nofChannels) {
			dev.write(highTime0Address + channel * REGISTER_WIDTH, highTime);
		}
	}
}
