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

import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 26.05.11	NTB/MILR	drive ramp by start and stop
 * 14.04.09 NTB/SP		Method desiredPosition removed
 * 24.02.09 NTB/SP		TPU_A position failure corrected
 * 06.03.08 NTB/SP		assigned to java
 * 18.05.06	NTB/HS		stub creation
 */
/**
 * Step motor driver on TPU-A or TPU-B.<br>
 * Full step and half step modes are possible.<br>
 * Full step requires 2 pins on the TPU. The pins deliver A and B signals. These 
 * signals have to be externally inverted to drive the coils of the motor (A, A', B, B').
 * Half step uses 4 pins on the TPU.<br>
 * When starting a motor the signals provide a ramping up to the final speed. 
 * When stopping they are ramping down to zero. A ramp will also be used when the 
 * stepping speed changes.<br> 
 */
public class StepMotor implements IntbMpc555HB {

	int channel;
	boolean fullStep;
	int diff;
	int openSteps;

	/**
	 * Creates a step motor driver.
	 * 
	 * @param tpuA
	 *            <code>true</code> uses TPU-A. <code>false</code> uses TPU-B.
	 * @param fullStep
	 *            <code>true</code>: full step mode. <code>false</code>: half step mode.
	 * @param stepPeriod
	 *            Maximum stepping speed in \u00b5s (stepPeriod &lt;= 6553)
	 * @param channel
	 *            First pin on TPU. For full step channel and channel+1 pin will be used.
	 *            For half step channel, channel+1, channel+2 and channel+3 will be used.
	 *            Accordingly for full step channel is in the range of 0..14 while for half
	 *            step the range is between 0..12.
	 */
	public StepMotor(boolean tpuA, boolean fullStep, int stepPeriod, int channel) {
		this.channel = channel;
		this.fullStep = fullStep;
		int shiftl, reg;
		short s;

		if (tpuA) {diff = 0; TPUA.init();}
		else {diff = TPUMCR_B - TPUMCR_A; TPUB.init();}

		int cycle;
		if (diff == 0) cycle = TPUA.getCycleTime();
		else cycle = TPUB.getCycleTime();
		int stepPeriodTCR1 = stepPeriod * 1000 / cycle;

		//Disable interrupts for first channel
		s = US.GET2(CIER_A + diff);
		s &= ~(1 << channel);
		US.PUT2(CIER_A + diff, s);

		//Disable the TPU channels by clearing the priority bits
		//first channel
		shiftl = (channel % 8) * 2;
		reg = CPR1_A + diff - (channel / 8) * 2;
		s = US.GET2(reg);
		s &= ~(3 << shiftl);
		US.PUT2(reg, s);
		//second channel
		shiftl = ((channel + 1) % 8) * 2;
		reg = CPR1_A + diff - ((channel + 1) / 8) * 2;
		s = US.GET2(reg);
		s &= ~(3 << shiftl);
		US.PUT2(reg, s);
		if(!fullStep){
			//second channel
			shiftl = ((channel + 2) % 8) * 2;
			reg = CPR1_A + diff - ((channel + 2) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			US.PUT2(reg, s);
			//second channel
			shiftl = ((channel + 3) % 8) * 2;
			reg = CPR1_A + diff - ((channel + 3) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			US.PUT2(reg, s);
		}

		//function code (D) for stepper
		//first channel
		shiftl = ((channel % 4) * 4);
		reg = CFSR3_A + diff - (channel / 4) * 2;
		s = US.GET2(reg);
		s &= ~(0xF << shiftl);
		s |= (0xD << shiftl);
		US.PUT2(reg,s);
		//second channel
		shiftl = (((channel + 1) % 4) * 4);
		reg = CFSR3_A + diff - ((channel + 1) / 4) * 2;
		s = US.GET2(reg);
		s &= ~(0xF << shiftl);
		s |= (0xD << shiftl);
		US.PUT2(reg,s);
		if (!fullStep) {
			//third channel
			shiftl = (((channel + 2) % 4) * 4);
			reg = CFSR3_A + diff - ((channel + 2) / 4) * 2;
			s = US.GET2(reg);
			s &= ~(0xF << shiftl);
			s |= (0xD << shiftl);
			US.PUT2(reg,s);
			//fourth channel
			shiftl = (((channel + 3) % 4) * 4);
			reg = CFSR3_A + diff - ((channel + 3) / 4) * 2;
			s = US.GET2(reg);
			s &= ~(0xF << shiftl);
			s |= (0xD << shiftl);
			US.PUT2(reg,s);
		}

		// Acceleration table
		US.PUT2(TPURAM0_A + diff + 0x10 * (channel + 1), 240 * 0x100 + 250);
		US.PUT2(TPURAM0_A + diff + 0x10 * (channel + 1) + 2, 205 * 0x100 + 225);
		US.PUT2(TPURAM0_A + diff + 0x10 * (channel + 1) + 4, 165 * 0x100 + 185);
		US.PUT2(TPURAM0_A + diff + 0x10 * (channel + 1) + 6, 125 * 0x100 + 145);
		US.PUT2(TPURAM0_A + diff + 0x10 * (channel + 1) + 8, 90 * 0x100 + 105);
		US.PUT2(TPURAM0_A + diff + 0x10 * (channel + 1) + 0xA, 70 * 0x100 + 80);

		//Current position, first channel
		US.PUT2(TPURAM0_A + diff + 0x10 * channel + 2,0x0);
		s = US.GET2(TPURAM0_A + diff + 0x10 * channel + 2);
		US.PUT2(TPURAM0_A + diff + 0x10 * channel, s);

		//Acceleration table size (12) and initializing table index
		US.PUT2(TPURAM0_A + diff + 0x10 * channel + 4, 0xC00);
		//Slew period and initializeng bit s = 0 (bit 0 = 0)
		US.PUT2(TPURAM0_A + diff + 0x10 * channel + 6, stepPeriodTCR1 *2);
		if (fullStep) {
			//Start period and bit a= 0 => two channel mode (bit 0 = 0)
			US.PUT2(TPURAM0_A + diff + 0x10 * channel + 8,4 * stepPeriodTCR1 * 2);

			//Pin sequence
			US.PUT2(TPURAM0_A + diff + 0x10 * channel + 0xA, 0x9999);

			//Operating mode, only first channel
			//Local table mode (no spare TPU channels), rotate pin sequence once => %00
			shiftl = (channel % 8) * 2;
			reg = HSQR1_A + diff - (channel / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			US.PUT2(reg, s);

			//Request initialization (Host Service Request) both channels
			//For pin pattern 0x9999, first channel = %10 (pin high), second channel = %10 (pin high)
			//first channel
			shiftl = (channel % 8) * 2;
			reg = HSRR1_A + diff - (channel / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (2 << shiftl);
			US.PUT2(reg, s);
			//second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = HSRR1_A + diff - ((channel + 1) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (2 << shiftl);
			US.PUT2(reg, s);
		} else { // half step
			//Start period and bit a = 1 => four channel mode (bit 0 = 1
			US.PUT2(TPURAM0_A + diff + 0x10 * channel + 8,4 * stepPeriodTCR1 * 2 + 1);

			//Pin sequence
			US.PUT2(TPURAM0_A + diff + 0x10 * channel + 0xA, 0xE0E0);

			//Operating mode, only first channel
			//Local table mode (no spare TPU channels), rotate pin sequence twice => %10
			shiftl = (channel % 8) * 2;
			reg = HSQR1_A + diff - (channel / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (2 << shiftl);
			US.PUT2(reg, s);

			//Request initialization (Host Service Request) both channels
			//For pin pattern 0x0E0E0, first channel = %10 (pin high), second channel = %01 (pin low)
			//third channel = %01 (pin low) fourth channel = %10 (pin high)
			//first channel
			shiftl = (channel % 8) * 2;
			reg = HSRR1_A + diff - (channel / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (2 << shiftl);
			US.PUT2(reg, s);
			//second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = HSRR1_A + diff - ((channel + 1) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(reg, s);
			//third channel
			shiftl = ((channel + 2) % 8) * 2;
			reg = HSRR1_A + diff - ((channel + 2) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(reg, s);
			//fourth channel
			shiftl = ((channel + 3) % 8) * 2;
			reg = HSRR1_A + diff - ((channel + 3) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (2 << shiftl);
			US.PUT2(reg, s);
		}

		//Set priority low
		//First channel
		shiftl = (channel % 8) * 2;
		reg = CPR1_A + diff - (channel / 8) * 2;
		s = US.GET2(reg);
		s &= ~(3 << shiftl);
		s |= (1 << shiftl);
		US.PUT2(reg, s);
		//Second channel
		shiftl = ((channel + 1) % 8) * 2;
		reg = CPR1_A + diff - ((channel + 1)  / 8) * 2;
		s = US.GET2(reg);
		s &= ~(3 << shiftl);
		s |= (1 << shiftl);
		US.PUT2(reg, s);

		if (!fullStep) {
			//Third channel
			shiftl = ((channel + 2) % 8) * 2;
			reg = CPR1_A + diff - ((channel + 2)  / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(reg, s);
			//Fourth channel
			shiftl = ((channel + 3) % 8) * 2;
			reg = CPR1_A + diff - ((channel + 3)  / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(reg, s);
		}
	}

	/**
	 * Moves the motor for <code>steps</code>.
	 * 
	 * @param steps
	 *            Number of steps. A negative value moves the motor in the opposite direction.
	 */
	public void move(int steps) {
		int shiftl, reg;
		short s;
		//Get current position
		short pos = US.GET2(TPURAM0_A + diff + 0x10 * channel + 2);
		//Set desired position
		US.PUT2(TPURAM0_A + diff  + 0x10 * channel,(short) pos + steps);

		//Move Request (Host Service Request) (Master only)
		shiftl = (channel % 8) * 2;
		reg = HSRR1_A + diff  - (channel / 8) * 2;
		s = US.GET2(reg);
		s |= (3 << shiftl);
		US.PUT2(reg,s);
	}

	/**
	 * Starts the step motor.<br>
	 * Use this method solely to start the motor after it has been stopped with <code>stop(...)</code>.
	 */
	public void start() {
		int shiftl,reg;
		short s;
		//Set priority low
		//first channel
		shiftl = (channel % 8) * 2;
		reg = CPR1_A + diff - (channel / 8) * 2;
		s = US.GET2(reg);
		s &= ~(3 << shiftl);
		s |= (1 << shiftl);
		US.PUT2(reg, s);
		//second channel
		shiftl = ((channel + 1) % 8) * 2;
		reg = CPR1_A + diff - ((channel + 1) / 8) * 2;
		s = US.GET2(reg);
		s &= ~(3 << shiftl);
		s |= (1 << shiftl);
		US.PUT2(reg,s);
		if(!fullStep){
			//third channel
			shiftl = ((channel + 2) % 8) * 2;
			reg = CPR1_A + diff - ((channel + 2) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(reg,s);
			//fourth channel
			shiftl = ((channel + 3) % 8) * 2;
			reg = CPR1_A + diff - ((channel + 3) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(reg,s);
		}
		move(openSteps);
	}

	/**
	 * Stops the step motor.<br>
	 * The motor can subsequently be restarted with <code>start(...)</code>.
	 */
	public void stop() {		
		if(!finished()){
			//save openSteps 
			short desPos = US.GET2(TPURAM0_A + diff + 0x10 * channel);
			move(16);
			while(!finished()); //wait 
			openSteps = desPos - US.GET2(TPURAM0_A + diff + 0x10 * channel + 2);			
		}
		int shiftl, reg;
		short s;
		//Set priority null
		//first channel
		shiftl = (channel % 8) * 2;
		reg = CPR1_A + diff - (channel / 8) * 2;
		s = US.GET2(reg);
		s &= ~(3 << shiftl);
		US.PUT2(reg,s);
		//second channel
		shiftl = ((channel + 1) % 8) * 2;
		reg = CPR1_A + diff - ((channel + 1) / 8) * 2;
		s = US.GET2(reg);
		s &= ~(3 << shiftl);
		US.PUT2(reg,s);
		if(!fullStep){
			//third channel
			shiftl = ((channel + 2) % 8) * 2;
			reg = CPR1_A + diff - ((channel + 2) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			US.PUT2(reg,s);
			//fourth channel
			shiftl = ((channel + 3) % 8) * 2;
			reg = CPR1_A + diff - ((channel + 3) / 8) * 2;
			s = US.GET2(reg);
			s &= ~(3 << shiftl);
			US.PUT2(reg,s);
		}
	}

	/**
	 * Queries whether the motor has reached its required position.
	 * 
	 * @return <code>true</code> if required position has been reached.
	 */
	public boolean finished() {
		short desPos = US.GET2(TPURAM0_A + diff + 0x10 * channel);
		short curPos = US.GET2(TPURAM0_A + diff + 0x10 * channel + 2);
		return desPos == curPos;
	}

	/**
	 * Queries actual position of the motor.
	 * 
	 * @return Actual position in steps.
	 */
	public int position() {
		return US.GET2(TPURAM0_A + diff + 0x10 * channel + 2);
	}


	/**
	 * Resets the position.<br>
	 * The final position is set to the actual position. This immediately stops the motor.
	 */
	public void reset() {
		openSteps = 0;
		//Get current position
		short pos = US.GET2(TPURAM0_A + diff + 0x10 * channel + 2);
		//Set desired position
		US.PUT2(TPURAM0_A + diff + 0x10 * channel, pos);	
	}


}