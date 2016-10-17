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

package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.lang.Math;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/* Changes:
 * 16.03.2012   NTB/UN  Fixed orientation
 * 02.09.2011	NTB/MZ	Bug fixes, anti wind-up implemented
 * 26.05.2011	NTB/MZ	controller type changed to PI, JavaDoc updated
 * 02.05.2011	NTB/MZ	bug fixes
 * 22.03.2011	NTB/RM	adapted to deep
 * 13.09.2010	NTB/MZ	motor controllers added
 * 20.08.2010	NTB/MZ	initial version
 */

/**
 * This driver is used for the Robi2. 
 * Robi2 is controlled by a MPC555 processor.
 */
public class Robi2 extends Task {
	
	/* Robi2 background task */
	/*****************************************************************************/
	static Robi2 robi2BackgroundTask;

	/* General definitions */
	/*****************************************************************************/
	@SuppressWarnings("unused")
	private static final boolean TPUA = true;				// use TPUA
	private static final boolean TPUB = false;				// use TPUB
	@SuppressWarnings("unused")
	private static final boolean QADCA = true;				// use QADCA
	private static final boolean QADCB = false;				// use QADCB
	@SuppressWarnings("unused")
	private static final boolean INPUT = false;				// use pin as input
	private static final boolean OUTPUT = true;				// use pin as output
	private static final boolean Mot_BridgeMode_SM = true;	// use H-bridge in signed magnitude mode

	/* Pin definitions */
	/*****************************************************************************/
	private static final int Mot1_PWMA_Pin = 0;				// Motor 1, PWM channel A
	private static final int Mot1_PWMB_Pin = 1;				// Motor 1, PWM channel B
	private static final int Mot1_EncA_Pin = 2;				// Motor 1, Encoder channel A
	@SuppressWarnings("unused")
	private static final int Mot1_EncB_Pin = 3;				// Motor 1, Encoder channel B
	private static final int Mot2_EncA_Pin = 4;				// Motor 2, Encoder channel A
	@SuppressWarnings("unused")
	private static final int Mot2_EncB_Pin = 5;				// Motor 2, Encoder channel B
	private static final int Mot2_PWMA_Pin = 6;				// Motor 2, PWM channel A
	private static final int Mot2_PWMB_Pin = 7;				// Motor 2, PWM channel B
	private static final int Mot_BridgeMode_Pin = 8;		// Motor controller, H bridge mode

	/* Constants */
	/*****************************************************************************/
	private static final float ts = 0.001f;					// Task period in seconds
	private static final int tpr = 64;						// Encoder ticks per rotation
	private static final int fqd = 4;						// FQD
	private static final int i = 17;						// Gear transmission ratio
	public static final float wheelDiameter = 0.0273f;		// Wheel diameter in meter
	public static final float wheelDistance = 0.167f;		// Wheel distance in meter
	private static final float scale = -(float)Math.PI * wheelDiameter / (tpr * fqd * i);
	private static final int pwmPeriod = 20000 / TPU_PWM.tpuTimeBase;

	private static final float k_P = 100f;
	private static final float t_I = 0.01f;

	public static final float v_max = 0.5f; // [m/s]
	private static final float u_max = 7.5f; // [V]
	private static final float u_min = -u_max; // [V]
	
	/* Private variables */
	/*****************************************************************************/
	@SuppressWarnings("unused")
	private static float s_R = 0, s_L = 0; // traveled distance of each wheel
	private static float v_R = 0, v_L = 0; // linear speed of each wheel
	private static short prevPos1 = 0, prevPos2 = 0;
	private static long time, lastTime; // [us]
	private static float dt; // [s]
	private static float w_k_L = 0, w_k_R = 0; // [m/s]
	private static float e_k_L = 0, e_k_R = 0, e_k1_L = 0, e_k1_R = 0; // [m/s]
	private static float u_k_L = 0, u_k_R = 0, u_Pk_L = 0, u_Pk_R = 0, u_Ik_L = 0, u_Ik_R = 0,
	u_Ik1_L = 0, u_Ik1_R = 0, u_Dk_L = 0, u_Dk_R = 0; // [m/s]
	private static float vy_R = 0,	// speed in y direction relative to the robot (vx_R is always 0) [m/s]
		vx_E = 0, vy_E = 0, 		// speed in x and y direction relative to the environment [m/s]
		w_R = 0,					// rotation speed of the robot [rad/s]
		phi = 0,					// orientation of the robot [rad]
		x = 0, y = 0;				// position of the robot [m]
	
