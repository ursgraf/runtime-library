package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.util.IntFifo;

/*
 * Changes:
 * 13.12.2008	NTB/SP	creation
 */

/**
 * Interface for the BlueRS driver to send and receive positive
 * integer values.
 */
public class BlueRSCmdInt extends Task {

	private static final int taskPeriod = 50;
	private static final int bufferSize = 15;
	private static final byte cmdStartSymbol = 0x11;
	private static final int timeout = 5000;
	private static final int disconnectDelay = 1500;

	// States
	/**
	 * No connection to a partner module established.
	 */
	public static final int disconnected = 0;
	/**
	 * Try to connect to a partner module.
	 */
	public static final int connecting = 1;
	/**
	 * The BlueRS module is connected to a partner module.
	 */
	public static final int connected = 2;
	/**
	 * Receive a command from the partner module.
	 */
	private static final int receiveCmd = 3;
	/**
	 * Try to disconnect from a partner module.
	 */
	public static final int disconnecting = 4;
	/**
	 * Returning to the at mode.
	 */
	private static final int returnAtm = 5;

	//Return codes
	/**
	 * Command successfully executed
	 */
	public static final int success = 1;
	/**
	 * The command can not be executed in the actual mode
	 */
	public static final int wrongMode = -1;
	/**
	 * Illegal command format. (The command to send is less the 1)
	 */
	public static final int illegalCmd = -2;
    /**
     * No received command to read.
     */
    public static final int	bufferEmpty = -3;

	private static int status;
	private static byte[] txCmd;
	private static IntFifo cmdBuffer;
	private static Task task;
	private static int time = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see mpc555.Task#Do()
	 */
	public void action() {
		switch (status) {
		case disconnected:
			if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
				status = connected;
			}
			break;
		case connecting:
			if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
				status = connected;
			} else if (time > timeout) {
				status = disconnected;
			} else
				time += taskPeriod;
			break;
		case connected:
			if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
				while (BlueRS.availableToReceive() > 0) {
					if (BlueRS.read() == cmdStartSymbol) {
						status = receiveCmd;
						break;
					}
				}
				if (status != receiveCmd)
					break;
			} else {
				status = disconnected;
				break;
			}
		case receiveCmd:
			if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
				if (BlueRS.availableToReceive() >= 4) {
					int cmd =BlueRS.read()  << 24;
					cmd |= (BlueRS.read() & 0xFF) << 16;
					cmd |= (BlueRS.read() & 0xFF) << 8;
					cmd |= (BlueRS.read() & 0xFF);
					cmdBuffer.enqueue(cmd);
					status = connected;
				}
			} else {
				status = disconnected;
			}
			break;
		case disconnecting:
			if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
				if (time >= disconnectDelay) {
					BlueRS.switchToConnectedATMode();
					status = returnAtm;
					time = 0;
				} else
					time += taskPeriod;
			} else {
				status = disconnected;
			}
			break;
		case returnAtm:
			if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
				if (time > timeout)
					status = disconnecting;
				else
					time += taskPeriod;
			} else if (BlueRS.getMode() == BlueRS.MODE_CONNECTED_AT) {
				BlueRS.disconnect();
				status = disconnected;
			} else {
				status = disconnected;
			}
			break;
		default:
			status = disconnected;
			break;
		}
	}

	/**
	 * Sends a command to the partner module
	 * 
	 * @param cmd
	 *            , the command to send
	 * @return {@link #success} if the command was successfully sent,
	 *         {@link #illegalCmd} if the command is less then 0,
	 *         {@link #wrongMode} if no connection is established
	 */
	public static int sendCommand(int cmd) {
		if (status == connected) {
			if (cmd >= 0) {
				txCmd[1] = (byte) (cmd >> 24);
				txCmd[2] = (byte) (cmd >> 16);
				txCmd[3] = (byte) (cmd >> 8);
				txCmd[4] = (byte) cmd;
				BlueRS.write(txCmd, txCmd.length);
				return success;
			}
			return illegalCmd;
		}
		return wrongMode;
	}

	/**
	 * Tries to connect to a partner module
	 * @param partner, the address of the partner module.
	 * @return {@link #success} if the connection process has been initiated, {@link #wrongMode} if the module is not disconnected.
	 */
	public static int connect(String partner) {
		if (status == disconnected) {
			status = connecting;
			time = 0;
			BlueRS.connect(partner);
			return success;
		}
		return wrongMode;
	}

	/**
	 * Tries to disconnect from a partner module.
	 * @return {@link #success} if the disconnection process has been initiated, {@link #wrongMode} if the module is not connected.
	 */
	public static int disconnect() {
		if (BlueRS.getMode() == BlueRS.MODE_CONNECTED) {
			status = disconnecting;
			time = 0;
		}
		return wrongMode;
	}

	/**
	 * Get a received command
	 * @return the received command, {@link #bufferEmpty} if no command is available.
	 */
	public static int getReceivedCmd() {
		if (cmdBuffer.availToRead() > 0) {
			return cmdBuffer.dequeue();
		}
		return bufferEmpty;
	}

	/**
	 * Returns the status of the interface
	 * 
	 * @return {@link #disconnected}, {@link #connecting}, {@link #connected},
	 *         {@link #disconnecting}
	 */
	public static int getStatus() {
		if (status == receiveCmd)
			return connected;
		else if (status == returnAtm)
			return disconnecting;
		return status;
	}

	static {
		status = disconnected;
		BlueRS.start();
		txCmd = new byte[5];
		txCmd[0] = cmdStartSymbol;
		cmdBuffer = new IntFifo(bufferSize);
		task = new BlueRSCmdInt();
		task.period = taskPeriod;
		Task.install(task);
	}
}
