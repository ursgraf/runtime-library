package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;


public class Robi2Obstacle2Demo extends Task {
	short state = 0;
	boolean turnLeft = false;
	
	private boolean hasBarrier() {
		boolean hasBarrier = false;
		for (int i=4;i<7;i++) {
			if (Robi2.getDistSensorValue(i) > 100) {
				hasBarrier = true;
				turnLeft = false;
			}
		}
		for (short i=10;i<13;i++) {
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
	
	 // Der ganze Code kommt in diese Methode hinein
	public void action() {
	
		// Switch Anweisung unterscheidet die verschiedenen Faelle 
		// anhand des Zustandes der state-Variable	
		switch (state) {
			case 0: // Hindernis im Weg, Roboter drehen
				if (hasBarrier()) {
					state = 1;
					Robi2.stopDrives();
					if(turnLeft){
						Robi2.setDrivesSpeedAntidormic(-80);
					}else{
						Robi2.setDrivesSpeedAntidormic(80);
					}
				}
				break;
			case 1: // Kein Hindernis, geradeaus fahren
				if (! hasBarrier()) {
					state = 0;
					Robi2.stopDrives(); 
					Robi2.setDrivesSpeedEqual(60);
				}
				break;
			default: 
				// Das duerfte nie vorkommen! 
				// Sicherheit, falls durch Fehler state 
				// einmal einen ungueltigen Wert annimmt. 
				state = 0;
				break;
		}
	}
	
	static { 
		// Task Initialisierung
		Robi2Obstacle2Demo task = new Robi2Obstacle2Demo();
		task.period = 0; // Periodenlaenge des Tasks in ms
		Task.install(task);
	}
}