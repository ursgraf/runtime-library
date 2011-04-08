package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.lang.Math;
import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.Task;

/**
 * This driver is used for the Robi2. Robi2 use basically the MPC555 processor.
 * Therefrom the TPUB, QADC, MPIOSM. The driver initialize all required modules
 * automatically.
 * 
 * @author Martin Zueger, Roger Millischer
 * 
 */
public class Robi2 extends Task {

	static Robi2 robi2BackgroundTask;

	/* Definitions */
	/*****************************************************************************/
	private static final boolean TPUA = true;
	private static final boolean TPUB = false;
	private static final boolean QADCA = true;
	private static final boolean QADCB = false;
	private static final boolean INPUT = false;
	private static final boolean OUTPUT = true;
	private static final boolean Mot_BridgeMode_SM = true;
	private static final boolean Mot_BridgeMode_LA = false;

	/* Pins */
	/*****************************************************************************/
	private static final int Mot1_PWMA_Pin = 0;
	private static final int Mot1_PWMB_Pin = 1;
	private static final int Mot1_EncA_Pin = 2;
	private static final int Mot1_EncB_Pin = 3;
	private static final int Mot2_EncA_Pin = 4;
	private static final int Mot2_EncB_Pin = 5;
	private static final int Mot2_PWMA_Pin = 6;
	private static final int Mot2_PWMB_Pin = 7;
	private static final int Mot_BridgeMode_Pin = 8;

	/* Constants */
	/*****************************************************************************/
	private static final float ts = 0.005f; // [s]
	private static final int tpr = 64 * 4; // ticks per rotation
	private static final int i = 17; // gear transmission ratio
	private static final float wheelDiameter = 0.0273f; // [m]
	private static final float scale = (float) Math.PI * wheelDiameter / (tpr * i);
	private static final int pwmPeriod = 1000000 / TPU_PWM.TpuTimeUnit;
	private static final float wheelDistance = 0.167f; // [m]

	/* Private variables */
	/*****************************************************************************/
	private static float s1 = 0, s2 = 0; // zurückgelegter Weg pro Rad
	private static float v1 = 0, v2 = 0; // linear speed of each wheel
	private static short prevPos1 = 0, prevPos2 = 0;
	private static long lastTime;

	private static float vy_R = 0, // speed in y direction rel. to the robot (vx_R is always 0)
			vx_E = 0, vy_E = 0, // speed in x and y direction rel. to the environment
			w_R = 0, // rotation speed of the robot
			phi = 0, // orientation of the robot
			x = 0, y = 0; // position of the robot
	
	private static boolean driveMode = Mot_BridgeMode_SM;

	/* Task */
	/*****************************************************************************/

	private Robi2() {
		this.period = (int) (1000 * ts);
		Task.install(this);
	}

	/**
	 * <p>
	 * Task method to determine the position.
	 * </p>
	 * <p>
	 * <b>Do not call this method.</b>
	 * </p>
	 * 
	 */
	public void action() {
		short actualPos, deltaPos;

		// calculate exact time increment
		long time = Kernel.time();
		long dt = time - lastTime;
		lastTime = time;

		// calculate distance traveled and speed for each wheel
		actualPos = TPU_FQD.getPosition(TPUB, Mot1_EncA_Pin);
		deltaPos = (short) (actualPos - prevPos1);
		prevPos1 = actualPos;
		s1 += deltaPos * scale;
		v1 = deltaPos * scale / dt;

		actualPos = TPU_FQD.getPosition(TPUB, Mot2_EncA_Pin);
		deltaPos = (short) (-(actualPos - prevPos2));
		prevPos2 = actualPos;
		s2 += deltaPos * scale;
		v2 = deltaPos * scale / dt;

		// speed in y direction of the robot
		vy_R = (v1 + v2) / 2;

		// rotation speed of the robot
		w_R = (v1 - v2) / wheelDistance;

		// orientation of the robot
		phi += w_R * dt;

		// speed relative to the environment
		vx_E = -vy_R * (float) Math.sin(phi);

		vy_E = vy_R * (float) Math.cos(phi);

		// positions relative to the environment
		x += vx_E * dt;
		y += vy_E * dt;
	}

