package org.deepjava.flink.subdevices;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;

public class FlinkADC implements FlinkDefinitions {
	
	public FlinkSubDevice dev;
	private static int RESOLUTION_ADDRESS = 0;
	private static int VALUE_0_ADDRESS = RESOLUTION_ADDRESS + REGISTER_WIDTH;
	private int resolution;
	private int bit_mask;
	
	public FlinkADC(FlinkSubDevice dev){
		this.dev = dev;
		this.resolution = dev.read(RESOLUTION_ADDRESS);
		for(int i = 0; i < resolution;i++) {
			bit_mask = bit_mask | (0x1<<i);
		}
	}
	
	public int getResolution(){
		return resolution;
	}
	
	public int getValue(int channel) {
		if (channel < dev.nofChannels) {
			return (dev.read(VALUE_0_ADDRESS + channel * REGISTER_WIDTH) & bit_mask);
		} else {
			return 0;
		}
	}
	
}
