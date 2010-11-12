package ch.ntb.inf.sts.mpc555.driver;

import ch.ntb.inf.sts.internal.SYS;
//import ch.ntb.inf.sts.mpc555.Exceptions;
import ch.ntb.inf.sts.mpc555.Interrupt;
import ch.ntb.inf.sts.util.ByteFifo;

/* 
 * 31.3.2007 NTB/SP read failure corrected and error states added
 * 12.2.2007 NTB/SP assigned to Java
 */
/**
 * Interrupt gesteuerter Treiber für das Serial Communication Interface 1 des
 * mpc555.<br>
 * <p>
 * <b>Achtung:</b><br>
 * Je nach eingestellter Baudrate kann es zu Abweichungen in der effektiven
 * Baudrate kommen.<br>
 * Dies kann bei angeschlossenen Endgeräten zu Fehlinterpretationen der
 * gesendeten Bytes führen.<br>
 * Siehe dazu im <a
 * href="http://inf.ntb.ch/infoportal/help/topic/ch.ntb.infoportal/resources/embeddedSystems/mpc555/pdfs/MPC555UM.pdf"
 * target="_blank">MPC555 Manual</a> Table 14-29 im Kapitel 14.8.7.3.
 * </p>
 */
public class SCI1 extends Interrupt {

	public static final byte NO_PARITY = 0, ODD_PARITY = 1, EVEN_PARITY = 2;

	// Driver states
	public static final int PORT_OPEN = 9, TX_EMPTY = 8, TX_COMPLETE = 7,
			RX_RDY = 6, RX_ACTIVE = 5;

	// Error states
	public static final int IDLE_LINE_DET = 4, OVERRUN_ERR = 3, NOISE_ERR = 2,
			FRAME_ERR = 1, PARITY_ERR = 0, LENGTH_NEG_ERR = -1,
			OFFSET_NEG_ERR = -2, NULL_POINTER_ERR = -3;

	private static final int QUEUE_LEN = 2047;
	private static final int CLOCK = 40000000;
	private static Interrupt rxInterrupt, txInterrupt;

	private static short portStat; // just for saving flag portOpen
	private static short scc1r1; // content of SCC1R1

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

	public static int intCtr;

	public void Do() {
		intCtr++;
		if (this == rxInterrupt) {
			short word = SYS.GET2(QSMCM.SC1DR);
			rxQueue.enqueue((byte) word);
		} else {
			if (txQueue.availToRead() > 0) {
				SYS.PUT2(QSMCM.SC1DR, txQueue.dequeue());
			} else {
				txDone = true;
				scc1r1 &= ~(1 << QSMCM.scc1r1TIE);
				SYS.PUT2(QSMCM.SCC1R1, scc1r1);
			}
		}
	}

	private static void startTransmission() {
		if (txDone && (txQueue.availToRead() > 0)) {
			txDone = false;
			SYS.PUT2(QSMCM.SC1DR, txQueue.dequeue());
			scc1r1 |= (1 << QSMCM.scc1r1TIE);
			SYS.PUT2(QSMCM.SCC1R1, scc1r1);
		}
	}

	public static void clearReceiveBuffer() {
		rxQueue.clear();
	}

	public static void clearTransmittBuffer() {
		scc1r1 &= ~(1 << QSMCM.scc1r1TIE);
		SYS.PUT2(QSMCM.SCC1R1, scc1r1);
		txQueue.clear();
		txDone = true;
	}

	public static void clear() {
		clearReceiveBuffer();
		clearTransmittBuffer();
	}

	/**
	 * Stoppt das Serial Communication Interface.<br>
	 */
	public static void stop() {
		clear();
		SYS.PUT2(QSMCM.SCC1R1, 0);
		portStat = 0;
	}

	/**
	 * Startet und initialisiert das Serial Communication Interface.<br>
	 * Diese Methode muss vor der Verwendung der SCI aufgerufen werden. Die
	 * Anzahl Stop Bits kann nicht gewählt werden. Standardmässig ist 1 Stop Bit
	 * eingestellt.
	 * 
	 * @param baudRate
	 *            Baudrate im Bereich von 64 bis 500'000 bits/sec.
	 * @param parity
	 *            Parity bits. Gültige Werte sind 0..2(4) (0 = no parity, 1 =
	 *            odd parity, 2 = even parity)
	 * @param data
	 *            Anzahl Data Bits. Gültige Werte sind 7..9. Fall 9 Data Bits
	 *            gewählt werden steht kein parity Bit zur Verfügung.
	 */
	public static void start(int baudRate, short parity, short data) {
		stop();
		short scbr = (short) ((CLOCK / baudRate + 16) / 32);
		if (scbr <= 0)
			scbr = 1;
		else if (scbr > 8191)
			scbr = 8191;
		scc1r1 |= (1 << QSMCM.scc1r1TE) | (1 << QSMCM.scc1r1RE)
				| (1 << QSMCM.scc1r1RIE); // Transmitter and Receiver enable
		if (parity == 0) {
			if (data >= 9)
				scc1r1 |= (1 << QSMCM.scc1r1M);
		} else {
			if (data >= 8)
				scc1r1 |= (1 << QSMCM.scc1r1M) | (1 << QSMCM.scc1r1PE);
			else
				scc1r1 = (1 << QSMCM.scc1r1PE);
			if (parity == 1)
				scc1r1 |= (1 << QSMCM.scc1r1PT);
		}
		SYS.PUT2(QSMCM.SCC1R0, scbr);
		SYS.PUT2(QSMCM.SCC1R1, scc1r1);
		portStat |= (1 << PORT_OPEN);
		short status = SYS.GET2(QSMCM.SC1SR); // Clear status register
	}

