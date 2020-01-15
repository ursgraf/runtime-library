package ch.ntb.inf.deep.flink.subdevices;

import ch.ntb.inf.deep.flink.core.FlinkDefinitions;
import ch.ntb.inf.deep.flink.core.FlinkSubDevice;

public class FlinkPWM implements FlinkDefinitions {
	
	public FlinkSubDevice dev;
	private static int BASE_CLOCK_ADDRESS = 0;
	private static int PERIOD_0_ADDRESS = BASE_CLOCK_ADDRESS + REGISTER_WIDTH;
	private int highTime0Address;
	
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
	
	public int getPeriod(int channel) {
		if (channel < dev.nofChannels) {
			return dev.read(PERIOD_0_ADDRESS + channel * REGISTER_WIDTH);
		} else {
			return 0;
		}
	}
	
	public int getHighTime(int channel) {
		if(channel < dev.nofChannels) {
			return dev.read(highTime0Address + channel * REGISTER_WIDTH);
		}else{
			return 0;
		}
	}
	
	public void setPeriod(int channel,int period) {
		if(channel < dev.nofChannels) {
			dev.write(PERIOD_0_ADDRESS + channel * REGISTER_WIDTH, period);
		}
	}
		
	public void setHighTime(int channel, int period) {
		if(channel < dev.nofChannels) {
			dev.write(highTime0Address + channel * REGISTER_WIDTH, period);
		}
	}
}
