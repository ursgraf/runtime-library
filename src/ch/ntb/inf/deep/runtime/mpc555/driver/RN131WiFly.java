package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.runtime.ppc32.Task;
import ch.ntb.inf.deep.runtime.util.ByteFifo;
import ch.ntb.inf.deep.runtime.util.ByteLiFo;

/*
 * Changes:
 * 3.6.2014		Urs Graf			exception handling added
 * 30.10.2013 NTB/AK: Initial Version
 */

/**
 * Driver for Roving RN-131C WiFly Module. <br>
 *
 * Usage: <br>
 * Connect the RN131WiFly module to the second serial port of the MPC555 (RXD2 & TXD2).<br>
 * The WiFly module can also be configured with a Terminal Program like PuTTY with a baud rate
 * of 115200 kbps, no flow control. More information to the RN131WiFly can be found at 
 * <a href = "http://www.microchip.com/wwwproducts/Devices.aspx?dDocName=en558369">microchip.com</a><br>
 */

public class RN131WiFly extends Task{

	public static RN131WiFlyOutputStream out;
	public static RN131WiFlyInputStream in;
	
	private static OutputStream outWifi;
	private static InputStream inWifi;
	
	private static final int TMPLEN = 2048, MAX_CURR_RSP_LEN = 255;
	private static byte[] tmp;
	private static ByteLiFo currentRsp;
	
	private static final byte CR = 0xd, LF = 0xa;
	private static boolean crReceived;
	
	private static boolean MODE_CMD = false;
	private static boolean MODE_DATA = true;
	
	private static final int RN_OK = 0, NO_ADHOC = 1, CONNECT_FAILED = 2, CMD_ERR_OCCURED = 3,
					IN_CMD_MODE = 4, ERR_CLOSE_CON = 5, TCP_CON_CLOSED = 6;
	private static int rnError = RN_OK;
		
	private static boolean gotCMD = false, gotAOK = false, gotREADY = false, gotSAVE = false,
						gotCLOS = false, gotOPEN = false, gotEXIT = false, gotErr = false,
						gotConOnAdHoc = false, gotAdHocLost = false, gotConnectFailed = false,
						tcpOpen = false, gotListenOn = false,
						gotListenOnBefore = false, gotKeyWord = false;
	
	// if dbg set true, errors will occur if RN131 is in Command Mode (unknown commands will generate errors)
	private static final boolean dbg = false; 
	
	// Config Parameter for RN131
	private static String ssid;
	private static boolean createAdHoc;
	private static String ip_address;
	private static String connectToIp;
	private static String connectToPort;
	private static String commIdle;
	private static boolean done = false;
	private static boolean closeTcpCon = false;
	private static boolean openTcpCon = false;
	private static boolean configInitiated = false;
	private static boolean configDone = false;
	
	//Escaping Bytes (mechanism used from SLIP (Serial Line Internet Protocol))
	private static final byte END = (byte)0300;
	private static final byte ESC = (byte)0333;
	private static final byte ESC_END = (byte)0334;
	private static final byte ESC_ESC = (byte)0335;
	
	private static final byte closeCmd = (byte)0xD4;
	private static final byte openCmd = (byte)0xD5;
	private static final byte remoteCmd = (byte)0xD3;
	private static final byte enterCmdMode = (byte)0x24; //$ -> entering CMD Mode
	
	private static final byte subs_Cmd = (byte)0xF1;
	private static final byte subs_Cmd_subs = (byte)0xF3;
	private static final byte subs_OpenCmd = (byte)0xF2;
	private static final byte subs_CloseCmd = (byte)0xF5;
	private static final byte subs_RemoteCmd = (byte)0xE6;
	private static final byte subs_EnterCmdMode = (byte)0xF0;
	
	// States for Config
	private static final int ENTER_CMD = 1, WAIT_ON_CMD = 2, CONFIG_SSID = 3, CONFIG_JOIN = 4,
						CONFIG_CHAN = 5, CONFIG_IP_ADDR = 6, CONFIG_IP_NETMASK = 7, CONFIG_DHCP = 8,
						CONFIG_SAVE = 9, CONFIG_REBOOT = 10, IDLE = 11, CONFIG_UART_MODE = 12,
						/*CONFIG_IP_HOST = 13,*/ /*CONFIG_IP_REMOTE = 14,*/ CONFIG_CLOSE_CON = 15,
						CONFIG_EXIT = 16, CONFIG_OPEN_CON = 17, CONFIG_CON_TIMEOUT = 18,
						CONFIG_COMM_CLOSE = 19, CONFIG_COMM_OPEN = 20, CONFIG_COMM_REMOTE = 21;
	private static int configState = IDLE;
	
