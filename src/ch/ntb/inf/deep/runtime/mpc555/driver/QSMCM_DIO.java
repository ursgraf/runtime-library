package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.unsafe.US;

/**
 * Digital-Ein-/Ausgabe über die PortQS-Schnittstelle.<br>
 * <b>Achtung:</b> Dieser Treiber belegt die gleichen Pins wie die QSPI.<br>
 * Folgende Pins können als GPIO benutzt werden.
 * <ul>
 * <li> {@link #QDMISO}</li>
 * <li> {@link #QDMOSI}</li>
 * <li> {@link #QDPCS0}</li>
 * <li> {@link #QDPCS1}</li>
 * <li> {@link #QDPCS2}</li>
 * <li> {@link #QDPCS3}</li>
 * </ul>
 * 
 * 
 * @author NTB 13.03.2009<br>
 *         Simon Pertschy<br>
 */


public class QSMCM_DIO {
	
	public static final int QDMISO = 0;
	public static final int QDMOSI = 1;
	public static final int QDPCS0 = 3;
	public static final int QDPCS1 = 4;
	public static final int QDPCS2 = 5;
	public static final int QDPCS3 = 6;
	
	
	/**
	 * Initialisiert den verlangten Pin als Ein- oder Ausgang.<br>
	 * Jeder Pin muss vor der ersten Verwendung initialisiert werden.
	 * 
	 * @param channel
	 *            PORTQS-Pin welcher initialisiert wird.
	 * @param out
	 *            <code>true</code> definiert den PortQS-Pin als TTL-Ausgang.
	 *            <code>false</code> definiert den PortQS-Pin als TTL-Eingang.
	 */
	public static void init(int channel, boolean out) {
		byte s = US.GET1(QSMCM.PQSPAR);
		s &= ~(1 << channel);
		US.PUT1(QSMCM.PQSPAR, s);
		s = US.GET1(QSMCM.DDRQS);
		if(out) s |= (1 << channel);
		else s &= ~(1 << channel);
		US.PUT1(QSMCM.DDRQS,s);
	}

	/**
	 * Erfasst den Zustand des TTL-Signals an diesem Pin.<br>
	 * 
	 * @param channel
	 *            PortQS-Pin, dessen Wert erfasst werden soll.
	 * @return Funktionswert des gewählten PortQS-Pin. <code>true</code>
	 *         entspricht dabei dem Wert <i>logisch 1</i> während
	 *         <code>false</code> dem Wert <i>logisch 0</i> entspricht.
	 */
	public static boolean in(int channel) {
		return (US.GET1(QSMCM.PORTQS + 1) & (1 << channel)) != 0;
	}

	/**
	 * Ändert den Zustand eines initialisierten Pins.
	 * 
	 * @param channel
	 *            PortQS-Pin, dessen Wert verändert werden soll.
	 * @param val
	 *            Für <code>true</code> wird der Wert <i>logisch 1</i> auf
	 *            den TTL-Ausgang gelegt. Für <code>false</code> wird der Wert
	 *            <i>logisch 0</i> auf den TTL-Ausgang gelegt.
	 */
	public static void out(int channel, boolean val) {
		short s = US.GET1(QSMCM.PORTQS + 1);
		if(val) s |= (1 << channel);
		else s &= ~(1 << channel);
		US.PUT1(QSMCM.PORTQS + 1, s);
	}
}
