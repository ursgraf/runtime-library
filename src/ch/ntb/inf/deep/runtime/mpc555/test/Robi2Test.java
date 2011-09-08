package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.BlueRS;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.Robi2;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;

/**
 * This Class is used to test the Robi2 hardware.<br>
 * The different test cases can chosen by the hex-switch.<br>
 * <li>Position 0 does nothing</li>
 * <li>Position 1 tests distance sensors</li>
 * <li>Position 2 tests drives</li>
 * <li>Position 3 tests LEDs</li>
 * <li>Position 4 tests bluetooth</li>
 * <li>Position 5 tests IR receiver, not yet implemented </li>
 * <li>Position 6 tests compass, not yet implemented</li>
 * <li>Position 7 tests battery voltage</li>
 * <li>Position 8 tests temperature sensor, not yet implemented</li>
 * <li>Position 9</li>
 * <li>Position A</li>
 * <li>Position B</li>
 * <li>Position C</li>
 * <li>Position D</li>
 * <li>Position E</li>
 * <li>Position F</li>
 * 
 * @author Roger Millischer
 *
 */
public class Robi2Test extends Task {

	private int mode = -1; 
	private int oldMode = -1; 
	private int driveState = 0; // default: stop both
	private boolean centerLED = false;
	private boolean posLEDs = false;
	private int ledPattern_col = 0;
	private int ledPattern_row = 0;
	private boolean ledPattern_state = true;
	private static final String bt_paddr = "008025003E46";
	private static final byte[] bt_data = { 'B', 'i', 't', 't', 'e', ' ', 'd',
			'i', 'e', ' ', '5',' ' ,'d', 'r', 'u', 'e', 'c', 'k', 'e', 'n', 13, 10 };
	private boolean discon = true;
	private int bt_state = 0;
	private int count = 0;
	private static byte[] recData = new byte[128];

	private void connect() { // Connect to the partner module
		System.out.println("Try to connect");
		BlueRS.connect(bt_paddr);
	}

	private void disconnect() { // Disconnect from the partner module
		System.out.println("Try to disconnect");
		BlueRS.switchToConnectedATMode(); // Switch to the command mode
		discon = true;

	}

	private void send() {
		if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {// If BlueRS connected
			// send data
			System.out.println("Send data");
			BlueRS.write(bt_data, bt_data.length);
		} else {
			System.out.println("BlueRS not connected");
		}
	}
	
