package ch.ntb.inf.sts.mpc555.driver;

import ch.ntb.inf.sts.internal.SYS;

/**
 * Perioden- / Pulsweiten-Messung (PPWA) mit der TPU-A oder TPU-B.<br>
 * Es kann die Hightime oder die Periode des Signals gemessen.<br>
 * Grundsätzlich können die folgenden Operationen auf alle 2x 16 TPU-Pins
 * angewandt werden. <br>
 * Es muss jedoch beachtet werden, dass andere Treiber ebenfalls die TPU-A oder
 * TPU-B benutzen können.<br>
 * Auf dem Experimentierprint sind die TPU-Pins <i>2x 0..15</i> im Bereich
 * TPU-A und TPU-B zu finden.<br>
 */
public class PPWA {

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
	 *            Messen der Periodenlänge.
	 */
	public static void init(boolean tpuA, int channel, boolean pulseWidth) {
		if (tpuA) {
			// Disable interrupts for all channels
			int intChn = SYS.GET2(TPU_A.CIER);
			SYS.PUT2(TPU_A.CIER, 0);
			intChn &= (channel ^ 0xffffffff);

			// function code (5) for PPWA
			int low = (channel * 4) % 16;
			int value = SYS.GET2(TPU_A.CFSR3 - (channel / 4) * 2);
			value &= ((0xf << low) ^ 0xffffffff);
			value |= (0x5 << low);
			SYS.PUT2(TPU_A.CFSR3 - (channel / 4) * 2, value);

			// 24 bit pulse widths oder period, no links for channel (0b10)
			low = (channel * 2) % 16;
			value = SYS.GET2(TPU_A.HSQR1 - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			if (pulseWidth)
				value |= (0x2 << low);
			SYS.PUT2(TPU_A.HSQR1 - (channel / 8) * 2, value);

			// Channel control
			if (pulseWidth) {
				// Do not force any state, Detect falling edge
				SYS.PUT2(TPU_A.TPURAM0 + 0x10 * channel, 0x7);
			} else {
				// Do not force any state, Detect rising edge
				SYS.PUT2(TPU_A.TPURAM0 + 0x10 * channel, 0x0b);
			}
			// Max count
			SYS.PUT2(TPU_A.TPURAM0 + 0x10 * channel + 2, 0x0100);
			// Channel accum_rate = minimal
			SYS.PUT2(TPU_A.TPURAM0 + 0x10 * channel + 8, 0xff00);

			// Initialize
			low = (channel * 2) % 16;
			value = SYS.GET2(TPU_A.HSRR1 - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			value |= (0x2 << low);
			SYS.PUT2(TPU_A.HSRR1 - (channel / 8) * 2, value);

			// Set priority low
			low = (channel * 2) % 16;
			value = SYS.GET2(TPU_A.CPR1 - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			value |= (0x1 << low);
			SYS.PUT2(TPU_A.CPR1 - (channel / 8) * 2, value);

			// Enable interrupts for other channels
			SYS.PUT2(TPU_A.CIER, intChn);
		} else {
			// Disable interrupts for all channels
			int intChn = SYS.GET2(TPU_A.CIER);
			SYS.PUT2(TPU_B.CIER, 0);
			intChn &= (channel ^ 0xffffffff);

			// function code (5) for PPWA
			int low = (channel * 4) % 16;
			int value = SYS.GET2(TPU_B.CFSR3 - (channel / 4) * 2);
			value &= ((0xf << low) ^ 0xffffffff);
			value |= (0x5 << low);
			SYS.PUT2(TPU_B.CFSR3 - (channel / 4) * 2, value);

			// 24 bit pulse widths oder period, no links for channel (0b10)
			low = (channel * 2) % 16;
			value = SYS.GET2(TPU_B.HSQR1 - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			if (pulseWidth)
				value |= (0x2 << low);
			SYS.PUT2(TPU_B.HSQR1 - (channel / 8) * 2, value);

			// Channel control
			if (pulseWidth) {
				// Do not force any state, Detect falling edge
				SYS.PUT2(TPU_B.TPURAM0 + 0x10 * channel, 0x7);
			} else {
				// Do not force any state, Detect rising edge
				SYS.PUT2(TPU_B.TPURAM0 + 0x10 * channel, 0x0b);
			}
			// Max count
			SYS.PUT2(TPU_B.TPURAM0 + 0x10 * channel + 2, 0x0100);
			// Channel accum_rate = minimal
			SYS.PUT2(TPU_B.TPURAM0 + 0x10 * channel + 8, 0xff00);

			// Initialize
			low = (channel * 2) % 16;
			value = SYS.GET2(TPU_B.HSRR1 - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			value |= (0x2 << low);
			SYS.PUT2(TPU_B.HSRR1 - (channel / 8) * 2, value);

			// Set priority low
			low = (channel * 2) % 16;
			value = SYS.GET2(TPU_B.CPR1 - (channel / 8) * 2);
			value &= ((0x3 << low) ^ 0xffffffff);
			value |= (0x1 << low);
			SYS.PUT2(TPU_B.CPR1 - (channel / 8) * 2, value);

			// Enable interrupts for other channels
			SYS.PUT2(TPU_B.CIER, intChn);
		}
	}

	/**
	 * Auslesen des zuletzt gemessenen Wertes.<br>
	 * Die Werte werden über die TPU permanent aktualisiert. Mittels dieser
	 * Methode kann der zuletzt gemessene Wert ausgelesen werden.<br>
	 * <br>
	 * Die Werte sind in &micro;s.<br>
	 * Da direkt innerhalb der Methode die Umrechnung bzgl. der verwendeten
	 * CycleTime durchgeführt wird, ist keine weitere Umrechnung nötig.
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
			int lowValue = SYS.GET2(TPU_A.TPURAM0 + 0x10 * channel + 0xA);
			value = lowValue * TPU_A.getCycleTime() / 1000;
		} else {
			int lowValue = SYS.GET2(TPU_B.TPURAM0 + 0x10 * channel + 0xA);
			value = lowValue * TPU_B.getCycleTime() / 1000;
		}
		return value;
	}
}