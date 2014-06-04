package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.IOException;
import java.io.OutputStream;

/* Changes:
 * 3.6.2014		Urs Graf		exception handling added
 */
/**
*
* Stream to write bytes to the RN131WiFly.
* Don't forget to initialize the RN131WiFly before using this stream.
* 
*/
public class RN131WiFlyOutputStream extends OutputStream {

    /**
     * Writes a single byte to this stream. Only the least significant byte of
     * the integer {@code b} is written to the stream.
     *
     * @param oneByte
     *            the byte to be written.
     */
	public void write(int b) {
		try {
			RN131WiFly.write((byte)b);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public int write(byte[] b){
		return RN131WiFly.write(b);
	}
	
	public int write(byte[] b, int len){
		return RN131WiFly.write(b, len);
	}
	
	public int write(byte[] b, int off, int len){
		return RN131WiFly.write(b, off, len);
	}
	
	public int freeSpace() {
		return RN131WiFly.availToWrite();
	}

	public void reset() {
		RN131WiFly.clear();
	}

}
