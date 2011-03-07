package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.InputStream;
import java.io.OutputStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.util.ByteLiFo;

/*
 * Changes: 
 * 24.02.2011 NTB/MZ:	adapted to the new deep environment
 * 27.05.2008 NTB/SP:	task creation moved to static construct
 * 31.03.2008 NTB/SP:	RESET detection added
 * 07.02.2008 NTB/SP:	currentRsp failure corrected
 */

/**
 * Driver for the <i>Stollmann BlueRS+I</i> Bluetooth Module.<br>
 * 
 * The module supports one emulated serial connection using the Bluetooth Serial
 * Port Profile (SPP). <br>
 * The device is connected to the second serial port of the mpc555. To test and
 * configure the module, the Windows Hyperterminal can be used at a baud rate of
 * 9600 kbps, no flow control. Stollmann provides the complete documentation at
 * <a href="http://www.stollmann.de">www.stollmann.de</a>.<br>
 * 
 * <h3>Connection Modes</h3>
 * There are 3 different modes in which the Bluetooth module can operate. <br>
 * First there's the connection mode ({@link #MODE_CONNECTED}). In this mode,
 * data is sent ({@link #write(byte[], int)}) and received ({@link #read(byte[])}).<br>
 * Secondly there's the AT command mode ({@link #MODE_AT}). In this mode, the
 * device can receive AT commands like {@link #connect(String)} which are mainly
 * used for connection handling. After power up, the module is in this mode.
 * When in connection mode, the device can temporarily switch to AT mode and
 * back again ({@link #MODE_CONNECTED_AT}). The connection remains open in the
 * background. To send commands like {@link #disconnect()} to the module, the AT
 * mode has to be entered. In normal connection mode, the device would not
 * recognize the command and send it to its connection partner as normal text.<br>
 * The last mode is for configuring the device. This mode is not used in normal
 * operation and should only be used by experienced users.<br>
 * 
 * <h3>Checking for Results</h3>
 * After sending a command to the Bluetooth module (e.g.
 * {@link #connect(String)}), the method {@link #getResult()} can be called to
 * check, if the command has been sent successfully. If the result code is
 * {@link #RESULT_UNDEFINED}, the command has not got the result from the
 * Bluetooth module yet.
 * 
 * <h3>Starting the Driver</h3>
 * The <code>start()</code> method <b>must</b> been called first to start the
 * <code>BlueRS</code> driver!
 * 
 * @author schlaepfer
 * 
 */
public class BlueRS extends Task {

	public static OutputStream out;
	public static InputStream in;
	
	/**
	 * The receiver task period.
	 */
	public static final int RECEIVE_TASK_PERIOD = 100;

	private static final int MAX_CURR_RSP_LEN = 255, MAX_TMP_RSP_LEN = 2048, DATABUFFER_LEN = 2048;

	private static final byte CR = 0xd, LF = 0xa;

	private static final byte[] CRLF = { CR, LF };

	private static final byte[] BT_CONNECT = { 'C', 'O', 'N', 'N', 'E', 'C',
			'T' };

	private static final byte[] BT_RING = { 'R', 'I', 'N', 'G' };

	private static final byte[] BT_NO_CARRIER = { 'N', 'O', ' ', 'C', 'A', 'R',
			'R', 'I', 'E', 'R' };

	private static final byte[] BT_RESET = { '+', '+', '+', ' ', 'P', 'r', 'e',
			's', 's', ' ', '<', 'C', 'R', '>', ',', '<', 'C', 'R', '>', ',',
			'<', 'E', 'S', 'C', '>', ',', '<', 'E', 'S', 'C', '>', ' ', 't',
			'o', ' ', 'e', 'n', 't', 'e', 'r', ' ', 'B', 'l', 'u', 'e', 'R',
			'S', '+', ' ', 'c', 'o', 'n', 'f', 'i', 'g', 'u', 'r', 'a', 't',
			'o', 'r', ' ', '+', '+', '+' };

	private static final byte[] BT_OK = { 'O', 'K' };

