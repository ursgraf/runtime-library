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
	
	/* Robi2 background task */
	/*****************************************************************************/
	static Robi2 robi2BackgroundTask;

	/* General definitions */
	/*****************************************************************************/
	private static final boolean TPUA = true;				// use TPUA
	private static final boolean TPUB = false;				// use TPUB
	private static final boolean QADCA = true;				// use QADCA
	private static final boolean QADCB = false;				// use QADCB
	private static final boolean INPUT = false;				// use pin as input
	private static final boolean OUTPUT = true;				// use pin as output
	private static final boolean Mot_BridgeMode_SM = true;	// use H-bridge in signed magnitude mode

	/* Pin definitions */
	/*****************************************************************************/
	private static final int Mot1_PWMA_Pin = 0;				// Motor 1, PWM channel A
	private static final int Mot1_PWMB_Pin = 1;				// Motor 1, PWM channel B
	private static final int Mot1_EncA_Pin = 2;				// Motor 1, Encoder channel A
	private static final int Mot1_EncB_Pin = 3;				// Motor 1, Encoder channel B
	private static final int Mot2_EncA_Pin = 4;				// Motor 2, Encoder channel A
	private static final int Mot2_EncB_Pin = 5;				// Motor 2, Encoder channel B
	private static final int Mot2_PWMA_Pin = 6;				// Motor 2, PWM channel A
	private static final int Mot2_PWMB_Pin = 7;				// Motor 2, PWM channel B
	private static final int Mot_BridgeMode_Pin = 8;		// Motor controller, H bridge mode

	/* Constants */
	/*****************************************************************************/
	private static final float ts = 0.001f;					// Task period in seconds
	private static final int tpr = 64;						// Encoder ticks per rotation
	private static final int fqd = 4;						// FQD
	private static final int i = 17;						// gear transmission ratio
	private static final float wheelDiameter = 0.0273f;		// Wheel diameter in meter
	private static final float wheelDistance = 0.167f;		// Wheel distance in meter
	private static final float scale = (float) Math.PI * wheelDiameter / (tpr * fqd * i);
	private static final int pwmPeriod = 1000000 / TPU_PWM.TpuTimeUnit;

	private static final float kp = 10f;
	private static final float tn = 0.02f;
	private static final float b0 = kp * (1f + ts / (2f * tn));
	private static final float b1 = kp * (ts / (2f * tn) - 1f);

	public static final float v_max = 0.55f; // [m/s]
	
	/* Private variables */
	/*****************************************************************************/
	private static float s1 = 0, s2 = 0; // zurückgelegter Weg pro Rad
	private static float v1 = 0, v2 = 0; // linear speed of each wheel
	private static short prevPos1 = 0, prevPos2 = 0;
	private static long lastTime;
	private static float desiredSpeedLeft = 0, desiredSpeedRight = 0; // [m/s]
	private static float ev1 = 0, ev2 = 0, ev1old = 0, ev2old = 0;
	private static float cv1 = 0, cv2 = 0, cv1old = 0, cv2old = 0; 
	private static float vy_R = 0,	// speed in y direction rel. to the robot (vx_R is always 0)
		vx_E = 0, vy_E = 0, 		// speed in x and y direction rel. to the environment
		w_R = 0,					// rotation speed of the robot
		phi = 0,					// orientation of the robot
		x = 0, y = 0;				// position of the robot


	private static int counter = 0;
	
	/* Background task */
	/*****************************************************************************/

	private Robi2() {
		this.period = (int) (1000 * ts);
		Task.install(this);
	}

	/**
	 * Background Task.
	 * <p>
	 * <b>Do not call this method!</b>
	 * </p>
	 * 
	 */
	public void action() {
		
		/* Odometrie */
		short actualPos, deltaPos;

		// calculate exact time increment
		long time = Kernel.time();
		float dt = (time - lastTime) * 1e-6f;
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
		vx_E = -vy_R * (float)Math.sin(phi);

		vy_E = vy_R * (float)Math.cos(phi);

		// positions relative to the environment
		x += vx_E * dt;
		y += vy_E * dt;
		
//		if(counter > 10) {
//			System.out.println(v1);
//			counter = 0;
//		}
//		counter++;
		
		/* Motor controllers */
		ev1old = ev1;
		ev2old = ev2;
		ev1 = desiredSpeedRight - (-v1);
		ev2 = desiredSpeedLeft - v2;
		
		cv1 = cv1old + b0 * ev1 + b1 * ev1old;
		cv2 = cv2old + b0 * ev2 + b1 * ev2old;
		
		
		setPwmForRightMotor((int)(cv1 * 100 / v_max));
		setPwmForLeftMotor((int)(cv2 * 100 / v_max));
		
		cv1old = cv1;
		cv2old = cv2;
		
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
	 * Returns the current speed of the Robi in his drive direction. The given value is in meter per second [m/s].
	 * 
	 *@return speed in meter per second [m/s]   	 
	 */   
	public static float getRobiSpeed() {
    return vy_R;
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

	private static void setPwmForLeftMotor(int dutyCycle) {
		dutyCycle = checkDutyCycle(dutyCycle);
		if (dutyCycle >= 0) { // forward
			TPU_PWM.update(TPUB, Mot2_PWMA_Pin, pwmPeriod, 0); // direction
		} else { // backward
			TPU_PWM.update(TPUB, Mot2_PWMA_Pin, pwmPeriod, pwmPeriod); // direction
			dutyCycle = dutyCycle + 100;
		}
		TPU_PWM.update(TPUB, Mot2_PWMB_Pin, pwmPeriod,
				(dutyCycle * pwmPeriod) / 100); // speed
	}
	
	private static void setPwmForRightMotor(int dutyCycle) {
		dutyCycle = checkDutyCycle(dutyCycle);

		if (dutyCycle >= 0) { // forward
			TPU_PWM.update(TPUB, Mot1_PWMA_Pin, pwmPeriod, 0); // direction
		} else { // backward
			TPU_PWM.update(TPUB, Mot1_PWMA_Pin, pwmPeriod, pwmPeriod); // direction
			dutyCycle = dutyCycle + 100;
		}
		TPU_PWM.update(TPUB, Mot1_PWMB_Pin, pwmPeriod,
				(dutyCycle * pwmPeriod) / 100); // speed

	}
	
	/**
	 * Sets the speed of the left drive in percent.<br>
	 * The sign define the direction. A negative value for backwards and a
	 * positive value for forward.
	 * 
	 * @param speed
	 *            range -100..100
	 */
	public static void setLeftDriveSpeed(int speed) {
		desiredSpeedLeft = speed * v_max / 100;
	}
	
	public static void setLeftDriveSpeed(float speed) {
		desiredSpeedLeft = speed;
	}
	
	public static void setRightDriveSpeed(float speed) {
		desiredSpeedRight = speed;
	}

	/**
	 * Sets the speed of the right drive in percent.<br>
	 * The sign define the direction. A negative value for backwards and a
	 * positive value for forward.
	 * 
	 * @param speed
	 *            range -100..100
	 */
	public static void setRightDriveSpeed(int speed) {
		desiredSpeedRight = speed * v_max / 100;
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

	private static int checkDutyCycle(int speed) {
		if (speed > 100)
			return 100;
		if (speed < -100)
			return -100;
		return speed;
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