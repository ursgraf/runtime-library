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

package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.BlueRS;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/* CHANGES:
 * 09.03.11 NTB/Roger Millischer	change to SCI1 and fix problem with String.getBytes()
 * 22.02.11 NTB/Martin Züger		adapted to the new deep environment
 */

/**
 * Demo Application for the BlueRS driver.
 */
public class BlueRSDemo extends Task {

	private static Task task;

	public static int state, oldTime, sendCounter;

	private static final byte[] sendBytes = { 'h', 'e', 'l', 'l', 'o' };

	private static byte[] receiveBuffer = new byte[80];

	// Task states
	static final int STATE_IDLE = 0, STATE_CONNECT = 1, STATE_WAIT_CONNECT = 2,
			STATE_SEND = 3, STATE_WAIT_RESET = 4, STATE_SEND_RECEIVE = 5,
			STATE_SET_AT = 6, STATE_DISCONNECT = 7;

	// timouts/intervals
	static final int TIMEOUT_IDLE = 2000, TIMEOUT_RESET = 10000,
			SEND_INTERVAL = 1000, WAIT_AT_MODE = 1000;

	static final int MAX_SEND_COUTER = 5;

	/**
	 * Send the reset command module (the module has to be in AT mode).
	 */
	public static void bt_reset() {
		System.out.println("bt.reset()");
		BlueRS.reset();
	}

	/**
	 * Send the inquiry command to the modul (the module has to be in AT mode).
	 */
	public static void bt_inquiry() {
		System.out.println("bt.inquiry(false)");
		BlueRS.inquiry(false);
	}

	/**
	 * Fragt die Resultate einer Inquiry ab (im AT-Mode).
	 */
	public static void bt_bdlist() {
		System.out.println("at**bdlist");
		BlueRS.sendCommand("at**bdlist");
	}

	/**
	 * Verbindet zu einem Bluetooth-Gerät (im AT-Mode).
	 */
	public static void bt_connect() {
		System.out.println("bt.connect()");
		BlueRS.connect("008025003E46");
		
	}

	/**
	 * Wechselt vom Connection in den AT-Mode.
	 */
	public static void bt_switchToAtMode() {
		System.out.println("bt.switchToATMode() -> wait 1 second");

		BlueRS.switchToConnectedATMode();
		int oldTime = Task.time();
		while (Task.time() - WAIT_AT_MODE < oldTime)
			;
	}

	/**
	 * Wechselt vom AT-Mode in den Connection-Mode.
	 */
	public static void bt_returnFromAtMode() {
		System.out.println("bt.returnFromATMode()");
		BlueRS.returnFromATMode();
	}

	/**
	 * Trennt eine bestehende Verbindung (im AT-Mode).
	 */
	public static void bt_disconnect() {
		System.out.println("bt.disconnect()");
		BlueRS.disconnect();
	}

	/**
	 * Gibt den bestehenden Mode auf das Log aus.
	 */
	public static void bt_getMode() {
		System.out.print("bt.getMode(): "); System.out.print('\t');
		switch (BlueRS.getMode()) {
		case BlueRS.MODE_AT:
			System.out.println("MODE_AT");
			break;
		case BlueRS.MODE_CONFIG:
			System.out.println("MODE_CONFIG");
			break;
		case BlueRS.MODE_CONNECTED:
			System.out.println("MODE_CONNECTED");
			break;
		case BlueRS.MODE_CONNECTED_AT:
			System.out.println("MODE_CONNECTED_AT");
			break;
		default:
			System.out.println("INVALID MODE");
			break;
		}
	}

	/**
	 * Gib das Resultat der letzten Operation auf das Log aus.
	 */
	public static void bt_getResult() {
		System.out.print("bt.getResult(): "); System.out.print('\t');
		switch (BlueRS.getResult()) {
		case BlueRS.RESULT_UNDEFINED:
			System.out.println("RESULT_UNDEFINED");
			break;
		case BlueRS.RESULT_OK:
			System.out.println("RESULT_OK");
			break;
		case BlueRS.RESULT_ERROR:
			System.out.println("RESULT_ERROR");
			break;
		default:
			System.out.println("INVALID RESULT");
			break;
		}
	}

	/**
	 * Wechselt vom AT-Mode in den Configuration-Mode.
	 */
	public static void at_enter_conf() {
		System.out.println("Enter Config");
		BlueRS.sendCommand("atconf");
	}

