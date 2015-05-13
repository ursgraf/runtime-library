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

package ch.ntb.sysp.lib;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.mpc555.driver.TPU_FQD;
import ch.ntb.inf.deep.runtime.mpc555.driver.TPU_PWM;

/**
 * Speed controller (PI control) for DC motor. <br/>
 * This controller uses two channels of the time processor unit 
 * and operates in sign-magnitude mode.
 */
public class SpeedController4DCMotor {
	
	private int pwmChannelA, pwmChannelB;
	private boolean pwmTPUA;
	private int encChannelA;
	private boolean encTPUA;
	
	private static final int pwmPeriod = 50000 / TPU_PWM.tpuTimeBase;	// period time of the PWM signal as multiple of the TPU time base
	private static final int fqd = 4;			// factor for fast quadrature decoding
	
	private float scale;						// scaling factor [rad/tick]
	private float b0, b1;						// controller coefficients
	private float umax;							// [V]
	
	private float desiredSpeed = 0,				// [1/s]
		controlValue = 0,						// [V]
		prevControlValue = 0;					// [V]
	
	private long time = 0, lastTime = 0;		// [us]
	private float dt;							// [s]
	private short actualPos, deltaPos, prevPos; // [ticks]
	private int absPos;
	private float speed = 0;					// [1/s]
	private float e = 0, e_1 = 0;				// [1/s]
	
	/**
	 * Create a new speed controller for a DC motor.
	 * @param ts task period in seconds [s]
	 * @param pwmChannelA TPU channel for the first PWM signal. For the second PWM signal the channel of the first + 1 will be used.
	 * @param useTPUA4PWM Time processing unit to use for PWM signals: true for TPU-A and false for TPU-B.
	 * @param encChannelA TPU channel for the encoder signal A. For the signal B the channel of A + 1 will be used.
	 * @param useTPUA4Enc Time processing unit to use for FQD: true for TPU-A and false for TPU-B.
	 * @param encTPR impulse/ticks per rotation of the encoder.
	 * @param i gear transmission ratio.
	 * @param kp controller gain factor. For experimental evaluating the controller parameters, begin with kp = 1.
	 * @param tn time constant of the controller. For experimental evaluating the controller parameters, set tn to the mechanical time constant of your axis. If the motor has a gear it's assumed that the torque of inertia of the rotor is dominant. That means you can set tn equals to the mechanical time constant of your motor. 
	 */
	public SpeedController4DCMotor(float ts, int pwmChannelA, boolean useTPUA4PWM, int encChannelA, boolean useTPUA4Enc, int encTPR, float umax, float i, float kp, float tn) {
		// set parameters
		this.scale = (float)((2 * Math.PI) / (encTPR * fqd * i));
		this.b0 = kp * (1f + ts / (2f * tn));
		this.b1 = kp * (ts / (2f * tn) - 1f);
		this.umax = umax;
		
		// initialize PWM channels
		this.pwmChannelA = pwmChannelA;
		this.pwmChannelB = pwmChannelA + 1;
		this.pwmTPUA = useTPUA4PWM;
		TPU_PWM.init(useTPUA4PWM, pwmChannelA, pwmPeriod, 0);
		TPU_PWM.init(useTPUA4PWM, pwmChannelA + 1, pwmPeriod, 0);
		
		// initialize FQD channels
		this.encChannelA = encChannelA;
		this.encTPUA = useTPUA4Enc;
		TPU_FQD.init(useTPUA4Enc, encChannelA);
	}

	
	/**
	 * Controller task method. Call this method periodically with the given period time (ts)!
	 */
	public void run() {
		// calculate exact time increment
		time = Kernel.time();
		dt = (time - lastTime) * 1e-6f;
		lastTime = time;
		
		// Read encoder and calculate actual speed
		actualPos = TPU_FQD.getPosition(encTPUA, encChannelA);
		deltaPos = (short)(actualPos - prevPos);
		absPos += deltaPos;
		speed = (deltaPos * scale) / dt;
		
		// Calculate control value
		e = desiredSpeed - speed;
		controlValue = prevControlValue + b0 * e + b1 * e_1;
		
		//PARV: 11.1.2014
		//Anti Wind Up, so the duty cycle will always be  <= 100%
        if(controlValue > umax) {
               controlValue = umax;
        }
		
		// Update PWM
		setPWM(controlValue / umax);
		
		// Save actual values for the next round
		prevPos = actualPos;
		e_1 = e;
		prevControlValue = controlValue;
	}
	
	/**
	 * Set desired speed.
	 * @param v desired speed in radian per second [1/s]
	 */
	public void setDesiredSpeed(float v) {
		this.desiredSpeed = v;
	}
	
	
	/** Returns the current speed.
	 * @return current speed in radian per second [1/s]
	 */
	public float getActualSpeed() {
		return speed;
	}
	
	/** Returns the current absolute position.
	 * @return absolute position in radian
	 */
	public float getActualPosition() {
		return absPos * scale;
	}
	
	private static float limitDutyCycle(float d) {
		if (d > 1)
			return 1;
		if (d < -1)
			return -1;
		return d;
	}
	
	private void setPWM(float dutyCycle) {
		dutyCycle = limitDutyCycle(dutyCycle);

		if (dutyCycle >= 0) { // forward
			TPU_PWM.update(pwmTPUA, pwmChannelA, pwmPeriod, 0); // direction
		} else { // backward
			TPU_PWM.update(pwmTPUA, pwmChannelA, pwmPeriod, pwmPeriod); // direction
			dutyCycle = dutyCycle + 1;
		}
		TPU_PWM.update(pwmTPUA, pwmChannelB, pwmPeriod,	(int)(dutyCycle * pwmPeriod)); // speed
	}
	
}
