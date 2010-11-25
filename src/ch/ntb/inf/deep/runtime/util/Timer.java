package ch.ntb.inf.deep.runtime.util;

import ch.ntb.inf.deep.runtime.mpc555.Task;

/**
 * A class to measure a time span.
 *
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
	 * Reset the timer. This call only work if the timer has not been expired.
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
	 * @return if the time is expired.
	 */
	public boolean expired(){
		if(active && Task.time() > time){
			active = false;
			return true;
		}
		return false;
	}
	
}
