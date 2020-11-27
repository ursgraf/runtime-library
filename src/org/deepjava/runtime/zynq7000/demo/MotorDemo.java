/*
 * Copyright 2011 - 2013 NTB University of Applied Sciences in Technology
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

package org.deepjava.runtime.zynq7000.demo;

import java.io.PrintStream;

import org.deepjava.flink.core.FlinkDevice;
import org.deepjava.flink.subdevices.FlinkInfo;
import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.zynq7000.driver.UART;
import org.deepjava.runtime.zynq7000.sysp.SpeedController4DCMotor;


/**
 * Demo application for motor controller in sign magnitude mode. <br>
 * Use the Maxon motor module with a RE-max13 and connect it as follows:
 * <ul>
 *   <li>PWM A (Right): PWM, channel 0</li>
 *   <li>PWM B (Left): PWM, channel 1</li>
 *   <li>Encoder A: FQD, channel 0 A</li>
 *   <li>Encoder B: FQD, channel 0 B</li>
 * </ul>
 *
 */
public class MotorDemo extends Task {
	private static final float ts = 0.01f;				// task period [s]
	private static final float umax = 5;				// maximum voltage [V]
	private static final float i = 17;					// gear transmission ratio
	private static final float kp = 0.2f;				// controller gain factor
	private static final float tn = 0.1f;				// time constant of the controller (equal to the mechanical time constant of the connected DC motor)
	
	private static boolean auto = false;
	private static float speed = 0;
	private static SpeedController4DCMotor ctrl;
	
	private MotorDemo() {}
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.arm32.Task#action()
	 */
	public void action() {
		if (nofActivations % (int)(1/ts) == 0) {
			if (auto) {
				speed += 0.5 * Math.PI;
				if (speed > 10 * Math.PI) speed = (float) (-10 * Math.PI);
				ctrl.setSpeed(speed);
			}
			System.out.print("actual speed: ");
			System.out.print(ctrl.getSpeed());
			System.out.println(" 1/s");
		}
		ctrl.run();
	}
	
	/**
	 * Command: Stop motor.
	 */
	public static void stop() {
		speed = 0;
		ctrl.setSpeed(0);
		System.out.println("Speed set to 0");
	}
	
	/**
	 * Command: Rotate motor with 5 turns per second in anticlockwise direction.
	 */
	public static void left10pi() {
		ctrl.setSpeed((float) (10 * Math.PI));
		System.out.print("Speed set to ");
		System.out.print((float) (10 * Math.PI));
		System.out.println(" 1/s");
	}
	
	/**
	 * Command: Rotate motor with 1 turn per second in anticlockwise direction.
	 */
	public static void left2pi() {
		ctrl.setSpeed((float) (2 * Math.PI));
		System.out.print("Speed set to ");
		System.out.print((float) (2 * Math.PI));
		System.out.println(" 1/s");
	}
	
	/**
	 * Command: Rotate motor with 5 turns per second in clockwise direction.
	 */
	public static void right10pi() {
		ctrl.setSpeed((float) (-10 * Math.PI));
		System.out.print("Speed set to ");
		System.out.print((float) (-10 * Math.PI));
		System.out.println(" 1/s");
	}
	
	/**
	 * Command: Rotate motor with 1 turn per second in clockwise direction.
	 */
	public static void right2pi() {
		ctrl.setSpeed((float) (-2 * Math.PI));
		System.out.print("Speed set to ");
		System.out.print((float) (-2 * Math.PI));
		System.out.println(" 1/s");
	}
	
	/**
	 * Command: Disable automatic mode.
	 */
	public static void disableAutoMode() {
		auto = false;
		System.out.println("Automatic demo mode disabled");
	}
	
	/**
	 * Command: Enable automatic mode.
	 */
	public static void enableAutoMode() {
		auto = true;
		System.out.println("Automatic demo mode enabled");
	}
	
	static {
		// Initialize UART (115200 8N1)
		UART uart = UART.getInstance(UART.pUART1);
		uart.start(115200, (short)0, (short)8);
		
		// Use the UART for stdout and stderr
		System.out = new PrintStream(uart.out);
		System.err = System.out;
		System.out.println("DC Motor Demo");
		FlinkDevice.getInstance().lsflink();
		FlinkInfo info = FlinkDevice.getInfo();
		System.out.print("info description: ");
		System.out.println(info.getDescription());
				
		ctrl = new SpeedController4DCMotor(ts, 0, 1, 0, 64, umax, i, kp, tn);
		Task t = new MotorDemo();
		t.period = (int)(1000 * ts);
		Task.install(t);
	}

}