	/* LEDs */
	/*****************************************************************************/

	/**
	 * Activate all three position LEDs (white)
	 */
	public static void activatePosLEDs() {
		for (int i = 12; i < 15; i++)
			TPU_DIO.set(true, i, false);
	}

	/**
	 * Deactivate all three position LEDs (white)
	 */
	public static void deactivatePosLEDs() {
		for (int i = 12; i < 15; i++)
			TPU_DIO.set(true, i, true);
	}

	/**
	 * Set the state for all three position LEDs (the white ones).
	 * This method is useful for toggling the positions LEDs.
	 * 
	 * @param state
	 *            the state of the LED (true = on, false = off)
	 */
	public static void setPosLEDs(boolean state) {
		for (int i = 12; i < 15; i++)
			TPU_DIO.set(true, i, !state);
	}

	/**
	 * <p>
	 * Set the state of head position LED (LED16).
	 * </p>
	 * 
	 * @param state
	 *            the state of LED16 (true = on, false = off)
	 */
	public static void setHeadPosLED(boolean state) {
		TPU_DIO.set(true, 12, !state);
	}

	/**
	 * <p>
	 * Get the state of head position LED (LED16).
	 * </p>
	 * 
	 * @return the state of LED16 (true = on, false = off)
	 */
	public static boolean getHeadPosLED() {
		return !TPU_DIO.get(true, 12);
	}

	/**
	 * <p>
	 * Set the state of the rear left position LED (LED17).
	 * </p>
	 * 
	 * @param state
	 *            the state of LED17 (true = on, false = off)
	 */
	public static void setLeftPosLED(boolean state) {
		TPU_DIO.set(true, 13, !state);
	}

	/**
	 * <p>
	 * Get the state of the rear left position LED (LED17).
	 * </p>
	 * 
	 * @return the state of LED17. (true = on, false = off)
	 */
	public static boolean getLeftPosLED() {
		return !TPU_DIO.get(true, 13);
	}

	/**
	 * <p>
	 * Set the state of the rear right position LED (LED18).
	 * </p>
	 * 
	 * @param state
	 *            the state of LED18 (true = on, false = off)
	 */
	public static void setRightPosLED(boolean state) {
		TPU_DIO.set(true, 14, !state);
	}

	/**
	 * <p>
	 * Get the state of the rear right position LED (LED18).
	 * </p>
	 * 
	 * @return the state of LED18 (true = off, false = on)
	 */
	public static boolean getRightPosLED() {
		return TPU_DIO.get(true, 14);
	}

	/**
	 * Set the state of the chosen pattern LED. Pattern LEDs are the red ones.
	 * <p><b>Examples:</b><ul>
	 * <li><code>setPatternLED(0,0,true)</code> turns LED11 on</li>
	 * <li><code>setPatternLED(3,2,false)</code> turns LED13 off</li>
	 * </ul></p>
	 * 
	 * @param r
	 *            row of the LED(range 0..3);
	 * @param c
	 *            column of the LED (range 0..2)
	 * @param state
	 *            the state of the LED (true = on, false = off)
	 */
	public static void setPatternLED(int r, int c, boolean state) {
		state = !state;
		switch (r) {
		case 0:
			switch (c) {
			case 0:
				TPU_DIO.set(true, 7, state);
				break;
			case 1:
				TPU_DIO.set(true, 6, state);
				break;
			case 2:
				TPU_DIO.set(true, 5, state);
				break;
			}
			break;
		case 1:
			switch (c) {
			case 0:
				TPU_DIO.set(true, 4, state);
				break;
			case 1:
				TPU_DIO.set(true, 3, state);
				break;
			case 2:
				TPU_DIO.set(true, 2, state);
				break;
			}
			break;
		case 2:
			switch (c) {
			case 0:
				TPU_DIO.set(true, 1, state);
				break;
			case 1:
				TPU_DIO.set(true, 0, state);
				break;
			case 2:
				TPU_DIO.set(true, 8, state);
				break;
			}
			break;
		case 3:
			switch (c) {
			case 0:
				TPU_DIO.set(true, 11, state);
				break;
			case 1:
				TPU_DIO.set(true, 10, state);
				break;
			case 2:
				TPU_DIO.set(true, 9, state);
				break;
			}
			break;
		}
	}

