/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.mpc555.Task;

/**
 * Das nachfolgende Programm wurde entwickelt um die auf dem Robotoer
 * verwendeten Sensoren zu testen. Es werden dabei drei Klassen verwendet. 
 * 
 * Die Haupt-Klasse Robi2SensorProximityDemo wird zum Installieren der beiden anderen Klassen
 * verwendet. 
 * Über die Klasse Robi2SensorProximityDemo_Out werden die Werte aller Sensoren
 * abgefragt und über die serielle Schnittstelle auf das TargetLog (USB)
 * ausgegeben. 
 * Die dritte Klasse Robi2SensorProximityDemo_Led schaltet eine Led ein, wenn
 * ein Sensor anspricht. Für die Sensoren 0 bis 11 wird jeweils eine rote
 * Pattern LED geschaltet. Die restlichen Sensoren sind den Positions Leds und
 * der Center Led zugeordnet.
 * 
 * <strong>Beschreibung:</strong>
 * Die Haupt-Klasse Robi2SensorProximityDemo installiert die beiden Klassen Robi2SensorProximityDemo_Out 
 * und Robi2SensorProximityDemo_Led als Task. Wobei über die globale Variabel DEBUG das Installieren 
 * von Robi2SensorProximityDemo_Out verhindert werden kann.
 * 
 * Die Klasse Robi2SensorProximityDemo_Out erweitert Task was sicherstellt, dass nach der Installation 
 * die Methode action() periodisch aufgerufen wird. In dieser Methode werden die aktuellen 
 * Werte der Sensoren ausgelesen und über die serielle Schnittstelle auf das USBLog ausgegeben.
 * 
 * Die Klasse Robi2SensorProximityDemo_Led erweitert ebenfalls Task. Hierbei wird in der Methode action() 
 * über die Konstante Limit ermittelt, wieviele Sensoren momentan angesprochen werden. 
 * Entsprechend werden die Anzahl Leds eingeschaltet .
 */
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