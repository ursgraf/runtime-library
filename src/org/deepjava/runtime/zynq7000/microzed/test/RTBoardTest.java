package org.deepjava.runtime.zynq7000.microzed.test;

/*
 * Copyright 2011 - 2022 NTB University of Applied Sciences in Technology
 * Buchs, Switzerland, http://www.ntb.ch/inf
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

import java.io.IOException;
import java.io.PrintStream;
import org.deepjava.flink.core.FlinkDevice;
import org.deepjava.flink.subdevices.FlinkGPIO;
import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.zynq7000.driver.UART;
import org.deepjava.runtime.zynq7000.microzed.driver.RTBoard;

/**
 * Test program for the Microzed controller board<br>
 * PL must be loaded with flink3 configuration.
 * 
 * @author Urs Graf / OST 7.12.2021<br>
 */
public class RTBoardTest extends Task {

	// Enable/Disable tests
	private static final boolean enableLedTest = true;
	private static final boolean enableButtonTest = true;
	private static final boolean enableDioTest = true; 
	private static final boolean enableAnalogTest = true; 
	private static final boolean enableDacTest = true; 
	private static final boolean enableAdcTest = true;

	// Commands
	private static final byte breakCmd = 0x1B, contCmd = 0x0D;

	// States
	private Test test = Test.menu;
	private State state = State.sendMessage;

	private int currTime = 0;
	private int	ledCtr = 3;
	private int ioCtr = 0;
	private int dacCtr = 0;
	private int voltage;
	private float voltage1;
	private boolean minus;
	private int count;
	private int channel;
	private int nofError;

	private static FlinkGPIO gpio;
	
	/**
	 * Tests IOs.
	 */
	private void menu() {
		switch (state) {
		case sendMessage:
			System.out.println("\nChoose action: ");
			System.out.println("\trun leds -> 1, read buttons -> 2, test DIO -> 3");
			System.out.println("\tanalog -> 4, test dac -> 5, test adc -> 6");
			state = State.wait;
			break;
		case wait:
			int cmd = 0;
			try {
				if (System.in.available() > 0){
					cmd = System.in.read();
				}
			} catch (IOException e) {break;}
			if (cmd == '1') {
				System.out.println("running lights on leds for 5s");
				state = State.sendMessage;
				test = Test.led;
				break;
			} else if (cmd == '2') {
				System.out.println("read buttons for 5s");
				state = State.sendMessage;
				test = Test.button;
				break;
			} else if (cmd == '3') {
				System.out.println("test digital in / out");
				state = State.sendMessage;
				test = Test.dio;
				break;
			} else if (cmd == '4') {
				System.out.println("test analog in / out");
				state = State.sendMessage;
				test = Test.analog;
				break;
			} else if (cmd == '5') {
				System.out.println("test analog out");
				state = State.sendMessage;
				test = Test.dac;
				break;
			} else if (cmd == '6') {
				System.out.println("test analog in");
				state = State.sendMessage;
				test = Test.adc;
				break;
			}
			break;
		default:
			state = State.sendMessage;
		}
	}

	/**
	 * Tests the LEDs. <br>
	 * Period of the running light is given by constant <code>ledPeriod</code>.
	 */
	private void led() {
		if (Task.time() > currTime) {
			currTime = Task.time() + 500;
			RTBoard.ledOut(ledCtr, false);
			ledCtr = (ledCtr + 1) % 4;
			RTBoard.ledOut(ledCtr, true);
			if (count++ == 12) {
				RTBoard.ledOut(ledCtr, false);
				test = Test.menu;
				count = 0;
				ledCtr = 3;
			}
		}
	}
	
	/**
	 * Tests the push buttons.
	 * When pressed the first two LED's light up.
	 */
	private void button() {
		RTBoard.ledOut(0, RTBoard.buttonIn(0));
		RTBoard.ledOut(1, RTBoard.buttonIn(1));
		if (count++ == 500) {
			test = Test.menu;
			count = 0;
		}
	}