	private static MPIOSM_DIO sw12, sw13, sw14, sw15;		// HEX-switch
	private static TPU_PWM mot1_PWMA, mot1_PWMB;			// pwm signals for motor 1
	private static TPU_PWM mot2_PWMA, mot2_PWMB;			// pwm signals for motor 2
	private static TPU_FQD mot1_Enc, mot2_Enc;				// encoder for motor 1 and 2
	private static TPU_DIO[] led;							// 16 led
	static HLC1395Pulsed distSense;							// distance sensors
	

	/* Background task */
	/*****************************************************************************/

	private Robi2() {
		this.period = (int)(1000 * ts);
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
		
		/* Odometry */
		short actualPos, deltaPos;

		// calculate exact time increment
		time = Kernel.time();
		dt = (time - lastTime) * 1e-6f;	// [s]
		lastTime = time;

		// calculate distance traveled and speed for each wheel
		actualPos = mot1_Enc.getPosition();
		deltaPos = (short)(actualPos - prevPos1);
		prevPos1 = actualPos;
		s_R += deltaPos * scale;
		v_R = deltaPos * scale / dt;

		actualPos = mot2_Enc.getPosition();
		deltaPos = (short)(actualPos - prevPos2);
		prevPos2 = actualPos;
		s_L += deltaPos * scale;
		v_L = deltaPos * scale / dt;

		// speed in y direction of the robot
		vy_R = (v_L - v_R) / 2;
		
		// rotation speed of the robot
		w_R = (v_R + v_L) / wheelDistance;

		// orientation of the robot
		phi -= w_R * dt;

		// speed relative to the environment
		vx_E = -vy_R * (float)Math.sin(phi);

		vy_E = vy_R * (float)Math.cos(phi);

		// positions relative to the environment
		x += vx_E * dt;
		y += vy_E * dt;
		
		/* Motor controllers */
		
		// control deviation
		e_k_L = w_k_L - v_L;
		e_k_R = w_k_R - v_R;
		
		// control value (P part only)
		u_Pk_L = k_P * e_k_L;
		u_Pk_R = k_P * e_k_R;
		
		// control value (I part only)
		u_Ik_L = u_Ik1_L + k_P * dt / t_I * e_k1_L;
		u_Ik_R = u_Ik1_R + k_P * dt / t_I * e_k1_R;
		
		// control value (P + I)
		u_k_L = u_Pk_L + u_Ik_L;
		u_k_R = u_Pk_R + u_Ik_R;
		
		// anti wind up
		if(u_k_L > u_max) u_Ik_L = u_max - u_Pk_L;
		else if(u_k_L < u_min) u_Ik_L = u_min - u_Pk_L; 
		
		if(u_k_R > u_max) u_Ik_R = u_max - u_Pk_R;
		else if( u_k_R < u_min) u_Ik_R = u_min - u_Pk_R;
		
		// control value (D part only
//		u_Dk_L = k_P / dt * (e_k_L - e_k1_L);
//		u_Dk_R = k_P / dt * (e_k_R - e_k1_R);
		u_Dk_L = 0;
		u_Dk_R = 0;
		
		// control value (P, I and D part)
		u_k_L = u_k_L + u_Dk_L;
		u_k_R = u_k_R + u_Dk_R;	
		
		// set PWM signal to control value
		setPwmForLeftMotor(u_k_L / u_max);
		setPwmForRightMotor(u_k_R  / u_max);
		
		// store necessary values for next loop
		e_k1_L = e_k_L;
		e_k1_R = e_k_R;
		u_Ik1_L = u_Ik_L;
		u_Ik1_R = u_Ik_R;
	}
	
	/* LEDs */
	/*****************************************************************************/

	/**
	 * Enable all three position LEDs (white)
	 */
	public static void activatePosLEDs() {
		for (int i = 12; i < 15; i++)
			led[i].set(false);
	}

