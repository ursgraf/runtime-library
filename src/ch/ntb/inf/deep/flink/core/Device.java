package ch.ntb.inf.deep.flink.core;

public class Device implements Definitions{
	private BusInterface busInterface;
	private SubDevice list[];

	
	public Device(BusInterface busInterface){		
		this.busInterface = busInterface;
		findSubdevices();
	}
	
	public int getNumberOfSubDevices(){
		return list.length;
	}
	
	public SubDevice[] getDeviceList(){
		return list;
	}

	public SubDevice getSubdeviceByNr(int nr){
		if(nr< list.length){
			return list[nr];
		}else{
			return null;
		}
	}
	
	public SubDevice getSubdeviceByType(int type, int nr){
		int subDevNr = 0;
		for(int i = 0; i <list.length;i++){
			if(list[i].getInterfaceType() == type){
				if(subDevNr == nr){
					return list[i];
				}
				subDevNr++;
			}
		}
		return null;
	}
	
	public SubDevice getSubdeviceByUniceID(int id){
		for(int i = 0; i <list.length;i++){
			if(list[i].getUniqueID() == id){
				return list[i];
			}
		}
		return null;
	}
	
	
	public SubDevice getSubdeviceByType(int type){
		return getSubdeviceByType(type,0);
	}
	
	
	private void findSubdevices(){
		int memptr = 0;
		int numberOfSubdevices = 0;
		int deviceLength = 0;
		SubDevice firstDevice = new SubDevice();
		SubDevice actualDevice = firstDevice;
		if(busInterface.hasInfoDev()){
			deviceLength = busInterface.read(memptr + TOTAL_HEADER_SIZE); //device size register
		}else{
			System.out.println("no info device");
			deviceLength = busInterface.getMemoryLength();
		}
		
		
		
		
		while (memptr < deviceLength){
			System.out.print("memptr: ");
			System.out.printHexln(memptr);
			numberOfSubdevices++;
			actualDevice.setBaseAddress(memptr);
			actualDevice.setBusInterface(busInterface);
			
			//id register
			int reg = busInterface.read(memptr + TYPE_OFFSET);
			actualDevice.setInterfaceType(reg >> 16);
			actualDevice.setSupType((reg >> 8) & 0xFF);
			actualDevice.setVersion(reg & 0xFF);
			//memory size register
			actualDevice.setMemSize(busInterface.read(memptr + SIZE_OFFSET));
			//number of channels register
			actualDevice.setChannels(busInterface.read(memptr + CHANNEL_OFFSET));
			//unice id
			actualDevice.setUniqueID(busInterface.read(memptr + UNIQUE_ID_OFFSET));
			
			//address of next subdevice
			memptr = memptr + actualDevice.getMemSize();
			
			//create new device
			if(memptr < deviceLength){
				SubDevice nextDevice = new SubDevice();
				actualDevice.setNextSubdevice(nextDevice);
				actualDevice = nextDevice;
			}
		}
		//create array for easier access
		this.list = new SubDevice[numberOfSubdevices];
		actualDevice = firstDevice;
		for(int i = 0 ; i<numberOfSubdevices;i++){
			this.list[i] = actualDevice;
			actualDevice = actualDevice.getNext();
		}
	}
	
	public static String idToCharArray(int id){
		switch(id){
		case PWM_INTERFACE_ID:
			return "PWM";
		case GPIO_INTERFACE_ID:
			return "GPIO";
		case COUNTER_INTERFACE_ID:
			return "FQD";
		case WD_INTERFACE_ID:
			return "WD";
		case PPWA_INTERFACE_ID:
			return "PPWA";
		case ANALOG_INPUT_INTERFACE_ID:
			return "ANALOG INPUT";
		case ANALOG_OUTPUT_INTERFACE_ID:
			return "ANALOG OUTPUT";
		case INFO_DEVICE_ID:
			return "INFO DEVICE";
		default:
			return Integer.toString(id);
		}	
	}
}
