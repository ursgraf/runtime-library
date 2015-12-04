package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.IOException;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCIInputStream;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCIOutputStream;
import ch.ntb.inf.deep.runtime.mpc555.driver.DigitalOutput;
import ch.ntb.inf.deep.runtime.ppc32.Task;
import ch.ntb.inf.deep.runtime.util.ByteFifo;
import ch.ntb.inf.deep.runtime.util.CmdInt;
import ch.ntb.inf.deep.runtime.util.SLIP;


/**
 * Driver for Roving RN-131C WiFly Module. <br>
 *
 * Usage: <br>
 * Connect the RN131WiFly module to the serial port of the MPC555 (RXD1/TXD1 or RXD2/TXD2).<br>
 * The WiFly module can also be configured with a Terminal Program like PuTTY with a baud rate
 * of 115200 kbps, no flow control. More information to the RN131WiFly can be found at 
 * <a href = "http://www.microchip.com/wwwproducts/Devices.aspx?dDocName=en558369">microchip.com</a><br>
 */

public class RN131 extends Task {
	
	public static enum State {
		wait,
		error,
		reset,
		boot,
		enterCmdMode,
		waitForCmdMode,
		configure,
		reboot,
		ready
	};
	

	/**
	 * Creates a new RN131 object.
	 * @param config	the configuration to use
	 */
	public RN131(RN131Config config) throws Exception {
		
		if (config.in == null)
			throw new NullPointerException("RN131Config.in");
		
		if (config.out == null)
			throw new NullPointerException("RN131Config.out");
		
		if (config.reset == null)
			throw new NullPointerException("RN131Config.reset");
		
		this.in = config.in;
		this.out = config.out;
		this.reset = config.reset;
		this.config = config;
		
		local_ip = config.localIP.getBytes();
		remote_ip = config.remoteIP.getBytes();
		ssid = config.ssid.getBytes();
//		passphrase = config.passphrase.getBytes();
		passphrase = "0".getBytes();
		
		if (config.channel < 1 || config.channel > 13)
			throw new IOException("RN131Config.channel out of range");

		channel[0] = (byte)((config.channel >= 10) ? '1' : ' ');
		channel[1] = (byte)((config.channel % 10) + '0');
		
		period = 20;
		LOCAL_HEARTBEAT = sec2ticks(1);
		REMOTE_HEARTBEAT = sec2ticks(2);
		
		cmd = new CmdInt(new SLIP(rx, tx));
		
		Task.install(this);
	}

	/**
	 * Gets the internal state of the driver.
	 * @return current state of the driver
	 */
	public State getState() {
		return (state == State.wait) ? last : state;
	}

	/**
	 * Checks if a TCP connection 
	 * @return true if a TCP connection exists, false otherwise
	 */
	public boolean connected() {
		return hostConnected;
	}
	
	private void connected(boolean value) {
		hostConnected = value;
	}

	/**
	 * Resets the internal state machine of the driver.
	 */
	public void reset() {
		state = State.reset;
		next = State.reset;
	}

