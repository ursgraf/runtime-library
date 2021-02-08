package org.deepjava.flink.subdevices;

import java.io.IOException;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;
import org.deepjava.runtime.zynq7000.driver.FlinkUARTInputStream;
import org.deepjava.runtime.zynq7000.driver.FlinkUARTOutputStream;

/**
 * The flink UART subdevice realizes an UART function within a flink device.
 * It offers several channels. Each channel has its own baudrate generator
 * together with transmit and receive queues.
 * 
 * @author Urs Graf 
 */
public class FlinkUART implements FlinkDefinitions {
	
	public static final int pUART0 = 0; 
	public static final int pUART1 = 1; 
	public static final int pUART2 = 2; 
	public static final int pUART3 = 3; 
	public static final int pUART4 = 4; 
	public static final int pUART5 = 5; 
	public static final int pUART6 = 6; 
	public static final int pUART7 = 7; 
	public static final int TX_FULL = 6;

	/** Handle to the subdevice within our flink device */
	private FlinkSubDevice dev;
	
	@SuppressWarnings("unused")
	private static FlinkUART uart0, uart1, uart2, uart3, uart4, uart5, uart6, uart7;
	private int uartNr;
	private static final int BASE_CLOCK_ADDRESS = 0;
	private static final int DIVIDER_0_ADDRESS = BASE_CLOCK_ADDRESS + REGISTER_WIDTH;
	private int tx0Addr;
	private int rx0Addr;
	private int status0Addr;
	
	/**
	 * Output stream to write to this <i>UART</i>.
	 */
	public FlinkUARTOutputStream out;
	/**
	 * Input stream to read from this <i>UART</i>..
	 */
	public FlinkUARTInputStream in;

	/**
	 * Returns an instance of <i>FlinkUART Interface</i> 
	 * operating one of the UARTS in the flink UART subdevice.
	 * @param dev handle to the subdevice
	 * @param uartNr 0 selects UART0, 1 selects UART1, ..
	 * @return instance of UART
	 */
	public static FlinkUART getInstance(FlinkSubDevice dev, int uartNr) {
		if (uartNr == pUART0) {
			if (uart0 == null) uart0 = new FlinkUART(dev, pUART0);
			return uart0;
		} else if (uartNr == pUART1) {
			if (uart1 == null) uart1 = new FlinkUART(dev, pUART1);
			return uart1;
		} else return null;
	}

	/**
	 * Creates an UART subdevice.
	 * @param dev handle to the subdevice
	 * @param uartNr channel number of this UART instance
	 */
	private FlinkUART(FlinkSubDevice dev, int uartNr) {
		this.dev = dev;
		this.uartNr = uartNr;
		tx0Addr = DIVIDER_0_ADDRESS + dev.nofChannels * REGISTER_WIDTH;
		rx0Addr = tx0Addr + dev.nofChannels * REGISTER_WIDTH;
		status0Addr = rx0Addr + dev.nofChannels * REGISTER_WIDTH;
		out = new FlinkUARTOutputStream(this);
		in = new FlinkUARTInputStream(this);
	}
	
	/**
	 * <p>Initialize and start the <i>flink Universal Asynchronous Receiver Transmitter</i>.</p>
	 * <p>This method have to be called before using the flink UART! The number of
	 * stop bits, the parity mode and number of data bits can't be set. 
	 * There is always one stop bit. Parity is off. Number of data bits is 8.<p>
	 * 
	 * @param baudRate
	 *            The baud rate. Allowed Range: 64 to 1'000'000 bits/s.
	 */
	public void start(int baudRate) {
		int base = dev.read(BASE_CLOCK_ADDRESS);
		dev.write(DIVIDER_0_ADDRESS + uartNr * REGISTER_WIDTH, base / baudRate);
	}

	/**
	 * Resets the UART. All the pending transmissions will be stopped, 
	 * the buffers will be emptied and the UART will be restarted.
	 */	
	public void reset() {
		dev.setConfigReg(1);
	}
	
