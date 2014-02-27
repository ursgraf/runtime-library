/*
 * Copyright 2011 - 2013 NTB University of Applied Sciences in Technology
 * Buchs, Switzerland, http://www.ntb.ch/inf
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Interrupt;
import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.runtime.util.ByteFifo;
import ch.ntb.inf.deep.unsafe.US;

/* Changes:
 * 13.10.2011	NTB/Martin Zueger	reset() implemented, JavaDoc fixed
 * 08.03.2011	NTB/Urs Graf		ported to deep
 * 31.03.2007	NTB/Simon Pertschy	read failure corrected and error states added
 * 12.02.2007	NTB/Simon Pertschy	assigned to Java
 */

/**
 * <p>Interrupt controlled driver for the <i>Serial Communicatin Interface 2</i>
 * of the Freescale MPC555.</p>
 * <p><b>Remember:</b><br>
 * Depending on the baudrate configured, the effective baudrate can be different.
 * This may cause miss interpretation of the bytes sent at the receiver! For more
 * details, please consider table 14-29 in chapter 14.8.7.3 in the <a href=
 * "http://www.ntb.ch/infoportal/_media/embedded_systems:mpc555:mpc555_usermanual.pdf"
 * >MPC555 User's manual</a>.
 * </p>
 */
public class SCI2 extends Interrupt {

	public static SCI2OutputStream out;
	public static SCI2InputStream in;
	private static int d = 0;
	
	public static final byte NO_PARITY = 0, ODD_PARITY = 1, EVEN_PARITY = 2;

	// Driver states
	public static final int PORT_OPEN = 9, TX_EMPTY = 8, TX_COMPLETE = 7,
			RX_RDY = 6, RX_ACTIVE = 5;

	// Error states
	public static final int IDLE_LINE_DET = 4, OVERRUN_ERR = 3, NOISE_ERR = 2,
			FRAME_ERR = 1, PARITY_ERR = 0, LENGTH_NEG_ERR = -1,
			OFFSET_NEG_ERR = -2, NULL_POINTER_ERR = -3;
	public static final int QUEUE_LEN = 2047;
	public static final int CLOCK = Kernel.clockFrequency;
	private static Interrupt rxInterrupt, txInterrupt;

	private static short portStat; // just for saving flag portOpen
	private static short scc2r1; // content of SCC2R1

	private static int currentBaudRate = 9600;
	private static short currentParity = NO_PARITY;
	private static short currentDataBits = 8;
	
	/*
	 * rxQueue: the receive queue, head points to the front item, tail to tail
	 * item plus 1: head=tail -> empty q head is moved by the interrupt proc
	 */
	private static ByteFifo rxQueue;

	/*
	 * txQueue: the transmit queue, head points to the front item, tail to tail
	 * item plus 1: head=tail -> empty q head is moved by the interrupt proc,
	 * tail is moved by the send primitives called by the application
	 */
	private static ByteFifo txQueue;
	private static boolean txDone;

	@SuppressWarnings("unused")
	private static int intCtr;

	/* (non-Javadoc)
	 * @see ch.ntb.inf.deep.runtime.mpc555.Interrupt#action()
	 */
	public void action() {
		intCtr++;
		if (this == rxInterrupt) {
			short word = US.GET2(QSMCM.SC2DR);
			rxQueue.enqueue((byte) word);
		} else {
			if (txQueue.availToRead() > 0) {
				d = txQueue.dequeue();
				US.PUT2(QSMCM.SC2DR, d);
			} else {
				txDone = true;
				scc2r1 &= ~(1 << QSMCM.scc2r1TIE);
				US.PUT2(QSMCM.SCC2R1, scc2r1);
			}
		}
	}

	private static void startTransmission() {
		if (txDone && (txQueue.availToRead() > 0)) {
			txDone = false;
			US.PUT2(QSMCM.SC2DR, txQueue.dequeue());
			scc2r1 |= (1 << QSMCM.scc2r1TIE);
			US.PUT2(QSMCM.SCC2R1, scc2r1);
		}
	}

	/**
	 * Clear the receive buffer.
	 */
	public static void clearReceiveBuffer() {
		rxQueue.clear();
	}

	/**
	 * Clear the transmit buffer.
	 */
	public static void clearTransmitBuffer() {
		scc2r1 &= ~(1 << QSMCM.scc2r1TIE);
		US.PUT2(QSMCM.SCC2R1, scc2r1);
		txQueue.clear();
		txDone = true;
	}

