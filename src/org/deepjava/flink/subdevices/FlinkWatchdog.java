package org.deepjava.flink.subdevices;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;

public class FlinkWatchdog implements FlinkDefinitions{
	private static int BASE_CLOCK_ADDRESS = 0;
	private static int STATUS_CONF_ADDRESS = BASE_CLOCK_ADDRESS + REGISTER_WIDTH;
	private static int COUNTER_ADDRESS = STATUS_CONF_ADDRESS + REGISTER_WIDTH;
	private static int STATUS_BIT_MASK = 0x1;
	private static int REARM_BIT_MASK = 0x2;
	public FlinkSubDevice dev;
	
	public FlinkWatchdog(FlinkSubDevice dev){
		this.dev = dev;
	}
	
	public int getBaseClock(){
		return dev.read(BASE_CLOCK_ADDRESS);
	}
	
	public int getCounterValue(){
		return dev.read(COUNTER_ADDRESS);
	}
	public void setCounterValue(int value){
			dev.write(COUNTER_ADDRESS,value);
	}
	
	public void rearm(){
			int regValue = dev.read(STATUS_CONF_ADDRESS);
			regValue = regValue | REARM_BIT_MASK;
			dev.write(STATUS_CONF_ADDRESS,regValue);
	}
	
	public boolean getStatus(){
		int regValue = dev.read(STATUS_CONF_ADDRESS);
		if((regValue & STATUS_BIT_MASK) == STATUS_BIT_MASK){
			return true;
		}else{
			return false;
		}
	}
	
}
