package ch.ntb.inf.deep.runtime.zynq7000.driver;

import java.io.OutputStream;


/**
 *
 * Stream to write bytes to a UART interface.
 * Don't forget to initialize the interface before using this stream.
 * 
 */
public class UARTOutputStream extends OutputStream {
	
    public UARTOutputStream() {
	}

	public void write(int b) {
		UART1.write((byte)b);
	}

	public void write(byte buffer[]) {
		UART1.write(buffer); 
	}
	
	public void write(byte buffer[], int off, int count) {
		UART1.write(buffer, off, count);
	}
}