	/**
	 * Tests IOs.
	 */
	private void ioTest() {
		switch (state) {
		case sendMessage:
			System.out.print("Connect DI");
			System.out.print(ioCtr + 1);
			System.out.print(" to DI");
			System.out.print(ioCtr + 3);
			System.out.println(": Continue -> Enter, Break -> ESC");
			state = State.wait;
			break;
		case wait:
			int cmd = 0;
			try {
				if (System.in.available() > 0) cmd = System.in.read();
			} catch (IOException e) {break;}
			if (cmd == breakCmd) {
				state = State.sendMessage;
				test = Test.menu;
				break;
			} else if (cmd == contCmd) {
				state = State.runTest;
			} 
			break;
		case runTest:
			gpio.setDir(ioCtr, true);
			gpio.setDir(ioCtr + 2, false);
			gpio.setValue(ioCtr, false);
			if (gpio.getValue(ioCtr + 2)) {
				System.out.println("Failed: '1' detected instead of '0'");
			} else {
				gpio.setValue(ioCtr, true);
				if (!gpio.getValue(ioCtr + 2)) {
					System.out.println("Failed: '0' detected instead of '1'");
				} else {
					gpio.setDir(ioCtr + 2, true);
					gpio.setDir(ioCtr, false);
					gpio.setValue(ioCtr + 2, false);
					if (gpio.getValue(ioCtr)) {
						System.out.println("Failed: '1' detected instead of '0'");
					} else {
						gpio.setValue(ioCtr + 2, true);
						if (!gpio.getValue(ioCtr)) {
							System.out.println("Failed: '0' detected instead of '1'");
						} else {
							System.out.println("Success");
						}
					}
				}
			}
			if (ioCtr < 1) ioCtr++;
			else {
				test = Test.menu;
				ioCtr = 0;
			}
			state = State.sendMessage;
			break;
		default:
			test = Test.menu;
			state = State.sendMessage;
		}
	}

	/**
	 * Gibt auf den analog Ausgängen des RTBoards einen Sägezahn mit einer
	 * Frequenz von ca. 1/4Hz aus.
	 */
	private void analogTest() {
		switch (state) {
		case sendMessage:
			System.out.print("Connect AOut");
			System.out.print(dacCtr + 1);
			System.out.print(" to AIn");
			System.out.print(dacCtr + 1);
			System.out.println(": Continue -> Enter, Break -> ESC");
			state = State.wait;
			break;
		case wait:
			int cmd = 0;
			try {
				if (System.in.available() > 0) cmd = System.in.read();
			} catch (IOException e) {break;}
			if (cmd == breakCmd) {
				state = State.sendMessage;
				test = Test.menu;
				break;
			} else if (cmd == contCmd) {
				voltage1 = -9.5f;
				nofError = 0;
				RTBoard.analogOut(dacCtr, voltage1);
				System.out.print("testing "); System.out.println(dacCtr);
				state = State.runTest;
			} 
			break;
		case runTest:
			if (count < 20) {
				float diff = Math.abs(RTBoard.analogIn(dacCtr) - voltage1);
				if (diff < 0.5) {
					System.out.print("\tSuccess at ");
					System.out.print(voltage1);
					System.out.print("V, diff = ");
					System.out.println(diff);
				} else {
					nofError++;
					System.out.print("\tFailed at ");
					System.out.print(voltage1);
					System.out.print("V, diff = ");
					System.out.println(diff);
				}
				voltage1 += 1;
				RTBoard.analogOut(dacCtr, voltage1);
				count++;
			} else {
				count = 0;
				if (dacCtr < 1) dacCtr++;
				else {
					test = Test.menu;
					if (nofError > 0) System.out.println("Test failed");
					else System.out.println("Test ok");
					dacCtr = 0;
				}
				state = State.sendMessage;
			}
			break;
		default:
			test = Test.menu;
			state = State.sendMessage;
		}
	}

