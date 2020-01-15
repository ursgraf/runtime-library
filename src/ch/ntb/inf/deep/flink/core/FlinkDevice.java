package ch.ntb.inf.deep.flink.core;

import ch.ntb.inf.deep.flink.interfaces.zynq.AXIInterface;
import ch.ntb.inf.deep.flink.subdevices.FlinkCounter;
import ch.ntb.inf.deep.flink.subdevices.FlinkGPIO;
import ch.ntb.inf.deep.flink.subdevices.FlinkInfo;
import ch.ntb.inf.deep.flink.subdevices.FlinkPPWA;
import ch.ntb.inf.deep.flink.subdevices.FlinkPWM;

public class FlinkDevice implements FlinkDefinitions {
	
	private FlinkBusInterface busInterface;
	private FlinkSubDevice list[];
	private static FlinkDevice instance;

	private FlinkDevice(FlinkBusInterface busInterface) {		
		this.busInterface = busInterface;
		findSubdevices();
	}
	
	/** 
	 * Returns an flink device. The processor reads over a AXI bus interface the content of 
	 * a flink device implemented in the FPGA hardware. Depending on the configuration of the FPGA
	 * various flink subdevices are available.
	 * 
	 * @return flink device
	 */
	public static FlinkDevice getInstance() {
		if (instance == null) instance = new FlinkDevice(new AXIInterface());
		return instance;
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
			
			// id register
			int reg = busInterface.read(memptr + TYPE_OFFSET);
			actualDevice.function = reg >> 16;
			actualDevice.subFunction = (reg >> 8) & 0xFF;
			actualDevice.version = reg & 0xFF;
			actualDevice.memSize = busInterface.read(memptr + SIZE_OFFSET);
			actualDevice.nofChannels = busInterface.read(memptr + CHANNEL_OFFSET);
			actualDevice.uniqueID = busInterface.read(memptr + UNIQUE_ID_OFFSET);
			actualDevice.id = nofSubdevices;
			memptr = memptr + actualDevice.memSize;
			
			// create new device
			if (memptr < deviceLength) {
				FlinkSubDevice nextDevice = new FlinkSubDevice();
				actualDevice.next = nextDevice;
				actualDevice = nextDevice;
			}
			nofSubdevices++;
		}
		// create array for easier access
		this.list = new FlinkSubDevice[nofSubdevices];
		actualDevice = firstDevice;
		for (int i = 0 ; i < nofSubdevices;i++) {
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
	
	public void lsflink() {
		FlinkSubDevice[] list = getDeviceList();
		System.out.println("Subdevices of flink device 0:");
		for(FlinkSubDevice s : list) {
			System.out.print("\t");
			System.out.print(s.id);
			System.out.println(":");
			System.out.print("\t\tAddress range: ");
			System.out.printHex(s.baseAddress);
			System.out.print(" - ");
			System.out.printHexln(s.baseAddress + s.memSize);
			System.out.print("\t\tMemory Size: ");
			System.out.printHexln(s.memSize);
			System.out.print("\t\tFunction: ");
			System.out.println(FlinkDevice.idToCharArray(s.function));
			System.out.print("\t\tSubfunction: ");
			System.out.println(s.subFunction);
			System.out.print("\t\tFunction version: ");
			System.out.println(s.version);
			System.out.print("\t\tNof channels: ");
			System.out.println(s.nofChannels);
			System.out.print("\t\tUnique id: ");
			System.out.println(s.uniqueID);
		}
	}

	public static FlinkPWM getPWM() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(PWM_INTERFACE_ID);
		if (d != null) return new FlinkPWM(d);
		return null;
	}

	public static FlinkCounter getCounter() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(COUNTER_INTERFACE_ID);
		if (d != null) return new FlinkCounter(d);
		return null;
	}

	public static FlinkInfo getInfo() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(INFO_DEVICE_ID);
		if (d != null) return new FlinkInfo(d);
		return null;
	}

	public static FlinkGPIO getGPIO() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(GPIO_INTERFACE_ID);
		if (d != null) return new FlinkGPIO(d);
		return null;
	}

	public static FlinkPPWA getPPWA() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(PPWA_INTERFACE_ID);
		if (d != null) return new FlinkPPWA(d);
		return null;
	}
}
