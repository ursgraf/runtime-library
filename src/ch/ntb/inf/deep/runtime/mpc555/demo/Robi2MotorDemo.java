package ch.ntb.inf.deep.runtime.mpc555.demo;
import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;

/* Changes:
 * 26.05.2011	NTB/MZ	enhanced and JavaDoc updated
 * 11.05.2011	NTB/RM	initial version
 */


/**
 * Robi2 Motor Demo. Use the given commands to control the Robi.
 *
 */
public class Robi2MotorDemo extends Task {

	static final int driveForward = 0, stop1 = 1, driveBackward = 2, stop2 = 3; // Task states

	static Robi2MotorDemo task;	// das Task-Objekt

	int state;	// der Zustand des Tasks

	/**
	 * Drive forward with full speed.
	 */
	public static void driveFullSpeedForward() {
		Robi2.setDrivesSpeedAntidormic(100);
		System.out.println("Drive forward (100%)");
	}
	
	/**
	 * Drive forward with half speed.
	 */
	public static void driveHalfSpeedForward() {
		Robi2.setDrivesSpeedAntidormic(50);
		System.out.println("Drive forward (50%)");
	}

	/**
	 * Drive backward with full speed.
	 */
	public static void driveFullSpeedBackward() {
		Robi2.setDrivesSpeedAntidormic(-100);
		System.out.println("Drive backward (100%)");
	}
	
	/**
	 * Drive backward with half speed.
	 */
	public static void driveHalfSpeedBackward() {
		Robi2.setDrivesSpeedAntidormic(-50);
		System.out.println("Drive backward (50%)");
	}
	
	/**
	 * Turn right around (clockwise).
	 */
	public static void turnRight() {
		Robi2.setDrivesSpeedEqual(100);
		System.out.println("Rotating clockwise");
	}
	
	/**
	 * Turn left around (anticlockwise).
	 */
	public static void turnLeft() {
		Robi2.setDrivesSpeedEqual(-100);
		System.out.println("Rotating anticlockwise");
	}

	/**
	 * Stop both motors.
	 */
	public static void stop() {
		Robi2.stopDrives() ;
		System.out.println("Stop");
	}
	
	/**
	 * Start automatic demo mode.
	 */
	public static void startTask() {
		task.state = 0;		// Initialisierung des Task-Zustandes
		Task.install(task);	// Installieren des Tasks
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
		switch (state) {
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
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI1.out);
		System.out.println("Robi2: MotorDemo");
		
		task = new Robi2MotorDemo();	// erstellen des Task-Objekts
		task.period = 1000;	// Periodenlänge des Tasks in ms
	}
}
