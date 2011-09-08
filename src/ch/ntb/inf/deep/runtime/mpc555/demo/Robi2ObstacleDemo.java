package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;

public class Robi2ObstacleDemo extends Task {
	short state = 0;
	boolean turnLeft = false;
	
	private boolean hasBarrier() {
		boolean hasBarrier = false;
		for (int i = 4; i < 7; i++) {
			if (Robi2.getDistSensorValue(i) > 100) {
				hasBarrier = true;
				turnLeft = false;
			}
		}
		for (short i = 10; i < 13; i++) {
			if (Robi2.getDistSensorValue(i) > 100) {
				hasBarrier = true;
				turnLeft = true;
			}
		}
		if (Robi2.getDistSensorValue(2) > 100) {
				hasBarrier = true;
				turnLeft = true;
			}
		return hasBarrier;
	}
	
	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action() {
		switch (state) {
			case 0: // barrier ahead, turn roboter
				if (hasBarrier()) {
					state = 1;
					Robi2.stop();
					if(turnLeft){
						Robi2.setRightDriveSpeed(-80);
						Robi2.setLeftDriveSpeed(-80);
					}else{
						Robi2.setRightDriveSpeed(80);
						Robi2.setLeftDriveSpeed(80);
					}
				}
				break;
			case 1: // noc barrier ahead, go straigt forward
				if (! hasBarrier()) {
					state = 0;
					Robi2.stop(); 
					Robi2.setRightDriveSpeed(-60);
					Robi2.setLeftDriveSpeed(60);
				}
				break;
			default: 
				// should never be reachead
				state = 0;
				break;
		}
	}
	
	static { 
		// Task initialization
		Robi2ObstacleDemo task = new Robi2ObstacleDemo();
		task.period = 0;
		Task.install(task);
		
		for(int i = 0; i < 4; i++ ) {
			Robi2.setPatternLED(i, 0, true);
			Robi2.setPatternLED(i, 2, true);
		}
		Robi2.setPatternLED(0, 1, true);
		Robi2.setPatternLED(3, 1, true);
	}
}