	/**
	 * Disable all three position LEDs (white)
	 */
	public static void deactivatePosLEDs() {
		for (int i = 12; i < 15; i++)
			led[i].set(true);
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
			led[i].set(!state);
	}

	/**
	 * Set the state of head position LED (LED16).
	 * 
	 * @param state
	 *            the state of LED16 (true = on, false = off)
	 */
	public static void setHeadPosLED(boolean state) {
		led[12].set(!state);
	}

	/**
	 * Get the state of head position LED (LED16).
	 * 
	 * @return the state of LED16 (true = on, false = off)
	 */
	public static boolean getHeadPosLED() {
		return !led[12].get();
	}

	/**
	 * Set the state of the rear left position LED (LED17).
	 * 
	 * @param state
	 *            the state of LED17 (true = on, false = off)
	 */
	public static void setLeftPosLED(boolean state) {
		led[13].set(!state);
	}

	/**
	 * Get the state of the rear left position LED (LED17).
	 * 
	 * @return the state of LED17. (true = on, false = off)
	 */
	public static boolean getLeftPosLED() {
		return !led[13].get();
	}

	/**
	 * Set the state of the rear right position LED (LED18).
	 * 
	 * @param state
	 *            the state of LED18 (true = on, false = off)
	 */
	public static void setRightPosLED(boolean state) {
		led[14].set(!state);
	}

	/**
	 * Get the state of the rear right position LED (LED18).
	 * 
	 * @return the state of LED18 (true = off, false = on)
	 */
	public static boolean getRightPosLED() {
		return led[14].get();
	}

	/**
	 * Set the state of the chosen pattern LED. Pattern LEDs are the red ones.
	 * <b>Examples:</b><ul>
	 * <li><code>setPatternLED(0,0,true)</code> turns LED11 on</li>
	 * <li><code>setPatternLED(3,2,false)</code> turns LED13 off</li>
	 * </ul>
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
				led[7].set(state);
				break;
			case 1:
				led[6].set(state);
				break;
			case 2:
				led[5].set(state);
				break;
			}
			break;
		case 1:
			switch (c) {
			case 0:
				led[4].set(state);
				break;
			case 1:
				led[3].set(state);
				break;
			case 2:
				led[2].set(state);
				break;
			}
			break;
		case 2:
			switch (c) {
			case 0:
				led[1].set(state);
				break;
			case 1:
				led[0].set(state);
				break;
			case 2:
				led[8].set(state);
				break;
			}
			break;
		case 3:
			switch (c) {
			case 0:
				led[11].set(state);
				break;
			case 1:
				led[10].set(state);
				break;
			case 2:
				led[9].set(state);
				break;
			}
			break;
		}
	}

	/**
	 * Set the state of the chosen pattern LED. Pattern LEDs are the red ones.
	 * <b>Examples:</b><ul>
	 * <li><code>getPatternLED(0,0)</code> gets the state of LED11</li>
	 * <li><code>getPatternLED(3,2)</code> gets the state of LED13</li>
	 * </ul>
	 * 
	 * @param r		row of the LED (range 0..3);
	 * @param c		column of the LED (range 0..2)
	 * @return		the state of the LED (true = on, false = off)
	 */
	public static boolean getPatternLED(int r, int c) {
		boolean state = false;
		switch (r) {
		case 0:
			switch (c) {
			case 0:
				state = led[7].get();
				break;
			case 1:
				state = led[6].get();
				break;
			case 2:
				state = led[5].get();
				break;
			}
			break;
		case 1:
			switch (c) {
			case 0:
				state = led[4].get();
				break;
			case 1:
				state = led[3].get();
				break;
			case 2:
				state = led[2].get();
				break;
			}
			break;
		case 2:
			switch (c) {
			case 0:
				state = led[1].get();
				break;
			case 1:
				state = led[0].get();
				break;
			case 2:
				state = led[8].get();
				break;
			}
			break;
		case 3:
			switch (c) {
			case 0:
				state = led[11].get();
				break;
			case 1:
				state = led[10].get();
				break;
			case 2:
				state = led[9].get();
				break;
			}
			break;
		}
		return !state;
	}

