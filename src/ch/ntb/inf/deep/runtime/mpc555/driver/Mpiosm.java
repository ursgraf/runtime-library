package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.unsafe.HWD;

/*changes:
 * 15.2.2007 NTB/SP assigned to java
 * 09.02.06	NTB/HS	stub creation
 */
/**
 * Digital-Ein-/Ausgabe über die Mpiosm-Schnittstelle.<br>
 * Diese Variante ist der Benutzung des DIO-Treiber vorzuziehen, da dadruch
 * keine TPU-A-Pins benutzt werden.<br>
 * Grundsätzlich können die folgenden Operationen auf alle 16 Mpiosm-Pins
 * angewandt werden. <br>
 * Wenn man jedoch den auf dem Print vorhandenen CAN-Controller verwendet,
 * dürfen die Pins 13, 14 und 15 nicht verwendet werden.<br>
 * Es muss jedoch beachtet werden, dass der <code>DistSense</code> Treiber
 * ebenfalls die MPIOB-Schnittstelle benutzt.<br>
 * Auf dem Experimentierprint befinden sich die Pins <i>5..15</i> im Bereich
 * MPIOB. Die Pins <i>0..4</i> sind infolge Doppelbelegung als <i>VF0</i>,
 * <i>VF1</i>, <i>VF2</i>, <i>VFLS0</i>, <i>VFLS1</i> beschriftet.
 */
public class Mpiosm {


	/**
	 * Initialisiert den verlangten Pin als Ein- oder Ausgang.<br>
	 * Jeder Pin muss vor der ersten Verwendung initialisiert werden.
	 * 
	 * @param channel
	 *            Mpiosm-Pin <code>0..15</code>, welcher initialisiert wird.
	 * @param out
	 *            <code>true</code> definiert den Mpiosm-Pin als TTL-Ausgang.
	 *            <code>false</code> definiert den Mpiosm-Pin als TTL-Eingang.
	 */
	public static void init(int channel, boolean out) {
		short s = HWD.GET2(Kernel.MPIOSMDDR);
		if(out) s |= (1 << channel);
		else s &= ~(1 << channel);
		HWD.PUT2(Kernel.MPIOSMDDR,s);
	}

	/**
	 * Erfasst den Zustand des TTL-Signals an diesem Pin.<br>
	 * 
	 * @param channel
	 *            Mpiosm-Pin, dessen Wert erfasst werden soll.
	 * @return Funktionswert des gewählten Mpiosm-Pin. <code>true</code>
	 *         entspricht dabei dem Wert <i>logisch 1</i> während
	 *         <code>false</code> dem Wert <i>logisch 0</i> entspricht.
	 */
	public static boolean in(int channel) {
		return (HWD.GET2(Kernel.MPIOSMDR) & (1 << channel)) != 0;
	}

	/**
	 * Ändert den Zustand eines initialisierten Pins.
	 * 
	 * @param channel
	 *            Mpiosm-Pin, dessen Wert verändert werden soll.
	 * @param val
	 *            Für <code>true</code> wird der Wert <i>logisch 1</i> auf
	 *            den TTL-Ausgang gelegt. Für <code>false</code> wird der Wert
	 *            <i>logisch 0</i> auf den TTL-Ausgang gelegt.
	 */
	public static void out(int channel, boolean val) {
		short s = HWD.GET2(Kernel.MPIOSMDR);
		if(val) s |= (1 << channel);
		else s &= ~(1 << channel);
		HWD.PUT2(Kernel.MPIOSMDR, s);
	}

}