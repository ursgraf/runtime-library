package org.deepjava.runtime.zynq7000.microzed.test;

import java.io.PrintStream;

import org.deepjava.flink.core.*;
import org.deepjava.flink.subdevices.*;
import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.zynq7000.driver.UART;
import org.deepjava.runtime.zynq7000.driver.WatchdogTask;
import org.deepjava.runtime.zynq7000.microzed.IMicroZed;
import org.deepjava.unsafe.arm.US;

/**
 * Test application for the MicroZed adapter board. Use <i>flink2</i> configuration.<br>
 * <ul>
 *   <li>Connect digital IO as follows: IO0 with IO16</li>
 *   <li>Connect the 8 channels of the ADC128S102 with an input voltage of 0 - 3.3V</li>
 *   <li>Connect the input of the ADC7476 with an input voltage of 0 - 3.3V</li>
 * </ul>
 *
 */public class EvmTest2 extends Task implements FlinkDefinitions, IMicroZed {
	
	public static int outputPeriod = 0;
	public static int outputHighTime = 0;
	
	static FlinkDevice fDev;
	static FlinkInfo info;
	static FlinkGPIO gpio;
	static FlinkADC adc1, adc2;
	static FlinkPWM pwm;
	static FlinkPPWA ppwa;
	static FlinkCounter fqd;
	static WatchdogTask wdt;
	
	
	public void action() {
		wdt.kick = true;
		if (nofActivations == 1) {
			System.out.println("setting directions");
			gpio.setDir(0, true);
			gpio.setDir(16, false);
		}
		if (nofActivations == 2) {
			System.out.println("setting one");
			gpio.setValue(0, true);
		}
		if (nofActivations == 3) {
			System.out.println("checking for one");
			if (!gpio.getValue(16)) {
				System.out.print(16);
				System.out.println(" FAILED");
			}
		}
		if (nofActivations == 4) {
			gpio.setValue(0, false);
		}
		if (nofActivations == 5) {
			System.out.println("checking for zero");
			if (gpio.getValue(16)) {
				System.out.print(16);
				System.out.println(" FAILED");
			}
		}
		if (nofActivations == 6) {
			System.out.println("setting opposite direction");
			gpio.setDir(0, false);
			gpio.setDir(16, true);
		}
		if (nofActivations == 7) {
			System.out.println("setting one");
			gpio.setValue(16, true);
		}
		if (nofActivations == 8) {
			System.out.println("checking for one");
			if (!gpio.getValue(0)) {
				System.out.print(0);
				System.out.println(" FAILED");
			}
		}
		if (nofActivations == 9) {
			System.out.println("setting zero");
			gpio.setValue(16, false);
		}
		if (nofActivations == 10) {
			System.out.println("checking for zero");
			if (gpio.getValue(0)) {
				System.out.print(0);
				System.out.println(" FAILED");
			}
		}
		if (nofActivations == 11) {
			System.out.println("setting pwm");
			pwm.setPeriod(1, (int) (0.01 * pwm.getBaseClock()));
			pwm.setHighTime(1, (int) (0.6 * 0.01 * pwm.getBaseClock()));
			System.out.print("PWM base clock = ");
			System.out.println(pwm.getBaseClock());
			System.out.print("PWM period = ");
			System.out.println(pwm.getPeriod(1));
			System.out.print("PWM hightime = ");
			System.out.println(pwm.getHighTime(1));
		}
		if (nofActivations == 12) {
			System.out.println("reading ppwa");
			System.out.print("PPWA base clock = ");
			System.out.println(ppwa.getBaseClock());
			System.out.print("PPWA period = ");
			System.out.println((double)ppwa.getPeriod(2) / 100000000);
			System.out.print("PPWA hightime = ");
			System.out.println((double)ppwa.getHighTime(2) / 100000000);
		}
		if (nofActivations > 20) {
			System.out.print("adc:  ");
			for (int i = 0; i < adc1.dev.nofChannels; i++) {
				System.out.print(adc1.getValue(i));
				System.out.print('\t');
			}
			System.out.print("//\t");
			System.out.print(adc2.getValue(0));
			System.out.print("//\t");
			System.out.println(fqd.getCount(3));
			Task.remove(wdt);
		}
	}

	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		System.out.println("\n\rEVM test 2");
		
		fDev = FlinkDevice.getInstance();
		fDev.lsflink();
	
		info = FlinkDevice.getInfo();
		System.out.print("info description: ");
		System.out.println(info.getDescription());
		gpio = FlinkDevice.getGPIO();
		adc1 = FlinkDevice.getADC128S102();
		adc2 = FlinkDevice.getAD7476();
		pwm = FlinkDevice.getPWM();
		ppwa = FlinkDevice.getPPWA();
		fqd = FlinkDevice.getCounter();
		wdt = new WatchdogTask(1000, 1500);
		
		Task t = new EvmTest2();
		t.period = 1000;
		Task.install(t);
	}

}
