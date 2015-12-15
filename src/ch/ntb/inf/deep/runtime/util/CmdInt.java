package ch.ntb.inf.deep.runtime.util;

/**
 * Interface for the RN131 driver to send and receive integer values.
 */
public class CmdInt {

	public static enum Type {
		/** No packet received. */
		None(0),
		
		/** Illegal packet received. */
		Illegal(1),
		
		/** Unknown packet header. */
		Unknown(2),
		
		/** <i>Cmd</i> packet header. */
		Cmd(0x11),
		
		/** <i>Code</i> packet header. */
		Code(0x12);
		
		private Type(int t) {
			value = (byte)t;
		}
		
		byte get() {
			return value;
		}
		
		private byte value;
	}

	/**
	 * Creates a CmdInt object on top of the SLIP interface.
	 * @param slip slip interface to use
	 */
	public CmdInt(SLIP slip) {
		this.slip = slip;
		tx = new byte[BUFFER_SIZE];
		rx = new byte[BUFFER_SIZE];
	}

	/**
	 * Writes an integer with the default header to the send buffer.
	 * @param cmd integer to send
	 * @return false if buffer is full, true otherwise
	 */
	public boolean writeCmd(int cmd) {
		return writeCmd(Type.Cmd.get(), cmd);
	}

	/**
	 * Writes an integer of the specified type to the send buffer.
	 * @param type type of the integer
	 * @param cmd integer to send
	 * @return false if buffer is full, true otherwise
	 */
	public boolean writeCmd(Type type, int cmd) {
		return writeCmd(type.get(), cmd);
	}

	/**
	 * Writes an integer with a specified header to the send buffer.
	 * @param header header of the integer
	 * @param cmd integer to send
	 * @return false if buffer is full, true otherwise
	 */
	public boolean writeCmd(byte header, int cmd) {
		tx[0] = header;
		tx[1] = (byte) (cmd >> 24);
		tx[2] = (byte) (cmd >> 16);
		tx[3] = (byte) (cmd >> 8);
		tx[4] = (byte) cmd;
		return slip.write(tx, 0, 5);
	}

	/**
	 * Reads an integer from the receive buffer.
	 * The value of the integer can be retrieved with the getCmd method.
	 * The value of the header can be retrieved with the getHeader method.
	 * @return Type.Cmd, Type.Code or Type.None if data was received. Type.Illegal if a corrupted packet was received and Type.None if buffer is empty.
	 */
	public Type readCmd() {
		Type result = Type.None; // workaround for deep bug (null pointer exception)
		while (true) {
			int r = slip.read(rx, count, BUFFER_SIZE);
			if (r == -1) { // end of packet
				if (discard) {
					discard = false;
					continue;
				}
				
				if (count == 0) // ignore empty packets
					continue;
				
				if (count != 5) {
					count = 0;
					cmd = 0;
					return Type.Illegal;
				}
				
				header = rx[0];
				cmd = ((int)rx[1]) << 24;
				cmd |= (((int)rx[2]) & 0xff) << 16;
				cmd |= (((int)rx[3]) & 0xff) << 8;
				cmd |= (((int)rx[4]) & 0xff);
				
				count = 0;
				
				if (header == Type.Cmd.get())
					return Type.Cmd;
				else if (header == Type.Code.get())
					return Type.Code;
				else
					return Type.Unknown;
			}
			else if (r == 0) {
				return Type.None;
			}
			else if (r > 0) {
				count += r;
			}
//			else if (r == -2) { // buffer is full
			else {				// buffer is full or illegal return value
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
		return cmd;
	}
	
	private SLIP slip;
	private byte header = 0;
	private int cmd;
	private byte[] rx, tx;
	private int count = 0;
	private boolean discard = false;
	private static final int BUFFER_SIZE = 8;
}