	/**
	 * Sendet den Firmware Reset Command und stellt die Standard-Werte wieder
	 * her. Es muss {@link #conf_save()} zum bestätigen aufgerufen und das Modul
	 * neu gestartet werden.<br>
	 * ACHTUNG: Die Standard Baud Rate ist 115200 kbps.<br>
	 * 1. Starten des Hyperterminals mit 115200 kpbs, 8 data bits, 1 stop bit,
	 * no parity<br>
	 * 2. Fogende Commands eingeben:<br>
	 * atconf<br>
	 * br=4<br>
	 * save<br>
	 * exit<br>
	 * Nun ist die Baud Rate auf 9600 kbps konfiguriert.
	 */
	public static void conf_reset_firmware() {
		System.out.println("Reset Firmware");
		BlueRS.sendCommand("defa");
	}

	/**
	 * Gibt alle Konfigurationsparameter aus (im Config-Modus).
	 */
	public static void conf_showall() {
		System.out.println("Show All");
		BlueRS.sendCommand("showall");
	}

	/**
	 * Setzt den Device Namen (im Config-Modus).
	 */
	public static void conf_set_dev_name() {
		System.out.println("set the device name");
		BlueRS.sendCommand("bname=Device Name");
	}

	/**
	 * Setzt den Service Namen (im Config-Modus).
	 */
	public static void conf_set_bsname() {
		System.out.println("set service Name");
		BlueRS.sendCommand("bsname=Serial Port Name");
	}

	/**
	 * Setzt die Baud Rate (im Config-Modus).
	 */
	public static void conf_set_baud() {
		System.out.println("set Baudrate to 9600 bit/s");
		BlueRS.sendCommand("br=4");
	}

	/**
	 * Deaktiviert die Flusskontrolle (im Config-Modus).
	 */
	public static void conf_set_flc() {
		System.out.println("disable flow control");
		BlueRS.sendCommand("flc=0");
	}

	// Mögliche Konfigurationsparameter
	// --------------------------------
	// BlueRS.sendCommand("bcrypt=0"); // switch encryption off
	// BlueRS.sendCommand("bpin=0000,0000"); // set bluetooth device PIN
	// BlueRS.sendCommand("bpsm=3"); // enable page scan and inquiry scan
	// BlueRS.sendCommand("bpsrm=1"); // set the page scan repetition mode
	// BlueRS.sendCommand("bsecin=0"); // no active authentication
	// BlueRS.sendCommand("bsecout=0"); // no active authentication
	// BlueRS.sendCommand("bname=Device Name"); // set the device name
	// BlueRS.sendCommand("bofcon=1"); // set fast connection mode
	// BlueRS.sendCommand("bosch=1"); // set up server channel number
	// BlueRS.sendCommand("brsch=1"); // set remote Bluetooth server channel
	// BlueRS.sendCommand("bsname=Serial Port Name"); // set Service Name
	// BlueRS.sendCommand("pwd=0"); // disable power down mode
	// BlueRS.sendCommand("br=4"); // set Baudrate to 9600 bit/s
	// BlueRS.sendCommand("cato=30"); // set timeout to abort connection attempt
	// BlueRS.sendCommand("capa=3"); // set call pause between to call attempts
	// BlueRS.sendCommand("ccts=1"); // CTS always ON
	// BlueRS.sendCommand("cdcd=1"); // DCD indicates Bluetooth connection
	// BlueRS.sendCommand("cdsr=0"); // DSR always ON
	// BlueRS.sendCommand("cdtr=4"); // DTR ignored and DTR drop disconnects
	// BlueRS.sendCommand("cmds=0"); // set AT command set
	// BlueRS.sendCommand("cri=0"); // set RI with an incoming BT link request
	// BlueRS.sendCommand("dbits=8"); // asynchronous databits
	// BlueRS.sendCommand("flc=0"); // no flow control
	// BlueRS.sendCommand("idle=0"); // disable idle timeout
	// BlueRS.sendCommand("prty=0"); // no parity
	// BlueRS.sendCommand("rbaccl=1"); // config port accessible but not visible
	// BlueRS.sendCommand("rsttim=10"); // 1 second startup delay
	// BlueRS.sendCommand("sbits=1"); // one stop bit
	// // S Registers
	// BlueRS.sendCommand("S0=1"); // immediate call acceptance
	// BlueRS.sendCommand("S7=30"); // wait time for carrier
	// BlueRS.sendCommand("S91=0");

	/**
	 * Speichert die aktuelle Konfiguration (im Confg-Mode).
	 */
	public static void conf_save() {
		System.out.println("Save Config");

		BlueRS.sendCommand("save");
	}

