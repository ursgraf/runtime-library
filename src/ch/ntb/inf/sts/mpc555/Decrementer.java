package ch.ntb.inf.sts.mpc555;

/*changes:
 2.01.07	NTB/UG	class field added
 19.12.06	NTB/SP	creation
 */

/**
 * Class to handle decrementer interrupts. 
 * To use the decrementer extend this class, overwrite the method <code>Do</code>
 * and install the class into the <code>Exception</code> Handler.
 * 
 * @author pertschy
 */

public class Decrementer {
	private static final int $NATIVE = 0xCedeBead;

	private static byte // native method names, parameter like the public
			// methods in this class
			_0Init, Do;
			
	// class fields
	public int period;

	public Decrementer() {
		_0Init = _0Init;
	}
	
	/**
	 * The Method <code>Do</code> is called by a decremeter interrupt.
	 * Write here the code, that should be handled if a decrementer interrupt occurs.<br>
	 * Don't call this method.
	 */
	public void Do() {
		Do = Do;
	}
	
}