	/**
	 * Do not call this method.
	 */
	public void action() {
		if (state != State.wait)
			last = state;
		
		state = next;
		
		if (state != State.ready)
			connected(false);
		
		switch (state) {
			case reset:
				reset.set(false);
				next(1, State.boot);
				break;
				
			case boot:
				reset.set(true);
				if (config.configure)
					next(2, State.enterCmdMode);
				else
					next(2, State.ready);
				break;
				
			case enterCmdMode:
				out.write(CMD_MODE);
				next(0.3, State.waitForCmdMode);
				break;
		
			case waitForCmdMode:
				if (checkResult(CMD_MODE_ENTERED))
					next(State.configure);
				break;
				
			case configure:
				if (configure())
					next(State.reboot);
				break;
				
			case reboot:
				if (reboot())
					next(1, State.ready);
				break;
				
			case ready:
				while (in.available() > 0) {
					byte b = (byte)in.read();
					remoteHeartbeat = REMOTE_HEARTBEAT;
					if (b == CLOSE) {
						connected(false);
					}
					else {
						connected(true);
					}
					
					if (b == OPEN) {
						// ignore
					}
					else if (b == CLOSE) {
						// ignore
					}
					else if (b == HEARTBEAT) {
						// ignore
					}
					else if (rxEscapeMode) {
						if (b == ESCAPED_OPEN) {
							rx.enqueue(OPEN);
						}
						else if (b == ESCAPED_CLOSE) {
							rx.enqueue(CLOSE);
						}
						else if (b == ESCAPED_HEARTBEAT) {
							rx.enqueue(HEARTBEAT);
						}
						else if (b == ESCAPED_CMD) {
							rx.enqueue(CMD);
						}
						else if (b == ESCAPED_ESCAPE) {
							rx.enqueue(ESCAPE);
						}
						else {
							rx.enqueue(b);
						}
						rxEscapeMode = false;
					}
					else if (b == ESCAPE) {
						rxEscapeMode = true;
					}
					else {
						rx.enqueue(b);
					}
				}
				
				if (remoteHeartbeat <= 0) {
					remoteHeartbeat = 0;
					connected(false);
				}
				else {
					remoteHeartbeat--;
					
					if (remoteHeartbeat < 0)
						remoteHeartbeat = 0;
				}
				
				if (connected() && localHeartbeat <= 0) {
//				if (localHeartbeat <= 0) {
					out.write(HEARTBEAT);
					localHeartbeat = LOCAL_HEARTBEAT;
				}
				else {
					localHeartbeat--;
					
					if (localHeartbeat < 0)
						localHeartbeat = 0;
				}
				
				if (connected()) {
					while (tx.availToRead() > 0) {
						byte b = 0;
						try {
							b = (byte)tx.dequeue();
						}
						catch (Exception e) {
							break;
						}
						if (b == OPEN) {
							out.write(ESCAPE);
							out.write(ESCAPED_OPEN);
						}
						else if (b == CLOSE) {
							out.write(ESCAPE);
							out.write(ESCAPED_CLOSE);
						}
						else if (b == HEARTBEAT) {
							out.write(ESCAPE);
							out.write(ESCAPED_HEARTBEAT);
						}
						else if (b == CMD) {
							out.write(ESCAPE);
							out.write(ESCAPED_CMD);
						}
						else if (b == ESCAPE) {
							out.write(ESCAPE);
							out.write(ESCAPED_ESCAPE);
						}
						else {
							out.write(b);
						}
						localHeartbeat = LOCAL_HEARTBEAT;
					}
				}
				break;
			
			case wait:
				if (waitTicks <= 0) {
					waitTicks = 0;
					next(stateAfterWait);
				}
				else {
					waitTicks--;
				}
				break;
				
			case error:
				break;
		
			default:
				next(State.reset);
				break;
		}
	}
	
	private boolean configure() {
		boolean done = false;
		switch (configStep) {
			case 0:
				done = sendCommand(CMD_UART_MODE);
				break;
				
			case 1:
				done = sendCommand(CMD_PRINTLVL);
				break;
				
			case 2:
				done = sendCommand(CMD_COMM_OPEN, CMD_COMM_OPEN_BYTE, CMD_RESULT_OK);
				break;
				
			case 3:
				done = sendCommand(CMD_COMM_CLOSE, CMD_COMM_CLOSE_BYTE, CMD_RESULT_OK);
				break;
				
			case 4:
				done = sendCommand(CMD_COMM_REMOTE, CMD_COMM_REMOTE_BYTE, CMD_RESULT_OK);
				break;
				
			case 5:
				done = sendCommand(CMD_COMM_IDLE);
				break;
				
			case 6:
				done = sendCommand(CMD_IP_ADDRESS, local_ip, CMD_RESULT_OK);
				break;
				
			case 7:
				done = sendCommand(CMD_IP_HOST, remote_ip, CMD_RESULT_OK);
				break;
				
			case 8:
				done = sendCommand(CMD_IP_NETMASK);
				break;
				
			case 9:
				done = sendCommand(CMD_IP_FLAGS);
				break;
				
			case 10:
				if (config.apMode)
					done = sendCommand(CMD_IP_DHCP_SERVER);
				else
					done = sendCommand(CMD_IP_DHCP_CLIENT);
				break;
				
			case 11:
				if (config.apMode)
					done = sendCommand(CMD_WLAN_AP);
				else
					done = sendCommand(CMD_WLAN_CLIENT);
				break;
				
			case 12:
				if (config.apMode)
					done = sendCommand(CMD_AP_SSID, ssid, CMD_RESULT_OK);
				else
					done = sendCommand(CMD_WLAN_SSID, ssid, CMD_RESULT_OK);
				break;
				
			case 13:
				if (config.apMode)
					done = sendCommand(CMD_AP_PHRASE, passphrase, CMD_RESULT_OK);
				else
					done = sendCommand(CMD_WLAN_PHRASE, passphrase, CMD_RESULT_OK);
				break;
				
			case 14:
				done = sendCommand(CMD_WLAN_AUTH);
				break;
				
			case 15:
				if (config.apMode)
					done = sendCommand(CMD_WLAN_CHANNEL, channel, CMD_RESULT_OK);
				else
					done = sendCommand(CMD_WLAN_CHANNEL_AUTO);
				break;
				
			case 16:
				if (config.autoConnect)
					done = sendCommand(CMD_WLAN_AUTOCONNECT_ON);
				else
					done = sendCommand(CMD_WLAN_AUTOCONNECT_OFF);
				break;
				
			case 17:
				if (config.useExternalAntenna)
					done = sendCommand(CMD_WLAN_EXTANTENNA);
				else
					done = sendCommand(CMD_WLAN_INTANTENNA);
				break;
				
			case 18:
				done = sendCommand(CMD_SAVE, CMD_SAVE_OK);
				break;
				
			default:
				configStep = 0;
				return true;
		}
		if (done) {
			configStep++;
		}
		return false;
	}
	
