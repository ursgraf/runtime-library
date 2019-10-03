package ch.ntb.inf.deep.runtime.zynq7000.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.flink.core.*;
import ch.ntb.inf.deep.flink.interfaces.zynq.AXIInterface;
import ch.ntb.inf.deep.flink.subdevices.*;
import ch.ntb.inf.deep.runtime.arm32.Task;
import ch.ntb.inf.deep.runtime.zynq7000.driver.UART1;

public class FlinkDemo extends Task{
	
	public static boolean led1 = false;
	public static int outputPeriod = 0;
	public static int outputHighTime = 0;
	
	static Device fDevice;
	
	FlinkInfo info;
	FlinkGPIO gpios;
	FlinkPWM pwm;
	FlinkPPWA ppwa;
	FlinkWatchdog wd;
	
	public void action() {
		/*System.out.print("ppwa 1 period: ");
		outputPeriod = ppwa.getPeriodTime(1);
		System.out.println(outputPeriod);
		System.out.print("ppwa 1 hightime: ");
		outputHighTime = ppwa.getHighTime(1);
		System.out.println(outputHighTime);
		System.out.println("------------------------------------------------");*/
		
		/*for(int i = 3; i < 7; i++) {
			if(gpios.getValue(i)) {
				System.out.print("1\t|\t");
			}else {
				System.out.print("0\t|\t");
			}
		}
		
		if(led1) {
			System.out.print("led 1-3 on");
		}else {
			System.out.print("led 1-3 off");
		}
		for(int i = 0; i < 3; i++) {
			gpios.setValue(i, led1);
		}
		led1 = !led1;
		System.out.println();*/
		
	}

	static {
		// Initialize UART (115200 8N1)
		UART1.start(115200, (short)0, (short)8);

		// Use the UART for stdout and stderr
		System.out = new PrintStream(UART1.out);
		System.err = System.out;
		
		// Print a string to the stdout
		System.out.println("flink demo");
		
		fDevice = new Device(new AXIInterface());
		System.out.println("before get device list");
		SubDevice[] list = fDevice.getDeviceList();
		System.out.println("done");
		
		/*for(FLinkSubDevice f : list) {
			System.out.println(f.uniqueID);
		}
		
		
		
		info = new FlinkInfo(list[0]);
		System.out.println("got info device");
		gpios = new FLinkGPIO(list[1]);
		System.out.println("got gpio device");
		pwm = new FLinkPWM(list[2]);
		System.out.println("got pwm device");
		ppwa = new FLinkPPWA(list[3]);
		System.out.println("got ppwa device");
		wd = new FLinkWatchdog(list[4]);
		System.out.println("got wd device");
		//subDev = fDevice.getSubdeviceByNr(1);
		//subDev.getSubtype();
		
		//gpio = new FLinkGPIO(subDev);
		System.out.print("gpio channels: ");
		System.out.println(list[1].getNumberOfChannels());
		gpios.setDir(0, false);
		gpios.setDir(1, false);
		gpios.setDir(2, false);
		gpios.setDir(3, true);
		gpios.setDir(4, true);
		gpios.setDir(5, true);
		gpios.setDir(6, true);
		
		System.out.print("baseclk: ");
		System.out.println(pwm.getBaseClock());
		
		int period1 = 100000/pwm.TIMEBASE; // 100 us = 100.000 ns
		int period2 = 100000000/pwm.TIMEBASE; // 100 ms = 100.000.000 ns*/
		
		/*int period1 = 20/pwm.TIMEBASE;		// 20 ns
		int period2 = pwm.getBaseClock();*/
		
		/*pwm.setPeriodTime(0, period1);
		pwm.setPeriodTime(1, period2);
		
		pwm.setHighTime(0, period1/2);
		pwm.setHighTime(1, period2/2);
		
		
		for(int i = 0; i < 3; i++) {
			gpios.setValue(i, false);
		}
		
		System.out.print("pwm timebase: ");
		System.out.println(pwm.TIMEBASE);
		//gpio.setDir(0, false);
		//gpio.setValue(0, false);
		period = 500;
		Task.install(this);*/
	}

}
