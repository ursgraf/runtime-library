package org.deepjava.flink.core;

import org.deepjava.flink.interfaces.zynq.AXIInterface;
import org.deepjava.flink.subdevices.FlinkADC;
import org.deepjava.flink.subdevices.FlinkCounter;
import org.deepjava.flink.subdevices.FlinkDAC;
import org.deepjava.flink.subdevices.FlinkGPIO;
import org.deepjava.flink.subdevices.FlinkInfo;
import org.deepjava.flink.subdevices.FlinkPPWA;
import org.deepjava.flink.subdevices.FlinkPWM;
import org.deepjava.flink.subdevices.FlinkUART;
import org.deepjava.flink.subdevices.FlinkWatchdog;

/**
 * A flink device is a hardware configuration in a FPGA device, 
 * @see <a href="http://www.flink-project.ch">www.flink-project.ch</a>. 
 * It offers a multitude of specific subdevice with unique functionalities.
 * 
 * @author Urs Graf
 */
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
	
	/**
	 * A flink device incorporates one or several subdevices. Use this 
	 * method to query the number of subdevices in a flink device.
	 * @return number of subdevices
	 */
	public int getNumberOfSubDevices(){
		return list.length;
	}
	
	/**
	 * A flink device incorporates one or several subdevices. Use this
	 * method to get a list of all subdevices in a flink device.
	 * @return list of subdevices
	 */
	public FlinkSubDevice[] getDeviceList(){
		return list;
	}

	/**
	 * A flink device incorporates one or several subdevices. This
	 * method returns the subdevice with a given number. The sundevices are numbered
	 * starting from 0. 
	 * @param nr number of subdevices
	 * @return subdevice, null if number not present
	 */
	public FlinkSubDevice getSubdeviceByNr(int nr) {
		if (nr < list.length) return list[nr]; else return null;
	}
	
	/**
	 * A flink device incorporates one or several subdevices. This
	 * method returns the subdevice with a given type and subtype.
	 * @param type type of the subdevice
	 * @param subType subtype of the subdevice
	 * @return subdevice, null if type and subtype not present
	 */
	public FlinkSubDevice getSubdeviceByType(int type, int subType) {
		for (int i = 0; i < list.length; i++) {
			if (list[i].function == type && list[i].subType == subType) return list[i];
		}
		return null;
	}
	
	/**
	 * A flink device incorporates one or several subdevices. This
	 * method returns the subdevice with a given type.
	 * @param type type of the subdevice
	 * @return subdevice, null if type not present
	 */
	public FlinkSubDevice getSubdeviceByType(int type) {
		return getSubdeviceByType(type, 0);
	}
	
	/**
	 * A flink device incorporates one or several subdevices. This
	 * method returns the subdevice with a given unique id.
	 * @param id unique id of the subdevice
	 * @return subdevice, null if unique id not present
	 */
	public FlinkSubDevice getSubdeviceByUniqueID(int id) {
		for (int i = 0; i < list.length; i++) {
			if (list[i].uniqueID == id)	return list[i];
		}
		return null;
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
			actualDevice.subType = (reg >> 8) & 0xFF;
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
	
	/**
	 * Use this method to return a string which describes the function 
	 * of a subdevice.
	 * @param id type of a subdevice
	 * @return string
	 */
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
		case UART_INTERFACE_ID:
			return "UART";
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
	
	/**
	 * Prints the content of a flink device with all its subdevices 
	 * on System.out. 
	 */
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
			System.out.printHexln(s.baseAddress + s.memSize - 1);
			System.out.print("\t\tMemory Size: ");
			System.out.printHexln(s.memSize);
			System.out.print("\t\tFunction: ");
			System.out.println(FlinkDevice.idToCharArray(s.function));
			System.out.print("\t\tSubtype: ");
			System.out.println(s.subType);
			System.out.print("\t\tFunction version: ");
			System.out.println(s.version);
			System.out.print("\t\tNof channels: ");
			System.out.println(s.nofChannels);
			System.out.print("\t\tUnique id: ");
			System.out.printHexln(s.uniqueID);
		}
	}

	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkPWM} subdevice if present in this flink device.
	 * @return pwm subdevice, null if not available
	 */
	public static FlinkPWM getPWM() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(PWM_INTERFACE_ID);
		if (d != null) return new FlinkPWM(d);
		return null;
	}

	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkCounter} subdevice if present in this flink device.
	 * @return counter subdevice, null if not available
	 */
	public static FlinkCounter getCounter() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(COUNTER_INTERFACE_ID);
		if (d != null) return new FlinkCounter(d);
		return null;
	}

	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkInfo} subdevice if present in this flink device.
	 * @return info subdevice, null if not available
	 */
	public static FlinkInfo getInfo() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(INFO_DEVICE_ID);
		if (d != null) return new FlinkInfo(d);
		return null;
	}

	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkGPIO} subdevice if present in this flink device.
	 * @return gpio subdevice, null if not available
	 */
	public static FlinkGPIO getGPIO() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(GPIO_INTERFACE_ID);
		if (d != null) return new FlinkGPIO(d);
		return null;
	}

	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkPPWA} subdevice if present in this flink device.
	 * @return ppwa subdevice, null if not available
	 */
	public static FlinkPPWA getPPWA() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(PPWA_INTERFACE_ID);
		if (d != null) return new FlinkPPWA(d);
		return null;
	}

	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkADC} subdevice with subtype 1 
	 * if present in this flink device. Subtype 1 is used for a ADC128S102 device. 
	 * @return adc subdevice, null if not available
	 */
	public static FlinkADC getADC128S102() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(ANALOG_INPUT_INTERFACE_ID, 1);
		if (d != null) return new FlinkADC(d);
		return null;
	}
	
	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkADC} subdevice with subtype 2 
	 * if present in this flink device. Subtype 2 is used for a AD7606 device. 
	 * @return adc subdevice, null if not available
	 */
	public static FlinkADC getAD7606() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(ANALOG_INPUT_INTERFACE_ID, 2);
		if (d != null) return new FlinkADC(d);
		return null;
	}
	
	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkADC} subdevice with subtype 3 
	 * if present in this flink device. Subtype 3 is used for a AD7476 device. 
	 * @return adc subdevice, null if not available
	 */
	public static FlinkADC getAD7476() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(ANALOG_INPUT_INTERFACE_ID, 3);
		if (d != null) return new FlinkADC(d);
		return null;
	}

	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkDAC} subdevice with subtype 0 
	 * if present in this flink device. Subtype 3 is used for a AD7476 device. 
	 * @return adc subdevice, null if not available
	 */
	public static FlinkDAC getAD5668() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(ANALOG_OUTPUT_INTERFACE_ID, 1);
		if (d != null) return new FlinkDAC(d);
		return null;
	}

	/**
	 * Returns a {@link org.deepjava.flink.subdevices.FlinkWatchdog} subdevice if present in this flink device.
	 * @return watchdog subdevice, null if not available
	 */
	public static FlinkWatchdog getWatchdog() {
		FlinkSubDevice d = getInstance().getSubdeviceByType(WD_INTERFACE_ID);
		if (d != null) return new FlinkWatchdog(d);
		return null;
	}

	/**
	 * Returns an instance of {@link org.deepjava.flink.subdevices.FlinkUART} subdevice if present in this flink device.
	 * @param uartNr uart number such as 0, 1, ..
	 * @return uart subdevice, null if not available
	 */
	public static FlinkUART getUART(int uartNr) {
		FlinkSubDevice d = getInstance().getSubdeviceByType(UART_INTERFACE_ID);
		if (d != null) return FlinkUART.getInstance(d, uartNr);
		return null;
	}

}
