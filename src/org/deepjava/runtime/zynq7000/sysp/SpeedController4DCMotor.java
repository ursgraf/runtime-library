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

package org.deepjava.runtime.zynq7000.sysp;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkDevice;
import org.deepjava.flink.subdevices.FlinkCounter;
import org.deepjava.flink.subdevices.FlinkPWM;
import org.deepjava.runtime.Kernel;

/**
 * Speed controller (PI control) for DC motor. <br>
 * This controller uses two pwm channels and operates in sign-magnitude mode. <br>
 * 
 *  <strong>IMPORTANT:</strong> The motor and the encoder have to be connected carefully. A positive speed control must lead to a positive speed reading.
 *  If this is not the case you have to change either the connections to the motor or the signals of the encoder (but not both!). 
 */
public class SpeedController4DCMotor implements FlinkDefinitions {
	
	private FlinkPWM pwm;
	public FlinkCounter enc;
	private int pwmChannel0, pwmChannel1, encChannel;
	
	private static final int pwmFreq = 20000;	// frequency of the pwm control signal in Hz
	private static final int fqd = 4;			// factor for fast quadrature decoding
	
	private float scale;						// scaling factor [rad/tick]
	private float b0, b1;						// controller coefficients
	private float umax;							// [V]
	private float desiredSpeed = 0;				// [1/s]
	private float prevControlValue = 0;			// [V]
	private long prevTime = 0;					// [us]
	private float dt;							// [s]
	private short prevPos; 						// [ticks]
	private int absPos;							// [ticks]
	private float speed = 0;					// [1/s]
	private float e_1 = 0;						// [1/s]
	private int period;							// period in pwm setting
	private boolean lock;						// locked-antiphase mode
	
	/**
	 * Create a new speed controller for a DC motor. The controller works in sign-magnitude mode using two PWM signals. 
	 * 
	 * @param ts task period in seconds [s]
	 * @param pwmChannel1 channel for the first PWM signal.
	 * @param pwmChannel2 channel for the second PWM signal.
	 * @param encChannel channel for the encoder signal. Connect both A and B to the associated pins.
	 * @param encTPR impulse/ticks per rotation of the encoder.
	 * @param umax maximum output voltage of set value.
	 * @param i gear transmission ratio.
	 * @param kp controller gain factor. For experimental evaluating the controller parameters, begin with kp = 1.
	 * @param tn time constant of the controller. For experimental evaluating the controller parameters, set tn to the mechanical time constant of your axis. If the motor has a gear it's assumed that the torque of inertia of the rotor is dominant. That means you can set tn equals to the mechanical time constant of your motor. 
	 */
	public SpeedController4DCMotor(float ts, int pwmChannel1, int pwmChannel2, int encChannel, int encTPR, float umax, float i, float kp, float tn) {
		this.scale = (float)((2 * Math.PI) / (encTPR * fqd * i));
		this.b0 = kp * (1f + ts / (2f * tn));
		this.b1 = kp * (ts / (2f * tn) - 1f);
		this.umax = umax;
		this.pwmChannel0 = pwmChannel1;
		this.pwmChannel1 = pwmChannel2;
		this.encChannel = encChannel;
		
		pwm = FlinkDevice.getPWM();
		enc = FlinkDevice.getCounter();

		// initialize PWM channels
		period = pwm.getBaseClock() / pwmFreq;
		pwm.setPeriod(pwmChannel1, period);
		pwm.setPeriod(pwmChannel2, period);
		pwm.setHighTime(pwmChannel1, 0);
		pwm.setHighTime(pwmChannel2, 0);
		
		// initialize FQD channels
		enc.reset();
	}

	/**
	 * Create a new speed controller for a DC motor. The controller works in locked-antiphase mode using a single PWM signal. 
	 * 
	 * @param ts task period in seconds [s]
	 * @param pwmChannel channel for the PWM signal.
	 * @param encChannel channel for the encoder signal. Connect both A and B to the associated pins.
	 * @param encTPR impulse/ticks per rotation of the encoder.
	 * @param umax maximum output voltage of set value.
	 * @param i gear transmission ratio.
	 * @param kp controller gain factor. For experimental evaluating the controller parameters, begin with kp = 1.
	 * @param tn time constant of the controller. For experimental evaluating the controller parameters, set tn to the mechanical time constant of your axis. If the motor has a gear it's assumed that the torque of inertia of the rotor is dominant. That means you can set tn equals to the mechanical time constant of your motor. 
	 */
	public SpeedController4DCMotor(float ts, int pwmChannel, int encChannel, int encTPR, float umax, float i, float kp, float tn) {
		this.scale = (float)((2 * Math.PI) / (encTPR * fqd * i));
		this.b0 = kp * (1f + ts / (2f * tn));
		this.b1 = kp * (ts / (2f * tn) - 1f);
		this.umax = umax;
		this.pwmChannel0 = pwmChannel;
		this.encChannel = encChannel;
		lock = true;
		
		pwm = FlinkDevice.getPWM();
		enc = FlinkDevice.getCounter();

		// initialize PWM channels
		period = pwm.getBaseClock() / pwmFreq;
		pwm.setPeriod(pwmChannel, period);
		pwm.setHighTime(pwmChannel, 0);
		
		// initialize FQD channels
		enc.reset();
	}

	/**
	 * Controller task method. Call this method periodically with the given period time (ts)!
	 */
	public void run() {
		long time = Kernel.timeNs();
		dt = (time - prevTime) * 1e-9f;
		prevTime = time;
		
		short actualPos = enc.getCount(encChannel);
		short deltaPos = (short)(actualPos - prevPos);
		prevPos = actualPos;
		absPos += deltaPos;
		speed = (deltaPos * scale) / dt;
		
		float e = desiredSpeed - speed;
		float controlValue = prevControlValue + b0 * e + b1 * e_1;
		
		// anti windup
        if (controlValue > umax) controlValue = umax;
        if (controlValue < -umax) controlValue = -umax;
        
		setPWM(controlValue / umax);	// update PWM
		e_1 = e;
		prevControlValue = controlValue;
	}
	
	/**
	 * Set desired speed.
	 * @param v desired speed in radian per second [1/s]
	 */
	public void setSpeed(float v) {
		this.desiredSpeed = v;
	}
	
	
	/** 
	 * Returns the current speed.
	 * @return current speed in radian per second [1/s]
	 */
	public float getSpeed() {
		return speed;
	}
	
	/** 
	 * Returns the current absolute position.
	 * @return absolute position in radian
	 */
	public float getPosition() {
		return absPos * scale;
	}
	
	private void setPWM(float dutyCycle) {
		if (lock) {
			pwm.setHighTime(pwmChannel0, (int)((dutyCycle + 1) / 2 * period)); // speed
		} else {
			if (dutyCycle >= 0) { // forward
				pwm.setHighTime(pwmChannel0, 0); // direction, set to 0
			} else { // backward
				pwm.setHighTime(pwmChannel0, period); // direction, set to 1
				dutyCycle = dutyCycle + 1;
			}
			pwm.setHighTime(pwmChannel1, (int)(dutyCycle * period)); // speed
		}
	}
	
}