	private static final byte[] RN_CMD = {'C', 'M', 'D'};
	private static final byte[] RN_AOK = {'A', 'O', 'K'};
	private static final byte[] RN_STORING_CONF = {'S','t','o','r','i','n','g',' ','i','n',' ','c','o','n','f','i','g'};
	private static final byte[] RN_READY = {'*','R','E','A','D','Y','*'};
	private static final byte[] RN_ERR = {'E','R','R',':'};
	private static final byte[] RN_CON_FAIL = {'C','o','n','n','e','c','t',' ','F','A','I','L','E','D'};
	private static final byte[] RN_EXIT = {'E','X','I','T'};
	private static final byte[] RN_CLOS = {closeCmd};
	private static final byte[] RN_OPEN = {openCmd};
	private static final byte[] RN_REMOTE = {remoteCmd};
	private static final byte[] RN_ADHOC_CON = {'C','o','n','n','e','c','t','e','d',' ','v','i','a',' ','A','d','-','H','o','c',' ','o','n',' '};
	private static final byte[] RN_ADHOC_LOST = {'A','d','-','H','o','c',' ','i','s',' ','l','o','s','t'};
	private static final byte[] RN_LISTEN_ON = {'L','i','s','t','e','n',' ','o','n',' ','2','0','0','0'};	
	
	public static final int LENGTH_NEG_ERR = 11,
			OFFSET_NEG_ERR = -2, NULL_POINTER_ERR = -3, TCP_CONN_CLOSED_ERR = -4;
	
	private static ByteFifo rxQueueSlip, rxQueue, txQueue;
	private static final int QUEUE_LEN = 2047;
	private static byte temp;
	
	private static RN131WiFly t;
	
	
	/**
	 * Close active TCP Connection
	 */
	public static void closeConnection(){
		if (MODE_CMD){
			rnError = IN_CMD_MODE;
		}
		else{
			configState = ENTER_CMD;
			closeTcpCon = true;
			rnError = RN_OK;
		}
	}
	
	/**
	 * Open TCP Connection to other Device
	 * @param conToIp	IP Address of Device you want to connect to
	 * @param conToPort		Port Number to connect with
	 * @param conTimeout	TCP Connection gets closed when no Data is transfered 
	 * 						in the x Seconds (0 = Idle Timer deactivated)
	 */
	public static void openTcpConnection(String conToIp, String conToPort, String conTimeout){
		if (MODE_CMD){
			rnError = IN_CMD_MODE;
		}
		else if(!gotConOnAdHoc){
			rnError = NO_ADHOC;
		}
		else{
			connectToIp = conToIp;
			connectToPort = conToPort;
			commIdle = conTimeout;
			openTcpCon = true;
			configState = ENTER_CMD;
			rnError = RN_OK;
		}
	}
	
	/**
	 * Configure the RN131 WiFly with the following Parameters. <br>
	 * Note: Netmask set to 255.255.0.0, so use IP Addresses like 169.254.x.x 
	 * (first three parts of the IP must be the same, otherwise the modules can't communicate)
	 * @param conSsid		SSID of desired Network
	 * @param createSelf	true: create AdHoc Network with this Module, false: join Network with given SSID
	 * @param ip_adr		desired IP Address, note: Netmask 255.255.255.0
	 */
	public static void init(String conSsid, boolean createSelf, String ip_adr){
		if(MODE_CMD){
			rnError = IN_CMD_MODE;
		}
		else{
			ssid = conSsid;
			createAdHoc = createSelf;
			ip_address = ip_adr;
			configState = ENTER_CMD;
			rnError = RN_OK;
			configInitiated = true;
		}		
	}
	
	/**
	 * returns Error Status of RN131WiFly
	 * @return 	0 = RN_OK;
				1 = NO_ADHOC;
				2 = CONNECT_FAILED;
				3 = CMD_ERR_OCCURED;
				4 = IN_CMD_MODE;
				5 = ERR_CLOSE_CON;
				6 = TCP_CON_CLOSED;
	 */
	public static int getErrStatus(){
		return rnError;
	}
	
