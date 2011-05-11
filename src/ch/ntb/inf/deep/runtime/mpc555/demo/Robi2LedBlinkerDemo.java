package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;



public class Robi2LedBlinkerDemo extends Task{
	static int col = 2;
	static int col1 = 0;
	static int col2 = 1;
	static int col3 = 2;
	static int zaehler = 0;
	
	static void toggleCenterLED() { // command
		Robi2.setCenterLED(!Robi2.getCenterLED());
	}

	public void action( ) {
			
			zaehler = zaehler + 1;
			
			//PositionLEDs
			Robi2.setPosLEDs(!Robi2.getHeadPosLED());
			
			//Pattern LEDs
			//Row 0
			Robi2.setPatternLED(0,col,false);
			col = (col + 1) % 3;
			Robi2.setPatternLED(0,col,true);
			
			//Row 1
			Robi2.setPatternLED(0,col1,false);
			col1 = (col1 + 1) % 3;
			Robi2.setPatternLED(0,col1,true);
			
			//Row 2
			Robi2.setPatternLED(0,col2,false);
			col2 = (col2 + 1) % 3;
			Robi2.setPatternLED(0,col2,true);
			
			//Row 3
			Robi2.setPatternLED(0,col3,false);
			col3 = (col3 + 1) % 3;
			Robi2.setPatternLED(0,col3,true);			 
	}
	
	static { // Task Initialisierung
		Robi2LedBlinkerDemo task = new Robi2LedBlinkerDemo();
		task.period = 1000;	// Periodenlänge des Tasks in ms
		Task.install(task);
	}
}
