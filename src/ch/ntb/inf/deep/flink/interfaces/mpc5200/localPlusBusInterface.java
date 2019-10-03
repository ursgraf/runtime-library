package ch.ntb.inf.deep.flink.interfaces.mpc5200;

import ch.ntb.inf.deep.runtime.mpc5200.IphyCoreMpc5200tiny;
import ch.ntb.inf.deep.unsafe.US;
import ch.ntb.inf.deep.flink.core.BusInterface;

public class localPlusBusInterface implements IphyCoreMpc5200tiny, BusInterface{
	private static final int CS3START = 0xf000001C;
	private static final int CS3STOP =  0xf0000020;
	private static final int CS3STARTADDRESS = 0xe0000000;
	private static final int CS3STOPADDRESSS =  0xe1FF0000;
	
	private int memLength;
	private boolean infoDevice;

	public localPlusBusInterface(){
		US.PUT4(CS3START, CS3STARTADDRESS>>16); //set CS3 start address
		US.PUT4(CS3STOP, CS3STOPADDRESSS>>16); 	//set CS3 end address
		US.PUT4(CS3CR, 0x0005FF00);	//configure CS3
		int ipbiReg = US.GET4(IPBICR);
		ipbiReg = ipbiReg | 0x00080000; //enable CS3
		US.PUT4(IPBICR, ipbiReg);
		infoDevice = true;
		memLength = 0;
	}
	
	
	public localPlusBusInterface(int memoryLength){
		this();
		this.memLength = memoryLength;
		infoDevice = false;
	}

	public int getMemoryLength() {
		return this.memLength;
	}

	public int read(int address) {
		return  US.GET4(CS3STARTADDRESS + address);
	}

	
	public void write(int address, int data) {	
		US.PUT4(CS3STARTADDRESS+address, data);
	}


	public boolean hasInfoDev() {
		return infoDevice;
	}

}
