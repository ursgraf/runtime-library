package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;


public class Robi2LineDemo extends Task {
	
	private boolean sensors() {
		boolean isSensor = false;
		if (Robi2.getDistSensorValue(2) > 250) {
			isSensor = true;
		}
		for (short i=4;i<16;i++) {
			if (Robi2.getDistSensorValue(i) > 250) {
				isSensor = true;
			}
		}
		return isSensor;
	}

	private boolean leftFloorSensor() {
		return (Robi2.getDistSensorValue(0) > 550);
	}

	private boolean rightFloorSensor() {
		return (Robi2.getDistSensorValue(1) > 550); 
	}

	public void action() {
		if (sensors()) {
			Robi2.stop();
		} else if (leftFloorSensor() && rightFloorSensor()) {
			Robi2.setRightDriveSpeed(50);
			Robi2.setLeftDriveSpeed(100);
		} else if (!rightFloorSensor() && !leftFloorSensor()) {
			Robi2.setRightDriveSpeed(-50);
			Robi2.setLeftDriveSpeed(-100);
		} else if(!leftFloorSensor() && rightFloorSensor()){
			Robi2.setRightDriveSpeed(-50);
			Robi2.setLeftDriveSpeed(-100);
		}else{
			Robi2.drive(80);					
		}
	}
	
	static {
		Robi2LineDemo task = new Robi2LineDemo();
		task.period = 0;
		Task.install(task);
		
		for(int i = 0; i < 4; i++ ) {
			Robi2.setPatternLED(i, 1, true);
		}
	}
}