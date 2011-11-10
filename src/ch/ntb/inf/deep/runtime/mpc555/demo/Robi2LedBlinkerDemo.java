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
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;

/**
 * Wenn der Roboter autonom betrieben werden soll, d.h. er ist nicht mehr an den
 * PC angeschlossen, ist es nicht mehr möglich vom PC aus über einen Command
 * etwas auszuführen. Daher ist es nötig, dass ein Programm eine Operation
 * nacheinander immer wieder aufruft. Dazu gibt es den Treiber Task. Über diesen
 * kann eine Methode definiert werden, welche nacheinander mit einer gewissen
 * Unterbruchszeit (Periode) aufgerufen wird.
 * 
 * <strong>Beschreibung:</strong>
 * Damit die eigene Klasse eine Methode implementieren kann, welche immer wieder
 * über das Task-System aufgerufen wird, muss sie die Klasse Task erweitern:
 * public class Robi2LedBlinkerDemo extends Task Die Methode, welche periodisch
 * aufgerufen werden soll muss action() genannt werden. Um das wiederholte Aufrufen
 * der Methode zu starten, muss die eigene Klasse installiert werden: 
 * 		
 * <pre> Robi2LedBlinkerDemo task = new LedBlinker(); 
 * task.period = 1000; // Periodenlänge des Tasks in ms
 * Task.install(task);</pre>
 * 
 * Alle Initialisierungen und auch das Installieren der eigenen Klasse muss im
 * Konstrukt static { ... } erfolgen. Dies ist ein statischer Konstruktor und
 * wird beim ersten Benutzen der Klasse Robi2LedBlinkerDemo aufgerufen. Dementsprechend
 * werden dabei auch die Initialisierungen und das Installieren ausgeführt.
 * 
 * Dieses Programm erhält zusätzlich noch eine Methode static void
 * toggleCenterLED () { ... }, welche wiederum von aussen aufgerufen werden
 * kann. In dieser Methode wird die center Led entweder ein- oder ausgeschaltet.
 * 
 * Die Methode public void action( ) { ... } wird jede Sekunde aufgerufen
 * (task.period = 1000;). Dabei werden die Leds nacheinander ein- und
 * ausgeschaltet.
 * 
 * Die Variable static int zaehler = 0; ist eine globale Variable und kann vom
 * PC aus abgefragt werden.
 * 
 */
public class Robi2LedBlinkerDemo extends Task {
	static int col = 2;
	static int col1 = 0;
	static int col2 = 1;
	static int col3 = 2;
	static int zaehler = 0;

	static void toggleCenterLED() { // command
		Robi2.setCenterLED(!Robi2.getCenterLED());
	}

	public void action() {

		zaehler = zaehler + 1;

		// PositionLEDs
		Robi2.setPosLEDs(!Robi2.getHeadPosLED());

		/* Pattern LEDs */
		// Row 0
		Robi2.setPatternLED(0, col, false);
		col = (col + 1) % 3;
		Robi2.setPatternLED(0, col, true);

		// Row 1
		Robi2.setPatternLED(0, col1, false);
		col1 = (col1 + 1) % 3;
		Robi2.setPatternLED(0, col1, true);

		// Row 2
		Robi2.setPatternLED(0, col2, false);
		col2 = (col2 + 1) % 3;
		Robi2.setPatternLED(0, col2, true);

		// Row 3
		Robi2.setPatternLED(0, col3, false);
		col3 = (col3 + 1) % 3;
		Robi2.setPatternLED(0, col3, true);
	}

	static { // Task Initialisierung
		Robi2LedBlinkerDemo task = new Robi2LedBlinkerDemo();
		task.period = 1000; // Periodenlänge des Tasks in ms
		Task.install(task);
	}
}