	/**
	 * @return true: TCP Connection open, false: TCP Connection closed
	 */
	public static boolean tcpConnectionOpen(){
		return (tcpOpen && !gotAdHocLost);
	}
	
	/**
	 * Returns if the initialization of the RN131WiFly finished  
	 * @return true: if finished, false: if not
	 */
	public static boolean initDone(){
		return (configDone && configInitiated);
	}
	
	/**
	 * Enters the config-mode of the RN131WiFly module and configures the module according
	 * to the flags previous set.
	 */
	private static void configure() {
		switch (configState){
			case ENTER_CMD:
				doEnterCmdState();
				break;
			case WAIT_ON_CMD:
				doWaitOnCmdState();
				break;
			case CONFIG_COMM_CLOSE:
				doConfigCommClose();
				break;
			case CONFIG_COMM_OPEN:
				doConfigCommOpen();
				break;
			case CONFIG_COMM_REMOTE:
				doConfigCommRemote();
				break;
			case CONFIG_JOIN:
				if(createAdHoc){
					sendCMD_AOK("set wlan join 4\r\n",CONFIG_SSID);
				}
				else{
					sendCMD_AOK("set wlan join 1\r\n",CONFIG_SSID);
				}
				break;
			case CONFIG_SSID:
				doConfigSsid();
				break;
			case CONFIG_CHAN:
				if(createAdHoc){
					sendCMD_AOK("set wlan chan 1\r\n",CONFIG_IP_ADDR);
				}
				else{
					configState = CONFIG_IP_ADDR;
				}
				break;
			case CONFIG_IP_ADDR:
				doConfigIpAddr();
				break;
			case CONFIG_IP_NETMASK:
				sendCMD_AOK("set ip netmask 255.255.0.0\r\n",CONFIG_DHCP);//("set ip netmask 255.255.255.0\r\n",CONFIG_DHCP);
				break;
			case CONFIG_DHCP:
				sendCMD_AOK("set ip dhcp 0\r\n",CONFIG_UART_MODE);
				break;
			case CONFIG_CON_TIMEOUT:
				doConfigConTimeout();
				break;
			case CONFIG_UART_MODE:
				sendCMD_AOK("set uart mode 0\r\n",CONFIG_SAVE);
				break;
			case CONFIG_SAVE:
				doConfigSave();
				break;
			case CONFIG_REBOOT:
				doConfigRebootState();
				break;
			case CONFIG_OPEN_CON:
				doConfigOpenConState();
				break;
			case CONFIG_CLOSE_CON:
				doConfigCloseConState();
				break;
			case CONFIG_EXIT:
				doConfigExitState();
				break;
			case IDLE:
				// do nothing
				break;
			default:
				configState = IDLE;		
		}
	}
	
	/**
	 * Enters command mode of the RN131WiFly module
	 */
	private static void doEnterCmdState(){
		outWifi.write("$$$".getBytes());
		MODE_CMD = true;
		MODE_DATA = false;
		tcpOpen = false;
		configState = WAIT_ON_CMD;
	}
	
	/**
	 * Wait on CMD returned from RN131 when it receives $$$
	 */
	private static void doWaitOnCmdState(){
		if(gotCMD){
			rnError = RN_OK;
			if(dbg){
				outWifi.write("gotCMD\r\n".getBytes());
			}
			if(closeTcpCon){
				configState = CONFIG_CLOSE_CON;
				closeTcpCon = false;
			}
			else if(openTcpCon){
				configState = CONFIG_CON_TIMEOUT;
			}
			else{
				configState = CONFIG_COMM_CLOSE;
			}
			gotCMD = false;
		}
	}
	
	/**
	 * Configures the close statement
	 */
	private static void doConfigCommClose(){
		if(!done){
			outWifi.write("set comm close ".getBytes());
			outWifi.write(RN_CLOS);
			outWifi.write("\r\n".getBytes());
			done = true;
		}
		else if(gotAOK){
			if(dbg){
				outWifi.write("gotAOK\r\n".getBytes());
			}
			gotAOK = false;
			done = false;
			configState = CONFIG_COMM_OPEN;
		}
		else if(gotErr){
			rnError = CMD_ERR_OCCURED;
			gotErr = false;
			done = false;
			configState = CONFIG_EXIT;
		}
	}
	