	/**
	 * Set the state of the chosen pattern LED. Pattern LEDs are the red ones.
	 * <p><b>Examples:</b><ul>
	 * <li><code>getPatternLED(0,0)</code> gets the state of LED11</li>
	 * <li><code>getPatternLED(3,2)</code> gets the state of LED13</li><br>
	 * </ul></p>
	 * 
	 * @param r
	 *            row of the LED (range 0..3);
	 * @param c
	 *            column of the LED (range 0..2)
	 * @return the state of the LED (true = on, false = off)
	 */
	public static boolean getPatternLED(int r, int c) {
		boolean state = false;
		switch (r) {
		case 0:
			switch (c) {
			case 0:
				state = TPU_DIO.get(true, 7);
				break;
			case 1:
				state = TPU_DIO.get(true, 6);
				break;
			case 2:
				state = TPU_DIO.get(true, 5);
				break;
			}
			break;
		case 1:
			switch (c) {
			case 0:
				state = TPU_DIO.get(true, 4);
				break;
			case 1:
				state = TPU_DIO.get(true, 3);
				break;
			case 2:
				state = TPU_DIO.get(true, 2);
				break;
			}
			break;
		case 2:
			switch (c) {
			case 0:
				state = TPU_DIO.get(true, 1);
				break;
			case 1:
				state = TPU_DIO.get(true, 0);
				break;
			case 2:
				state = TPU_DIO.get(true, 8);
				break;
			}
			break;
		case 3:
			switch (c) {
			case 0:
				state = TPU_DIO.get(true, 11);
				break;
			case 1:
				state = TPU_DIO.get(true, 10);
				break;
			case 2:
				state = TPU_DIO.get(true, 9);
				break;
			}
			break;
		}
		return !state;
	}

	/**
	 * Set the state of the center LED (the blue one).
	 * 
	 * @param state
	 *            the state of the blue center LED (true = on, false = off)
	 */
	public static void setCenterLED(boolean state) {
		TPU_DIO.set(true, 15, !state);
	}

	/**
	 * <p>
	 * Get the state of the center LED (the blue one).
	 * </p>
	 * 
	 * @return the state of the blue center LED (true = on, false = off)
	 */
	public static boolean getCenterLED() {
		return !TPU_DIO.get(true, 15);
	}

	/**
	 * Turn <u>all</u> LEDs off.
	 */
	public static void disableAllLEDs() {
		for (int i = 0; i < 16; i++)
			TPU_DIO.set(true, i, true);
	}

	/* Hex switch */
	/*****************************************************************************/

	/**
	 * Reads the position of the HEX-switch.
	 * 
	 * @return the position/value of the HEX-switch
	 */
	public static int getSwitchValue() {
		int value = 0;
		if (MPIOSM_DIO.get(12))
			value |= 0x01;
		if (MPIOSM_DIO.get(13))
			value |= 0x02;
		if (MPIOSM_DIO.get(14))
			value |= 0x04;
		if (MPIOSM_DIO.get(15))
			value |= 0x08;

		return value;
	}

	/* Distance sensors */
	/*****************************************************************************/

	/**
	 * Read the converted value of a choosen distance sensor.
	 * 
	 * @param sensor
	 *            the sensor which should be read (range 0..15)
	 * @return converted value (range 0..1023)
	 */
	public static int getDistSensorValue(int sensor) {
		return HLC1395Pulsed.read(sensor);
	}

	/* Drive */
	/*****************************************************************************/

	/**
	 * Returns the x position of the Robi2 referenced to the point of origin.<br>
	 * The points of origin is set, where the Robi2 stood at the start time of
	 * the program
	 * 
	 * @return the difference of the x-coordinate to the point of origin
	 */
	public static float getPosX() {
		return x;
	}