	/**
	 * Gibt die Port Status Bits zurück.<br>
	 * Jedes Bit repräsentiert ein Flag (z.B. {@link #FLAG_PORT_OPEN}).
	 * 
	 * @return die Port Status Bits
	 */
	public static short portStatus() {
		return (short) (portStat | SYS.GET2(QSMCM.SC1SR));
	}

	/**
	 * Gibt die Anzahl Bytes zurück, welche sich im Lese-Puffer befinden.<br>
	 * 
	 * @return die Anzahl Bytes, welche sich im Lese-Puffer befinden
	 */
	public static int availToRead() {
		return rxQueue.availToRead();
	}

	/**
	 * Gibt der verfügbare Platz im Sende-Puffer in Bytes zurück.<br>
	 * Diese Anzahl kann in einem nicht blockierenden Transfer gesendet werden.
	 * 
	 * @return der verfügbare Platz im Sende-Puffer in Bytes
	 */
	public static int availToWrite() {
		return txQueue.availToWrite();
	}

	/**
	 * Liest eine Anzahl Bytes.<br>
	 * Der Aufruf ist nicht blockierend (im Gegensatz zu Java Streams).
	 * 
	 * @param b
	 *            In dieses Array werden die gelesenen Daten geschrieben.
	 * @param off
	 *            Ab diesem Offset wird in das Array geschrieben.
	 * @param len
	 *            Länge der zu lesenden Daten.
	 * @return die Anzahl gelesener Bytes. 0 falls keine Daten verfügbar oder
	 *         <code>len</code> = 0. {@link #LENGTH_NEG_ERR} falls
	 *         <code>len</code> negativ ist. {@link #OFFSET_NEG_ERR} falls
	 *         <code>off</code> negativ ist. {@link #NULL_POINTER_ERR} falls
	 *         <code>b == null</code>.
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
	 * Liest eine Anzahl Bytes.<br>
	 * Der Aufruf ist nicht blockierend (im Gegensatz zu Java Streams).
	 * 
	 * @param b
	 *            In dieses Array werden die gelesenen Daten geschrieben.
	 * @return die Anzahl gelesener Bytes. 0 falls keine Daten verfügbar oder
	 *         <code>len</code> = 0. {@link #NULL_POINTER_ERR} falls
	 *         <code>b == null</code>.
	 */
	public static int read(byte[] b) {
		return read(b, 0, b.length);
	}

	/**
	 * Liest ein Byte.<br>
	 * Der Aufruf ist nicht blockierend (im Gegensatz zu Java Streams).
	 * 
	 * @return datum oder {@link mpc555.util.ByteFifo#NO_DATA} falls keine Daten
	 *         verfügbar sind.
	 */
	public static int read() {
		return rxQueue.dequeue();
	}

	/**
	 * Schreibt eine Anzahl Bytes in den Sende-Puffer.<br>
	 * Der Aufruf ist nicht blockierend. Es werden soviele Bytes geschrieben,
	 * wie im Sende-Puffer Platz haben.
	 * 
	 * @param b
	 *            Bytes welche gesendet werden.
	 * @param off
	 *            Ab diesem Offset in <code>b</code> werden die Daten
	 *            gesendet.
	 * @param len
	 *            Länge der zu senden Daten.
	 * @return Anzahl der gesendeten Daten. {@link #LENGTH_NEG_ERR} falls
	 *         <code>len</code> negativ ist. {@link #OFFSET_NEG_ERR} falls
	 *         <code>off</code> negativ ist. {@link #NULL_POINTER_ERR} falls
	 *         <code>b == null</code>.
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
	 * Schreibt eine Anzahl Bytes in den Sende-Puffer.<br>
	 * Der Aufruf ist nicht blockierend. Es werden soviele Bytes geschrieben,
	 * wie im Sende-Puffer Platz haben.
	 * 
	 * @param b
	 *            Bytes welche gesendet werden.
	 * @return Anzahl der gesendeten Daten. {@link #NULL_POINTER_ERR} falls
	 *         <code>b == null</code>.
	 */
	public static int write(byte[] b) {
		return write(b, 0, b.length);
	}

	/**
	 * Schreibt ein Byte in den Sende-Puffer.<br>
	 * Der Aufruf ist blockierend. Das heisst, dass die Methode nicht terminiert
	 * solange keinen Platz im Puffer vorhanden ist.
	 * 
	 * @param b
	 *            Zu sendendes Byte
	 */
	public static void write(byte b) {
		while (txQueue.availToWrite() <= 0)
			;
		txQueue.enqueue(b);
		startTransmission();
	}

	static {
		QSMCM.init();

		rxQueue = new ByteFifo(QUEUE_LEN);
		txQueue = new ByteFifo(QUEUE_LEN);

		rxInterrupt = new SCI1();
		rxInterrupt.enableRegAdr = QSMCM.SCC1R1;
		rxInterrupt.enBit = QSMCM.scc1r1RIE;
		rxInterrupt.flagRegAdr = QSMCM.SC1SR;
		rxInterrupt.flag = QSMCM.sc1srRDRF;

		txInterrupt = new SCI1();
		txInterrupt.enableRegAdr = QSMCM.SCC1R1;
		txInterrupt.enBit = QSMCM.scc1r1TIE;
		txInterrupt.flagRegAdr = QSMCM.SC1SR;
		txInterrupt.flag = QSMCM.sc1srTDRE;

//		Exceptions.installInternalIntProc(rxInterrupt, 5);	anpassen
//		Exceptions.installInternalIntProc(txInterrupt, 5);
	}
}