	/**
	 * Clear the receive and transmit buffers.
	 */
	public static void clear() {
		clearReceiveBuffer();
		clearTransmitBuffer();
	}

	/**
	 * Stop the <i>Serial Communication Interface 1</i>.
	 */
	public static void stop() {
		clear();
		US.PUT2(QSMCM.SCC2R1, 0);
		portStat = 0;
	}

	/**
	 * <p>Initialize and start the <i>Serial Communication Interface 2</i>.</p>
	 * <p>This method have to be called before using the SCI1! The number of
	 * stop bits can't be set. There is always one stop bit!<p>
	 * 
	 * @param baudRate
	 *            The baud rate. Allowed Range: 64 to 500'000 bits/s.
	 * @param parity
	 *            Parity bits configuration. Possible values: {@link #NO_PARITY},
	 *            {@link #ODD_PARITY} or {@link #EVEN_PARITY}.
	 * @param data
	 *            Number of data bits. Allowed values are 7 to 9 bits. If you
	 *            choose 9 data bits, than is no parity bit more available!
	 */
	public static void start(int baudRate, short parity, short data) {
		stop();
		currentBaudRate = baudRate;
		currentParity = parity;
		currentDataBits = data;
		short scbr = (short) ((CLOCK / baudRate + 16) / 32);
		if (scbr <= 0)
			scbr = 1;
		else if (scbr > 8191)
			scbr = 8191;
		scc2r1 |= (1 << QSMCM.scc2r1TE) | (1 << QSMCM.scc2r1RE)
				| (1 << QSMCM.scc2r1RIE); // Transmitter and Receiver enable
		if (parity == 0) {
			if (data >= 9)
				scc2r1 |= (1 << QSMCM.scc2r1M);
		} else {
			if (data >= 8)
				scc2r1 |= (1 << QSMCM.scc2r1M) | (1 << QSMCM.scc2r1PE);
			else
				scc2r1 = (1 << QSMCM.scc2r1PE);
			if (parity == 1)
				scc2r1 |= (1 << QSMCM.scc2r1PT);
		}
		US.PUT2(QSMCM.SCC2R0, scbr);
		US.PUT2(QSMCM.SCC2R1, scc2r1);
		portStat |= (1 << PORT_OPEN);
		US.GET2(QSMCM.SC2SR); // Clear status register
	}

	/**
	 * Check the port status. Returns the port status bits.<br>
	 * Every bit is representing a flag (e.g. {@link #FLAG_PORT_OPEN}).
	 * 
	 * @return the port status bits.
	 */
	public static short portStatus() {
		return (short) (portStat | US.GET2(QSMCM.SC2SR));
	}

	/**
	 * Returns the number of bytes available in the receive buffer.
	 * 
	 * @return number of bytes in the receive buffer.
	 */
	public static int availToRead() {
		return rxQueue.availToRead();
	}

	/**
	 * Returns the number of free bytes available in the transmit buffer.
	 * It is possible, to send the returned number of bytes in one
	 * nonblocking transfer.
	 * 
	 * @return the available free bytes in the transmit buffer.
	 */
	public static int availToWrite() {
		return txQueue.availToWrite();
	}

	/**
	 * Reads the given number of bytes from the SCI1. A call of
	 * this method is not blocking!
	 * 
	 * @param b
	 *            Byte Array to write the received data.
	 * @param off
	 *            Offset in the array to start writing the data.
	 * @param len
	 *            Length (number of bytes) to read.
	 * @return the number of bytes read. 0 if there were no data
	 *            available to read or if the given number of bytes
	 *            was zero (len == 0).
	 *            {@link #LENGTH_NEG_ERR} if the given number of
	 *            bytes was negative (len < 0).
	 *            {@link #OFFSET_NEG_ERR} if the given offset was
	 *            negative (off < 0).
	 *            {@link #NULL_POINTER_ERR} if the array reference
	 *            was null (b == null).
	 */
	public static int read(byte[] b, int off, int len) {
		if (b == null)
			return NULL_POINTER_ERR;
		if (len < 0)
			return LENGTH_NEG_ERR;
		if (len == 0)
			return 0;
		if (off < 0)
			return OFFSET_NEG_ERR;
		int bufferLen = rxQueue.availToRead();
		if (len > bufferLen)
			len = bufferLen;
		if (len > b.length)
			len = b.length;
		if (len + off > b.length)
			len = b.length - off;
		for (int i = 0; i < len; i++) {
			b[off + i] = rxQueue.dequeue();
		}
		return len;
	}

