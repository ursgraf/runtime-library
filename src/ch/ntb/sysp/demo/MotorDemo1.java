package ch.ntb.sysp.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;
import ch.ntb.sysp.lib.SpeedController4DCMotor;

public class MotorDemo1 extends Task {
	
	private static final float ts = 1e-3f;
	private static final int pwmChannelA = 0;
	private static final boolean useTPUA4PWM = true;
	private static final int encChannelA = 2;
	private static final boolean useTPUA4Enc = true;
	private static final int encTPR = 64; 
	private static final float umax = 5;
	private static final float i = 17; 
	private static final float kp = 1; 
	private static final float tn = 0.01f;
	private static final float maxSpeed = 1;
	
	private short counter = 0;
	private float speed = 0;
	private SpeedController4DCMotor controller;
	
	public MotorDemo1() {
		// Create motor controller
		controller = new SpeedController4DCMotor(ts, pwmChannelA, useTPUA4PWM, encChannelA, useTPUA4Enc, encTPR, umax, i, kp, tn);
	}
	
	public void action() {
		if(counter > 2000) {
			speed += 0.1f;
			if(speed > maxSpeed) speed = -maxSpeed;
			controller.setDesiredSpeed(speed);
			counter = 0;
		}
		controller.run();
		counter++;
	}
	
	static {
		// Initialize SCI1 (9600 8N1)
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		
		// Use the SCI1 for stdout and stderr
		System.out = new PrintStream(SCI1.out);
		System.err = System.out;
		
		// Print a string to the stdout
		System.out.print("SysP: DC Motor Demo 1");
				
		// Create and install the demo task
		Task t = new MotorDemo1();
		t.period = (int)(1000 * ts);
		Task.install(t);
	}

}
