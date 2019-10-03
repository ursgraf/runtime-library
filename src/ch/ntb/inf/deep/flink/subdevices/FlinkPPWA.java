package ch.ntb.inf.deep.flink.subdevices;

import ch.ntb.inf.deep.flink.core.Definitions;
import ch.ntb.inf.deep.flink.core.SubDevice;

public class FlinkPPWA implements Definitions{
	private static int BASE_CLOCK_ADDRESS = 0;
	private static int PERIOD_0_ADDRESS = BASE_CLOCK_ADDRESS + REGISTER_WIDTH;
	public SubDevice dev;
	private int highTime0Address;
	public int TIMEBASE;
	
	public FlinkPPWA(SubDevice dev){
		this.dev = dev;
		this.highTime0Address = PERIOD_0_ADDRESS + dev.getNumberOfChannels()*REGISTER_WIDTH;
		this.TIMEBASE = 1000000000 / dev.read(BASE_CLOCK_ADDRESS);
	}
	
	public int getBaseClock(){
		return dev.read(BASE_CLOCK_ADDRESS);
	}
	
	public int getPeriod(int channel){
		if(channel<dev.getNumberOfChannels()){
			return ((dev.read(PERIOD_0_ADDRESS+channel*REGISTER_WIDTH)-1)*TIMEBASE);
		}else{
			return 0;
		}
	}
	
	public int getHighTime(int channel){
		if(channel<dev.getNumberOfChannels()){
			return (dev.read(highTime0Address + channel*REGISTER_WIDTH)*TIMEBASE);
		}else{
			return 0;
		}
	}
}
