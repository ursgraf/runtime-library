/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;

/* Changes:
 * 02.09.2011	NTB/MZ	Adapted to the changes in the Robi2 driver
 * 26.05.2011	NTB/MZ	enhanced and JavaDoc updated
 * 11.05.2011	NTB/RM	initial version
 */

/**
 * Robi2 Motor Demo. Use the given commands to control the Robi.
 *
 */
public class Robi2MotorDemo extends Task {

	static final int driveForward = 0, stop1 = 1, driveBackward = 2, stop2 = 3; // task states

	static Robi2MotorDemo task;	// the task object

	int state;	// the state of the state machine

	/**
	 * Drive forward with full speed.
	 */
	public static void driveFullSpeedForward() {
		Robi2.drive(100);
		System.out.println("Drive forward (100%)");
	}
	
	/**
	 * Drive forward with half speed.
	 */
	public static void driveHalfSpeedForward() {
		Robi2.drive(50);
		System.out.println("Drive forward (50%)");
	}

	/**
	 * Drive backward with full speed.
	 */
	public static void driveFullSpeedBackward() {
		Robi2.drive(-100);
		System.out.println("Drive backward (100%)");
	}
	
	/**
	 * Drive backward with half speed.
	 */
	public static void driveHalfSpeedBackward() {
		Robi2.drive(-50);
		System.out.println("Drive backward (50%)");
	}
	
	/**
	 * Turn right around (clockwise).
	 */
	public static void turnRight() {
		Robi2.turn(100);
		System.out.println("Rotating clockwise");
	}
	
	/**
	 * Turn left around (anticlockwise).
	 */
	public static void turnLeft() {
		Robi2.turn(-100);
		System.out.println("Rotating anticlockwise");
	}

	/**
	 * Stop both motors.
	 */
	public static void stop() {
		Robi2.stop() ;
		System.out.println("Stop");
	}
	
	/**
	 * Start automatic demo mode.
	 */
	public static void startTask() {
		task.state = 0;		// Initialze the state machine
		Task.install(task);	// Install the task
	}

	/**
	 * Stop automatic demo mode.
	 */
	public static void stopTask() {
		Task.remove(task);
		stop();
	}
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action() {	
		switch (state) { // the state machine
			case driveForward:
				driveFullSpeedForward();
				state = stop1;
			break;
			case stop1:
				stop();
				state = driveBackward;
			break;
			case driveBackward:
				driveFullSpeedBackward();
				state = stop2;
			break;
			case stop2:
				stop();
				state = driveForward;
			break;
		}
	}
	
	static {
		// Initialize the SCI1 as standard output
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI1.out);
		System.out.println("Robi2: MotorDemo");
		
		// Create and install the task
		task = new Robi2MotorDemo(); // create an instance of this class
		task.period = 1000;	// set the period time for the task (in ms)
	}
}