	private boolean sendCommand(byte[] cmd) {
		return sendCommand(cmd, CMD_RESULT_OK);
	}
	
	private boolean sendCommand(byte[] cmd, byte[] result) {
		return sendCommand(cmd, null, result);
	}
	
	private boolean sendCommand(byte[] cmd1, byte[] cmd2, byte[] result) {
		if (commandSent) {
			if (checkResult(result)) {
				commandSent = false;
				return true;
			}
		}
		else {
			out.write(cmd1);
			
			if (cmd2 != null)
				out.write(cmd2);
			
			out.write(CMD_TERMINATOR);
			commandSent = true;
		}
		return false;
	}
	
	private boolean checkResult(byte[] result) {
		while (in.available() > 0) {
			byte b = (byte)in.read();
			if (b == '\n') {
				if (compare(rxcmd, result))
					return true;
				else {
//					next(State.error);
					rxcmd.clear();
				}
			}
			else if (isWhitespace(b)) continue;
			else rxcmd.enqueue(b);
		}
		return false;
	}
	
	private boolean reboot() {
		if (commandSent) {
			while (in.available() > 0) {
				byte b = (byte)in.read();
				if (rxcmd.availToRead() == CMD_REBOOT_OK.length) {
					if (compare(rxcmd, CMD_REBOOT_OK)) {
						commandSent = false;
						return true;
					}
					else {
						next(State.error);
					}
				}
				else if (isWhitespace(b)) continue;
				else rxcmd.enqueue(b);
			}
		}
		else {
			out.write(CMD_REBOOT);
			out.write(CMD_TERMINATOR);
			commandSent = true;
		}
		return false;
	}
	
	private void next(State s) {
		next = s;
	}
	
	private void next(double sec, State s) {
		next = State.wait;
		stateAfterWait = s;
		waitTicks = sec2ticks(sec);
	}
	
	private int sec2ticks(double sec) {
		return (int)(1000 * sec / period);
	}
	
	private static boolean isWhitespace(byte b) {
		return (b == 0 || b == '$' || b == ' ' || b == '\t' || b == '\r' || b == '\n');
	}
	
	private static boolean compare(ByteFifo x, byte[] y) {
		try {
			for (int i = 0; i < y.length; i++)
				if (x.dequeue() != y[i])
					return false;
			
			return true;
		}
		catch (IOException e) { }
		return false;
	}

	public ByteFifo rx = new ByteFifo(2047);
	public ByteFifo tx = new ByteFifo(2047);
	public CmdInt cmd;
	
	private SCIInputStream in;
	private SCIOutputStream out;
	private DigitalOutput reset;
	private State state = State.reset;
	private State last = State.reset;
	private State next = State.reset;
	private State stateAfterWait = State.reset;
	private int waitTicks = 0;
	private int localHeartbeat = 0;
	private int remoteHeartbeat = 0;
	private int configStep = 0;
	
	private boolean commandSent = false;
	
	private boolean hostConnected = false;
	
	private ByteFifo rxcmd = new ByteFifo(127);
	private boolean rxEscapeMode = false;
	
	private final int LOCAL_HEARTBEAT;
	private final int REMOTE_HEARTBEAT;
	
