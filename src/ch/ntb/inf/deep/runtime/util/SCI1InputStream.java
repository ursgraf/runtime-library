package ch.ntb.inf.deep.runtime.util;

import java.io.InputStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI1;

/**
 *
 * Input Stream to read bytes form the SCI1.
 * Don't forget to inialize the SCI1 before using this class.
 * 
 * @author 06.01.2010 simon.pertschy@ntb.ch
 */
public class SCI1InputStream extends InputStream{

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	public int available() {
		return SCI1.availToRead();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() {
		return SCI1.read();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte b[]){
		return SCI1.read(b);
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte b[], int off, int len){
		return SCI1.read(b, off, len);
	}
	
}
