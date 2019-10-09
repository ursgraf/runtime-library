package ch.ntb.inf.deep.flink.subdevices;

import ch.ntb.inf.deep.flink.core.FlinkDefinitions;
import ch.ntb.inf.deep.flink.core.FlinkSubDevice;

public class FlinkCounter implements FlinkDefinitions {

	public FlinkSubDevice dev;
	private static final int COUNT_0_ADRESS = 0;
	
	public FlinkCounter(FlinkSubDevice dev){
		this.dev = dev;
		
	}
	
	public int getCount(int channel) {
		return dev.read(COUNT_0_ADRESS + channel * REGISTER_WIDTH);
	}
	
	public void reset(){
//		int confReg = dev.getModConfReg();
//		confReg = confReg | 0x1;
//		dev.setModConfReg(confReg);
	}
}
