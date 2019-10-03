package ch.ntb.inf.deep.flink.subdevices;

import ch.ntb.inf.deep.flink.core.Definitions;
import ch.ntb.inf.deep.flink.core.SubDevice;

public class FlinkADC implements Definitions {
	private static int RESOLUTION_ADDRESS = 0;
	private static int VALUE_0_ADDRESS = RESOLUTION_ADDRESS + REGISTER_WIDTH;
	public SubDevice dev;
	private int resolution;
	private int bit_mask;
	
	public FlinkADC(SubDevice dev){
		this.dev = dev;
		//cache resolution
		this.resolution = dev.read(RESOLUTION_ADDRESS);
		//create bitmask
		for(int i = 0;i<resolution;i++){
			bit_mask = bit_mask | (0x1<<i);
		}
	}
	
	public int getResolution(){
		return resolution;
	}
	
	public int getValue(int channel){
		if(channel<dev.getNumberOfChannels()){
			return (dev.read(VALUE_0_ADDRESS+channel*REGISTER_WIDTH)&bit_mask);
		}else{
			return 0;
		}
	}
	
}
