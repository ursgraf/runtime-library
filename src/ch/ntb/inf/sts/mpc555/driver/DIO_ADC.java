package ch.ntb.inf.sts.mpc555.driver;

import ch.ntb.inf.sts.internal.SYS;
import ch.ntb.inf.sts.mpc555.Kernel;

/**
 * Driver to use the QADC_A and QADC_B module as digital I/O.<br>
 * Please prefer the <code>Mpiosm</code> module for I/O applications, because
 * it's not possible to use the same QADC Module for ADC and I/0 applications at the same time. <br>
 * To access the module use the <code>channel</code> numbers 0-15. Whereby  Port numbers PQA0-PQA7 correspondents
 * with the <code>channel</code> numbers 8-15.
 * Alternatively it's also possible to use the ADC channel numbers AN0-AN3 and AN48-AN49 for accessing
 * the ports.<br>
 * WARNING: Don't initialize the Ports PQB (PQB0 - PQB7 respectively AN0-AN3,AN48-AN51) as output, because
 * these are only input ports.
 * @see mpc555.Mpiosm
 */
public class DIO_ADC {

	private static final int UIMB = Kernel.UIMB;
	private static final int  PORTQA_A=UIMB+0x4806, DDRQA_A=UIMB+0x4808;
	private static final int  PORTQA_B=UIMB+0x4C06, DDRQA_B=UIMB+0x4C08;
	
	private static int getChannel(int channel){
		if(channel>15) channel -= 44;
		return channel;
	}

	/**
	 * Initialize a pin of the QADC_A respectively QADC_B port as I/O.<br>
	 * @param qadcA select module QADC_A (<code>true</code>) or QADC_B (<code>false</code>)
	 * @param channel select module pin 0-15 respectively 0-3 and 48-59
	 * @param out set I/O direction,<code>true</code> => output, <code>false</code> => input
	 * @see "The general description of this driver"
	 */
	public static void init(boolean qadcA, int channel, boolean out){
		short oldState;
		channel=getChannel(channel);
		if(qadcA){
			oldState=SYS.GET2(DDRQA_A);
			if(out) SYS.PUT2(DDRQA_A,oldState | 1 << channel);
			else SYS.PUT2(DDRQA_A,oldState & ~(1 << channel));
		}else{
			oldState=SYS.GET2(DDRQA_B);
			if(out) SYS.PUT2(DDRQA_B,oldState | 1 << channel);
			else SYS.PUT2(DDRQA_B,oldState & ~(1 << channel));
		}
	}
	
	/**
	 * Read the TTL-Signal value of the corresponding <code>channel</code>
	 * 
	 * @param qadcA select module QADC_A (<code>true</code>) or QADC_B (<code>false</code>)
	 * @param channel select module pin 0-15 respectively 0-3 and 48-59
	 * @return the TTL-Signal value of <code>channel</code>
	 */
	public static boolean in(boolean qadcA, int channel){
		channel=getChannel(channel);
		if(qadcA){
			return (SYS.GET2(PORTQA_A) & (1<< channel)) != 0;
		}else{
			return (SYS.GET2(PORTQA_B) & (1<< channel)) != 0;
		}
	}
	
	/**
	 * Set the TTL-Signal value <code>val</code> to the corresponding <code>channel</code>
	 *  
	 * @param qadcA select module QADC_A (<code>true</code>) or QADC_B (<code>false</code>)
	 * @param channel select module pin 0-15 respectively 0-3 and 48-59
	 * @param val the TTL-Signal value
	 */
	public static void out(boolean qadcA, int channel, boolean val){
		channel=getChannel(channel);
		if(qadcA){
			if(val) SYS.PUT2(PORTQA_A, SYS.GET2(PORTQA_A) | (1 << channel));
			else SYS.PUT2(PORTQA_A, SYS.GET2(PORTQA_A) & ~(1 << channel));
		}else{
			if(val) SYS.PUT2(PORTQA_B, SYS.GET2(PORTQA_B) | (1 << channel));
			else SYS.PUT2(PORTQA_B, SYS.GET2(PORTQA_B) & ~(1 << channel));
		}
	}

}