	/**
	 * <b>Do not call this method</b>
	 * 
	 */
	public void action() {
		oldMode = mode;
		mode = Robi2.getSwitchValue();
		if(mode != oldMode){
			System.out.print("Switchpos:");
			System.out.println(mode);
			if(oldMode == 0x2){//was drive
				Robi2.stop();
			}
			if(oldMode == 0x3){//was led
				Robi2.disableAllLEDs();
				ledPattern_col = 0;
				ledPattern_row = 0;
				ledPattern_state = true;
			}
		}
		switch (mode) {
		case 0x0: // idle
			count = 0;
			// nothing to do...
			break;
		case 0x1: // Distance sensors
			if(count < 1){
				for (int i = 0; i < 3; i++) {
					System.out.print("Sens ");
					System.out.print(i);
					System.out.print("\t");
				}
				for (int i = 4; i < 10; i++) {
					System.out.print("Sens ");
					System.out.print(i);
					System.out.print("\t");
				}
				System.out.print("Sens ");
				System.out.print(3);
				System.out.print("\t");
				for (int i = 15; i > 9; i--) {
					System.out.print("Sens ");
					System.out.print(i);
					System.out.print("\t");
				}
				System.out.println();
			}
			count = ++count %20;
			
			for (int i = 0; i < 3; i++) {
				System.out.print(Robi2.getDistSensorValue(i));
				System.out.print("\t");
			}
			for (int i = 4; i < 10; i++) {
				System.out.print(Robi2.getDistSensorValue(i));
				System.out.print("\t");
			}
			System.out.print(Robi2.getDistSensorValue(3));
			System.out.print("\t");
			for (int i = 15; i > 9; i--) {
				System.out.print(Robi2.getDistSensorValue(i));
				System.out.print("\t");
			}
			System.out.println();
			break;
		case 0x2: // Drive
			switch (driveState) {
			case 0: // forward
				Robi2.drive(80);
				driveState = 1;
				break;
			case 1: // turn right
				Robi2.turn(28);
				driveState = 2;
				break;
			case 2:// forward
				Robi2.drive(80);
				driveState = 3;
				break;
			case 3: // turn right
				Robi2.turn(28);
				driveState = 4;
				break;
			case 4:// forward
				Robi2.drive(80);
				driveState = 5;
				break;
			case 5: // turn right
				Robi2.turn(28);
				driveState = 6;
				break;
			case 6: // forward
				Robi2.drive(80);
				driveState = 7;
				break;
			case 7: // stop
				Robi2.stop();
				driveState = 8;
				break;
			case 8: // Backward
				Robi2.drive(-80);
				driveState = 9;
				break;
			case 9: // turn left
				Robi2.turn(-28);
				driveState = 10;
				break;
			case 10: // Backward
				Robi2.drive(-80);
				driveState = 11;
				break;
			case 11: // turn left
				Robi2.turn(-28);
				driveState = 12;
				break;
			case 12: // Backward
				Robi2.drive(-80);
				driveState = 13;
				break;
			case 13: // turn left
				Robi2.turn(-28);
				driveState = 14;
				break;
			case 14: // Backward
				Robi2.drive(-80);
				driveState = 15;
				break;
			case 15: // stop
				Robi2.stop();
				driveState = 16;
				break;
			case 16: // turn fast
				Robi2.turn(100);
				driveState = 17;
				break;
			case 17: // stop
				Robi2.stop();
				driveState = 18;
				break;
			case 18: // trun fast backward
				Robi2.turn(-100);
				driveState = 19;
				break;
			case 19: // stop
				Robi2.stop();
				driveState = 0;
				break;

			}
			System.out.print("x = ");
			System.out.print(Robi2.getPosX());
			System.out.print(" m");
			System.out.print("\t");

			System.out.print("y = ");
			System.out.print(Robi2.getPosY());
			System.out.print(" m");
			System.out.print("\t");

			System.out.print("phi = ");
			System.out.print(Robi2.getOrientation());
			System.out.print(" rad");
			System.out.println();
			break;

		case 0x3: // LEDs
			count = 0;
			if (ledPattern_col == 3) {
				ledPattern_col = 0;
				ledPattern_row++;
			}
			if (ledPattern_row == 4) {
				ledPattern_row = 0;
				ledPattern_state = !ledPattern_state;
			}
			Robi2.setPatternLED(ledPattern_row, ledPattern_col,	ledPattern_state);
			Robi2.setCenterLED(centerLED);
			centerLED = !centerLED;
			Robi2.setPosLEDs(posLEDs);
			posLEDs = !posLEDs;
			ledPattern_col++;
			break;
		case 0x4: // Bluetooth
			System.out.print("BT-State: ");
			System.out.println(BlueRS.getMode());

			switch (bt_state) {
			case 0: // connect
				if (discon && BlueRS.getMode() == BlueRS.MODE_AT && count == 0){
					connect();
				}
				if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
					System.out.println("CONNECTED!!!");
					discon = false;
					bt_state++;
					count = 0;
				}
				System.out.print("count = ");
				System.out.println(count);
				count++;
				if(count > 7){
					count = 0;
				}
				
		
				break;
			case 6:// receive data
				if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) { // If BlueRS
																	// connected
					int len = BlueRS.read(recData);
					for (int i = 0; i < len; i++) {
						System.out.print((char) recData[i]);
					}
					if (recData[0] == '5') {
						bt_state = 7;
					}
				}
				break;
			case 7:// switch mode to connected_at
				if (!discon) {
					disconnect();
				}
				bt_state = 8;
				break;
			case 8:// wait cycle
				bt_state = 9;
				break;				
			case 9: // disconnect
				if (discon && BlueRS.getMode() == BlueRS.MODE_CONNECTED_AT) {
					bt_state++;
					BlueRS.disconnect();
					System.out.println("BlueRS disconnected");
				} else
					System.out.println("BlueRS not in connected at mode");
				break;

			default: // send
				if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) { // If BlueRS connected
					send();
					bt_state = 6;
				}else{
					bt_state = 0;
				}
				break;
			}
			break;

		case 0x5: // IR receiver
			break;

		case 0x6: // Compass
			break;

		case 0x7: // Batteryvoltage
			System.out.print("Vbatt = ");
			System.out.print(Robi2.getBatteryVoltage());
			System.out.println(" V");
			break;

		case 0x8: // Temperatur sensor
			break;
		case 0x9:
			break;
		case 0xA:
			break;
		case 0xB:
			break;
		case 0xC:
			break;
		case 0xD:
			break;
		case 0xE:
			break;
		case 0xF:
			break;
		}
	}


	static {
		//init SCI1
		SCI1.start(9600, SCI1.NO_PARITY, (short)8);
		//Hook SCI1 on System.out
		System.out = new PrintStream(SCI1.out);
		
		Robi2.disableAllLEDs();
		
		System.out.println("=== Demo for Robi 2! ===");
		MPIOSM_DIO.init(11, true); // Init Mpiosm
		MPIOSM_DIO.set(11, false); // Reset BlueRS
		BlueRS.start(); // Start Bluetooth Drive
		Robi2Test testTask = new Robi2Test();
		testTask.period = 1000;
		Task.install(testTask);
		MPIOSM_DIO.set(11, true);

	}
}
