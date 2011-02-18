package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.OutputStream;


/**
 *
 * Stream to write bytes to the SCI1.
 * Don't forget to open the SCI1 before using this stream.
 * 
 * @author 06.01.2010 simon.pertschy@ntb.ch
 */
public class SCI1OutputStream extends OutputStream{

	/* (non-Javadoc)
	 * @see java.io.OutputStream#freeSpace()
	 */
	public int freeSpace() {
		return SCI1.availToWrite();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#reset()
	 */
	public void reset() {
	 //TODO add SCI1.reset()
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte)
	 */
	public void write(byte b) {
		SCI1.write(b);
		
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	public int write(byte b[]){
		return SCI1.write(b);
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public int write(byte b[], int off, int len){
		return SCI1.write(b, off, len);
	}
}
