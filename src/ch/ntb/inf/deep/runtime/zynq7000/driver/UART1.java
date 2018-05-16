package ch.ntb.inf.deep.runtime.zynq7000.driver;

import ch.ntb.inf.deep.runtime.zynq7000.Izynq7000;
import ch.ntb.inf.deep.unsafe.arm.US;

public class UART1 implements Izynq7000 {
	public static UARTOutputStream out;
	protected static byte[] txData = new byte[64];

	public static void start(int baudRate, short parity, short data) {
		US.PUT4(SLCR_UNLOCK, 0xdf0d);
		US.PUT4(SLCR_MIO_PIN_48, 0x12e0);	// tx
		US.PUT4(SLCR_MIO_PIN_49, 0x12e1);	// rx
		US.PUT4(UART1_CR, 0x14);	// enable tx, rx
		US.PUT4(UART1_BAUDGEN, 11);	// CD = 11
		US.PUT4(UART1_MR, 0x20);	// no parity
	}

	public static void write(byte b) { 	 
//		while((US.GET4(UART0_SR+0x1000) & (1 << 13)) != 0);
		US.PUT1(UART1_FIFO, b); 
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
			UART1.write(txData, 0, len);
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
