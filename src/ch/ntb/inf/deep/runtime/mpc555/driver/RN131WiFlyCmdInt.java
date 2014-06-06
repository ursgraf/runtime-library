package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.util.IntFifo;
import ch.ntb.inf.deep.runtime.mpc555.driver.RN131WiFly;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/*
 * Changes:
 * 28.10.2013	NTB/KALA	initial version
 */

/**
 * Interface for the RN131WiFly driver to send and receive positive integer values.
 */

public class RN131WiFlyCmdInt extends Task{
	
	private static final int taskPeriod = 50;
	private static final int bufferSize = 15;
	private static IntFifo cmdBuffer;
	private static final byte cmdStartSymbol = 0x11;
	private static final String connectionTimeout = "0";	//no automatic disconnect
	
	private static Task tsk;
	
	//states of RN131WiFlyCmdInt
	/**
	 * No connection established with partner module.
	 */
	public static final int disconnected = 0;
	/**
	 * Initialize and configure the RN131WiFly module.
	 */
	public static final int configure = 1;
	/**
	 * Connecting to the partner module.
	 */
	public static final int connecting = 2;
	/**
	 * The RN131WiFly Module is connected to a partner module.
	 */
	public static final int connected = 3;
	/**
	 * Receiving a command from the partner module.
	 */
	public static final int receiveCmd = 4;
	
	private static int state = disconnected;
	
	//return values
	/**
	 * Returned if the process has been initiated successfully
	 */
	public static final int success = 0;
	/**
	 * Returned if module is not connected to a partner module
	 */
	public static final int notConnected = -1;
	/**
	 * Returned if the command to send has a invalid Value (negative etc.)
	 */
	public static final int illegalCmd = -2;
	/**
	 * Returned if there were no commands received -> buffer is empty
	 */
	public static final int bufferEmpty = -3;
	/**
	 * Returned if this process can not be initiated in the current mode
	 */
	public static final int wrongMode = -4;
	
	/*
	 * (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Task#action()
	 */
	public void action(){
		switch(state){
			case disconnected:
				if(RN131WiFly.tcpConnectionOpen()){
					state = connected;
				}
				break;
			case configure:
				if(RN131WiFly.inDataMode()){
					state = disconnected;
				}
				break;
			case connecting:
				if(RN131WiFly.tcpConnectionOpen()){
					state = connected;
				}
				else if(RN131WiFly.inDataMode()){
					state = disconnected; // connect failed -> module left command mode
				}
			case connected:
				while(RN131WiFly.availToRead() > 0){
					if(RN131WiFly.read() == cmdStartSymbol){
						state = receiveCmd;
						break;
					}
				}
				if(!RN131WiFly.tcpConnectionOpen()){
					state = disconnected;
				}
				break;
			case receiveCmd:
				if(RN131WiFly.availToRead() >= 4){
					int cmd = RN131WiFly.read() << 24;
					cmd |= (RN131WiFly.read() & 0xFF) << 16;
					cmd |= (RN131WiFly.read() & 0xFF) << 8;
					cmd |= (RN131WiFly.read() & 0xFF);
					cmdBuffer.enqueue(cmd);
					state = connected;
				}
				break;
		}
	}
	
	/**
	 * Initializes the RN131WiFly Module with the following parameters
	 * @param ssid SSID of adhoc network (SSID: Service Set Identifier)
	 * @param createSelf true: create adhoc network self, false: module will connect to network with ssid of first parameter
	 * @param ipAdr IP Address of module, with first two parts fix (169.254.x.x)
	 */
	public static void init(String ssid, boolean createSelf, String ipAdr){
		RN131WiFly.init(ssid, createSelf, ipAdr);
		state = configure;
	}
	
	/**
	 * Initiates connect to the partner module
	 * @param ipPartner IP Address of the partner Module
	 */
	/**
	 * Initiates connect to the partner module
	 * @param ipPartner IP Address of the partner module
	 * @return {@link #success} if process has been initiated, {@link #wrongMode} if module is not disconnected
	 */
	public static int connect(String ipPartner){
		if(state == disconnected){
			RN131WiFly.openTcpConnection(ipPartner, "2000",	connectionTimeout);
			state = connecting;
			return success;
		}
		else{
			return wrongMode;
		}
	}
	
	/**
	 * Initiates disconnect from the partner module
	 * @return {@link #success} if process has been initiated, {@link #notConnected} if module is already disconnected
	 */
	public static int disconnect(){
		if(state == connected || state == receiveCmd){
			RN131WiFly.closeConnection();
			state = disconnected;
			return success;
		}
		return notConnected;
	}
	
	/**
	 * Sends a command to the partner module
	 * @param cmd the command to send (positive integer >= 0)
	 * @return {@link #success} if the command was successfully sent,
	 * 			{@link #illegalCmd} if the command is less than 0,
	 * 			{@link #notConnected} if module is not connected to a partner
	 */
	public static int sendCmd(int cmd){
		if(state == connected){
			if(cmd >= 0){
				byte[] b = new byte[5];
				b[0] = cmdStartSymbol;
				b[1] = (byte) (cmd >> 24);
				b[2] = (byte) (cmd >> 16);
				b[3] = (byte) (cmd >> 8);
				b[4] = (byte) cmd;
				RN131WiFly.write(b);
				return success;
			}
			return illegalCmd;
		}
		else{
			return notConnected;
		}
	}
	
	/**
	 * Get a received command
	 * @return received command or {@link #bufferEmpty} if no command is in the input buffer
	 */
	public static int getReceivedCmd(){
		if(cmdBuffer.availToRead() > 0){
			return cmdBuffer.dequeue();
		}
		return bufferEmpty;
	}
	
	/**
	 * Returns the state of the RN131WiFlyCmdInt interface
	 * @return {@link #disconnected}, {@link #configure}, {@link #connected}
	 */
	public static int getState(){
		if(state == receiveCmd){
			return connected;
		}
		return state;
	}
	
	/**
	 * Returns if the initialization of the RN131WiFly finished  
	 * @return true: if finished, false: if not
	 */
	public static boolean initDone(){
		return RN131WiFly.initDone();
	}
	
	static{
		state = disconnected;
		cmdBuffer = new IntFifo(bufferSize);
		tsk = new RN131WiFlyCmdInt();
		tsk.period = taskPeriod;
		Task.install(tsk);
	}

}
