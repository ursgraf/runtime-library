package org.deepjava.runtime.zynq7000.microzed.test;

import java.io.PrintStream;

import org.deepjava.flink.core.*;
import org.deepjava.flink.subdevices.*;
import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.zynq7000.driver.UART;
import org.deepjava.runtime.zynq7000.microzed.IMicroZed;
import org.deepjava.unsafe.arm.US;

/**
 * Test application for the MicroZed adapter board. Use <i>flink1</i> configuration.<br>
 * <ul>
 *   <li>Connect all digital IOs as follows: IO0 with IO1, IO2 with IO3, etc. up to IO85 with IO86</li>
 *   <li>Connect the 8 channels of the ADC128S102 with an input voltage of 0 - 3.3V</li>
 *   <li>Connect the input of the ADC7476 with an input voltage of 0 - 3.3V</li>
 * </ul>
 *
 */public class EvmTest1 extends Task implements FlinkDefinitions, IMicroZed {
	
	public static int outputPeriod = 0;
	public static int outputHighTime = 0;
	
	static FlinkDevice fDev;
	static FlinkInfo info;
	static FlinkGPIO gpio;
	static FlinkADC adc1, adc2;
	
	public void action() {
		toggleMIO();
		
		int len = 88;
		if (nofActivations == 1) {
			System.out.println("setting directions");
			for (int i = 0; i < len; i++) gpio.setDir(i, i % 2 == 0);
		}
		if (nofActivations == 2) {
			System.out.println("setting ones");
			for (int i = 0; i < len; i += 2) gpio.setValue(i, true);
		}
		if (nofActivations == 3) {
			System.out.println("checking for ones");
			for (int i = 0; i < len; i += 2) 
				if (!gpio.getValue(i + 1)) {
					System.out.print(i);
					System.out.println(" FAILED");
				}
		}
		if (nofActivations == 4) {
			System.out.println("setting zeros");
			for (int i = 0; i < len; i += 2) gpio.setValue(i, false);
		}
		if (nofActivations == 5) {
			System.out.println("checking for zeros");
			for (int i = 0; i < len; i += 2) 
				if (gpio.getValue(i + 1)) {
					System.out.print(i);
					System.out.println(" FAILED");
				}
		}
		if (nofActivations == 6) {
			System.out.println("setting opposite directions");
			for (int i = 0; i < len; i++) gpio.setDir(i, i % 2 != 0);
		}
		if (nofActivations == 7) {
			System.out.println("setting ones");
			for (int i = 0; i < len; i += 2) gpio.setValue(i + 1, true);
		}
		if (nofActivations == 8) {
			System.out.println("checking for ones");
			for (int i = 0; i < len; i += 2) 
				if (!gpio.getValue(i)) {
					System.out.print(i);
					System.out.println(" FAILED");
				}
		}
		if (nofActivations == 9) {
			System.out.println("setting zeros");
			for (int i = 0; i < len; i += 2) gpio.setValue(i + 1, false);
		}
		if (nofActivations == 10) {
			System.out.println("checking for zeros");
			for (int i = 0; i < len; i += 2) 
				if (gpio.getValue(i)) {
					System.out.print(i);
					System.out.println(" FAILED");
				}
		}
		if (nofActivations > 10) {
			System.out.print("adc:  ");
			for (int i = 0; i < adc1.dev.nofChannels; i++) {
				System.out.print(adc1.getValue(i));
				System.out.print('\t');
			}
			System.out.print("//\t");
			System.out.println(adc2.getValue(0));
		}
	}

	private void toggleMIO() {
		US.PUT4(GPIO_OUT0, US.GET4(GPIO_OUT0) ^ 0x3e01);
	}

	static {
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		System.out.println("\n\rEVM test 1");
		
		fDev = FlinkDevice.getInstance();
		fDev.lsflink();
	
		info = FlinkDevice.getInfo();
		System.out.print("info description: ");
		System.out.println(info.getDescription());
		gpio = FlinkDevice.getGPIO();
		adc1 = FlinkDevice.getADC128S102();
		adc2 = FlinkDevice.getAD7476();
		
		US.PUT4(SLCR_UNLOCK, 0xdf0d);
		US.PUT4(MIO_PIN_00, 0x300);		// led, LVCMOS18, fast, GPIO 0, tristate disable
		US.PUT4(MIO_PIN_09, 0x300);		// led, LVCMOS18, fast, GPIO 9, tristate disable
		US.PUT4(MIO_PIN_10, 0x300);		// led, LVCMOS18, fast, GPIO 10, tristate disable
		US.PUT4(MIO_PIN_11, 0x300);		// led, LVCMOS18, fast, GPIO 11, tristate disable
		US.PUT4(MIO_PIN_12, 0x300);		// led, LVCMOS18, fast, GPIO 12, tristate disable
		US.PUT4(MIO_PIN_13, 0x300);		// led, LVCMOS18, fast, GPIO 13, tristate disable
		US.PUT4(SLCR_LOCK, 0x767b);
		US.PUT4(GPIO_DIR0, US.GET4(GPIO_DIR0) | 0x3e01);
		US.PUT4(GPIO_OUT_EN0, US.GET4(GPIO_OUT_EN0) | 0x3e01);

		Task t = new EvmTest1();
		t.period = 1000;
		Task.install(t);
	}

}
