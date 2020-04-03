package ch.ntb.inf.deep.flink.core;

public class FlinkSubDevice implements FlinkDefinitions {
	public FlinkSubDevice next;
	public int function;
	public int subType; 
	public int version; 
	public int memSize;
	public int nofChannels;
	public int baseAddress;
	public int uniqueID;
	public int id;
	public FlinkBusInterface busInterface; 
	
	public int read(int address){
		return busInterface.read(this.baseAddress + TOTAL_HEADER_SIZE + address);
	}
	
	public void write(int address,int data ){
		busInterface.write(this.baseAddress + TOTAL_HEADER_SIZE + address, data);
	}
	
	public int getConfigReg() {
		return busInterface.read(this.baseAddress + MOD_CONF_OFFSET);
	}
	
	public int getStatusReg() {
		return busInterface.read(this.baseAddress + MOD_STATUS_OFFSET);
	}

	public void setConfigReg(int confReg) {
		busInterface.write(this.baseAddress + MOD_CONF_OFFSET, confReg);
	}
}