	/**
	 * Configures the open statement
	 */
	private static void doConfigCommOpen(){
		if(!done){
			outWifi.write("set comm open ".getBytes());
			outWifi.write(RN_OPEN);
			outWifi.write("\r\n".getBytes());
			done = true;
		}
		else if(gotAOK){
			if(dbg){
				outWifi.write("gotAOK\r\n".getBytes());
			}
			gotAOK = false;
			done = false;
			configState = CONFIG_COMM_REMOTE;
		}
		else if(gotErr){
			rnError = CMD_ERR_OCCURED;
			gotErr = false;
			done = false;
			configState = CONFIG_EXIT;
		}
	}
	
	/**
	 * Configures the remote statement
	 */
	private static void doConfigCommRemote(){
		if(!done){
			outWifi.write("set comm remote ".getBytes());
			outWifi.write(RN_REMOTE);
			outWifi.write("\r\n".getBytes());
			done = true;
		}
		else if(gotAOK){
			if(dbg){
				outWifi.write("gotAOK\r\n".getBytes());
			}
			gotAOK = false;
			done = false;
			configState = CONFIG_JOIN;
		}
		else if(gotErr){
			rnError = CMD_ERR_OCCURED;
			gotErr = false;
			done = false;
			configState = CONFIG_EXIT;
		}
	}
	
	/**
	 * Configures the ssid
	 */
	private static void doConfigSsid(){
		if(!done){
			outWifi.write("set wlan ssid ".getBytes());
			outWifi.write(ssid.getBytes());
			outWifi.write("\r\n".getBytes());
			done = true;
		}
		else if(gotAOK){
			if(dbg){
				outWifi.write("gotAOK\r\n".getBytes());
			}
			gotAOK = false;
			done = false;
			configState = CONFIG_CHAN;
		}
		else if(gotErr){
			rnError = CMD_ERR_OCCURED;
			gotErr = false;
			done = false;
			configState = CONFIG_EXIT;
		}
	}
	
	/**
	 * Configures the IP Address
	 */
	private static void doConfigIpAddr(){
		if(!done){
			outWifi.write("set ip address ".getBytes());
			outWifi.write(ip_address.getBytes());
			outWifi.write("\r\n".getBytes());
			done = true;
		}
		else if(gotAOK){
			if(dbg){
				outWifi.write("gotAOK\r\n".getBytes());
			}
			gotAOK = false;
			done = false;
			configState = CONFIG_IP_NETMASK;
		}
		else if(gotErr){
			rnError = CMD_ERR_OCCURED;
			gotErr = false;
			done = false;
			configState = CONFIG_EXIT;
		}
	}
	
	/**
	 * Configures the connection timeout (after x Seconds of no data transmission, the
	 * TCP connection is closed)
	 */
	private static void doConfigConTimeout(){
		if(!done){
			outWifi.write("set comm idle ".getBytes());
			outWifi.write(commIdle.getBytes());
			outWifi.write("\r\n".getBytes());
			done = true;
		}
		else if(gotAOK){
			if(dbg){
				outWifi.write("gotAOK\r\n".getBytes());
			}
			gotAOK = false;
			done = false;
			configState = CONFIG_SAVE;
		}
		else if(gotErr){
			rnError = CMD_ERR_OCCURED;
			gotErr = false;
			done = false;
			configState = CONFIG_EXIT;
		}
	}
	
	/**
	 * Saves the actual configuration
	 */
	private static void doConfigSave(){
		if(!done){
			outWifi.write("save\r\n".getBytes());
			done = true;
		}
		else if(gotSAVE){
			if(dbg){
				outWifi.write("gotSAVE\r\n".getBytes());
			}
			gotSAVE = false;
			done = false;
			if(openTcpCon){
				configState = CONFIG_OPEN_CON;
			}
			else{
				configState = CONFIG_REBOOT;
			}
		}
	}
	
	/**
	 * Reboots the RN131WiFly
	 */
	private static void doConfigRebootState(){
		if(!done){
			outWifi.write("reboot\r\n".getBytes());
			gotListenOn = false;
			done = true;
		}
		else if(gotREADY){
			if(dbg){
				outWifi.write("gotREADY\r\n".getBytes());
			}
			gotREADY = false;
			done = false;
			MODE_CMD = false;
			MODE_DATA = true;
			outWifi.write("\r\n\r\n".getBytes());
			configState = IDLE;
			configDone = true;
		}
	}
	
