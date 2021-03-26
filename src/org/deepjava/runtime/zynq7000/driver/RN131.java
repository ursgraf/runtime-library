package org.deepjava.runtime.zynq7000.driver;

import java.io.IOException;
import org.deepjava.flink.core.FlinkDevice;
import org.deepjava.flink.subdevices.FlinkGPIO;
import org.deepjava.runtime.zynq7000.driver.UARTInputStream;
import org.deepjava.runtime.zynq7000.driver.UARTOutputStream;
import org.deepjava.runtime.arm32.Task;
import org.deepjava.runtime.util.ByteFifo;
import org.deepjava.runtime.util.IntPacket;
import org.deepjava.runtime.util.SLIP;


/**
 * Driver for Roving RN-131C WiFly Module. <br>
 *
 * Usage: <br>
 * Connect the RN131WiFly module to a serial port of the Zynq7000 (UART1 or UART2).<br>
 * The WiFly module can also be configured with a Terminal Program like PuTTY with a baud rate
 * of 115200 kbps, no flow control. More information to the RN131WiFly can be found at 
 * <a href = "http://www.microchip.com/wwwproducts/Devices.aspx?dDocName=en558369">microchip.com</a><br>
 * <br>
 * 
 * The WiFly module has to be configured as follows:
 * <p><code>
 * factory RESET<br>
 * set uart baudrate 115200<br>
 * set sys printlvl 0<br>
 * set uart mode 0x21<br>
 * savee<br>
 * </code></p>
 */
@SuppressWarnings("unused")
public class RN131 extends Task {

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

	public static enum State {
		wait,
		error,
		reset,
		boot,
		ready
	};
	
	public ByteFifo rx;
	public ByteFifo tx;
	public IntPacket intPacket;
	public boolean connected = false;
	
	private UARTInputStream in;
	private UARTOutputStream out;
	private FlinkGPIO gpio;
	private int resetChannel;
	private State state = State.reset;
	private State last = State.reset;
	private State next = State.reset;
	private State stateAfterWait = State.reset;
	private int waitTicks = 0;
	private int localHeartbeat = 0;
	private int remoteHeartbeat = 0;
	private int configStep = 0;
	private boolean commandSent = false;	
	private ByteFifo rxcmd = new ByteFifo(127);
	private boolean rxEscapeMode = false;
	
	private final int LOCAL_HEARTBEAT;
	private final int REMOTE_HEARTBEAT;
	
	private byte[] local_ip = "169.254.1.101".getBytes();
	private byte[] remote_ip = "169.254.1.102".getBytes();
	private byte[] ssid = "SysPNet_TeamXY".getBytes();
	private byte[] passphrase = "12345678".getBytes();
	private byte[] channel = "10".getBytes();

	/**
	 * Creates a new RN131 object. It uses an UART together with a single pin of a 
	 * flink gpio subdevice for resetting the device.
     *
	 * @param in
	 * 			UART Inputstream
	 * @param out
	 * 			UART Outputstream
	 * @param resetChannel
	 * 			Pin number of gpio channel for reset
	 */
	public RN131(UARTInputStream in, UARTOutputStream out, int resetChannel) {
		FlinkDevice fDev = FlinkDevice.getInstance();
		gpio = FlinkDevice.getGPIO();
		gpio.setDir(resetChannel, true);
		this.resetChannel = resetChannel;
		this.in = in;
		this.out = out;		
		period = 20;
		LOCAL_HEARTBEAT = sec2ticks(1);
		REMOTE_HEARTBEAT = sec2ticks(2);		
		rx = new ByteFifo(2047);
		tx = new ByteFifo(2047);
		intPacket = new IntPacket(new SLIP(rx, tx));	
		Task.install(this);
		System.out.println("start");
	}

	/**
	 * Gets the internal state of the driver.
	 * @return current state of the driver
	 */
	public State getState() {
		return (state == State.wait) ? last : state;
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
		if (state != State.wait) last = state;	
		state = next;
		if (state != State.ready) connected = false;
		
		switch (state) {
			case reset:
				gpio.setValue(resetChannel, false);
				next(1, State.boot);
				break;
				
			case boot:
				gpio.setValue(resetChannel, true);
				next(3, State.ready);
				rx.clear();
				break;
				
			case ready:
				while (in.available() > 0) { 
					// receiving from WLAN module, put into rx FIFO
					byte b = (byte)in.read();
					remoteHeartbeat = REMOTE_HEARTBEAT;
					if (b == CLOSE) connected = false;
					else connected = true;
					if (b == OPEN) ;	// ignore
					else if (b == CLOSE); // ignore
					else if (b == HEARTBEAT); // ignore
					else if (rxEscapeMode) {
						if (b == ESCAPED_OPEN) rx.enqueue(OPEN);
						else if (b == ESCAPED_CLOSE) rx.enqueue(CLOSE);
						else if (b == ESCAPED_HEARTBEAT) rx.enqueue(HEARTBEAT);
						else if (b == ESCAPED_CMD) rx.enqueue(CMD);
						else if (b == ESCAPED_ESCAPE) rx.enqueue(ESCAPE);
						else rx.enqueue(b);
						rxEscapeMode = false;
					}
					else if (b == ESCAPE) rxEscapeMode = true;
					else rx.enqueue(b);
				}
				
				if (remoteHeartbeat <= 0) {
					remoteHeartbeat = 0;
					connected = false;
				} else {
					remoteHeartbeat--;
					if (remoteHeartbeat < 0) remoteHeartbeat = 0;
				}
				
				if (connected && localHeartbeat <= 0) {
					out.write(HEARTBEAT);
					localHeartbeat = LOCAL_HEARTBEAT;
				} else {
					localHeartbeat--;
					if (localHeartbeat < 0)	localHeartbeat = 0;
				}
				
				if (connected) {
					while (tx.availToRead() > 0) {
						// get data from tx FIFO and transmit over WLAN module
						byte b = 0;
						try {
							b = (byte)tx.dequeue();
						} catch (Exception e) {
							break;
						}
						if (b == OPEN) {
							out.write(ESCAPE);
							out.write(ESCAPED_OPEN);
						} else if (b == CLOSE) {
							out.write(ESCAPE);
							out.write(ESCAPED_CLOSE);
						} else if (b == HEARTBEAT) {
							out.write(ESCAPE);
							out.write(ESCAPED_HEARTBEAT);
						} else if (b == CMD) {
							out.write(ESCAPE);
							out.write(ESCAPED_CMD);
						} else if (b == ESCAPE) {
							out.write(ESCAPE);
							out.write(ESCAPED_ESCAPE);
						} else {
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
				} else waitTicks--;
				break;
				
			case error:
				break;
		
			default:
				next(State.reset);
				break;
		}
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
	
}
