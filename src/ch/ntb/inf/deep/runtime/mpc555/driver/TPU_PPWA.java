package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Perioden- / Pulsweiten-Messung (PPWA) mit der TPU-A oder TPU-B.<br>
 * Es kann die Hightime oder die Periode des Signals gemessen.<br>
 * Grundsaetzlich können die folgenden Operationen auf alle 2x 16 TPU-Pins
 * angewandt werden. <br>
 * Es muss jedoch beachtet werden, dass andere Treiber ebenfalls die TPU-A oder
 * TPU-B benutzen können.<br>
 * Auf dem Experimentierprint sind die TPU-Pins <i>2x 0..15</i> im Bereich
 * TPU-A und TPU-B zu finden.<br>
 */
public class TPU_PPWA implements ntbMpc555HB{

	/**
	 * Initialisiert den verlangten TPU-Pin als PPWA-Kanal.<br>
	 * Jeder Kanal muss vor der ersten Verwendung initialisiert werden.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Pin <code>0..15</code>, welcher initialisiert wird.
	 * @param pulseWidth
	 *            <code>true</code>: Messen der Pulsweite. <code>false</code>:
	 *            Messen der Periodenlaenge.
	 */
	public static void init(boolean tpuA, int channel, boolean pulseWidth) {
		if (tpuA) {
			// Disable interrupts for all channels
			int intChn = US.GET2(CIER_A);
			US.PUT2(CIER_A, 0);
			intChn &= (channel ^ 0xffffffff);

			// function code (5) for PPWA
			int low = (channel * 4) % 16;
			int value = US.GET2(CFSR3_A - (channel / 4) * 2);
			value &= ((0xf << low) ^ 0xffffffff);
			value |= (0x5 << low);
			US.PUT2(CFSR3_A - (channel / 4) * 2, value);

			// 24 bit pulse widths oder period, no links for channel (0b10)
			low = (channel * 2) % 16;
			value = US.GET2(HSQR1_A - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			if (pulseWidth)
				value |= (0x2 << low);
			US.PUT2(HSQR1_A - (channel / 8) * 2, value);

			// Channel control
			if (pulseWidth) {
				// Do not force any state, Detect falling edge
				US.PUT2(TPURAM0_A + 0x10 * channel, 0x7);
			} else {
				// Do not force any state, Detect rising edge
				US.PUT2(TPURAM0_A + 0x10 * channel, 0x0b);
			}
			// Max count
			US.PUT2(TPURAM0_A + 0x10 * channel + 2, 0x0100);
			// Channel accum_rate = minimal
			US.PUT2(TPURAM0_A + 0x10 * channel + 8, 0xff00);

			// Initialize
			low = (channel * 2) % 16;
			value = US.GET2(HSRR1_A - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			value |= (0x2 << low);
			US.PUT2(HSRR1_A - (channel / 8) * 2, value);

			// Set priority low
			low = (channel * 2) % 16;
			value = US.GET2(CPR1_A - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			value |= (0x1 << low);
			US.PUT2(CPR1_A - (channel / 8) * 2, value);

			// Enable interrupts for other channels
			US.PUT2(CIER_A, intChn);
		} else {
			// Disable interrupts for all channels
			int intChn = US.GET2(CIER_B);
			US.PUT2(CIER_B, 0);
			intChn &= (channel ^ 0xffffffff);

			// function code (5) for PPWA
			int low = (channel * 4) % 16;
			int value = US.GET2(CFSR3_B - (channel / 4) * 2);
			value &= ((0xf << low) ^ 0xffffffff);
			value |= (0x5 << low);
			US.PUT2(CFSR3_B - (channel / 4) * 2, value);

			// 24 bit pulse widths oder period, no links for channel (0b10)
			low = (channel * 2) % 16;
			value = US.GET2(HSQR1_B - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			if (pulseWidth)
				value |= (0x2 << low);
			US.PUT2(HSQR1_B - (channel / 8) * 2, value);

			// Channel control
			if (pulseWidth) {
				// Do not force any state, Detect falling edge
				US.PUT2(TPURAM0_B + 0x10 * channel, 0x7);
			} else {
				// Do not force any state, Detect rising edge
				US.PUT2(TPURAM0_B + 0x10 * channel, 0x0b);
			}
			// Max count
			US.PUT2(TPURAM0_B + 0x10 * channel + 2, 0x0100);
			// Channel accum_rate = minimal
			US.PUT2(TPURAM0_B + 0x10 * channel + 8, 0xff00);

			// Initialize
			low = (channel * 2) % 16;
			value = US.GET2(HSRR1_B - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			value |= (0x2 << low);
			US.PUT2(HSRR1_B - (channel / 8) * 2, value);

			// Set priority low
			low = (channel * 2) % 16;
			value = US.GET2(CPR1_B - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			value |= (0x1 << low);
			US.PUT2(CPR1_B - (channel / 8) * 2, value);

			// Enable interrupts for other channels
			US.PUT2(CIER_B, intChn);
		}
	}

	/**
	 * Auslesen des zuletzt gemessenen Wertes.<br>
	 * Die Werte werden ueber die TPU permanent aktualisiert. Mittels dieser
	 * Methode kann der zuletzt gemessene Wert ausgelesen werden.<br>
	 * <br>
	 * Die Werte sind in &micro;s.<br>
	 * Da direkt innerhalb der Methode die Umrechnung bzgl. der verwendeten
	 * CycleTime durchgefuehrt wird, ist keine weitere Umrechnung nötig.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Pin <code>0..15</code>, welcher initialisiert wird.
	 * @return Zuletzt gemessener Wert
	 */
	public static int read(boolean tpuA, int channel) {
		int value = 0;
		if (tpuA) {
			int lowValue = US.GET2(TPURAM0_A + 0x10 * channel + 0xA);
			value = lowValue * TPUA.getCycleTime() / 1000;
		} else {
			int lowValue = US.GET2(TPURAM0_B + 0x10 * channel + 0xA);
			value = lowValue * TPUB.getCycleTime() / 1000;
		}
		return value;
	}
}