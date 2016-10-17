package ch.ntb.inf.deep.runtime.util;


/**
 * Interface for the RN131 driver to send and receive packets with the SLIP protocol.
 */
public class SLIP {

	/**
	 * Creates a SLIP object using the supplied buffers.
	 * @param rx receive buffer to use
	 * @param tx transmitt buffer to use
	 */
	public SLIP(ByteFifo rx, ByteFifo tx) {
		this.rx = rx;
		this.tx = tx;
	}

	/**
	 * Returns the number of available bytes to read.
	 * @return number of available bytes to read
	 */
	public int availToRead() {
		return rx.availToRead();
	}

	/**
	 * Returns the number of available bytes to write.
	 * @return number of available bytes to write
	 */
	public int availToWrite() {
		return tx.availToWrite();
	}

	/**
	 * Writes a packet to the transmit buffer.
	 * @param packet byte array with the packet
	 * @param offset start offset of the packet data
	 * @param length length of the packet
	 * @return false if buffer is full, true otherwise
	 */
	public boolean write(byte[] packet, int offset, int length) {
		if (tx.availToWrite() < (2 * length + 2))
			return false;
		
		tx.enqueue(END);
		for (int i = 0; i < length; i++) {
			byte b = packet[offset + i];
			switch (b) {
				case END:
					tx.enqueue(ESC);
					tx.enqueue(ESC_END);
					break;
					
				case ESC:
					tx.enqueue(ESC);
					tx.enqueue(ESC_ESC);
					break;
					
				default:
					tx.enqueue(b);
					break;
			}
		}
		tx.enqueue(END);
		return true;
	}

	/**
	 * Reads a packet from the receive buffer.
	 * @param packet buffer where to put the packet
	 * @param offset offset in the buffer
	 * @param length length of the buffer
	 * @return <table>
	 * <caption>return values</caption>
	 *   <tr>
	 *     <th> &gt;= 0 </th>
	 *     <td>number of received bytes</td>
	 *   </tr>
	 *   <tr>
	 *     <th>-1</th>
	 *     <td>end of packet received</td>
	 *   </tr>
	 *   <tr>
	 *     <th>-2</th>
	 *     <td>buffer overflow</td>
	 *   </tr>
	 * </table>
	 */
	public int read(byte[] packet, int offset, int length) {
		if (packetReceived) {
			packetReceived = false;
			escaping = false;
			return -1;
		}
		if (offset >= length) return -2;
		int received = 0;
		int n = rx.availToRead();
		int i = 0;
		for (;i < n && (offset + i) < length; i++) {
			byte b = 0;
			try {
				b = rx.dequeue();
			}
			catch (Exception ex) {
				return received;
			}
			if (escaping) {
				switch (b) {
					case ESC_END:
						packet[offset + i] = END;
						break;
						
					case ESC_ESC:
						packet[offset + i] = ESC;
						break;
						
					default: // protocol violation
						packet[offset + i] = b;
						break;
				}
				escaping = false;
				received++;
			}
			else {
				switch (b) {
					case END:
						if (received == 0) return -1;
						packetReceived = true;
						return received;
						
					case ESC:
						escaping = true;
						break;
						
					default:
						packet[offset + i] = b;
						received++;
						break;
				}
			}
		}
		return received;
	}

	private ByteFifo rx;
	private ByteFifo tx;
	private boolean packetReceived = false;
	private boolean escaping = false;
	
	private static final byte END = (byte)0300;
	private static final byte ESC = (byte)0333;
	private static final byte ESC_END = (byte)0334;
	private static final byte ESC_ESC = (byte)0335;
}
