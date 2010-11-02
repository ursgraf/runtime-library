package ch.ntb.inf.sts.mpc555;


/*changes:
	2.01.07	NTB/UG	some decrementer features changed
	19.12.06	NTB/SP	creation
*/

/** 
 * The <code>Excepiton</code> class allows to handle interrupts and to use the decrementer.
 * The interrupts are separatet in internal and external interrupts. <br>
 * To use interrupts extend the <code>Interrupt</code> class
 * and overwrite the method <code>do</code>. Then install this class using the method 
 * <code>installExternalIntProc</code> or respectively <code>installInternalIntProc</code>.<br>
 * To use the decrementer extend the <code>Decrementer</code> class
 * and overwrite the method <code>do</code>. Then install this class using the method 
 * <code>installDecrementer</code>. Please note that only
 * one <code>Decrementer</code> can be installed at the same time!
 * 
 * @author pertschy
 */

public class Exceptions {
	private static final int $NATIVE = 0xCedeBead;

	private static byte // native method names, parameter like the public
			// methods in this class
			_0Init, InstallExternalIntProc, InstallInternalIntProc, InstallDecrementer;

	private Exceptions(){
		_0Init=_0Init;
	}
	
	/**
	 * Installed an external interrupt. 
	 * 
	 * @param interrupt The method <code>Do</code> of the extended <code>Interrupt</code> class
	 * defines the source code that would be executed if an interrupt at the specific pin occurs.
	 * @param pin defines the external interrupt pin. Please note that a lower pin number 
	 * has a higher interrupt level
	 */
	public static void installExternalIntProc(Interrupt interrupt, int pin){
		InstallExternalIntProc=InstallExternalIntProc;
	}
	
	/**
	 * Installed an internal interrupt.
	 * 
	 * @param interrupt defines the internal interrupt source and through the method
	 * <code>Do</code> the source code that would be executed if an interrupt at the specific
	 * internal interrupt source occurs.
	 * @param level defines the interrupt priority. The lower the level, the higher the priority.
	 */
	public static void installInternalIntProc(Interrupt interrupt, int level){
		InstallInternalIntProc=InstallInternalIntProc;
	}
	
	/**
	 * Installe the decrementer
	 * 
	 * @param dec The method <code>Do</code> of the extended <code>Decrementer</code> class
	 * defines the source code that would be executed if an decrement interrupt occurs.
	 */
	public static void installDecrementer(Decrementer dec){
		InstallDecrementer=InstallDecrementer;
	}	
}
