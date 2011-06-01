package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 08.04.2011	NTB/MZ	methods in/out renamed to get/set
 * 15.02.2007	NTB/SP	adapted to java
 * 08.02.2006	NTB/HS	stub creation
 */
/**
 * Driver to use a channel of the TPU (A or B) as a GPIO.<br/>
 * Each 16 channels of both time processing units can be used as
 * general purpose in- or output.
 */
public class TPU_DIO implements ntbMpc555HB {

	/**
	 * Initialize an channel as a general purpose in- our output. 
	 * 
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param channel	TPU channel to initialize. Allowed values are 0..15.
	 * @param out		<code>true</code> initializes the channel as a digital output.
	 * 					<code>false</code> initializes the channel as a digital input.
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
	 * Returns the current state of the TTL signal at the given TPU channel.
	 * 
	 * @param channel	TPU pin to capture. Allowed numbers are 0..15.
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @return 			the current state of the TTL at the given pin. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public static boolean get(boolean tpuA, int channel) {
		if(tpuA){
			return (US.GET2(TPURAM0_A + 0x10 * channel + 2) & (1 << 15)) != 0; 
		}else{
			return (US.GET2(TPURAM0_B  + 0x10 * channel + 2) & (1 << 15)) != 0; 
		}
	}

	/**
	 * Set the TTL signal at the given pin.
	 * 
	 * @param channel	TPU pin to set. Allowed numbers are 0..15.
	 * @param tpuA		<code>true</code>: use TPU-A,
	 * 					<code>false</code>: use TPU-B.
	 * @param val		Value to set. <i>true</i> means logic 1 and <i>false</i> logic 0.
	 */
	public static void set(boolean tpuA, int channel, boolean val) {
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