	private RN131Config config;
	
	private byte[] local_ip = "169.254.1.101".getBytes();
	private byte[] remote_ip = "169.254.1.102".getBytes();
	private byte[] ssid = "SysPNet_TeamXY".getBytes();
	private byte[] passphrase = "12345678".getBytes();
	private byte[] channel = "10".getBytes();
	
	private static final byte OPEN = (byte)0xd5;
	private static final byte CLOSE = (byte)0xd4;
	private static final byte HEARTBEAT = (byte)0xd3;
	private static final byte CMD = '$';
	private static final byte ESCAPE = (byte)0xf1;
	private static final byte ESCAPED_ESCAPE = (byte)0xf3;
	private static final byte ESCAPED_OPEN = (byte)0xf2;
	private static final byte ESCAPED_CLOSE = (byte)0xf5;
	private static final byte ESCAPED_HEARTBEAT = (byte)0xe6;
	private static final byte ESCAPED_CMD = (byte)0xf0;
	private static final byte[] CMD_MODE ={ CMD, CMD, CMD };
	private static final byte[] CMD_TERMINATOR = "\r\n".getBytes();
	private static final byte[] CMD_MODE_ENTERED = "CMD".getBytes();
	private static final byte[] CMD_RESULT_OK = "AOK".getBytes();
//	private static final byte[] CMD_UART_MODE = "set uart mode 0x23".getBytes();
	private static final byte[] CMD_UART_MODE = "set uart mode 0x21".getBytes();
	private static final byte[] CMD_PRINTLVL = "set sys printlvl 0".getBytes();
	private static final byte[] CMD_COMM_OPEN = "set comm open ".getBytes();
	private static final byte[] CMD_COMM_OPEN_BYTE = { '0' };
	private static final byte[] CMD_COMM_CLOSE = "set comm close ".getBytes();
	private static final byte[] CMD_COMM_CLOSE_BYTE = { CLOSE };
	private static final byte[] CMD_COMM_REMOTE = "set comm remote ".getBytes();
	private static final byte[] CMD_COMM_REMOTE_BYTE = { OPEN };
	private static final byte[] CMD_COMM_IDLE = "set comm idle 2".getBytes();
	private static final byte[] CMD_IP_ADDRESS = "set ip address ".getBytes();
	private static final byte[] CMD_IP_NETMASK = "set ip netmask 255.255.0.0".getBytes();
	private static final byte[] CMD_IP_HOST = "set ip host ".getBytes();
	private static final byte[] CMD_IP_FLAGS = "set ip flags 0x6".getBytes();
	private static final byte[] CMD_IP_DHCP_CLIENT = "set ip dhcp 0".getBytes();
	private static final byte[] CMD_IP_DHCP_SERVER = "set ip dhcp 4".getBytes();
	private static final byte[] CMD_WLAN_SSID = "set wlan ssid ".getBytes();
	private static final byte[] CMD_WLAN_PHRASE = "set wlan phrase ".getBytes();
	private static final byte[] CMD_AP_SSID = "set apmode ssid ".getBytes();
	private static final byte[] CMD_AP_PHRASE = "set apmode passphrase ".getBytes();
	private static final byte[] CMD_WLAN_AUTH = "set wlan auth 0".getBytes();
	private static final byte[] CMD_WLAN_CLIENT = "set wlan join 1".getBytes();
	private static final byte[] CMD_WLAN_AP = "set wlan join 7".getBytes();
	private static final byte[] CMD_WLAN_CHANNEL = "set wlan channel ".getBytes();
	private static final byte[] CMD_WLAN_CHANNEL_AUTO = "set wlan channel 0".getBytes();
	private static final byte[] CMD_WLAN_EXTANTENNA = "set wlan ext_antenna 1".getBytes();
	private static final byte[] CMD_WLAN_INTANTENNA = "set wlan ext_antenna 0".getBytes();
	private static final byte[] CMD_WLAN_AUTOCONNECT_ON = "set sys autoconn 5".getBytes();
	private static final byte[] CMD_WLAN_AUTOCONNECT_OFF = "set sys autoconn 0".getBytes();
	private static final byte[] CMD_SAVE = "save".getBytes();
	private static final byte[] CMD_SAVE_OK = "Storinginconfig".getBytes();
	private static final byte[] CMD_REBOOT = "reboot".getBytes();
	private static final byte[] CMD_REBOOT_OK = "*Reboot*".getBytes();
}
