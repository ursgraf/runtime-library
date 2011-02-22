package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 15.2.07 NTB/SP adapted to java
 08.02.06	NTB/HS	stub creation
 */
/**
 * Digital-Ein-/Ausgabe mit der TPU-A oder TPU-B.<br>
 * Falls möglich ist der <code>Mpiosm</code> Treiber für die digitale Ein- /
 * Ausgabe zu benutzen. Da diverse andere Treiber die TPU Kanäle nutzen, kann es
 * sein dass durch den <code>DIO</code> Treiber unnötig Pins benutzt werden.<br>
 * Grundsätzlich können die folgenden Operationen auf alle 2x 16 TPU-Pins
 * angewandt werden. <br>
 * Es muss jedoch beachtet werden, dass andere Treiber ebenfalls die TPU-A oder
 * TPU-B benutzen können.<br>
 * Auf dem Experimentierprint sind die TPU-Pins <i>2x 0..15</i> im Bereich
 * TPU-A und TPU-B zu finden.
 */
public class TPU_DIO implements ntbMpc555HB {
	

	/**
	 * Initialisiert den verlangten Pin als Ein- oder Ausgang. <br>
	 * Jeder Pin muss vor der ersten Verwendung initialisiert werden.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Pin <code>0..15</code>, welcher initialisiert wird.
	 * @param out
	 *            <code>true</code> definiert den TPU-Pin als TTL-Ausgang.
	 *            <code>false</code> definiert den TPU-Pin als TTL-Eingang.
	 */
	public static void init(boolean tpuA, int channel, boolean out) {
		if(tpuA){
			//function code (2) for DIO
			short s = US.GET2(CFSR3_A - (channel / 4) * 2);
			int shiftl = (channel % 4) * 4;
			s &= ~(7 << shiftl);
			s |= (2 << shiftl);
			US.PUT2(CFSR3_A - (channel / 4) * 2,s);
			
			//Update on transition for inputs, dosen't have any effect for outputs
			s = US.GET2(HSQR1_A - (channel / 8) * 2);
			shiftl = (channel % 8) * 2;
			s &= ~(3 << shiftl);
			US.PUT2(HSQR1_A -(channel / 8) * 2, s);
			
			if(out){
				US.PUT2(TPURAM0_A + 0x10 * channel, 0x3);
			}else{
				s = US.GET2(HSQR1_A - (channel / 8) * 2);
				s &= ~(3 << shiftl);
				US.PUT2(HSQR1_A -(channel / 8) * 2, s);
				US.PUT2(TPURAM0_A + 0x10 * channel, 0xF);
			}
			
			//Request initialization
			s = US.GET2(HSRR1_A -(channel / 8)* 2);
			s |= (3 <<shiftl);
			US.PUT2(HSRR1_A -(channel / 8)* 2, s);
			
			//Set priority low
			s = US.GET2(CPR1_A - (channel / 8)* 2);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(CPR1_A - (channel / 8) * 2,s);
		}else{
			//function code (2) for DIO
			short s = US.GET2(CFSR3_B - (channel / 4) * 2);
			int shiftl = (channel % 4) * 4;
			s &= ~(7 << shiftl);
			s |= (2 << shiftl);
			US.PUT2(CFSR3_B - (channel / 4) * 2,s);
			
			//Update on transition for inputs, dosen't have any effect for outputs
			s = US.GET2(HSQR1_B - (channel / 8) * 2);
			shiftl = (channel % 8) * 2;
			s &= ~(3 << shiftl);
			US.PUT2(HSQR1_B -(channel / 8) * 2, s);
			
			if(out){
				US.PUT2(TPURAM0_B + 0x10 * channel, 0x3);
			}else{
				s = US.GET2(HSQR1_B - (channel / 8) * 2);
				s &= ~(3 << shiftl);
				US.PUT2(HSQR1_B -(channel / 8) * 2, s);
				US.PUT2(TPURAM0_B + 0x10 * channel, 0xF);
			}
			
			//Request initialization
			s = US.GET2(HSRR1_B -(channel / 8)* 2);
			s |= (3 <<shiftl);
			US.PUT2(HSRR1_B -(channel / 8)* 2, s);
			
			//Set priority low
			s = US.GET2(CPR1_B - (channel / 8)* 2);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			US.PUT2(CPR1_B - (channel / 8) * 2,s);
		}
	}

	/**
	 * Erfasst den Zustand des TTL-Signals an diesem Pin. <br>
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Pin, dessen Wert erfasst werden soll.
	 * @return Funktionswert des gewählten TPU-Pin. <code>true</code>
	 *         entspricht dabei dem Wert <i>logisch 1</i> während
	 *         <code>false</code> dem Wert <i>logisch 0</i> entspricht.
	 */
	public static boolean in(boolean tpuA, int channel) {
		if(tpuA){
			return (US.GET2(TPURAM0_A + 0x10 * channel + 2) & (1 << 15)) != 0; 
		}else{
			return (US.GET2(TPURAM0_B  + 0x10 * channel + 2) & (1 << 15)) != 0; 
		}
	}

	/**
	 * Ändert den Zustand eines initialisierten Pins.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Pin, dessen Wert verändert werden soll.
	 * @param val
	 *            Für <code>true</code> wird der Wert <i>logisch 1</i> auf
	 *            den TTL-Ausgang gelegt. Für <code>false</code> wird der Wert
	 *            <i>logisch 0</i> auf den TTL-Ausgang gelegt.
	 */
	public static void out(boolean tpuA, int channel, boolean val) {
		if(tpuA){
			//Disable all Interrupts
			short sh = US.GET2(CISR_A);
			US.PUT2(CISR_A,(short)0);
			
			short s = US.GET2(HSRR1_A - ((channel / 8) * 2));
			int shiftl = (channel % 8) * 2;
			s &= ~(3 << shiftl);
			if(val) s |= (1 << shiftl);
			else s |= (2 << shiftl);
			US.PUT2(HSRR1_A - ((channel / 8) * 2), s);
			
			//Restore Interrupts
			US.PUT2(CISR_A, sh);
		}else{
			//Disable all Interrupts
			short sh = US.GET2(CISR_B);
			US.PUT2(CISR_B,(short)0);
			
			int shiftl = (channel % 8) * 2;
			short s = US.GET2(HSRR1_B - ((channel / 8) * 2));
			s &= ~(3 << shiftl);
			if(val) s |= (1 << shiftl);
			else s |= (2 << shiftl);
			US.PUT2(HSRR1_B - ((channel / 8) * 2), s);
			
			//Restore Interrupts
			US.PUT2(CISR_B, sh);
			
		}
	}

}