	/**
	 * Set the state of the center LED (the blue one).
	 * 
	 * @param state		the state of the blue center LED (true = on, false = off)
	 */
	public static void setCenterLED(boolean state) {
		led[15].set(!state);
	}

	/**
	 * Get the state of the center LED (the blue one).
	 * 
	 * @return the state of the blue center LED (true = on, false = off)
	 */
	public static boolean getCenterLED() {
		return !led[15].get();
	}

	public static void enableAllLEDs() {
		for (int i = 0; i < 16; i++)
			led[i].set(false);
	}
	
	/**
	 * Turn <u>all</u> LEDs off.
	 */
	public static void disableAllLEDs() {
		for (int i = 0; i < 16; i++)
			led[i].set(true);
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
		if (sw12.get())	value |= 0x01;
		if (sw13.get()) value |= 0x02;
		if (sw14.get()) value |= 0x04;
		if (sw15.get()) value |= 0x08;
		return value;
	}

	/* Distance sensors */
	/*****************************************************************************/

	/**
	 * Read the converted value of a chosen distance sensor.
	 * 
	 * @param sensor	the sensor which should be read (range 0..15)
	 * @return			converted value (range 0..1023), -1 = failed.
	 */
	public static int getDistSensorValue(int sensor) {
		if(sensor >= 0 && sensor < 16)	return distSense.read(sensor);
		return -1;
	}

	/* Drive and odometry */
	/*****************************************************************************/

	/**
	 * Returns the x position of the Robi2 referenced to the point of origin.<br>
	 * The point of origin is set, where the Robi2 stood at the start time of
	 * the program or resetPos has been called.
	 * 
	 * @return the difference of the x-coordinate to the point of origin
	 */
	public static float getPosX() {
		return x;
	}

	/**
	 * Returns the x position of the Robi2 referenced to the point of origin.<br>
	 * The point of origin is set, where the Robi2 stood at the start time of
	 * the program or resetPos has been called.
	 * 
	 * @return the difference of the y-coordinate to the point of origin
	 */
	public static float getPosY() {
		return y;
	}

	/**
	 * Returns the orientation of the Robi2 referenced to the orientation of the
	 * point of origin.<br>
	 * The point of origin is set, where the Robi2 stood at the start time of
	 * the program or resetPos has been called.
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
		s_R = 0;
		s_L = 0;
		x = 0;
		y = 0;
		phi = 0.0f;
	}
	
	/**
	 * Sets the speed of the left drive in percent.<br>
	 * The sign define the direction. A negative value for backwards and a
	 * positive value for forward.
	 * 
	 * @param speed		range: -100..100
	 */
	public static void setLeftDriveSpeed(int speed) {
		w_k_L = speed * v_max / 100;
	}
	
	/**
	 * Sets the speed of the left drive in meter per second [m/s].<br>
	 * The sign define the direction. A negative value for backwards and a
	 * positive value for forward.
	 * @param speed		range: -v_max...v_max
	 */
	public static void setLeftDriveSpeed(float speed) {
		w_k_L = speed;
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
		w_k_R = speed * v_max / 100;
	}

	/**
	 * Sets the speed of the right drive in meter per second [m/s].<br>
	 * The sign define the direction. A negative value for backwards and a
	 * positive value for forward.
	 * @param speed		range: -v_max...v_max
	 */
	public static void setRightDriveSpeed(float speed) {
		w_k_R = speed;
	}

