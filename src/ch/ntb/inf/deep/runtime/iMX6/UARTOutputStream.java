package ch.ntb.inf.deep.runtime.iMX6;

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
		UART3.write((byte)b);
	}

	public void write(byte buffer[]) {
		UART3.write(buffer); 
	}
	
	public void write(byte buffer[], int off, int count) {
		UART3.write(buffer, off, count);
	}
}
