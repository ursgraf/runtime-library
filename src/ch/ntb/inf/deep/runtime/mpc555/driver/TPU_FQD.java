package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 15.2.2007 NTB/SP assigned to java
 * 18.05.06	NTB/HS	tpu selection added, ch => channel
 * 08.02.06	NTB/HS	stub creation
 */
/**
 * Decodierung quadratur-codierter Signale <i>(FQD - Fast Quadrature Decoding)</i>
 * mit der TPU-A oder TPU-B. Quadratur-codierter Signale werden unter anderem
 * gebraucht, um die Winkelposition einer Motoren-Achse zu erfassen. Es handelt
 * sich dabei immer um ein Signalpaar (Signal A,B). Nähere Beschreibung siehe
 * Spezialistenausbildung.
 * 
 * Grundsätzlich können die folgenden Operationen auf alle 16 TPU-Pins angewandt
 * werden. <br>
 * Es muss jedoch beachtet werden, dass andere Treiber ebenfalls die TPU-A oder
 * TPU-B benutzen können.<br>
 * Auf dem Experimentierprint sind die TPU-Pins <i>0..15</i> im Bereich TPU-A
 * und TPU-B zu finden.<br>
 * Bei jedem Methodenaufruf kann die gewünschte TPU mittels dem Parameter
 * <code>tpuA</code> gewählt werden.
 */
public class TPU_FQD implements ntbMpc555HB{