	/**
	 * Open a TCP Connection
	 */
	private static void doConfigOpenConState(){
		if(!done){
			outWifi.write("open ".getBytes());
			outWifi.write(connectToIp.getBytes());
			outWifi.write(" ".getBytes());
			outWifi.write(connectToPort.getBytes());
			outWifi.write("\r\n".getBytes());
			done = true;
			openTcpCon = false;
		}
		else if(gotOPEN){
			if(dbg){
				outWifi.write("gotOPEN\r\n".getBytes());
			}
			gotOPEN = false;
			if(gotCLOS){
				tcpOpen = false;
				gotCLOS = false;
			}
			else{
				tcpOpen = true;
			}
			MODE_CMD = false;
			MODE_DATA = true;
			done = false;
			configState = IDLE;
		}
		else if (gotAdHocLost){
			if(dbg){
				outWifi.write("AdHoc lost!\r\n".getBytes());
			}
			done = false;
			rnError = NO_ADHOC;
			configState = CONFIG_EXIT;
		}
		else if (gotConnectFailed){
			if(dbg){
				outWifi.write("Connect failed received.\r\n".getBytes());
			}
			done = false;
			gotConnectFailed = false;
			rnError = CONNECT_FAILED;
			configState = CONFIG_EXIT;
		}
		else if (gotErr){
			if(dbg){
				outWifi.write("gotErr\r\n".getBytes());
			}
			rnError = CMD_ERR_OCCURED;
			gotErr = false;
			done = false;
			configState = CONFIG_EXIT;
		}
	}
	
	/**
	 * Close the TCP connection
	 */
	private static void doConfigCloseConState(){
		if(!done){
			outWifi.write("close\r\n".getBytes());
			done = true;
		}
		else if(gotCLOS){
			if(dbg){
				outWifi.write("gotCLOS\r\n".getBytes());
			}
			gotCLOS = false;
			done = false;
			configState = CONFIG_EXIT;
		}
		else if(gotErr){
			if(dbg){
				outWifi.write("gotErr\r\n".getBytes());
			}
			rnError = ERR_CLOSE_CON;
			gotErr = false;
			done = false;
			configState = CONFIG_EXIT;
		}
	}
	
	/**
	 * Exits the command mode of the RN131WiFly
	 */
	private static void doConfigExitState(){
		if(!done){
			outWifi.write("exit\r\n".getBytes());
			done = true;
		}
		else if(gotEXIT){
			if(dbg){
				outWifi.write("gotEXIT\r\n".getBytes());
			}
			gotEXIT = false;
			done = false;
			MODE_CMD = false;
			MODE_DATA = true;
			configState = IDLE;
		}
	}
	
	/**
	 * Use this for RN131 Commands which are confirmed with "AOK\r\n"
	 * @param cmd: Command to the RN131
	 * @param nextState: changes state for configure() to the next desired state
	 */
	private static void sendCMD_AOK(String cmd, int nextState){
		if(!done){
			byte[] temp = cmd.getBytes();
			outWifi.write(temp);
			done = true;
		}
		else if(gotAOK){
			if(dbg){
				outWifi.write("gotAOK\r\n".getBytes());
			}
			gotAOK = false;
			done = false;
			configState = nextState;
		}
		else if(gotErr){
			rnError = CMD_ERR_OCCURED;
			gotErr = false;
			done = false;
			configState = CONFIG_EXIT;
		}
	}
	
	/**
	 * Do not call this Method!!
	 */
	public void action(){
		try {
			if (inWifi.available() > 0){
				int availLen =  inWifi.read(tmp);
				int tmpIndex;
				for (tmpIndex = 0; tmpIndex < availLen; tmpIndex++){
					checkKeyWords(tmpIndex, availLen);
					if(tcpConnectionOpen() && !gotKeyWord){
						int writeLen = rxQueueSlip.availToWrite();
						if(!(writeLen < (availLen-tmpIndex))){
							rxQueueSlip.enqueue(tmp[tmpIndex]);
						}
					}
					if(gotListenOnBefore){
						gotListenOn = true;
					}
					gotKeyWord = false;
				}
			}
		} catch (IOException e) {}
		if(txQueue.availToRead() > 0){
			if(tcpConnectionOpen()){
				for(int writeLen = txQueue.availToRead() ; writeLen > 0; writeLen--){
					outWifi.write(txQueue.dequeue());
				}
			}
			else{
				rnError = TCP_CON_CLOSED;
			}
		}
		if(rxQueueSlip.availToRead() > 0){
			receiveSlipPacket();
		}
		configure();
	}
	