	/**
	 * Set the speed and direction of both drives so that they rotates in the
	 * same direction.<br>
	 * The sign define the direction. A negative value for backwards and a
	 * positive value for forward.
	 * 
	 * @param speed range: -100..100
	 */
	public static void drive(int speed) {
			setRightDriveSpeed(-speed);
			setLeftDriveSpeed(speed);
	}
	
	
	/**
	 * Set the speed and direction of both drives so that they rotates in the same direction.
	 * The sign define the direction. A negative value for backwards and a positive value for forward.
	 * @param speed range: -100..100
	 */
	@Deprecated
	public static void setDriveSpeedEqual(int speed) {
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
	 * @param speed range: -100..100
	 */
	public static void turn(int speed) {
			setRightDriveSpeed(speed);
			setLeftDriveSpeed(speed);
	}
	
	
	/**
	 * Set the speed and direction of both drives so that they rotates antidormic.
	 * The sign define the direction of the rotation of the Robi2.
	 * A positive value turns clockwise, a negative value turns counterclockwise.
	 * @param speed range: -100..100
	 */
	@Deprecated
	public static void setDrivesSpeedAntidormic(int speed) {
		setRightDriveSpeed(speed);
		setLeftDriveSpeed(speed);
	}

	
	/**
	 * Stop both drives
	 */
	public static void stop() {
			setRightDriveSpeed(0);
			setLeftDriveSpeed(0);
	}
	
	/**
	 * Update the PWM signal for the left motor to a given duty cycle.
	 * @param dutyCycle		the new duty cycle (-1..1). 
	 */
	private static void setPwmForLeftMotor(float dutyCycle) {
		dutyCycle = limitDutyCycle(dutyCycle);
		if (dutyCycle >= 0) { // forward
			mot2_PWMA.update(pwmPeriod, 0); // direction
		} else { // backward
			mot2_PWMA.update(pwmPeriod, pwmPeriod); // direction
			dutyCycle = dutyCycle + 1;
		}
		mot2_PWMB.update(pwmPeriod, (int)(dutyCycle * pwmPeriod)); // speed
	}
	
	/**
	 * Update the PWM signal for the right motor to a given duty cycle.
	 * @param dutyCycle		the new duty cycle (-1..1). 
	 */
	private static void setPwmForRightMotor(float dutyCycle) {
		dutyCycle = limitDutyCycle(dutyCycle);

		if (dutyCycle >= 0) { // forward
			mot1_PWMA.update(pwmPeriod, 0); // direction
		} else { // backward
			mot1_PWMA.update(pwmPeriod, pwmPeriod); // direction
			dutyCycle = dutyCycle + 1;
		}
		mot1_PWMB.update(pwmPeriod, (int)(dutyCycle * pwmPeriod)); // speed

	}
	
	/**
	 * Limit the duty cycle to allowed values.
	 * @param d	duty cycle to limit
	 * @return	limited duty cycle
	 */
	private static float limitDutyCycle(float d) {
		if (d > 1)
			return 1;
		if (d < -1)
			return -1;
		return d;
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
	}

	/* Static constructor */
	/*****************************************************************************/
	static {

		// Initialize all LEDs
		led = new TPU_DIO[16];
		for (int i = 0; i <= 15; i++) {
			led[i] = new TPU_DIO(true, i, true);
			led[i].set(true);
		}

		// Initialize I/Os for hex switch
		sw12 = new MPIOSM_DIO(12, false);
		sw13 = new MPIOSM_DIO(13, false);
		sw14 = new MPIOSM_DIO(14, false);
		sw15 = new MPIOSM_DIO(15, false);

		// Initialize distance sensors
		distSense = HLC1395Pulsed.getInstance();
		distSense.init(16, 0x00059876, 59);
		distSense.start();

		// Initialize drive
		mot1_Enc = new TPU_FQD(TPUB, Mot1_EncA_Pin);
		mot2_Enc = new TPU_FQD(TPUB, Mot2_EncA_Pin);
		mot1_PWMA = new TPU_PWM(TPUB, Mot1_PWMA_Pin, pwmPeriod, 0);
		mot1_PWMB = new TPU_PWM(TPUB, Mot1_PWMB_Pin, pwmPeriod, 0);
		mot2_PWMA = new TPU_PWM(TPUB, Mot2_PWMA_Pin, pwmPeriod, 0);
		mot2_PWMB = new TPU_PWM(TPUB, Mot2_PWMB_Pin, pwmPeriod, 0);
		TPU_DIO out = new TPU_DIO(TPUB, Mot_BridgeMode_Pin, OUTPUT);
		out.set(Mot_BridgeMode_SM);

		// Initialize ADC for battery voltage measuring
		QADC_AIN.init(QADCB);

		// Install task for controller
		robi2BackgroundTask = new Robi2();
	}
}