	/**
	 * Initialisiert die zwei TPU-Pins für FQD. <br>
	 * Jeder Pin muss vor der ersten Verwendung initialisiert werden. Da das
	 * Encodersignal aus einem Signalpaar besteht werden immer Zweierpaare
	 * initialisiert (<code>channel</code> und <code>channel+1</code>).<br>
	 * An den TPU-Pin <code>channel</code> wird das Signal A, an den TPU-Pin
	 * <code>channel+1</code> das Signal B angeschlossen.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            Erster TPU-Pin. Der zweite Pin <code>channel+1</code> wird
	 *            automatisch initialisiert. Es können nur folgende Werte
	 *            übergeben werden: <code>0, 2, 4, 6, 8, 12, 14</code>.
	 */
	public static void init(boolean tpuA, int channel) {
		 if(tpuA){
			 int shiftl = (channel % 4) * 4;
			 //initalize TPU for quadrature decode function code = 6;
			 short s = US.GET2(CFSR3_A -(channel / 4) * 2);
			 s &= ~(0xFF << shiftl);
			 s |= (0x66 << shiftl);
			 US.PUT2(CFSR3_A - (channel / 4) * 2, s);
			
			 //ch14: Position_Count = 0
			 US.PUT2(TPURAM0_A + 0x10 * channel + 2, 0);
			 //Edge_time_LSB_Addr = TPRAM14+1
			 US.PUT2(TPURAM0_A + 0x10 * channel +10, TPURAM0_A + 0x10 * channel + 1);
			 //Edge_Time_LSB_addr = TPRAM14 + 1
			 US.PUT2(TPURAM0_A + 0x10 * (channel + 1) + 10, TPURAM0_A +0x10 * channel  + 1);
			 //Corr_Pinstate_addr = TPRAM15+6
			 US.PUT2(TPURAM0_A + 0x10 * channel + 8, TPURAM0_A +0x10 * (channel + 1) + 6);
			 //Corr_Pinstate_addr = TPRAM14+6
			 US.PUT2(TPURAM0_A + 0x10 * (channel + 1) + 8, TPURAM0_A +0x10 *channel  +6);
			 
			 shiftl = (channel % 8) * 2;
			 //Channel is primary, ch+ 1 is secondary channel
			 s = US.GET2(HSQR1_A - (channel / 8) * 2);
			 s &= ~(0x9 << shiftl);
			 s |= (0x4 << shiftl);
			 US.PUT2(HSQR1_A - (channel / 8) * 2,s);
			
			 //Initalize channel and ch + 1	 
			 s = US.GET2(HSRR1_A - (channel / 8) * 2);
			 s &= ~(0x9 << shiftl);
			 s |= (0xF << shiftl);
			 US.PUT2(HSRR1_A - (channel / 8) * 2,s);
			
			 //set priority high
			 s = US.GET2(CPR1_A - (channel / 8) * 2);
			 s &= ~(0x9 << shiftl);
			 s |= (0xF << shiftl);
			 US.PUT2(CPR1_A - (channel / 8) * 2, s);
		 }else{
			 int shiftl = (channel % 4) * 4;
			 //initalize TPU for quadrature decode function code = 6;
			 short s = US.GET2(CFSR3_B -(channel / 4) * 2);
			 s &= ~(0xFF << shiftl);
			 s |= (0x66 << shiftl);
			 US.PUT2(CFSR3_B - (channel / 4) * 2, s);
			 
			 //ch14: Position_Count = 0
			 US.PUT2(TPURAM0_B + 0x10 * channel + 2, 0);
			 //Edge_time_LSB_Addr = TPRAM14+1
			 US.PUT2(TPURAM0_B + 0x10 * channel +10, TPURAM0_B  + 0x10 * channel + 1);
			 //Edge_Time_LSB_addr = TPRAM14 + 1
			 US.PUT2(TPURAM0_B + 0x10 * (channel + 1) + 10, TPURAM0_B +0x10 * channel  + 1);
			 //Corr_Pinstate_addr = TPRAM15+6
			 US.PUT2(TPURAM0_B + 0x10 * channel + 8, TPURAM0_B +0x10 * (channel + 1) + 6);
			 //Corr_Pinstate_addr = TPRAM14+6
			 US.PUT2(TPURAM0_B + 0x10 * (channel + 1) + 8, TPURAM0_B +0x10 *channel  +6);
			 
			 shiftl = (channel % 8) * 2;
			 //Channel is primary, ch+ 1 is secondary channel
			 s = US.GET2(HSQR1_B - (channel / 8) * 2);
			 s &= ~(0x9 << shiftl);
			 s |= (0x4 << shiftl);
			 US.PUT2(HSQR1_B - (channel / 8) * 2,s);
			 
			 //Initalize channel and ch + 1	 
			 s = US.GET2(HSRR1_B - (channel / 8) * 2);
			 s &= ~(0x9 << shiftl);
			 s |= (0xF << shiftl);
			 US.PUT2(HSRR1_B - (channel / 8) * 2,s);
			 
			 //set priority high
			 s = US.GET2(CPR1_B - (channel / 8) * 2);
			 s &= ~(0x9 << shiftl);
			 s |= (0xF << shiftl);
			 US.PUT2(CPR1_B - (channel / 8) * 2, s);
			 
		 }
	}

	/**
	 * Gibt den aktuellen Econder-Wert für das Pinpaar beginnend mit dem Pin
	 * <code>channel</code> zurück.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            Erster Pin des Pinpaares, welches abgefragt werden soll.
	 * 
	 * @return Aktuelle Encoderwert für das abgefragte Pinpaar.
	 */
	public static short getPosition(boolean tpuA, int channel) {
		if(tpuA){
			return US.GET2(TPURAM0_A + 0x10 * channel + 2);
		}else{
			return US.GET2(TPURAM0_B + 0x10 * channel + 2);
		}
	}

	/**
	 * Setzt einen Wert <code>pos</code> für das Pinpaar beginnend mit dem Pin
	 * <code>channel</code>.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            Erster Pin des Pinpaares, dessen Wert gesetzt werden soll.
	 * @param pos
	 *            Neuer Wert, welcher für das Pinpaar gesetzt werden soll.
	 */
	public static void setPosition(boolean tpuA, int channel, int pos) {
		if(tpuA){
			US.PUT2(TPURAM0_A + 0x10 * channel + 2, pos);
		}else{			
			US.PUT2(TPURAM0_B + 0x10 * channel + 2, pos);
		}
	}

}