	/**
	 * Checks if a module-specific Character is received
	 * @param index: current index position in receive buffer
	 * @param availableLen: length of the receive buffer
	 */
	private static void checkKeyWords(int index, int availableLen){
		currentRsp.push(tmp[index]);
		if (tmp[index] == CR){
			crReceived = true;
		}
		else if (crReceived && (tmp[index] == LF)){
			crReceived = false;
			if(MODE_CMD){
				if (currentRsp.compare(RN_CMD, 2, min(availableLen, RN_CMD.length))){
					gotCMD = true;
				}
				else if (currentRsp.compare(RN_AOK, 2, min(availableLen, RN_AOK.length))){
					gotAOK = true;
				}
				else if (currentRsp.compare(RN_READY, 2, min(availableLen, RN_READY.length))){
					gotREADY = true;
				}
				else if (currentRsp.compare(RN_STORING_CONF, 2, min(availableLen, RN_STORING_CONF.length))){
					gotSAVE = true;
				}
				else if (currentRsp.compare(RN_EXIT, 2, min(availableLen, RN_EXIT.length))){
					gotEXIT = true;
				}
			}
		}
		else if (tmp[index] == LF){
			if (currentRsp.compare(RN_CLOS, 0, min(availableLen, RN_CLOS.length))){
				gotCLOS = true;
				gotKeyWord = true;
				if(MODE_DATA){
					tcpOpen = false;
				}
			}
		}
		else if (currentRsp.compare(RN_OPEN, 0, min(availableLen, RN_OPEN.length))){
			if(configState != CONFIG_COMM_OPEN){	
				if(MODE_DATA){
					tcpOpen = true;
				}
				else{
					gotOPEN = true;
					gotCLOS = false; //to prevent that occurence of two close after unsuccessful connect prevents next open of connection
				}
				gotKeyWord = true;
			}
		}
		else if (currentRsp.compare(RN_CLOS, 0, min(availableLen, RN_CLOS.length))){
			if(MODE_DATA){
				tcpOpen = false;
			}
			gotCLOS = true;
			gotKeyWord = true;
		}
		else if (currentRsp.compare(RN_ADHOC_CON, 0, min(availableLen, RN_ADHOC_CON.length))){
			gotConOnAdHoc = true;
			gotAdHocLost = false;
		}
		else if (MODE_CMD && currentRsp.compare(RN_ERR, 0, min(availableLen, RN_ERR.length))){
			gotErr = true;
		}
		else if (currentRsp.compare(RN_ADHOC_LOST, 0, min(availableLen, RN_ADHOC_LOST.length))){
			gotAdHocLost = true;
			gotConOnAdHoc = false;
			closeConnection();
			rnError = NO_ADHOC;
		}
		else if (currentRsp.compare(RN_CON_FAIL, 0, min(availableLen, RN_CON_FAIL.length))){
			gotConnectFailed = true;
		}
		else if (currentRsp.compare(RN_LISTEN_ON, 0, RN_LISTEN_ON.length)){//min(availableLen, RN_LISTEN_ON.length))){
			gotListenOnBefore = true;
		}
		else if (currentRsp.compare(RN_REMOTE, 0, min(availableLen, RN_REMOTE.length))){
			gotKeyWord = true;
		}
	}
	
	/**
	 * Returns the smaller value of both parameters
	 * @param a
	 * @param b
	 * @return smaller of both input parameters
	 */
	private static int min(int a, int b) {
		return (a <= b) ? a : b;
	}
	
	/**
	 * Checks if RN131WiFly is in data mode
	 * @return true if in data mode, false if in command mode
	 */
	public static boolean inDataMode(){
		return MODE_DATA;
	}
	
	/**
	 * @return true if module has finished initialize and is waiting for connection
	 */
	public static boolean gotListenOnPort(){
		return gotListenOn;
	}
	
	/**
	 * @return if there is data ready to read
	 */
	//Methods for Read/Write of User In/Out
	public static int availToRead(){
		return rxQueue.availToRead();
	}
	
	/**
	 * @return if there is space left in the writebuffer
	 */
	public static int availToWrite(){
		return txQueue.availToWrite();
	}
	
	/**
	 * clear the receive buffer
	 */
	public static void clearReceiveBuffer(){
		rxQueueSlip.clear();
		rxQueue.clear();
	}
	
