package org.deepjava.runtime.util;

/**
 * Interface for the RN131 driver to send and receive integer values.
 */
public class IntPacket {

	private SLIP slip;
	private byte header = 0;
	private int val;
	private byte[] rx, tx;
	private int count = 0;
	private boolean discard = false;
	private static final int BUFFER_SIZE = 8;

	public static enum Type {
		/** No packet received. */
		None(0),
		
		/** Illegal packet received. */
		Illegal(1),
		
		/** Unknown packet header. */
		Unknown(2),
		
		/** <i>Integer</i> packet header. */
		Int(0x11);
		
		private Type(int t) {
			value = (byte)t;
		}
		
		byte get() {
			return value;
		}
		
		private byte value;
	}

	/**
	 * Creates a IntPacket object on top of the SLIP interface.
	 * @param slip slip interface to use
	 */
	public IntPacket(SLIP slip) {
		this.slip = slip;
		tx = new byte[BUFFER_SIZE];
		rx = new byte[BUFFER_SIZE];
	}

	/**
	 * Writes an integer with the default header to the send buffer.
	 * @param val integer to send
	 * @return false if buffer is full, true otherwise
	 */
	public boolean writeInt(int val) {
		return writeInt(Type.Int.get(), val);
	}

	/**
	 * Writes an integer of the specified type to the send buffer.
	 * @param type type of the integer
	 * @param val integer to send
	 * @return false if buffer is full, true otherwise
	 */
	public boolean writeInt(Type type, int val) {
		return writeInt(type.get(), val);
	}

	/**
	 * Writes an integer with a specified header to the send buffer.
	 * @param header header of the integer
	 * @param val integer to send
	 * @return false if buffer is full, true otherwise
	 */
	public boolean writeInt(byte header, int val) {
		tx[0] = header;
		tx[1] = (byte) (val >> 24);
		tx[2] = (byte) (val >> 16);
		tx[3] = (byte) (val >> 8);
		tx[4] = (byte) val;
		return slip.write(tx, 0, 5);
	}

	/**
	 * Reads an integer from the receive buffer.
	 * The value of the integer can be retrieved with the getCmd method.
	 * The value of the header can be retrieved with the getHeader method.
	 * @return Type.Int or Type.None if data was received. Type.Illegal if a corrupted packet was received and Type.None if buffer is empty.
	 */
	public Type readInt() {
		@SuppressWarnings("unused")
		Type result = Type.None; // workaround for deep bug (null pointer exception)
		while (true) {
			int r = slip.read(rx, count, BUFFER_SIZE);
			if (r == -1) { // end of packet
				if (discard) {
					discard = false;
					continue;
				}
				if (count == 0) continue; // ignore empty packets		
				if (count != 5) {
					count = 0;
					val = 0;
					return Type.Illegal;
				}			
				header = rx[0];
				val = ((int)rx[1]) << 24;
				val |= (((int)rx[2]) & 0xff) << 16;
				val |= (((int)rx[3]) & 0xff) << 8;
				val |= (((int)rx[4]) & 0xff);	
				count = 0;		
				if (header == Type.Int.get()) return Type.Int;
				else return Type.Unknown;
			} else if (r == 0) {
				count = 0;
				discard = false;
				return Type.None;
			} else if (r > 0) {
				count += r;
			} else {				// buffer is full or illegal return value
				count = 0;		// empty buffer
				discard = true;	// discard the rest of the packet
			}
		}
	}

	/**
	 * Returns the header of the last received integer.
	 * @return header of the last received integer
	 */
	public byte getHeader() {
		return header;
	}

	/**
	 * Returns the last received integer.
	 * @return last received integer
	 */
	public int getInt() {
		return val;
	}
	
}
