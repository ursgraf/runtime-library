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

package org.deepjava.runtime.mpc555;

import org.deepjava.runtime.ppc32.Task;


/**
 * A class to measure a time span. 
 * The resolution will be in ms.
 *
 * @author 18.12.2009 simon.pertschy@ntb.ch
 */
public class Timer {

	int time;
	int timerTime;
	boolean active;
	
	/**
	 * Set the time to measure.
	 * @param ms the time in milliseconds
	 */
	public void set(int ms){
		timerTime = ms;
		time = Task.time() + ms;
		active = true;
	}
	
	/**
	 * Reset the timer. This call only works if the timer has not been expired.
	 */
	public void reset(){
		if(active){
			time = Task.time() + timerTime;
		}
	}
	
	/**
	 * Stop the timer.
	 */
	public void stop(){
		active = false;
	}
	
	/**
	 * Returns <code>true</code> if the time is expired. <code>false</code> otherwise.
	 * @return <code>true</code> if the time is expired.
	 */
	public boolean expired(){
		if(active && Task.time() > time){
			active = false;
			return true;
		}
		return false;
	}
	
}
