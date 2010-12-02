package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 10.1.2008 NTB/SP	to java ported
 * 08.02.06	NTB/HS	stub creation
 */
/**
 * PulsWeiten-Modulation (PWM) mit der TPU-A oder TPU-B.<br>
 * Grundsätzlich können die folgenden Operationen auf alle 2x 16 TPU-Pins
 * angewandt werden. <br>
 * Es muss jedoch beachtet werden, dass andere Treiber ebenfalls die TPU-A oder
 * TPU-B benutzen können.<br>
 * Auf dem Experimentierprint sind die TPU-Pins <i>2x 0..15</i> im Bereich
 * TPU-A und TPU-B zu finden.<br>
 * Zeitangaben müssen in ganzzahligen Vielfachen der TPU-Zeitbasis gemacht
 * werden. Ein Zeitzyklus entspricht 0.8 ns.
 */
public class PWM {


	/** TPU-Zeitbasis: 806 ns */
	public static final int TpuTimeUnit = 806;

	/**
	 * Initialisiert den verlangten TPU-Pin als PWM-Kanal. <br>
	 * Jeder Kanal muss vor der ersten Verwendung initialisiert werden.<br>
	 * <code>period</code> und <code>highTime</code> liegen im Wertebereich
	 * des Integers.<br>
	 * Zur Definitiion der <code>periode</code> sollte eine Konstante des Typs
	 * <code>int</code> angelegt werden.<br>
	 * z.B. für eine Periodenlänge von 50 us (20 kHz):<br>
	 * <code>final short pwmPeriod = 50000 / TpuTimeUnit;</code>
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Pin <code>0..15</code>, welcher initialisiert wird.
	 * @param period
	 *            Periodenlänge des TPU-Signals. Ist ein Vielfaches von
	 *            <code>TpuTimeUnit</code>.
	 * @param highTime
	 *            Länge, über welche das TPU-Signal eingeschaltet ist.
	 *            <code>highTime</code> ist ein Vielfaches von
	 *            <code>TpuTimeUnit</code> und sollte kleiner gleich
	 *            <code>period</code> sein.
	 */
	public static void init(boolean tpuA, int channel, int period, int highTime) {
		int shift, tpuAdr, s;
		if(tpuA){
			TPU_A.init();
			shift = (channel * 4) % 16;
			tpuAdr = TPU_A.CFSR3 - (channel / 4) * 2;			
			s = US.GET2(tpuAdr);
			s &= ~(0xF << shift);
			s |= 3 << shift;
			US.PUT2(tpuAdr,(short) s);
			//Force pin hig, use TCR1
			tpuAdr = TPU_A.TPURAM0 +0x10 * channel;
			US.PUT2(tpuAdr, 0x91 );
			//Define high time
			US.PUT2(tpuAdr + 4, highTime);
			//Define time of period
			US.PUT2(tpuAdr  + 6, period);
			//Request initialization
			tpuAdr = TPU_A.HSRR1 - (channel / 8) * 2;
			shift = (channel * 2) % 16;
			s = US.GET2(tpuAdr);
			s &= ~(0x3 << shift);
			s |= 2 << shift;
			US.PUT2(tpuAdr,s);
			//set priority low
			tpuAdr = TPU_A.CPR1 - (channel / 8) * 2;
			s = US.GET2(tpuAdr);
			s &= ~(0x3 << shift);
			s |= 1 << shift;
			US.PUT2(tpuAdr,s);
		}
		else{
			TPU_B.init();
			shift = (channel * 4) % 16;
			tpuAdr = TPU_B.CFSR3 - (channel / 4) * 2;			
			s = US.GET2(tpuAdr);
			s &= ~(0xF << shift);
			s |= 3 << shift;
			US.PUT2(tpuAdr,(short) s);
			//Force pin hig, use TCR1
			tpuAdr = TPU_B.TPURAM0 +0x10 * channel;
			US.PUT2(tpuAdr, 0x91 );
			//Define high time
			US.PUT2(tpuAdr + 4, highTime);
			//Define time of period
			US.PUT2(tpuAdr  + 6, period);
			//Request initialization
			tpuAdr = TPU_B.HSRR1 - (channel / 8) * 2;
			shift = (channel * 2) % 16;
			s = US.GET2(tpuAdr);
			s &= ~(0x3 << shift);
			s |= 2 << shift;
			US.PUT2(tpuAdr,s);
			//set priority low
			tpuAdr = TPU_B.CPR1 - (channel / 8) * 2;
			s = US.GET2(tpuAdr);
			s &= ~(0x3 << shift);
			s |= 1 << shift;
			US.PUT2(tpuAdr,s);
		}
	}

	/**
	 * Legt Periodenlänge und High-Time des Signals neu fest. <br>
	 * Vor dem Benutzen eines Kanals muss dieser über die Methode
	 * <code>init(..)</code> initialisiert werden.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Pin <code>0..15</code>, welcher benutzt wird.
	 * @param period
	 *            Periodenlänge des TPU-Signals. Ist ein Vielfaches von
	 *            <code>TpuTimeUnit</code>.
	 * @param highTime
	 *            Länge, über welche das TPU-Signal eingeschaltet ist.
	 *            <code>highTime</code> ist ein Vielfaches von
	 *            <code>TpuTimeUnit</code> und sollte kleiner gleich
	 *            <code>period</code> sein.
	 */
	public static void update(boolean tpuA, int channel, int period,
			int highTime) {
		int adr ;
		if(tpuA){
			//Define high time
			adr = TPU_A.TPURAM0 + 0x10 * channel;
			US.PUT2(adr + 4, highTime);
			//Define time of period
			US.PUT2(adr + 6, period);
		}else{
			//Define high time
			adr = TPU_B.TPURAM0 + 0x10 * channel;
			US.PUT2(adr + 4, highTime);
			//Define time of period
			US.PUT2(adr + 6, period);
		}
		
	}
}