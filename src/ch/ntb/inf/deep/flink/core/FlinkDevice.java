package ch.ntb.inf.deep.flink.core;

public class FlinkDevice implements FlinkDefinitions {
	
	private FlinkBusInterface busInterface;
	private FlinkSubDevice list[];

	public FlinkDevice(FlinkBusInterface busInterface) {		
		this.busInterface = busInterface;
		findSubdevices();
	}
	
	public int getNumberOfSubDevices(){
		return list.length;
	}
	
	public FlinkSubDevice[] getDeviceList(){
		return list;
	}

	public FlinkSubDevice getSubdeviceByNr(int nr) {
		if (nr < list.length) return list[nr]; else return null;
	}
	
	public FlinkSubDevice getSubdeviceByType(int type, int nr) {
		int subDevNr = 0;
		for (int i = 0; i < list.length; i++) {
			if (list[i].function == type) {
				if (subDevNr == nr) return list[i];
				subDevNr++;
			}
		}
		return null;
	}
	
	public FlinkSubDevice getSubdeviceByUniqueID(int id) {
		for (int i = 0; i < list.length; i++) {
			if (list[i].uniqueID == id){
				return list[i];
			}
		}
		return null;
	}
	
	public FlinkSubDevice getSubdeviceByType(int type) {
		return getSubdeviceByType(type, 0);
	}
	
	private void findSubdevices(){
		int memptr = 0;
		int nofSubdevices = 0;
		int deviceLength = 0;
		FlinkSubDevice firstDevice = new FlinkSubDevice();
		FlinkSubDevice actualDevice = firstDevice;
		deviceLength = busInterface.getMemoryLength();
		if (!busInterface.hasInfoDev()) System.out.println("no info device");
			
		while (memptr < deviceLength) {
			actualDevice.baseAddress = memptr;
			actualDevice.busInterface = busInterface;
			
			//id register
			int reg = busInterface.read(memptr + TYPE_OFFSET);
			actualDevice.function = reg >> 16;
			actualDevice.subFunction = (reg >> 8) & 0xFF;
			actualDevice.version = reg & 0xFF;
			actualDevice.memSize = busInterface.read(memptr + SIZE_OFFSET);
			actualDevice.nofChannels = busInterface.read(memptr + CHANNEL_OFFSET);
			actualDevice.uniqueID = busInterface.read(memptr + UNIQUE_ID_OFFSET);
			actualDevice.id = nofSubdevices;
			memptr = memptr + actualDevice.memSize;
			
			//create new device
			if(memptr < deviceLength) {
				FlinkSubDevice nextDevice = new FlinkSubDevice();
				actualDevice.next = nextDevice;
				actualDevice = nextDevice;
			}
			nofSubdevices++;
		}
		//create array for easier access
		this.list = new FlinkSubDevice[nofSubdevices];
		actualDevice = firstDevice;
		for(int i = 0 ; i < nofSubdevices;i++){
			this.list[i] = actualDevice;
			actualDevice = actualDevice.next;
		}
	}
	
	public static String idToCharArray(int id) {
		switch(id) {
		case PWM_INTERFACE_ID:
			return "PWM";
		case GPIO_INTERFACE_ID:
			return "GPIO";
		case COUNTER_INTERFACE_ID:
			return "FQD";
		case WD_INTERFACE_ID:
			return "WATCHDOG";
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