	/**
	 * Writes to a single dac channel.
	 */
	private void dacTest() {
		switch (state) {
		case sendMessage:
			System.out.println("choose DAC channel: 0(AOut1) or 1(AOut2), Break -> ESC");
			state = State.getChannel;
			break;
		case getChannel:
			int cmd = 0;
			try {
				if (System.in.available() > 0) cmd = System.in.read();
			} catch (IOException e) {break;}
			if (cmd == breakCmd) {
				test = Test.menu;
				state = State.sendMessage;
				break;
			} else if (cmd == '0') {
				channel = 0;
				state = State.sendVoltage;
			} else if (cmd == '1') {
				channel = 1;
				state = State.sendVoltage;
			} 
			break;
		case sendVoltage:
			System.out.println("choose voltage: complete with Enter, Break -> ESC");
			voltage = 0;
			minus = false;
			state = State.wait;
			break;
		case wait:
			cmd = 0;
			try {
				if (System.in.available() > 0) cmd = System.in.read();
			} catch (IOException e) {break;}
			if (cmd == breakCmd) {
				state = State.sendMessage;
				test = Test.menu;
				break;
			} else if (cmd >= '0' && cmd <= '9') {
				voltage *= 10;
				voltage += cmd - '0';
			} else if (cmd == '-') {
				minus = true;
			} else if (cmd == contCmd) {
				voltage *= (minus ? -1 : 1);
				System.out.print("AOut");
				System.out.print(channel + 1);
				System.out.print(": ");
				System.out.print(voltage);
				System.out.println("V");
				state = State.runTest;
			}
			break;
		case runTest:
			cmd = 0;
			RTBoard.analogOut(channel, voltage);
			try {
				if (System.in.available() > 0) cmd = System.in.read();
			} catch (IOException e) {}
			if (cmd == breakCmd) {
				RTBoard.analogOut(channel, 0);
				test = Test.menu;
				state = State.sendMessage;
			}
			break;
		default:
			state = State.sendMessage;
		}
	}

	/**
	 * Reads from a single adc channel  
	 */
	private void adcTest() {
		switch (state) {
		case sendMessage:
			System.out.println("choose ADC channel: 0(AIn1) or 1(AIn1), Break -> ESC");
			state = State.wait;
			break;
		case wait:
			int cmd = 0;
			try {
				if (System.in.available() > 0) cmd = System.in.read();
			} catch (IOException e) {break;}
			if (cmd == breakCmd) {
				test = Test.menu;
				state = State.sendMessage;
				break;
			} else if (cmd == '0') {
				channel = 0;
				state = State.runTest;
			} else if (cmd == '1') {
				channel = 1;
				state = State.runTest;
			} 
			break;
		case runTest:
			cmd = 0;
			try {
				if (System.in.available() > 0) cmd = System.in.read();
			} catch (IOException e) {}
			if (cmd == breakCmd) {
				test = Test.menu;
				state = State.sendMessage;
			}
			if (Task.time() > currTime) {
				currTime = Task.time() + 500;
				System.out.println(RTBoard.analogIn(channel));
			}
			break;
		default:
			state = State.sendMessage;
		}
	}
	

	public void action() {
		switch (test) {
		case menu:
			menu();
			break;
		case led:
			if (enableLedTest) {
				led();
			} else
				test = Test.menu;
			break;
		case button:
			if (enableButtonTest)
				button();
			else
				test = Test.menu;
			break;
		case dio:
			if (enableDioTest)
				ioTest();
			else
				test = Test.menu;
			break;
		case analog:
			if (enableAnalogTest)
				analogTest();
			else
				test = Test.menu;
			break;
		case dac:
			if (enableDacTest)
				dacTest();
			else
				test = Test.menu;
			break;
		case adc:
			if (enableAdcTest)
				adcTest();
			else
				test = Test.menu;
			break;
		default:
		}
	}

	static {
		UART uart1 = UART.getInstance(UART.pUART1);
		uart1.start(115200, (short)0, (short)8);
		System.out = new PrintStream(uart1.out);
		System.err = System.out;
		System.out.println("\n\n\r**** RTBoard testprogram *****");
		gpio = FlinkDevice.getGPIO();
		System.in = uart1.in;
		
		RTBoardTest task = new RTBoardTest();
		task.period = 10;
		Task.install(task);
	}
}

enum Test {menu, led, button, dio, analog, dac, adc}
enum State {sendMessage, wait, runTest, getChannel, sendVoltage}
