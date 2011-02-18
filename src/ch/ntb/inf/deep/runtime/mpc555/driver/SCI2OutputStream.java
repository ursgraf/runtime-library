package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.OutputStream;


/**
 *
 * Stream to write bytes to the SCI2.
 * Don't forget to open the SCI2 before using this stream.
 * 
 * @author 06.01.2010 simon.pertschy@ntb.ch
 */
public class SCI2OutputStream extends OutputStream{

	/* (non-Javadoc)
	 * @see java.io.OutputStream#freeSpace()
	 */
	public int freeSpace() {
		return SCI2.availToWrite();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#reset()
	 */
	public void reset() {
	 //TODO add SCI2.reset()
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte)
	 */
	public void write(byte b) {
		SCI2.write(b);
		
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	public int write(byte b[]){
		return SCI2.write(b);
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public int write(byte b[], int off, int len){
		return SCI2.write(b, off, len);
	}
}