	private static final byte[] BT_ERROR = { 'E', 'R', 'R', 'O', 'R' };

	private static final byte[] BT_NO_ANSWER = { 'N', 'O', ' ', 'A', 'N', 'S',
			'W', 'E', 'R' };

	private static final byte[] CMD_CONNECT = { 'a', 't', 'd', ' ' };

	private static final byte[] CMD_DISCONNECT = { 'a', 't', 'h' };

	private static final byte[] CMD_RETURN_TO_ONLINE_STATUS = { 'a', 't', 'o' };

	private static final byte[] CMD_CONNECTION_ACCEPT = { 'a', 't', 'a' };

	private static final byte[] CMD_INQIRY_NO_SERVICES = { 'a', 't', '*', '*',
			'b', 'd', 'i', 'n', 'q', ' ', '1' };

	private static final byte[] CMD_INQIRY_GET_SERVICES = { 'a', 't', '*', '*',
			'b', 'd', 'i', 'n', 'q' };

	private static final byte[] CMD_RESET = { 'a', 't', '*', '*', 'r', 'e',
			's', 'e', 't' };

	private static final byte[] CMD_ENABLE_ERROR_CODES = { 'a', 't', 'w', '1' };

	private static final byte[] CMD_ENTER_CONFIG = { 'a', 't', 'c', 'o', 'n',
			'f' };

	private static final byte[] CMD_EXIT_CONFIG = { 'e', 'x', 'i', 't' };

	private static final byte[] CMD_ESCAPE_SEQUENCE = { '+', '+', '+' };

	/**
	 * The maximum number of bytes which can be sent in one packet.
	 */
	public static final int RECEIVE_MTU = 512;

	/**
	 * Result code for {@link #getResult()}.
	 */
	public static final int RESULT_UNDEFINED = 0, RESULT_OK = 1,
			RESULT_ERROR = 2;

	/**
	 * Mode code for {@link #getMode()}.<br>
	 * The module can receive AT commands in this mode. After power-up or reset
	 * the module is in this mode.
	 */
	public static final int MODE_AT = 0;

	/**
	 * Mode code for {@link #getMode()}.<br>
	 * The module can receive configuration commands in this mode. In normal
	 * operation, this mode is not used.
	 */
	public static final int MODE_CONFIG = 1;

	/**
	 * Mode code for {@link #getMode()}.<br>
	 * If a connection between this module and another device is established,
	 * the module is in this mode.
	 */
	public static final int MODE_CONNECTED = 2;

	/**
	 * Mode code for {@link #getMode()}.<br>
	 * The module is still connected to another device, but can receive
	 * AT-commands.
	 */
	public static final int MODE_CONNECTED_AT = 3;

	// only used for switchMode
	private static final int MODE_NONE = 4;

	private static byte[] dataBuffer, tmpRsp ;
	
	private static ByteLiFo currentRsp;

	private static int currentRspIndex, dataBufferStart, dataBufferEnd;

	private static boolean waitForResult, crReceived;

	private static int lastResult;

	private static boolean connectionInitiated;

	private static final boolean dbg = false;

	private static int mode, switchMode;

	private static BlueRS taskRS;

	/**
	 * Static constructor.
	 */
	static {
		currentRsp = new ByteLiFo(MAX_CURR_RSP_LEN);
		dataBuffer = new byte[DATABUFFER_LEN];
		tmpRsp = new byte[MAX_TMP_RSP_LEN];

		init();
	}

	private static boolean compare(byte[] rsp, byte[] rspString, int start,
			int length) {
		for (int i = 0; i < length; i++) {
			if (rsp[start + i] != rspString[i])
				return false;
		}
		return true;
	}

	private static void setWaitForResult() {
		waitForResult = true;
		lastResult = RESULT_UNDEFINED;
	}

	/**
	 * Sends a command to the Bluetooth module.<br>
	 * 
	 * To use this command, the device must be in {@link #MODE_AT}. <br>
	 * After using this method, {@link #getResult()} can be used to get the
	 * result of this operation.
	 * 
	 * @param cmd
	 *            the command to send
	 */
	public static void sendCommand(String cmd) {
		byte[] temp = new byte[cmd.length()];
		for(int i = 0; i < cmd.length(); i++){
			temp[i] =(byte)cmd.charAt(i);
		}
		sendCommand(temp);
	}

