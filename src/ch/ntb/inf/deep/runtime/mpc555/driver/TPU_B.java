package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.unsafe.SYS;

/*
 * changes:
 * 18.02.2008 NTB/SP, assigned to java
 */

/**
 * TPU Klasse für die TPU-B Anschlüsse.
 */
public class TPU_B {

	/**
	 * TPU module configuration register
	 */
	public static final int TPUMCR = Kernel.UIMB + 0x4400;;

	/**
	 * TPU interrupt configuration register
	 */
	public static final int TICR = Kernel.UIMB + 0x4408;

	/**
	 * TPU channel interrupt enable register
	 */
	public static final int CIER = Kernel.UIMB + 0x440A;

	/**
	 * Channel function selection register 0
	 */
	public static final int CFSR0 = Kernel.UIMB + 0x440C;

	/**
	 * Channel function selection register 1
	 */
	public static final int CFSR1 = Kernel.UIMB + 0x440E;

	/**
	 * Channel function selection register 2
	 */
	public static final int CFSR2 = Kernel.UIMB + 0x4410;

	/**
	 * Channel function selection register 3
	 */
	public static final int CFSR3 = Kernel.UIMB + 0x4412;

	/**
	 * Host sequence register 0
	 */
	public static final int HSQR0 = Kernel.UIMB + 0x4414;

	/**
	 * Host sequence register 1
	 */
	public static final int HSQR1 = Kernel.UIMB + 0x4416;

	/**
	 * Host service register 0
	 */
	public static final int HSRR0 = Kernel.UIMB + 0x4418;

	/**
	 * Host service register 1
	 */
	public static final int HSRR1 = Kernel.UIMB + 0x441A;

	/**
	 * Channel priority register 0
	 */
	public static final int CPR0 = Kernel.UIMB + 0x441C;

	/**
	 * Channel priority register 1
	 */
	public static final int CPR1 = Kernel.UIMB + 0x441E;

	/**
	 * Channel interrupt status register
	 */
	public static final int CISR = Kernel.UIMB + 0x4420;

	/**
	 * TPU module configuration register 2
	 */
	public static final int TPUMCR2 = Kernel.UIMB + 0x4428;

	/**
	 * TPU module configuration register 3
	 */
	public static final int TPUMCR3 = Kernel.UIMB + 0x442A;

	/**
	 * TPU Parameter RAM for Channel 0
	 */
	public static final int TPURAM0 = Kernel.UIMB + 0x4500;

	/**
	 * TPU Parameter RAM for Channel 1
	 */
	public static final int TPURAM1 = Kernel.UIMB + 0x4510;

	/**
	 * TPU Parameter RAM for Channel 2
	 */
	public static final int TPURAM2 = Kernel.UIMB + 0x4520;

	/**
	 * TPU Parameter RAM for Channel 3
	 */
	public static final int TPURAM3 = Kernel.UIMB + 0x4530;

	/**
	 * TPU Parameter RAM for Channel 4
	 */
	public static final int TPURAM4 = Kernel.UIMB + 0x4540;

	/**
	 * TPU Parameter RAM for Channel 5
	 */
	public static final int TPURAM5 = Kernel.UIMB + 0x4550;

	/**
	 * TPU Parameter RAM for Channel 6
	 */
	public static final int TPURAM6 = Kernel.UIMB + 0x4560;

	/**
	 * TPU Parameter RAM for Channel 7
	 */
	public static final int TPURAM7 = Kernel.UIMB + 0x4570;

	/**
	 * TPU Parameter RAM for Channel 8
	 */
	public static final int TPURAM8 = Kernel.UIMB + 0x4580;

	/**
	 * TPU Parameter RAM for Channel 9
	 */
	public static final int TPURAM9 = Kernel.UIMB + 0x4590;

	/**
	 * TPU Parameter RAM for Channel 10
	 */
	public static final int TPURAM10 = Kernel.UIMB + 0x45A0;

	/**
	 * TPU Parameter RAM for Channel 11
	 */
	public static final int TPURAM11 = Kernel.UIMB + 0x45B0;

	/**
	 * TPU Parameter RAM for Channel 12
	 */
	public static final int TPURAM12 = Kernel.UIMB + 0x45C0;

	/**
	 * TPU Parameter RAM for Channel 13
	 */
	public static final int TPURAM13 = Kernel.UIMB + 0x45D0;

	/**
	 * TPU Parameter RAM for Channel 14
	 */
	public static final int TPURAM14 = Kernel.UIMB + 0x45E0;

	/**
	 * TPU Parameter RAM for Channel 15
	 */
	public static final int TPURAM15 = Kernel.UIMB + 0x45F0;

	/**
	 * Gibt die TCR1 Zykluszeit zurück.<br>
	 * Dies bei einem IMB-Clock von 40 MHz.
	 * 
	 * @return Aktuelle TCR1 Zykluszeit.
	 */
	public static int getCycleTime() {
		int prescale = 1;
		short value;
		short s = SYS.GET2(TPUMCR3);
		if((s & (1 << 6)) != 0){
			value = (short) (s & 0x1F);
			prescale = prescale * (value + 1) * 2;
		}else{
			s = SYS.GET2(TPUMCR);
			if((s & (1 << 6)) != 0) prescale *= 4;
			else prescale *= 32;
		}
		
		//TCR1 prescaler
		s = SYS.GET2(TPUMCR);
		value = (short) (s & 0x6000);
		value = (short) (value >> 12);
		prescale = prescale * (1 << value);
		
		//DIV2
		s = SYS.GET2(TPUMCR2);
		if((s & (1 << 8)) != 0) prescale *= 2;
		
		//40 MHz => cycle time = 25ns
		return 25 * prescale;
	}
	
	/**
	 * If you use the TCR1, call this method to initialize the pre-scaler.
	 */
	public static void init(){}
	
	static{
		SYS.PUT2(TPUMCR3,0x0);
		
		//SYS.PUT2(TPUMCR,0x000); //IMB Clock not divided for TCR1, 1 cycle = 0.8us
		//SYS.PUT2(TPUMCR,0x050); //IMB Clock divided by 4 instead of 32, 1 cycle = 0.1us
		//SYS.PUT2(TPUMCR3,0x40); // Enable the enhanced pre-scaler, 1 cycle = 0.05us
		
		/*
		 * CAUTION:
		 * If you change the cycle time, you have to adapt the TpuTimeUnit in the PWM.java file
		 */
		
	}

}