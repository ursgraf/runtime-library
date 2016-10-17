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

package ch.ntb.sysp.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.ppc32.Task;
import ch.ntb.sysp.lib.SpeedController4DCMotor;

/**
 * Demo application for motor controller. <br>
 * Use the Maxon Motor Module with a RE-max13 and connect it as follows:
 * <ul>
 *   <li>PWM A (Right): TPU A, Channel 0</li>
 *   <li>PWM B (Left): TPU A, Channel 1</li>
 *   <li>Encoder A: TPU A, Channel 2</li>
 *   <li>Encoder B: TPU A, Channel 3</li>
 *   <li>Mode: Sign Magnitude (GND)</li>
 * </ul>
 *
 */
public class MotorDemo1 extends Task {
	
	private static final float ts = 0.001f;				// task period [s]
	private static final int pwmChannelA = 0;			// channel for PWM signal A
	private static final int pwmChannelB = 1;			// channel for PWM signal A
	private static final boolean useTPUA4PWM = true;	// use TPU-A for PWM signals
	private static final int encChannelA = 2;			// channel for encoder signal A
	private static final boolean useTPUA4Enc = true;	// use TPU-A for encoder signals
	private static final int encTPR = 64; 				// number of impulse per rotation of the encoder [ticks] 
	private static final float umax = 5;				// maximum voltage [V]
	private static final float i = 17;					// gear transmission ratio
	private static final float kp = 2;					// controller gain factor
	private static final float tn = 0.01f;				// time constant of the controller (equal to the mechanical time constant of the connected DC motor)
	private static final float maxSpeed = 20;			// maximum speed [1/s]
	
	private static boolean auto = false;
	private static short counter = 0;
	private static float speed = 0;
	private static SpeedController4DCMotor controller = new SpeedController4DCMotor(ts, pwmChannelA, pwmChannelB, useTPUA4PWM, encChannelA, useTPUA4Enc, encTPR, umax, i, kp, tn);
	
	private MotorDemo1() {}
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action() {
		if(counter > 2000) {
			if(auto) {
				speed += 0.1f * maxSpeed;
				if(speed > maxSpeed) speed = -maxSpeed;
				controller.setDesiredSpeed(speed);
			}
			System.out.print("Actual speed: ");
			System.out.print(controller.getActualSpeed());
			System.out.println(" 1/s");
			counter = 0;
		}
		counter++;
		controller.run();
	}
	
	
	/**
	 * Command: Stop motor.
	 */
	public static void stop() {
		controller.setDesiredSpeed(0);
		System.out.println("Speed set to 0");
	}
	
	/**
	 * Command: Rotate motor with full speed in anticlockwise direction.
	 */
	public static void fullLeft() {
		controller.setDesiredSpeed(maxSpeed);
		System.out.print("Speed set to ");
		System.out.print(maxSpeed);
		System.out.println(" 1/s");
	}
	
	/**
	 * Command: Rotate motor with half speed in anticlockwise direction.
	 */
	public static void halfLeft() {
		controller.setDesiredSpeed(maxSpeed / 2);
		System.out.print("Speed set to ");
		System.out.print(maxSpeed / 2);
		System.out.println(" 1/s");
	}
	
	/**
	 * Command: Rotate motor with full speed in clockwise direction.
	 */
	public static void fullRight() {
		controller.setDesiredSpeed(-maxSpeed);
		System.out.print("Speed set to ");
		System.out.print(-maxSpeed);
		System.out.println(" 1/s");
	}
	
	/**
	 * Command: Rotate motor with half speed in clockwise direction.
	 */
	public static void halfRight() {
		controller.setDesiredSpeed(-maxSpeed / 2);
		System.out.print("Speed set to ");
		System.out.print(-maxSpeed / 2);
		System.out.println(" 1/s");
	}
	
	
	/**
	 * Command: Disable automatic demo mode.
	 */
	public static void disableAutoMode() {
		auto = false;
		System.out.println("Automatic demo mode disabled");
	}
	
	/**
	 * Command: Enable automatic demo mode.
	 */
	public static void enableAutoMode() {
		auto = true;
		System.out.println("Automatic demo mode enabled");
	}
	
	static {
		// Initialize SCI1 (9600 8N1)
		SCI sci = SCI.getInstance(SCI.pSCI1);
		sci.start(9600, SCI.NO_PARITY, (short)8);
		
		// Use the SCI1 for stdout and stderr
		System.out = new PrintStream(sci.out);
		System.err = System.out;
		
		// Print a string to the stdout
		System.out.println("SysP: DC Motor Demo 1\n");
				
		// Create and install the demo task
		Task t = new MotorDemo1();
		t.period = (int)(1000 * ts);
		Task.install(t);
	}

}