	/**
	 * clear the transmit buffer
	 */
	public static void clearTransmitBuffer(){
		txQueue.clear();
	}
	
	/**
	 * clear receive and transmit buffer
	 */
	public static void clear(){
		clearReceiveBuffer();
		clearTransmitBuffer();
	}
	
	/**
	 * Receives a SLIP escaped packet and decodes it
	 */
	// uses escaping from SLIP protocol: http://tools.ietf.org/html/rfc1055
	private static void receiveSlipPacket(){
		for(int i=rxQueueSlip.availToRead(); i > 0; i--){
			temp = rxQueueSlip.dequeue();
			switch(temp){
				case END:
					break;
				case ESC:
					temp = rxQueueSlip.dequeue();
					i--;
					switch(temp){
						case ESC_END:
							rxQueue.enqueue(END);
							break;
						case ESC_ESC:
							rxQueue.enqueue(ESC);
							break;
					}
					break;
				case subs_Cmd:
					temp = rxQueueSlip.dequeue();
					i--;
					switch(temp){
						case subs_OpenCmd:
							rxQueue.enqueue(openCmd);
							break;
						case subs_CloseCmd:
							rxQueue.enqueue(closeCmd);
							break;
						case subs_RemoteCmd:
							rxQueue.enqueue(remoteCmd);
							break;
						case subs_EnterCmdMode:
							rxQueue.enqueue(enterCmdMode);
							break;
						case subs_Cmd_subs:
							rxQueue.enqueue(subs_Cmd);
							break;
					}
					break;
				default:
					rxQueue.enqueue(temp);
			}
		}
	}
	
	/**
	 * Escapes a data packet which is sent over WiFi
	 * @param b byte array that contains the data
	 * @param len length of the byte array
	 * @return number of bytes which are sent (includes escaping/start/stop)
	 */
	// uses escaping from SLIP protocol: http://tools.ietf.org/html/rfc1055
	private static int sendSlipPacket(byte[] b, int len){
		int bufSpace = txQueue.availToWrite();
		int nofBytes = 0;
		
		txQueue.enqueue(END);
		nofBytes++;
		for(int i = 0; (i < len) && (nofBytes < bufSpace-1); i++){
			switch(b[i]){
				case END:
					txQueue.enqueue(ESC);
					txQueue.enqueue(ESC_END);
					nofBytes += 2;
					break;
				case ESC:
					txQueue.enqueue(ESC);
					txQueue.enqueue(ESC_ESC);
					nofBytes += 2;
					break;
				case openCmd:
					txQueue.enqueue(subs_Cmd);
					txQueue.enqueue(subs_OpenCmd);
					nofBytes += 2;
					break;
				case closeCmd:
					txQueue.enqueue(subs_Cmd);
					txQueue.enqueue(subs_CloseCmd);
					nofBytes += 2;
					break;
				case remoteCmd:
					txQueue.enqueue(subs_Cmd);
					txQueue.enqueue(subs_RemoteCmd);
					nofBytes += 2;
					break;
				case enterCmdMode:
					txQueue.enqueue(subs_Cmd);
					txQueue.enqueue(subs_EnterCmdMode);
					nofBytes += 2;
					break;
				case subs_Cmd:
					txQueue.enqueue(subs_Cmd);
					txQueue.enqueue(subs_Cmd_subs);
					nofBytes += 2;
					break;
				default:
					txQueue.enqueue(b[i]);
					nofBytes ++;
			}
		}
		txQueue.enqueue(END);
		nofBytes++;
		return nofBytes;
	}

	/**
	 * read from readbuffer
	 * @return
	 */
	public static int read(){
		return rxQueue.dequeue();
	}
	
	/**
	 * read data from readBuffer
	 * @param b destination byte array where to write the read data
	 * @return {@link #NULL_POINTER_ERR} if b is null, {@link #LENGTH_NEG_ERR} 
	 * if length is < 0, {@link OFFSET_NEG_ERR} if offset < 0, else read data length
	 */
	public static int read(byte[] b){
		return read(b, 0, b.length);
	}
	
	/**
	 * read data from readBuffer
	 * @param b destination byte array where to write the read data
	 * @param len how many bytes to read
	 * @return {@link #NULL_POINTER_ERR} if b is null, {@link #LENGTH_NEG_ERR} 
	 * if length is < 0, {@link OFFSET_NEG_ERR} if offset < 0, else read data length
	 */
	public static int read(byte[] b, int len){
		return read(b, 0, len);
	}
	