	/**
	 * Reads the given number of bytes from the SCI2. A call of
	 * this method is not blocking!
	 * 
	 * @param b
	 *            Byte Array to write the received data.
	 * @return the number of bytes read. 0 if there were no data
	 *            available to read or if the length of the array
	 *            was zero (b.length == 0).
	 *            {@link #NULL_POINTER_ERR} if the array reference
	 *            was null (b == null).
	 */
	public static int read(byte[] b) {
		return read(b, 0, b.length);
	}

	/**
	 * Reads one byte from the SCI2. A call of
	 * this method is not blocking!
	 * 
	 * @return byte read or {@link mpc555.util.ByteFifo#NO_DATA} if
	 *             no data was available.
	 */
	public static int read() {
		return rxQueue.dequeue();
	}

	/**
	 * Writes a given number of bytes into the transmit buffer.
	 * A call of this method is not blocking! There will only as
	 * many bytes written, which are free in the buffer.
	 * 
	 * @param b
	 *            Array of bytes to send.
	 * @param off
	 *            Offset to the data which should be sent.
	 * @param len
	 *            Number of bytes to send.
	 * @return the number of bytes written.
	 *            {@link #LENGTH_NEG_ERR} if the given number of
	 *            bytes was negative (len < 0).
	 *            {@link #OFFSET_NEG_ERR} if the given offset was
	 *            negative (off < 0).
	 *            {@link #NULL_POINTER_ERR} if the array reference
	 *            was null (b == null).
	 */
	public static int write(byte[] b, int off, int len) {
		if (b == null)
			return NULL_POINTER_ERR;
		if (len < 0)
			return LENGTH_NEG_ERR;
		if (off < 0)
			return OFFSET_NEG_ERR;
		if (len + off > b.length)
			len = b.length - off;
		int bufferSpace = txQueue.availToWrite();
		if (bufferSpace < len)
			len = bufferSpace;
		for (int i = 0; i < len; i++) {
			txQueue.enqueue(b[off + i]);
		}
		startTransmission();
		return len;
	}

	/**
	 * Writes a given number of bytes into the transmit buffer.
	 * A call of this method is not blocking! There will only as
	 * many bytes written, which are free in the buffer.
	 * 
	 * @param b
	 *            Array of bytes to send.
	 * @param off
	 *            Offset to the data which should be sent.
	 * @param len
	 *            Number of bytes to send.
	 * @return the number of bytes written.
	 *            {@link #NULL_POINTER_ERR} if the array reference
	 *            was null (b == null).
	 */
	public static int write(byte[] b) {
		return write(b, 0, b.length);
	}

	/**
	 * Writes a given byte into the transmit buffer.
	 * A call of this method is blocking! That means
	 * this method won't terminate until the byte is
	 * written to the buffer!
	 * 
	 * @param b
	 *            Byte to write.
	 */
	public static void write(byte b) {
		while (txQueue.availToWrite() <= 0);
		txQueue.enqueue(b);
		startTransmission();
	}

	/**
	 * Resets the SCI1. This means, the SCI will be
	 * stopped and reinitialized with the same configuration.
	 */
	public static void reset() {
		stop();
		start(currentBaudRate, currentParity, currentDataBits);
	}
	
	static {
		out = new SCI2OutputStream();
		in = new SCI2InputStream();
		QSMCM.init();

		rxQueue = new ByteFifo(QUEUE_LEN);
		txQueue = new ByteFifo(QUEUE_LEN);

		rxInterrupt = new SCI2();
		rxInterrupt.enableRegAdr = QSMCM.SCC2R1;
		rxInterrupt.enBit = QSMCM.scc2r1RIE;
		rxInterrupt.flagRegAdr = QSMCM.SC2SR;
		rxInterrupt.flag = QSMCM.sc2srRDRF;

		txInterrupt = new SCI2();
		txInterrupt.enableRegAdr = QSMCM.SCC2R1;
		txInterrupt.enBit = QSMCM.scc2r1TIE;
		txInterrupt.flagRegAdr = QSMCM.SC2SR;
		txInterrupt.flag = QSMCM.sc2srTDRE;

		Interrupt.install(rxInterrupt, 5, true);	
		Interrupt.install(txInterrupt, 5, true);	
	}
}