	/**
	 * Returns the x position of the Robi2 referenced to the point of origin.<br>
	 * The points of origin is set, where the Robi2 stood at the start time of
	 * the program
	 * 
	 * @return the difference of the y-coordinate to the point of origin
	 */
	public static float getPosY() {
		return y;
	}

	/**
	 * Returns the orientation of the Robi2 referenced to the orientation of the
	 * point of origin.<br>
	 * The points of origin is set, where the Robi2 stood at the start time of
	 * the program
	 * 
	 * @return the difference of the orientation to the point of origin
	 */
	public static float getOrientation() {
		return phi;
	}

	/**
	 * Returns the current speed of the Robi2 in X-direction.
	 * 
	 * @return speed
	 */
	public static float getSpeedX() {
		return vx_E;
	}

	/**
	 *Returns the current speed of the Robi2 in Y-direction.
	 * 
	 * @return speed
	 */
	public static float getSpeedY() {
		return vy_E;
	}

	/**
	 * Reset the position.<br>
	 * The current position is the new point of origin.
	 */
	public static void resetPos() {
		s1 = 0;
		s2 = 0;
		x = 0;
		y = 0;
	}

	private static int checkMaxSpeed(int speed) {
		if (speed > 100)
			return 100;
		if (speed < -100)
			return -100;
		return speed;
	}

	/**
	 * Switch the mode of the H-bridge for both motors to locked antiphase. This will stop both motors!
	 */
	public static void switchDriveModeToLA() {
		stopDrives();
		driveMode = Mot_BridgeMode_LA;
		TPU_DIO.set(TPUB, Mot_BridgeMode_Pin, Mot_BridgeMode_LA);
		TPU_PWM.update(TPUB, Mot1_PWMA_Pin, pwmPeriod, pwmPeriod/2); // PWM A -> 0% = full speed forward, 50% = stop, 100% = full speed backward
		TPU_PWM.update(TPUB, Mot1_PWMB_Pin, pwmPeriod, 0); // PWM B -> doesn't care
		TPU_PWM.update(TPUB, Mot2_PWMA_Pin, pwmPeriod, pwmPeriod/2); // PWM A -> 0% = full speed forward, 50% = stop, 100% = full speed backward
		TPU_PWM.update(TPUB, Mot2_PWMB_Pin, pwmPeriod, 0); // PWM B -> doesn't care
		}

	/**
	 * Switch the mode of the H-bridge for both motors to locked antiphase. This will stop both motors!
	 */
	public static void switchDriveModeToSM() {
		stopDrives();
		driveMode = Mot_BridgeMode_SM;
		TPU_DIO.set(TPUB, Mot_BridgeMode_Pin, Mot_BridgeMode_SM);
		TPU_PWM.update(TPUB, Mot1_PWMB_Pin, pwmPeriod, 0); // PWM B -> speed: if direction is hight, the speed have to be inverted (e.g. 5% -> 95%)
		TPU_PWM.update(TPUB, Mot1_PWMA_Pin, pwmPeriod, 0); // direction
		TPU_PWM.update(TPUB, Mot2_PWMB_Pin, pwmPeriod, 0); // PWM B -> speed: if direction is hight, the speed have to be inverted (e.g. 5% -> 95%)
		TPU_PWM.update(TPUB, Mot2_PWMA_Pin, pwmPeriod, 0); // direction
		}
	
	/**
	 * Set the speed of the left drive in percent.<br>
	 * The sign defines the direction.
	 * 
	 * @param speed
	 *            range -100..100
	 */
	public static void setLeftDriveSpeed(int speed) {
		speed = checkMaxSpeed(speed);
		if(driveMode) { // true means Mot_BridgeMode_SM
			if (speed >= 0) { // forward
				TPU_PWM.update(TPUB, Mot2_PWMA_Pin, pwmPeriod, 0); // direction
			} else { // backward
				TPU_PWM.update(TPUB, Mot2_PWMA_Pin, pwmPeriod, pwmPeriod); // direction
				speed = speed + 100;
			}
			TPU_PWM.update(TPUB, Mot2_PWMB_Pin, pwmPeriod, (speed * pwmPeriod) / 100); // speed
		}
		else { // false means Mot_BridgeMode_LA
			TPU_PWM.update(TPUB, Mot2_PWMA_Pin, pwmPeriod, (-speed * pwmPeriod / 200) + pwmPeriod / 2);
		}
	}