	/**
	 * Verlässt den Konfigurationsmodus (im Confg-Mode).
	 */
	public static void conf_exit() {
		System.out.println("Exit Config");

		BlueRS.sendCommand("exit");
	}

	public void action() {
		switch (state) {
		case STATE_IDLE:
			// wait 2 sec
			if (Task.time() - oldTime > TIMEOUT_IDLE) {
				oldTime = Task.time();
				state = STATE_CONNECT;
				BlueRSDemo.bt_connect();
				System.out.println("STATE_CONNECT");
			}
			break;
		case STATE_CONNECT:
			if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
				state = STATE_SEND_RECEIVE;
				sendCounter = 0;
				oldTime = Task.time();
				System.out.println("STATE_SEND_RECEIVE");
			} else if (Task.time() - oldTime > TIMEOUT_RESET) { // timeout
				oldTime = Task.time();
				BlueRSDemo.bt_reset();
				state = STATE_WAIT_RESET;
				System.out.println("STATE_WAIT_RESET");
			}
			break;
		case STATE_SEND_RECEIVE:
			if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
				if (Task.time() - oldTime > SEND_INTERVAL) {
					BlueRS.write(sendBytes, sendBytes.length);
					sendCounter++;
					oldTime = Task.time();
				}

				if (BlueRS.availableToReceive() > 0) {
					int nofBytesRead = BlueRS.read(receiveBuffer);
					System.out.print("data received: ");
					System.out.println(nofBytesRead);

					for (int i = 0; i < nofBytesRead; i++) {
						System.out.print((char) receiveBuffer[i]);
						System.out.println();
					}
				}

				if (sendCounter > MAX_SEND_COUTER) {
					state = STATE_SET_AT;
					System.out.println("STATE_SET_AT");
					oldTime = Task.time();
				}

			} else { // not connected
				oldTime = Task.time();
				state = STATE_IDLE;
				System.out.println("STATE_IDLE");
			}
			break;
		case STATE_SET_AT:
			if (BlueRS.getMode() == BlueRS.MODE_CONNECTED_AT) {
				BlueRSDemo.bt_disconnect();
				state = STATE_DISCONNECT;
				System.out.println("STATE_DISCONNECT");
			} else if (Task.time() - oldTime > WAIT_AT_MODE) {
				BlueRSDemo.bt_switchToAtMode();
				oldTime = Task.time();
			}
			break;
		case STATE_DISCONNECT:
			if (BlueRS.getMode() == BlueRS.MODE_AT) {
				oldTime = Task.time();
				state = STATE_IDLE;
			} else if (Task.time() - oldTime > TIMEOUT_RESET) { // timeout
				oldTime = Task.time();
				BlueRSDemo.bt_reset();
				state = STATE_WAIT_RESET;
			}
			break;
		case STATE_WAIT_RESET:
			if (BlueRS.getResult() == BlueRS.RESULT_OK) {
				state = STATE_IDLE;
				System.out.println("STATE_IDLE");
			} else if (Task.time() - oldTime > TIMEOUT_RESET) {
				oldTime = Task.time();
				BlueRSDemo.bt_reset();
			}
			break;
		default:
			System.out.print("invalid case");
			System.out.println(state);
			break;
		}
	}

	/**
	 * Startet den Verdindungstask.
	 */
	public static void startTask() {
		state = STATE_IDLE;
		oldTime = Task.time();
		Task.install(task);
		System.out.println("STATE_IDLE");
	}

	/**
	 * Stoppt den Verdindungstask.
	 */
	public static void stopTask() {
		Task.remove(task);
	}

	/**
	 * Schickt den String "Test".
	 */
	public static void write() {
		byte[] test = new byte[]{'t','e','s','t'};
		BlueRS.write(test, 4);
	}

	/**
	 * Gibt aus, wieviele Bytes momentan lesbar sind.
	 */
	public static void availableToReceive() {
		System.out.println(BlueRS.availableToReceive());
	}
	/**
	 * Aktiviert Fehlermeldungen des Bluetooth Modules
	 */
	public static void enableErrorMessages() {
		BlueRS.enableErrorMessages();
	}

	static {
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI1.out);
				
		System.out.println("Demo");
		MPIOSM_DIO.init(15, true); // Init Mpiosm
		MPIOSM_DIO.set(15, false); // Reset BlueRS
		BlueRS.start();
		task = new BlueRSDemo();
		task.period = 100;
		startTask();
		MPIOSM_DIO.set(15, true);
	}

}
