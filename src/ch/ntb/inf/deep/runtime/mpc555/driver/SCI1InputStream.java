package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.InputStream;

/* Changes:
 * 13.10.2011	Martin Zueger	JavaDoc fixed
 * 06.01.2010	Simon Pertschy	initial version
 */

/**
 *
 * Input Stream to read bytes form the SCI1.
 * Don't forget to initialize the SCI1 before using this class.
 * 
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