	/**
	 * read data from readBuffer
	 * @param b destination byte array where to write the read data
	 * @param off offset position in destination byte array to write
	 * @param len length of data to read
	 * @return {@link #NULL_POINTER_ERR} if b is null, {@link #LENGTH_NEG_ERR} 
	 * if length is < 0, {@link OFFSET_NEG_ERR} if offset < 0, else read data length
	 */
	public static int read(byte[] b, int off, int len){
		if (b == null)
			return NULL_POINTER_ERR;
		if (len < 0)
			return LENGTH_NEG_ERR;
		if (len == 0)
			return 0;
		if (off < 0)
			return OFFSET_NEG_ERR;
		int bufferLen = rxQueue.availToRead();
		if (len > bufferLen)
			len = bufferLen;
		if (len > b.length)
			len = b.length;
		if (len + off > b.length)
			len = b.length - off;
		for (int i = 0; i < len; i++) {
			b[off + i] = rxQueue.dequeue();
		}
		return len;
	}
	
	/**
	 * write data to writeBuffer
	 * @param b byte array with data to write
	 * @param off offset in byte array from which position data is written
	 * @param len length of data to write
	 * @return {@link #NULL_POINTER_ERR} if byte array == null, {@link #LENGTH_NEG_ERR}
	 * if len < 0, {@link #OFFSET_NEG_ERR} if off < 0, {@link #TCP_CONN_CLOSED_ERR} if 
	 * TCP connection is closed, else length of written data
	 */
	public static int write(byte[] b, int off, int len){
		if(tcpConnectionOpen()){
			
			if(b == null)
				return NULL_POINTER_ERR;
			if(len <= 0)
				return LENGTH_NEG_ERR;
			if(off < 0)
				return OFFSET_NEG_ERR;
			if(len + off > b.length)
				len = b.length - off;
			if(off > 0){
				byte[] bBuf = new byte[len];
				for(int i = 0; i < len; i++){
					bBuf[i] = b[off + i];
				}
				return sendSlipPacket(bBuf,len);
			}
			else{
				return sendSlipPacket(b,len);
			}
		}
		else{
			rnError = TCP_CON_CLOSED;
			return TCP_CONN_CLOSED_ERR;
		}
	}
	
	/**
	 * write data to writeBuffer
	 * @param b byte array with data to write
	 * @param len length of data to write
	 * @return {@link #NULL_POINTER_ERR} if byte array == null, {@link #LENGTH_NEG_ERR}
	 * if len < 0, {@link #OFFSET_NEG_ERR} if off < 0, {@link #TCP_CONN_CLOSED_ERR} if 
	 * TCP connection is closed, else length of written data
	 */
	public static int write(byte[] b, int len){
		return write(b, 0, len);
	}
	
	/**
	 * write data to writeBuffer
	 * @param b byte array with data to write
	 * @return {@link #NULL_POINTER_ERR} if byte array == null, {@link #LENGTH_NEG_ERR}
	 * if len < 0, {@link #OFFSET_NEG_ERR} if off < 0, {@link #TCP_CONN_CLOSED_ERR} if 
	 * TCP connection is closed, else length of written data
	 */
	public static int write(byte[] b){
		return write(b, 0, b.length);
	}
	
	/**
	 * write data to writeBuffer
	 * @param b byte array with data to write
	 */
	public static void write(byte b){
		byte[] bBuf = new byte[1];
		bBuf[0] = b;
		int i = write(bBuf, 0, 1);
		if (i != 1){
		}
	}
	
	//create/install Task, init arrays
	static{
		tmp = new byte[TMPLEN];
		currentRsp = new ByteLiFo(MAX_CURR_RSP_LEN);
		rxQueueSlip = new ByteFifo(QUEUE_LEN);
		rxQueue = new ByteFifo(QUEUE_LEN);
		txQueue = new ByteFifo(QUEUE_LEN);
		out = new RN131WiFlyOutputStream();
		in = new RN131WiFlyInputStream();
		t = new RN131WiFly();
		t.period = 20;
		Task.install(t);
		// initialize SCI2
		SCI2.start(115200, SCI2.NO_PARITY, (short)8);
		outWifi = SCI2.out;
		inWifi = SCI2.in;
		clear();
		rnError = RN_OK;
	}	
}
