package ch.ntb.inf.deep.runtime.mpc555.demo;
import ch.ntb.inf.deep.runtime.mpc555.Task;

public class Robi2SensorProximityDemo {
	static final boolean DEBUG = true;
	static final short NoOfSensors = 16;
	
	private static Robi2SensorProximityDemo_Out outTask;
	private static Robi2SensorProximityDemo_Led readTask;
	
	static { // Task Initialisierung
		
		if (DEBUG) {
			outTask = new Robi2SensorProximityDemo_Out();
			outTask.period = 2000;
			Task.install(outTask);
		}
		
		readTask = new Robi2SensorProximityDemo_Led();
		readTask.period = 100;
		Task.install(readTask);
		
	}
}