	/**
	 * Sends a command to the Bluetooth module.<br>
	 * 
	 * See {@link #sendCommand(String)}.
	 * 
	 * @param cmd
	 *            the command to send
	 */
	public static void sendCommand(byte[] cmd) {
		out.write(cmd);
		out.write(CRLF);
		setWaitForResult();
	}

	/**
	 * Returns true if a connection has been initiated by a remote device.<br>
	 * 
	 * When a remote device wants to connect to this module, the module sends a
	 * "RING" command. Depending on the configuration of the module, the
	 * connection is accepted immediately or only if {@link #connectionAccept()}
	 * is called.
	 * 
	 * @return true if a connection has been initiated
	 */
	public static boolean isConnectionInitiated() {
		return connectionInitiated;
	}

	/**
	 * Accepts a initiated connection.<br>
	 * 
	 * Use {@link #isConnectionInitiated()} to check if a connection has been
	 * initiated by a remote device.
	 */
	public static void connectionAccept() {
		if (connectionInitiated) {
			sendCommand(CMD_CONNECTION_ACCEPT);
			connectionInitiated = false;
		} else {
			//OutT.println("connectionAccept(): no connection initiated");
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Starts an inquiry.<br>
	 * 
	 * To use this command, the device must be in {@link #MODE_AT}. <br>
	 * 
	 * @param getServices
	 *            <code>true</code> if a list of the services of the found
	 *            devices should be retrieved, else false
	 */
	public static void inquiry(boolean getServices) {
		if (mode == MODE_AT) {
			if (getServices) {
				sendCommand(CMD_INQIRY_GET_SERVICES);
			} else {
				sendCommand(CMD_INQIRY_NO_SERVICES);
			}
		} else {
			//OutT.print("inquiry(): wrong mode: ");
			//OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Connects to a device.<br>
	 * 
	 * To use this command, the device must be in {@link #MODE_AT}. <br>
	 * If the device is connected {@link #getMode()} returns
	 * {@link #MODE_CONNECTED}. The result code will be <code>RESULT_OK</code>.
	 * If the target device does not respond, the result code will be
	 * <code>RESULT_ERROR</code>.
	 * 
	 * @param connectTo
	 *            This string represents a Bluetooth address (e.g.
	 *            "0002ee36b272") or a previously inquired device (e.g. "d1").
	 */
	public static void connect(String connectTo) {
		if (mode == MODE_AT) {
			out.write(CMD_CONNECT);
			for(int i = 0; i < connectTo.length(); i++){
				out.write((byte)connectTo.charAt(i));
			}
//			out.write(connectTo.getBytes());
			out.write(CRLF);
			setWaitForResult();
		} else {
			//OutT.print("connect(): wrong mode: ");
			//OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Connects to a device.<br>
	 * 
	 * See {@link #connect(String)}.
	 * 
	 * @param connectTo
	 *            This byte array represents a Bluetooth address (e.g.
	 *            "0002ee36b272") or a previously inquired device (e.g. "d1").
	 */
	public static void connect(byte[] connectTo) {
		if (mode == MODE_AT) {
			out.write(CMD_CONNECT);
			out.write(connectTo);
			out.write(CRLF);
			setWaitForResult();
		} else {
			//OutT.print("connect(): wrong mode: ");
			//OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Disconnects the connection.<br>
	 * 
	 * To use this command, the device must be in {@link #MODE_CONNECTED_AT}.
	 */
	public static void disconnect() {
		if (mode == MODE_CONNECTED_AT) {
			sendCommand(CMD_DISCONNECT);
			mode = MODE_AT;
		} else {
			//OutT.print("disconnect(): wrong mode: ");
			//OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Switches from connection-mode to AT-mode {@link #MODE_CONNECTED_AT}.<br>
	 * 
	 * To use this command, the device must be in {@link #MODE_CONNECTED}.<br>
	 * It is required to wait 1 second before using this command (no data
	 * transfer).<br>
	 * Because the device is still connected but in AT-mode, the resulting mode
	 * will be {@link #MODE_CONNECTED_AT} and not {@link #MODE_AT}. Make shure
	 * the device is in that mode before using any other commands (e.g.
	 * {@link #disconnect()}.
	 */
	public static void switchToConnectedATMode() {
		if (mode == MODE_CONNECTED) {
			out.write(CMD_ESCAPE_SEQUENCE);
			switchMode = MODE_CONNECTED_AT;
			setWaitForResult();
		} else {
			//OutT.print("switchToATMode(): wrong mode: ");
			//OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Switches from {@link #MODE_AT} to {@link #MODE_CONFIG}.<br>
	 * 
	 * To use this command, the device must be in {@link #MODE_AT}.
	 * 
	 */
	public static void switchToConfigMode() {
		if (mode == MODE_AT) {
			out.write(CMD_ENTER_CONFIG);
			switchMode = MODE_CONFIG;
			setWaitForResult();
		} else {
			//OutT.print("switchToConfigMode(): wrong mode: ");
			//OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Returns from AT-mode to connection-mode.<br>
	 * 
	 * To use this command, the device must be in {@link #MODE_CONNECTED_AT}.
	 */
	public static void returnFromATMode() {
		if (mode == MODE_CONNECTED_AT) {
			sendCommand(CMD_RETURN_TO_ONLINE_STATUS);
		} else {
			//OutT.print("returnFromATMode(): wrong mode: ");
			//OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Returns from {@link #MODE_CONFIG} to {@link #MODE_AT}.<br>
	 * 
	 * To use this command, the device must be in {@link #MODE_CONFIG}.
	 * 
	 */
	public static void returnFromConfigMode() {
		if (mode == MODE_CONFIG) {
			out.write(CMD_EXIT_CONFIG);
			lastResult = RESULT_OK;
			mode = MODE_AT;
		} else {
			//OutT.print("returnFromConfigMode(): wrong mode: ");
			//OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Sends a reset command to the module.<br>
	 * 
	 * To use this command, the device must be in {@link #MODE_AT}.
	 * Configuration mode may be entered after using this command.
	 */
	public static void reset() {
		if (mode == MODE_AT) {
			sendCommand(CMD_RESET);
		} else {
			//OutT.print("reset(): wrong mode: ");
			//OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	/**
	 * Checks the space of the send buffer.<br>
	 * 
	 * If the device is not in {@link #MODE_CONNECTED}, the result will be -1.
	 * 
	 * @return number of bytes to be able to send or -1 if not in
	 *         {@link #MODE_CONNECTED}.
	 */
	public static int availableToSend() {
		if (mode != MODE_CONNECTED)
			return -1;
		return out.freeSpace();
	}

	/**
	 * Checks the number of bytes which are available to receive.<br>
	 * 
	 * If the device is not in {@link #MODE_CONNECTED}, the result will be -1.
	 * 
	 * 
	 * @return number of bytes which are available to receive or -1 if not in
	 *         {@link #MODE_CONNECTED}.
	 */
	public static int availableToReceive() {
		if (mode != MODE_CONNECTED)
			return -1;
		return (dataBufferEnd - dataBufferStart + DATABUFFER_LEN)
				% DATABUFFER_LEN;
	}

	/**
	 * Writes data to the device.<br>
	 * 
	 * This command must only be used in {@link #MODE_CONNECTED} (see
	 * {@link #getMode()}).
	 * 
	 * @param b
	 *            data to write to the device
	 * @param len
	 *            length of the data to write to the device
	 * @return true if the data has been sent, else false
	 */
	public static boolean write(byte[] b, int len) {
		if (mode == MODE_CONNECTED) {
			out.write(b, 0, len);
			return true;
		}
		return false;
	}

	/**
	 * Reads data from the device.<br>
	 * 
	 * This command must only be used in {@link #MODE_CONNECTED} (see
	 * {@link #getMode()}).
	 * 
	 * @param b
	 *            the byte buffer to which the data will be written
	 * @return number of bytes read, -1 if the device is not connected or 0 if
	 *         no data is available.<br>
	 *         If the number of bytes available will exceed the buffer size,
	 *         only the buffer size is returned.
	 */
	public static int read(byte[] b) {
		if (mode != MODE_CONNECTED){
			return -1; }
		int readLen = min((dataBufferEnd - dataBufferStart + DATABUFFER_LEN)
				% DATABUFFER_LEN, b.length);
		for (int i = 0; i < readLen; i++) {
			b[i] = dataBuffer[(dataBufferStart + i) % DATABUFFER_LEN];
		}
		dataBufferStart = (dataBufferStart + readLen) % DATABUFFER_LEN;
		return readLen;
	}
	
	/**
	 * Reads one byte from the device.<br>
	 * 
	 * This command must only be used in {@link #MODE_CONNECTED} (see
	 * {@link #getMode()}).
	 * 
	 * @return one byte from the device, -1 if the device is not connected or 0 if
	 *         no data is available.<br>
	 *         If the number of bytes available will exceed the buffer size,
	 *         only the buffer size is returned.
	 */
	public static byte read(){
		if (mode != MODE_CONNECTED) return -1; 
		if(availableToReceive() > 0){
			byte data = dataBuffer[(dataBufferStart) % DATABUFFER_LEN];
			dataBufferStart = (dataBufferStart + 1) % DATABUFFER_LEN;
			return data;
		}
		return  0;
	}

	/**
	 * Returns the result of the last operation.<br>
	 * 
	 * The values are <code>RESULT_UNDEFINED, RESULT_OK or RESULT_ERROR</code>.
	 * After calling a method which writes a command to the module (e.g.
	 * {@link #connect(String)}, the result will be RESULT_UNDEFINED. As soon
	 * as the module returns its status, the return value of
	 * <code>getResult()</code> will return <code>RESULT_OK</code> or
	 * <code>RESULT_ERROR</code>.
	 * 
	 * @return the result of the last operation.
	 */
	public static int getResult() {
		return lastResult;
	}

	/**
	 * Returns the current mode.<br>
	 * 
	 * The values are <code>MODE_AT, MODE_CONNECTED, MODE_CONNECTED_AT</code>
	 * and <code>MODE_CONFIG</code>
	 * 
	 * @return the current mode.
	 */
	public static int getMode() {
		return mode;
	}

	/**
	 * Receive Task<br>
	 * 
	 * <b>Do not call this method!</b>
	 */
	// @Override
	public void action() {
		if (in.available() > 0) {
			int lenRead = in.read(tmpRsp);
			if (dbg) {
				System.out.print('*');
				System.out.write(tmpRsp, 0, lenRead);
			}
			int tmpIndex;
			for (tmpIndex = 0; tmpIndex < lenRead; tmpIndex++) {
				if (mode == MODE_CONNECTED) {
					dataBuffer[dataBufferEnd] = tmpRsp[tmpIndex];
					currentRsp.push(tmpRsp[tmpIndex]);
					dataBufferEnd = (dataBufferEnd + 1) % DATABUFFER_LEN;
				} else {
					currentRsp.push(tmpRsp[tmpIndex]);
				}
				if (tmpRsp[tmpIndex] == CR) {
					crReceived = true;
					// return;
				} else if (crReceived && (tmpRsp[tmpIndex] == LF)) {
					
					crReceived = false;
					if (mode == MODE_CONNECTED) { // connection mode
						// check for "NO CARRIER"
						if (currentRsp.compare(BT_NO_CARRIER, 2, min(lenRead,
								BT_NO_CARRIER.length))) {
							//OutT.println("BT_NO_CARRIER");
							mode = MODE_AT;
						}else if (currentRsp.compare(BT_RESET, 2,
								BT_RESET.length)) {
							//OutT.println("BT_RESET");
							init();
						}
					} else { // AT mode
						// interpret commands
						if (currentRsp.compare(BT_CONNECT, 2,min(lenRead,
								BT_CONNECT.length))) {
							//OutT.println("BT_CONNECT");
							mode = MODE_CONNECTED;
							lastResult = RESULT_OK;
							connectionInitiated = false;
							resetDataBuffer();
							waitForResult = false;
						} else if (currentRsp.compare(BT_RING, 2, min(lenRead,
								BT_RING.length))) {
							//OutT.println("BT_RING");
							lastResult = RESULT_OK;
							connectionInitiated = true;
							mode = MODE_AT;
							waitForResult = false;
						} else if (currentRsp.compare(BT_NO_CARRIER, 2, min(
								lenRead, BT_NO_CARRIER.length))) {
							//OutT.println("BT_NO_CARRIER");
							connectionInitiated = false;
							lastResult = RESULT_ERROR;
							mode = MODE_AT;
							waitForResult = false;
						} else if (currentRsp.compare(BT_NO_ANSWER, 2, min(
								lenRead, BT_NO_ANSWER.length))) {
							//OutT.println("BT_NO_ANSWER");
							lastResult = RESULT_ERROR;
							mode = MODE_AT;
							waitForResult = false;
							connectionInitiated = false;
						} else if (currentRsp.compare(BT_RESET, 2,
								BT_RESET.length)) {
							//OutT.println("BT_RESET");
							init();
						}
					}
					if (waitForResult) {
						// get result
						if (currentRsp.compare(BT_OK, 2, BT_OK.length)) {
							switch (switchMode) {
							case MODE_AT:
								switchMode = MODE_NONE;
								mode = MODE_CONNECTED_AT;
								break;
							case MODE_CONFIG:
								switchMode = MODE_NONE;
								mode = MODE_CONFIG;
								break;
							case MODE_CONNECTED_AT:
								switchMode = MODE_NONE;
								mode = MODE_CONNECTED_AT;
								break;
							default:
								break;
							}
							lastResult = RESULT_OK;
							waitForResult = false;
						} else if (currentRsp.compare(BT_ERROR, 0,
								BT_ERROR.length)) {
							lastResult = RESULT_ERROR;
							waitForResult = false;
						}
					}
				}
			}
		}
	}

	/**
	 * Starts the BlueRS driver.<br>
	 * 
	 * This method must been called first to start the background task which is
	 * needed to receive and process characters from the Bluetooth device.
	 * 
	 */
	public static void start() {
		taskRS.period = RECEIVE_TASK_PERIOD;
		Task.install(taskRS);
	}

	/**
	 * Stops the BlueRS driver.<br>
	 * 
	 * If the driver is not used in a time critical application, there is no
	 * need to call this method. It will remove the background task to receive
	 * and process characters from the Bluetooth device. After
	 * <code>stop()</code>, {@link #start()} must be called to reactivate the
	 * driver.
	 * 
	 */
	public static void stop() {
		Task.remove(taskRS);
	}

	/**
	 * Enables error messages from the Bluetooth module.<br>
	 * 
	 * In addition to the AT commands, the Bluetooth module will display error
	 * codes after each command. A detailed description can be found in the
	 * Stollmann BlueRS+E/I manual (section 5.2). To display this error codes on
	 * the Target Log use {@link #setDebugging(boolean)}.
	 */
	public static void enableErrorMessages() {
		if (mode == MODE_AT) {
			sendCommand(CMD_ENABLE_ERROR_CODES);
		} else {
//			OutT.print("reset(): wrong mode: ");
//			OutT.println(mode);
			lastResult = RESULT_ERROR;
		}
	}

	private static void init() {
		crReceived = false;
		mode = MODE_AT;
		connectionInitiated = false;
		switchMode = MODE_NONE;
		currentRspIndex = 0;
		lastResult = RESULT_OK;
		waitForResult = false;
		resetDataBuffer();
	}

	private static void resetDataBuffer() {
		dataBufferStart = 0;
		dataBufferEnd = 0;
	}

	private static int min(int a, int b) {
		return (a <= b) ? a : b;
	}
	
	static{
		taskRS = new BlueRS();
	}
}
