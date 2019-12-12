package ch.ntb.inf.deep.runtime.zynq7000.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.flink.core.*;
import ch.ntb.inf.deep.flink.interfaces.zynq.AXIInterface;
import ch.ntb.inf.deep.flink.subdevices.*;
import ch.ntb.inf.deep.runtime.arm32.Task;
import ch.ntb.inf.deep.runtime.zynq7000.driver.UART;

public class FlinkDemo extends Task implements FlinkDefinitions {
	
	public static int outputPeriod = 0;
	public static int outputHighTime = 0;
	
	static FlinkDevice fDev;
	static FlinkInfo info;
	static FlinkGPIO gpio;
	static FlinkPWM pwm;
	static FlinkPPWA ppwa;
	static FlinkWatchdog wd;
	
	public void action() {
		for (int i = 0; i <= 3; i++) {
			gpio.setValue(i, !gpio.getValue(i));
		}
	}

	private static void lsflink(FlinkSubDevice[] list) {
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

	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		System.out.println("\n\rflink demo");
		
		fDev = new FlinkDevice(new AXIInterface());
		FlinkSubDevice[] list = fDev.getDeviceList();
		
		lsflink(list);
	
		FlinkSubDevice d = fDev.getSubdeviceByType(INFO_DEVICE_ID);
		if (d != null) info = new FlinkInfo(d);
		System.out.print("info description: ");
		System.out.println(info.getDescription());
		d = fDev.getSubdeviceByType(GPIO_INTERFACE_ID);
		if (d != null) gpio = new FlinkGPIO(d);
		d = fDev.getSubdeviceByType(PWM_INTERFACE_ID);
		if (d != null) pwm = new FlinkPWM(d);
		d = fDev.getSubdeviceByType(PPWA_INTERFACE_ID);
		if (d != null) ppwa = new FlinkPPWA(d);
		
		for(int i = 0; i <= 3; i++) gpio.setDir(i, true);
		for(int i = 4; i <= 7; i++) gpio.setDir(i, false);
		for(int i = 0; i <= 3; i++) gpio.setValue(i, i % 2 == 0);		
		Task t = new FlinkDemo();
		t.period = 500;
		Task.install(t);
	}

}