	/**
	 * Writes a given byte into the transmit buffer.
	 * A call of this method is blocking! 
	 * If the buffer is full, the method blocks for a short period of time
	 * until a small amount of space is available again.
	 * After this an IOException is thrown.
	 * 
	 * @param b
	 *            Byte to write.
	 * @throws IOException 
	 *            if an error occurs while writing to this stream.
	 */
	public void write(byte b) throws IOException { 	
		int status = dev.read(status0Addr + uartNr * REGISTER_WIDTH);
		if ((status & (1 << TX_FULL)) != 0) throw new IOException("IOException");
		dev.write(tx0Addr + uartNr * REGISTER_WIDTH, b); 
	}

	/**
	 * Writes a given number of bytes into the transmit buffer.
	 * A call of this method is not blocking! There will only as
	 * many bytes written, which are free in the buffer.
	 * 
	 * @param buffer
	 *            Array of bytes to send.
	 * @return the number of bytes written.
	 * @throws IOException 
	 *            if an error occurs while writing to this stream.
	 */
	public int write(byte[] buffer) throws IOException {
		return write(buffer, 0, buffer.length);
	}

	/**
	 * Writes a given number of bytes into the transmit buffer.
	 * A call of this method is not blocking! There will only as
	 * many bytes written, which are free in the buffer.
	 * 
	 * @param buffer
	 *            Array of bytes to send.
	 * @param off
	 *            Offset to the data which should be sent.
	 * @param count
	 *            Number of bytes to send.
	 * @return the number of bytes written.
	 * @throws IOException
	 *            if an error occurs while writing to this stream.
	 * @throws NullPointerException
	 *            if {@code buffer} is null.
	 * @throws IndexOutOfBoundsException
	 *            if {@code off < 0} or {@code count < 0}, or if
	 *            {@code off + count} is bigger than the length of
	 *            {@code buffer}.
	 */
	public int write(byte[] buffer, int off, int count) throws IOException {
		int len = buffer.length;
		for (int i = 0; i < count; i++) {
			write(buffer[off + i]);
		}
		return len;
	}

	/**
	 * Returns the number of bytes available in the receive buffer.
	 * 
	 * @return number of bytes in the receive buffer.
	 */
	public int availToRead() {
		int status = dev.read(status0Addr + uartNr * REGISTER_WIDTH);
		return status >>> 16;
	}

	/**
	 * Reads one byte from the UART. A call of
	 * this method is not blocking!
	 * 
	 * @return byte read.
	 * @throws IOException 
	 *            if no byte available.
	 */
	public int read() throws IOException {
		if (availToRead() < 1) throw new IOException("IOException");
		return dev.read(rx0Addr + uartNr * REGISTER_WIDTH);
	}

	/**
	 * Reads the given number of bytes from the UART. A call of
	 * this method is not blocking!
	 * 
	 * @param buffer
	 *            Byte array to write the received data.
	 * @return the number of bytes read. 
	 * @throws IOException 
	 *            if no data available.
	 */
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	/**
	 * Reads the given number of bytes from the UART. A call of
	 * this method is not blocking!
	 * 
	 * @param buffer
	 *            Byte aray to write the received data.
	 * @param off
	 *            Offset in the array to start writing the data.
	 * @param count
	 *            Length (number of bytes) to read.
	 * @return the number of bytes read.
	 * @throws IOException
	 *            if an error occurs while reading from this stream.
	 * @throws NullPointerException
	 *            if {@code buffer} is null.
	 * @throws IndexOutOfBoundsException
	 *            if {@code off < 0} or {@code count < 0}, or if
	 *            {@code off + count} is bigger than the length of
	 *            {@code buffer}.
	 */
	public int read(byte[] buffer, int off, int count) throws IOException {
	   	int len = buffer.length;
        if ((off | count) < 0 || off > len || len - off < count) {
        	throw new ArrayIndexOutOfBoundsException(len, off, count);
        }
        if (availToRead() < count) throw new IOException("IOException");
		for (int i = 0; i < count; i++) {
			buffer[off + i] = (byte) dev.read(rx0Addr + uartNr * REGISTER_WIDTH);
		}
		return len;
	}

}
