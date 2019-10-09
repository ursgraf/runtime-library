package ch.ntb.inf.deep.flink.subdevices;

import ch.ntb.inf.deep.flink.core.FlinkDefinitions;
import ch.ntb.inf.deep.flink.core.FlinkSubDevice;

public class FlinkPPWA implements FlinkDefinitions {
	
	public FlinkSubDevice dev;
	private static int BASE_CLOCK_ADDRESS = 0;
	private static int PERIOD_0_ADDRESS = BASE_CLOCK_ADDRESS + REGISTER_WIDTH;
	private int highTime0Address;
	
	public FlinkPPWA(FlinkSubDevice dev) {
		this.dev = dev;
		this.highTime0Address = PERIOD_0_ADDRESS + dev.nofChannels * REGISTER_WIDTH;
	}
	
	public int getBaseClock() {
		return dev.read(BASE_CLOCK_ADDRESS);
	}
	
	public int getPeriod(int channel) {
		if(channel < dev.nofChannels) {
			return (dev.read(PERIOD_0_ADDRESS + channel * REGISTER_WIDTH) - 1);
		} else {
			return 0;
		}
	}
	
	public int getHighTime(int channel) {
		if(channel < dev.nofChannels) {
			return dev.read(highTime0Address + channel * REGISTER_WIDTH);
		} else {
			return 0;
		}
	}
}
