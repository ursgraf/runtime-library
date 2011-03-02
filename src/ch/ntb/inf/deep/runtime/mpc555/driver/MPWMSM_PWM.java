package ch.ntb.inf.deep.runtime.mpc555.driver;
import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Driver for the MPWM module for generating PWM signals
 */
public class MPWMSM_PWM implements ntbMpc555HB {

	static public final int TIME_BASE = 400;	// time base in ns
	
	/** 
	 * Initialize a pin of the MPWM sub module for generating PWM signals.
	 * The period and high time have to be a multiple of the TIME_BASE ({@value TIME_BASE}ns)!
	 * @param channel	Module channel to initialize. Allowed channels are 0..3 and 6..9 or 0..3 and 16..19.
	 * @param period	Period of the PWM signal. Have to be a multiple of the MPWMSM time base.
	 * @param highTime	High time of the PWM signal. Have to be a multiple of the MPWMSM time base.
	 */
	public static void init(int channel, int period, int highTime){
		if(channel >=6  && channel <= 9) channel += 10;
		US.PUT2(MPWMSM0SCR + channel * 8, 0x04FC);	// enable, prescaler = 4 -> 400ns 
		US.PUT2(MPWMSM0PERR + channel * 8, period);	// set period 
		US.PUT2(MPWMSM0PULR + channel * 8, highTime);	//set pulse width 
	}
	
	/** 
	 * Update the PWM signals at the given channel.
	 * The period and high time have to be a multiple of the TIME_BASE ({@value TIME_BASE}ns)!
	 * @param channel	Module channel to update. Allowed channels are 0..3 and 6..9 or 0..3 and 16..19.
	 * @param period	Period of the PWM signal. Have to be a multiple of the MPWMSM time base!
	 * @param highTime	High time of the PWM signal. Have to be a multiple of the MPWMSM time base!
	 */
	public static void update(int channel, int period, int highTime){
		if(channel >=6  && channel <= 9) channel += 10;
		US.PUT2(MPWMSM0SCR + channel * 8, 0x04FC);	// enable, prescaler = 4 -> 400ns 
		US.PUT2(MPWMSM0PERR + channel * 8, period);	// set period 
		US.PUT2(MPWMSM0PULR + channel * 8, highTime);	//set pulse width 
	}

	static{
		US.PUT2(MIOS1MCR, 0);		// enable supervisor access 
		US.PUT2(MCPSMSCR, 0x8004);	// prescaler = 4, clock for MIOS = system clock / 4 = 10MHz
	}
}