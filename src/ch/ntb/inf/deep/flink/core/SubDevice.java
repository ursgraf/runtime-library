package ch.ntb.inf.deep.flink.core;

public class SubDevice implements Definitions{
	public SubDevice next;
	public int interfaceType;
	public int subType; 
	public int version; 
	public int memSize;
	public int channels;
	public int baseAddress;
	public int uniqueID;
	public BusInterface busInterface; 
	
	public SubDevice() {
		System.out.println("new subdevice");
	}
	
	public void setNextSubdevice(SubDevice next){
		this.next = next;
	}
	
	public void setInterfaceType(int type){
		this.interfaceType = type;
	}
	
	public void setSupType(int type){
		this.subType = type;
	}
	
	public void setVersion(int version){
		this.version = version;
	}
	
	public void setMemSize(int size){
		this.memSize = size;
	}
	
	public void setUniqueID(int uniqueID){
		this.uniqueID = uniqueID;
	}
	
	public void setChannels(int channels){
		this.channels = channels;
	}

	public void setBaseAddress(int base) {
		System.out.println("set base address");
		this.baseAddress = base;
	}

	public void setBusInterface(BusInterface busInterface) {
		this.busInterface = busInterface;
	}

	public int getMemSize() {
		return this.memSize;
	}

	public SubDevice getNext() {
		return this.next;
	}
	
	public int getInterfaceType(){
		return this.interfaceType;
	}

	public int getSubtype() {
		return this.subType;
	}

	public int getVersion() {
		return this.version;
	}

	public int getNumberOfChannels() {
		return this.channels;
	}
	
	public int getUniqueID() {
		return this.uniqueID;
	}
	
	public int read(int address){
		return busInterface.read(this.baseAddress + TOTAL_HEADER_SIZE + address);
	}
	
	public void write(int address,int data ){
		busInterface.write(this.baseAddress + TOTAL_HEADER_SIZE + address,data);
	}
	
	public int getModConfReg(){
		return busInterface.read(this.baseAddress + MOD_CONF_OFFSET);
	}
	
	public int getModStatusReg(){
		return busInterface.read(this.baseAddress + MOD_STATUS_OFFSET);
	}

	public void setModConfReg(int confReg) {
		busInterface.write(this.baseAddress + MOD_CONF_OFFSET, confReg);
	}
}
