package ch.ntb.inf.deep.runtime.util;

import java.io.OutputStream;



/**
 *
 * {@link OutputStream} adapter to write different data types as a string.
 * 
 * @author 18.12.2009 simon.pertschy@ntb.ch
 */
public class CharOutputStream extends OutputStream{
	
	static final char [ ] chars = new char[32];
	private OutputStream out;
	
	public CharOutputStream(OutputStream out){
		this.out = out;
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#freeSpace()
	 */
	public int freeSpace() {
		return out.freeSpace();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#reset()
	 */
	public void reset() {
		out.reset();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte)
	 */
	public void write(byte b) {
		out.write(b);		
	}
	
	/**
	 * Writes a single ascii <code>char</code> (8bit) to the output stream.
	 * @param c the char to write.
	 */
	public void write(char c){
		out.write((byte)c);
	}


	/**
	 * Writes, if possible, <code>chars.length</code> ascii chars (8bit each char) to this output stream.
	 * @param chars the chars to write
	 * @return the number of written chars
	 */
	public int write(char chars[]) {
		return write(chars, 0, chars.length);
	}

	/**
	 * Writes, if possible, <code>len</code> ascii chars (8bit each char) starting at
	 * <code>offset</code> to this output stream.
	 * @param b
	 *            the char array
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of char to write
	 * @return the number of written bytes.
	 */
	public int write(char b[], int off, int len) {
		if (len < 0)
			return illegalLength;
		if (off < 0)
			return illegalOffset;
		int free = out.freeSpace();
		if (len > free)
			len = free;
		len += off;
		if (len > b.length)
			return illegalOffset;
		for (int i = off ; i < len; i++)
			out.write((byte) b[i]);
		return len;
	}

	/**
	 * Writes a string to the output stream.
	 * @param str The string to write.
	 * @return The number of written chars.
	 */
	public int write(String str) {
		int len = str.length();
		int free = out.freeSpace();
		if (len > free)
			len = free;
		for (int i = 0; i < len; i++) {
			out.write((byte) str.charAt(i));
		}
		return len;
	}
	
	/**
	 * Writes a boolean as a string to the output stream.
	 * @param bool the boolean to write.
	 */
	public void write(boolean bool) {
		if (bool)
			write("true");
		else
			write("false");
	}
	
	/**
	 * Writes a integer as a string to the output stream.
	 * @param val the integer to write.
	 */
	public void write(int val) {
		if (val == 0) out.write((byte) '0');
		else {
			boolean neg = false;
			if (val < 0) {
				neg = true;
				val *= -1;
			}
			int ctr = chars.length;
			while (val != 0) {
				chars[--ctr] = (char) ('0' + (val % 10));
				val /= 10;
			}
			if (neg)
				chars[--ctr] = '-';
			for (int i = ctr; i < chars.length; i++)
				out.write((byte)chars[i]);
		}
	}
	
	/**
	 * Writes a float as a string to the output stream.
	 * @param val the float to write.
	 */
	public void write(float val){
		int	nofChars = Double.doubleToChars(val, 6, chars);
		int	n = 0;
		while (n < nofChars) {	out.write((byte) chars[n]);	n++;	}
	}
	
	/**
	 * Writes a double as a string to the output stream.
	 * @param val the double to write.
	 */
	public void write(double val){
		int	nofChars = Double.doubleToChars(val, 15, chars);
		int	n = 0;
		while (n < nofChars) {	out.write((byte)chars[n]);	n++;	}
	}
	
}
