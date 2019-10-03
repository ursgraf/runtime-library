package ch.ntb.inf.deep.flink.subdevices;

import ch.ntb.inf.deep.flink.core.Definitions;
import ch.ntb.inf.deep.flink.core.SubDevice;

public class FlinkDAC implements Definitions {
	private static int RESOLUTION_ADDRESS = 0;
	private static int VALUE_0_ADDRESS = RESOLUTION_ADDRESS + REGISTER_WIDTH;
	public SubDevice dev;
	private int resolution;
	private int bit_mask;
	
	public FlinkDAC(SubDevice dev){
		this.dev = dev;
		//cache resolution
		this.resolution = dev.read(RESOLUTION_ADDRESS);
		for(int i = 0;i<resolution;i++){
			bit_mask = bit_mask | (0x1<<i);
		}
		
	}
	
	public int getResolution(){
		return resolution;
	}
	
	public int getValue(int channel){
		if(channel<dev.getNumberOfChannels()){
			return dev.read(VALUE_0_ADDRESS+channel*REGISTER_WIDTH);
		}else{
			return 0;
		}
	}
	
	public void setValue(int channel,int value){
		if(channel<dev.getNumberOfChannels()){
			dev.write( VALUE_0_ADDRESS + channel*REGISTER_WIDTH , value & bit_mask);
		}
	}
	
	
}
