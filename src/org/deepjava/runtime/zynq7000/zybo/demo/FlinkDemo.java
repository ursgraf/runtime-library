package org.deepjava.runtime.zynq7000.zybo.demo;

import java.io.PrintStream;

import org.deepjava.flink.core.*;
import org.deepjava.flink.subdevices.*;
import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.zynq7000.driver.UART;

public class FlinkDemo extends Task implements FlinkDefinitions {
	
	static FlinkDevice fDev;
	static FlinkInfo info;
	static FlinkGPIO gpio;
	
	public void action() {
		for (int i = 0; i <= 3; i++) {
			gpio.setValue(i, !gpio.getValue(i));
		}
//		gpio.setValue(4, true);
//		gpio.setValue(5, false);
		System.out.print(gpio.getValue(0)); System.out.print("\t");
		System.out.print(gpio.getValue(1)); System.out.print("\t");
		System.out.print(gpio.getValue(2)); System.out.print("\t");
		System.out.print(gpio.getValue(3)); System.out.print("\t");
		System.out.print(gpio.getValue(4)); System.out.print("\t");
		System.out.print(gpio.getValue(5)); System.out.print("\t");
		System.out.print(gpio.getValue(6)); System.out.print("\t");
		System.out.print(gpio.getValue(7)); System.out.println();
	}

	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		System.out.println("\n\rflink demo");
		
		info = FlinkDevice.getInfo();
		System.out.print("info description: ");
		System.out.println(info.getDescription());
		gpio = FlinkDevice.getGPIO();
		
		for(int i = 0; i <= 3; i++) gpio.setDir(i, true);
//		for(int i = 6; i <= 7; i++) gpio.setDir(i, false);
		for(int i = 0; i <= 3; i++) gpio.setValue(i, i % 2 == 0);	
		
		Task t = new FlinkDemo();
		t.period = 500;
		Task.install(t);
	}

}
