package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.InputStream;

/* Changes:
 * 13.10.2011	Martin Zueger	JavaDoc fixed
 * 06.01.2010	Simon Pertschy	initial version
 */

/**
 *
 * Input Stream to read bytes from the SCI2.
 * Don't forget to initialize the SCI2 before using this class.
 * 
 */
public class SCI2InputStream extends InputStream{

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	public int available() {
		return SCI2.availToRead();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() {
		return SCI2.read();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte b[]){
		return SCI2.read(b);
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte b[], int off, int len){
		return SCI2.read(b, off, len);
	}
	
}
