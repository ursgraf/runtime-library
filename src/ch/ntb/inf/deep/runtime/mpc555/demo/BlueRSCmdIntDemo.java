package ch.ntb.inf.deep.runtime.mpc555.demo;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.BlueRSCmdInt;

/* CHANGES:
 * 09.03.11 NTB/Roger Millischer	adapted to the new deep environment
 */

public class BlueRSCmdIntDemo extends Task {
	private final static String partner = "008025003E46";
	private static final int resetPin = 15;
	private static int cmd = 1;

	public void action() { // Print status changes and received commands
		int status = BlueRSCmdInt.getStatus();
		printStatus(status);
		if (status == BlueRSCmdInt.getStatus()) {
			int rxCmd = BlueRSCmdInt.getReceivedCmd();
			if (rxCmd > 0) {
				System.out.print("Cmd received -> ");
				System.out.println(rxCmd);
			}
		}
	}

	public static void connect() { // Connect to the partner module
		if (BlueRSCmdInt.getStatus() == BlueRSCmdInt.disconnected)
			BlueRSCmdInt.connect(partner);
		else 
			System.out.println("Wrong mode");
	}

	public static void disconnect() {// Disconnect from the partner module
		if (BlueRSCmdInt.getStatus() == BlueRSCmdInt.connected)
			BlueRSCmdInt.disconnect();
		else
			System.out.println("Wrong mode");
	}

	public static void sendCmd() {// Send a command
		if (BlueRSCmdInt.getStatus() == BlueRSCmdInt.connected)
			BlueRSCmdInt.sendCommand(cmd++);
		else
			System.out.println("Wrong mode");
	}

	private static int lastStatus = -1;

	private void printStatus(int status) {
		if (status != lastStatus) {
			lastStatus = status;
			switch (status) {
			case BlueRSCmdInt.disconnected:
				System.out.println("BlueRS -> Disconnected");
				break;
			case BlueRSCmdInt.connecting:
				System.out.println("BlueRS -> Connecting");
				break;
			case BlueRSCmdInt.connected:
				System.out.println("BlueRS -> Connected");
				break;
			case BlueRSCmdInt.disconnecting:
				System.out.println("BlueRS -> Disconnecting");
				break;
			}
		}
	}

	static {
		//initialize SCI1
		SCI1.start(9600, SCI1.NO_PARITY, (short) 8);
		//hook SCI1 to System.out
		System.out = new PrintStream(SCI1.out);

		MPIOSM_DIO.init(resetPin, true); // Init Mpiosm
		MPIOSM_DIO.set(resetPin, false); // Reset BlueRS
		Task t = new BlueRSCmdIntDemo();
		t.period = 100;
		Task.install(t);
		MPIOSM_DIO.set(resetPin, true);
	}
}