package org.deepjava.runtime.zynq7000.demo;

import java.io.PrintStream;

import org.deepjava.flink.core.FlinkDevice;
import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.zynq7000.driver.UART;
import org.deepjava.runtime.zynq7000.driver.WatchdogTask;

public class WatchdogDemo extends Task {
	static final int period = 1000;
	static WatchdogTask supervisor;
	
	public void action() {
		supervisor.kick = true;
		if (nofActivations == 10) Task.remove(this);
	}
	
	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		FlinkDevice.getInstance().lsflink();
		System.out.print("info description: ");
		System.out.println(FlinkDevice.getInfo().getDescription());
		
		System.out.println("Watchdog demo started");
		supervisor = new WatchdogTask(period, (int) (period * 1.5));
		Task t = new WatchdogDemo();
		t.period = period;
		Task.install(t);
	}

}
