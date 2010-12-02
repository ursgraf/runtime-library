package ch.ntb.inf.deep.runtime.mpc555.driver;
import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Driver for the MPWM module.
 * Initialise and update the PWM-Signal generation as multible of the <code>TIME_BASE</code> ({@value 
 * TIME_BASE}us) with <code>initPWM</code>.<br>
 * The pins 6,7,8,9 has to be initialized with 16,17,18,19.
 *
 */
public class Mios {

	static public final int TIME_BASE = 400;	// time base in ns

	static final int MIOS1MCR = Kernel.USIU + 0x0A806; 
	static final int MCPSMSCR = Kernel.USIU + 0x0A816; 
	static final int MPWMSMPERR0 = Kernel.USIU + 0x0A000;
	static final int MPWMSMPULR0 = Kernel.USIU + 0x0A002;
	static final int MPWMSMSCR0 = Kernel.USIU + 0x0A006; 

	/**
  * Initialise and update the pwm signal as multible of the<code>TIME_BASE</code> ({@value TIME_BASE}us).<br>
  * The pins 6,7,8,9 has to be initialized with 16,17,18,19.
  * @param channel
  * @param period
  * @param highTime
  */
	public static void initPWM(int channel, int period, int highTime){
		US.PUT2(MPWMSMSCR0 + channel * 8, 0x04FC);	// enable, prescaler = 4 -> 400ns 
		US.PUT2(MPWMSMPERR0 + channel * 8, period);	// set period 
		US.PUT2(MPWMSMPULR0 + channel * 8, highTime);	//set pulse width 
	}

	static{
		US.PUT2(MIOS1MCR, 0);	//enable, supervisor access 
		US.PUT2(MCPSMSCR, 0x8004);	// prescaler = 4, clock for MIOS = system clock / 4 = 10MHz
	}
}