package ch.ntb.sysp.demo;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;
import ch.ntb.sysp.lib.SpeedController4DCMotor;

/**
 * Demo application for motor controller. <br/>
 * Use the Maxon Motor Module with a RE-max13 and connect it as follows:
 * <ul>
 *   <li>PWM A (Right): TPU A, Channel 0</li>
 *   <li>PWM B (Left): TPU A, Channel 1</li>
 *   <li>Encoder A: TPU A, Channel 2</li>
 *   <li>Encoder B: TPU A, Channel 3</li>
 * </ul>
 *
 */
public class MotorDemo1 extends Task {
	
	private static final float ts = 0.001f;
	private static final int pwmChannelA = 0;
	private static final boolean useTPUA4PWM = true;
	private static final int encChannelA = 2;
	private static final boolean useTPUA4Enc = true;
	private static final int encTPR = 64; 
	private static final float umax = 5;
	private static final float i = 17; 
	private static final float kp = 2; 
	private static final float tn = 0.01f;
	private static final float maxSpeed = 20;
	
	private static boolean auto = false;
	private static short counter = 0;
	private static float speed = 0;
	private static SpeedController4DCMotor controller = new SpeedController4DCMotor(ts, pwmChannelA, useTPUA4PWM, encChannelA, useTPUA4Enc, encTPR, umax, i, kp, tn);
	
	private MotorDemo1() {}
	
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
	
	public static void stop() {
		controller.setDesiredSpeed(0);
		System.out.println("Speed set to 0");
	}
	
	public static void fullLeft() {
		controller.setDesiredSpeed(maxSpeed);
		System.out.print("Speed set to ");
		System.out.println(maxSpeed);
	}
	
	public static void halfLeft() {
		controller.setDesiredSpeed(maxSpeed / 2);
		System.out.print("Speed set to ");
		System.out.println(maxSpeed / 2);
	}
	
	public static void fullRight() {
		controller.setDesiredSpeed(-maxSpeed);
		System.out.print("Speed set to ");
		System.out.println(-maxSpeed);
	}
	
	public static void halfRight() {
		controller.setDesiredSpeed(-maxSpeed / 2);
		System.out.print("Speed set to ");
		System.out.println(-maxSpeed / 2);
	}
	
	public static void disableAutoMode() {
		auto = false;
		System.out.println("Automatic demo mode disabled");
	}
	
	public static void enableAutoMode() {
		auto = true;
		System.out.println("Automatic demo mode enabled");
	}
	
	static {
		// Initialize SCI1 (9600 8N1)
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		
		// Use the SCI1 for stdout and stderr
		System.out = new PrintStream(SCI1.out);
		System.err = System.out;
		
		// Print a string to the stdout
		System.out.println("SysP: DC Motor Demo 1\n");
				
		// Create and install the demo task
		Task t = new MotorDemo1();
		t.period = (int)(1000 * ts);
		Task.install(t);
	}

}
