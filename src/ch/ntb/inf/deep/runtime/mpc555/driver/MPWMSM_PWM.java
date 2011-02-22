package ch.ntb.inf.deep.runtime.mpc555.driver;
import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Driver for the MPWM module.
 * Initialise and update the PWM-Signal generation as multible of the <code>TIME_BASE</code> ({@value 
 * TIME_BASE}us) with <code>initPWM</code>.<br>
 * The pins 6,7,8,9 has to be initialized with 16,17,18,19.
 *
 */
public class MPWMSM_PWM implements ntbMpc555HB {

	static public final int TIME_BASE = 400;	// time base in ns

	/**
  * Initialise and update the pwm signal as multible of the<code>TIME_BASE</code> ({@value TIME_BASE}us).<br>
  * The pins 6,7,8,9 has to be initialized with 16,17,18,19.
  * @param channel
  * @param period
  * @param highTime
  */
	public static void initPWM(int channel, int period, int highTime){
		US.PUT2(MPWMSM0SCR + channel * 8, 0x04FC);	// enable, prescaler = 4 -> 400ns 
		US.PUT2(MPWMSM0PERR + channel * 8, period);	// set period 
		US.PUT2(MPWMSM0PULR + channel * 8, highTime);	//set pulse width 
	}

	static{
		US.PUT2(MIOS1MCR, 0);	//enable, supervisor access 
		US.PUT2(MCPSMSCR, 0x8004);	// prescaler = 4, clock for MIOS = system clock / 4 = 10MHz
	}
}