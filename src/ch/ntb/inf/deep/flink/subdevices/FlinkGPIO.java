package ch.ntb.inf.deep.flink.subdevices;

import ch.ntb.inf.deep.flink.core.Definitions;
import ch.ntb.inf.deep.flink.core.SubDevice;

public class FlinkGPIO implements Definitions{
	private static int DIR_ADDRESS = 0;
	private int valAddress;
	public SubDevice dev;
	
	public FlinkGPIO(SubDevice dev){
		this.dev = dev;
		if(dev.getNumberOfChannels() == 1){
			this.valAddress = DIR_ADDRESS +REGISTER_WIDTH;
		}else{
			this.valAddress = DIR_ADDRESS + ((dev.getNumberOfChannels()-1)/REGISTER_WIDTH_BIT+1)*REGISTER_WIDTH;
		}
	}
	
	public void setDir(int channel, boolean input){
		int dirReg = dev.read(DIR_ADDRESS + (channel/REGISTER_WIDTH_BIT)*REGISTER_WIDTH);
		if(input){
			dirReg = dirReg & ~(1<<(channel%REGISTER_WIDTH_BIT));
		}else{
			dirReg = dirReg | (1<<(channel%REGISTER_WIDTH_BIT));
		}
		dev.write(DIR_ADDRESS + (channel/REGISTER_WIDTH_BIT)*REGISTER_WIDTH, dirReg);
	}
	public boolean getDir(int channel){
		int dirReg = dev.read(DIR_ADDRESS + (channel/REGISTER_WIDTH_BIT)*REGISTER_WIDTH);
		if((dirReg & (1<<(channel%REGISTER_WIDTH_BIT)))>0){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean getValue(int channel){
		int valueReg = dev.read(valAddress + (channel/REGISTER_WIDTH_BIT)*REGISTER_WIDTH);
		if((valueReg & (1<<(channel%REGISTER_WIDTH_BIT)))!=0){
			return true;
		}else{
			return false;
		}
	}
	
	public void setValue(int channel, boolean value){
		int valueReg = dev.read(valAddress + (channel/REGISTER_WIDTH_BIT)*REGISTER_WIDTH);
		if(value){
			valueReg = valueReg | (1<<(channel%REGISTER_WIDTH_BIT));
		}else{
			valueReg = valueReg & ~(1<<(channel%REGISTER_WIDTH_BIT));
		}
		dev.write(valAddress+ (channel/REGISTER_WIDTH_BIT)*REGISTER_WIDTH, valueReg);
	}
	

}