	/**
	 * Sets the speed of the right drive in percent.<br>
	 * The sign defines the direction.
	 * 
	 * @param speed
	 *            range -100..100
	 */
	public static void setRightDriveSpeed(int speed) {
		speed = checkMaxSpeed(speed);
		if(driveMode) { // true means Mot_BridgeMode_SM
			if (speed >= 0) { // forward
				TPU_PWM.update(TPUB, Mot1_PWMA_Pin, pwmPeriod, 0); // direction
			} else { // backward
				TPU_PWM.update(TPUB, Mot1_PWMA_Pin, pwmPeriod, pwmPeriod); // direction
				speed = speed + 100;
			}
			TPU_PWM.update(TPUB, Mot1_PWMB_Pin, pwmPeriod, (speed * pwmPeriod) / 100); // speed
		}
		else { // false means Mot_BridgeMode_LA
			TPU_PWM.update(TPUB, Mot1_PWMA_Pin, pwmPeriod, (-speed * pwmPeriod / 200) + pwmPeriod / 2);
		}
	}

	/**
	 * Set the speed and direction of both drives so that they rotates in the
	 * same direction.<br>
	 * The sign define the direction. A negative value for backwards and a
	 * positive value for forward.
	 * 
	 * @param speed
	 *            range -100..100
	 */
	public static void setDrivesSpeedEqual(int speed) {
			setRightDriveSpeed(-speed);
			setLeftDriveSpeed(speed);
	}

	/**
	 * Set the speed and direction of both drives so that they rotates
	 * antidormic.<br>
	 * The sign define the direction of the rotation of the Robi2.<br>
	 * A positive value turns clockwise, a negative value turns
	 * counterclockwise.
	 * 
	 * @param speed
	 *            range -100..100
	 */
	public static void setDrivesSpeedAntidormic(int speed) {
			setRightDriveSpeed(speed);
			setLeftDriveSpeed(speed);
	}

	/**
	 * Stop both drives
	 */
	public static void stopDrives() {
			setRightDriveSpeed(0);
			setLeftDriveSpeed(0);
	}

	/* Misc */
	/*****************************************************************************/

	/**
	 * Returns the voltage of the battery
	 * 
	 * @return voltage
	 */
	public static float getBatteryVoltage() {
		return 10f / 1023f * QADC_AIN.read(QADCB, 1);
		// return QADC.read(QADCB, 1);
	}

	/* Static constructor */
	/*****************************************************************************/

	static {

		// Initialize all LEDs
		for (int i = 0; i <= 15; i++) {
			TPU_DIO.init(true, i, true);
			TPU_DIO.set(true, i, true);
		}

		// Initialize I/Os for hex switch
		for (int i = 12; i < 16; i++)
			MPIOSM_DIO.init(i, false);

		// Initialize distance sensors
		HLC1395Pulsed.init(16, 0x00059876, 59);
		HLC1395Pulsed.start();

		// Initialize drive
		TPU_FQD.init(TPUB, Mot1_EncA_Pin);
		TPU_FQD.init(TPUB, Mot2_EncA_Pin);
		TPU_PWM.init(TPUB, Mot1_PWMA_Pin, pwmPeriod, 0);
		TPU_PWM.init(TPUB, Mot1_PWMB_Pin, pwmPeriod, 0);
		TPU_PWM.init(TPUB, Mot2_PWMA_Pin, pwmPeriod, 0);
		TPU_PWM.init(TPUB, Mot2_PWMB_Pin, pwmPeriod, 0);
		TPU_DIO.init(TPUB, Mot_BridgeMode_Pin, OUTPUT);
		TPU_DIO.set(TPUB, Mot_BridgeMode_Pin, Mot_BridgeMode_SM);

		// Initialize ADC for battery voltage messuring
		QADC_AIN.init(QADCB);

		// Install task for controller
		robi2BackgroundTask = new Robi2();
	}
}