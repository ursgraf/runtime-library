package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

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
public class DIO_ADC implements ntbMpc555HB{

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
			oldState=US.GET2(DDRQA_A);
			if(out) US.PUT2(DDRQA_A,oldState | 1 << channel);
			else US.PUT2(DDRQA_A,oldState & ~(1 << channel));
		}else{
			oldState=US.GET2(DDRQA_B);
			if(out) US.PUT2(DDRQA_B,oldState | 1 << channel);
			else US.PUT2(DDRQA_B,oldState & ~(1 << channel));
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
			return (US.GET2(PORTQA_A) & (1<< channel)) != 0;
		}else{
			return (US.GET2(PORTQA_B) & (1<< channel)) != 0;
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
			if(val) US.PUT2(PORTQA_A, US.GET2(PORTQA_A) | (1 << channel));
			else US.PUT2(PORTQA_A, US.GET2(PORTQA_A) & ~(1 << channel));
		}else{
			if(val) US.PUT2(PORTQA_B, US.GET2(PORTQA_B) | (1 << channel));
			else US.PUT2(PORTQA_B, US.GET2(PORTQA_B) & ~(1 << channel));
		}
	}

}