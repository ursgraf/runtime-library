package ch.ntb.inf.deep.flink.subdevices;

import ch.ntb.inf.deep.flink.core.Definitions;
import ch.ntb.inf.deep.flink.core.SubDevice;

public class FlinkCounter implements Definitions{

	public SubDevice dev;
	private static final int COUNT_0_ADRESS = 0;
	
	public FlinkCounter(SubDevice dev){
		this.dev = dev;
		
	}
	
	public int getCount(int channel){
		return dev.read(COUNT_0_ADRESS + channel*REGISTER_WIDTH);
	}
	
	public void reset(){
		int confReg = dev.getModConfReg();
		confReg = confReg | 0x1;
		dev.setModConfReg(confReg);
	}
}
