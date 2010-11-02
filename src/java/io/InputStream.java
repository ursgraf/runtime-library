package java.io;

/**
 * This abstract class is the superclass of all classes representing an input
 * stream of bytes.<br>
 * Applications that need to define a subclass of <code>InputStream</code> must
 * always provide a method that returns the next byte of input.
 * 
 * @author 24.08.2009 simon.pertschy@ntb.ch
 * 
 */
//TODO: Sobald Exceptions auf dem Target verfügbar sind, sollte diese Klasse
//durch die Orginal OutputStream Klasse ersetzt werden.
public abstract class InputStream {

	public static final int illegalLength = -2, illegalOffset = -3;
	public InputStream next;

	/**
	 * Reads the next byte from the input stream. The value byte is returned as
	 * an <code>int</code> in the range 0 to 255. If no byte is available, the
	 * value -1 is returned.
	 * 
	 * @return the next byte of data, or -1 if no byte is available.
	 */
	public abstract int read();

	/**
	 * Reads some number of bytes from the input stream and stores them into the
	 * buffer array <code>b</code>. The number of bytes actually read is
	 * returned as an integer.
	 * 
	 * @param b
	 *            the buffer into which the data is read.
	 * @return the total number of bytes read into the buffer.
	 */
	public int read(byte b[]) {
		return read(b, 0, b.length);
	}

	/**
	 * Reads up to <code>len</code> bytes of data from the input stream into an
	 * array of bytes. An attempt is made to read as many as <code>len</code>
	 * bytes, but a smaller number may be read. The number of bytes actually
	 * read is returned as an integer. The first byte read is stored into
	 * element <code>b[off]</code>, the next one into <code>b[off+1]</code> and
	 * so on.
	 * 
	 * @param b
	 *            the buffer into which the data is read.
	 * @param off
	 *            the start offset in array <code>b</code> at which the data is
	 *            written.
	 * @param len
	 *            the maximum number of bytes to read.
	 * @return the total number of bytes read into the buffer,
	 *         {@link #illegalLength} if the length is less than zero or
	 *         {@link #illegalOffset} if the offset is less than zero.
	 */
	public int read(byte b[], int off, int len) {
		if (len < 0)
			return illegalLength;
		if (off < 0)
			return illegalOffset;
		int avail = available();
		if (len > avail)
			len = avail;
		if (len + off >= b.length)
			return illegalOffset;
		for (int i = 0; i < len; i++)
			b[i + off] = (byte) read();
		return len;
	}

	/**
	 * Returns the available bytes to read from the input stream.
	 * @return the available byes.
	 */
	public abstract int available();



}
