package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;

public class Robi2SensorProximityDemo_Led extends Task {
	static final short NoOfSensors = Robi2SensorProximityDemo.NoOfSensors;
	
	static final short Limit = 100;

	public void action( ) {
		boolean barrier;
		for (short i = 0; i < NoOfSensors; i++) {
			barrier = false;
			int val = Robi2.getDistSensorValue(i);
			if (val > Limit) {
				barrier= true;
			}
			if(i <12){
				Robi2.setPatternLED( i/3, i%3, barrier);
			}else{
				switch(i){
				case 12:
					Robi2.setHeadPosLED(barrier);
					break;
				case 13:
					Robi2.setLeftPosLED(barrier);
					break;
				case 14:
					Robi2.setRightPosLED(barrier);
					break;
				case 15:
					Robi2.setCenterLED(barrier);
					break;
				}
			}
		}
	}
	
}