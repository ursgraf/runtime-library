package java.io;

/**
 * This abstract class is the superclass of all classes representing an output
 * stream of bytes. An output stream accepts output bytes and sends them to some
 * sink. Applications that need to define a subclass of
 * <code>OutputStream</code> must always provide at least a method that writes
 * one byte of output.<br>
 * 
 * @author 17.09.2009 simon.pertschy@ntb.ch
 * 
 */
// TODO: Sobald Exceptions auf dem Target verfügbar sind, sollte diese Klasse
// durch die Orginal OutputStream Klasse ersetzt werden.
public abstract class OutputStream {

	public static final int illegalLength = -1, illegalOffset = -2;
	public OutputStream next;

	/**
	 * Writes the specified byte to this output stream.
	 * 
	 * @param b
	 *            the byte.
	 */
	public abstract void write(byte b);

	/**
	 * Writes, if possible, <code>b.length</code> bytes to this output stream.
	 * This method should have the same effect as the call
	 * <code>write(b, 0, b.length)</code>.
	 * 
	 * @param b
	 *            the byte array
	 * @return the number of written bytes.
	 */
	public int write(byte b[]) {
		return write(b, 0, b.length);
	}

	/**
	 * Writes, if possible, <code>len</code> bytes starting at
	 * <code>offset</code> to this output stream.
	 * 
	 * @param b
	 *            the byte array
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of bytes to write
	 * @return the number of written bytes.
	 */
	public int write(byte b[], int off, int len) {
		if (len < 0)
			return illegalLength;
		if (off < 0)
			return illegalOffset;
		int free = freeSpace();
		if (len > free)
			len = free;
		len += off;
		if (len > b.length)
			return illegalOffset;
		for (int i = off; i < len; i++)
			write(b[i]);
		return len;
	}

	/**
	 * Returns the number of bytes which can be written to the output stream.
	 * @return
	 */
	public abstract int freeSpace();

	/**
	 * Resets the output stream.
	 */
	public abstract void reset();

}
