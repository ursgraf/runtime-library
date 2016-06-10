package ch.ntb.inf.deep.runtime.iMX6;

import ch.ntb.inf.deep.unsafe.US;


public class UART3 implements IiMX6 {
	public static UARTOutputStream out;
	protected static byte[] txData = new byte[64];

	public static void start(int baudRate, short parity, short data) {
	}

	public static void write(byte b) { 	 
		while((US.GET4(UART1_USR1) & (1 << 13)) == 0);
		US.PUT1(UART1_UTXD, b); 
	}

	public static int write(byte[] buffer) {
		return write(buffer, 0, buffer.length);
	}

	public static int write(byte[] buffer, int off, int count) {
		int len = buffer.length;
		for (int i = 0; i < count; i++) {
			write(buffer[off + i]);
		}
		return len;
	}

	public static void write(String msg) {
		if (msg != null) {
			int len = msg.length();
			for (int i = 0; i < len; i++) txData[i] = (byte)msg.charAt(i);
			UART3.write(txData, 0, len);
		}
	}

	public static void writeln(String msg) {
		write(msg);
		write("\n\r");
	}

	static {
		out = new UARTOutputStream();
	}

}
