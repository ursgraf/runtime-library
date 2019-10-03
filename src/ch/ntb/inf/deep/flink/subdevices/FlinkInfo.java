package ch.ntb.inf.deep.flink.subdevices;

import ch.ntb.inf.deep.flink.core.Definitions;
import ch.ntb.inf.deep.flink.core.SubDevice;

public class FlinkInfo implements Definitions{
	private static int DEV_SIZE_ADDRESS = 0;
	private static int NAME_0_ADDRESS = DEV_SIZE_ADDRESS + REGISTER_WIDTH;
	private static int NUBER_OF_NAME_REG = 7;
	public SubDevice dev;
	
	public FlinkInfo(SubDevice list){
		this.dev = list;
	}
	
	public int getDeviceSize(){
		return dev.read(DEV_SIZE_ADDRESS);
	}
	
	public byte[] getDescription(){
		byte result[] = new byte[NUBER_OF_NAME_REG*4];
		for(int i = 0; i<NUBER_OF_NAME_REG;i++){
			int reg = dev.read(NAME_0_ADDRESS+i*REGISTER_WIDTH);
			System.out.printHexln(reg);
			result[result.length-(i*4)-1] = (byte) (reg);
			result[result.length-(i*4)-2] = (byte) (reg>>8);
			result[result.length-(i*4)-3] = (byte) (reg>>16);
			result[result.length-(i*4)-4] = (byte) (reg>>24);
		}
		return result;
	}
}