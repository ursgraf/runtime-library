package ch.ntb.inf.deep.runtime.mpc555;

/*changes:
 19.12.06	NTB/SP	creation
 */

/**
 * Class to handle internal or external interrupts. 
 * To use interrupts extend this class, overwrite the method <code>Do</code>
 * and install the class into the <code>Exception</code> Handler.
 * To use internal interrupts the interrupt enable register address <code>enableRegAdr</code>,
 * the enable Bit <code>enBit</code>, the interrupt flag register address <code>flagRegAdr</code>
 * and the interrupt flag bit <code>flag</code> must be defined.
 * @author pertschy
 */

public class Interrupt {
	private static final int $NATIVE = 0xCedeBead;

	private static byte // native method names, parameter like the public
			// methods in this class
			_0Init, Do;

	// ---- read only class fields

	public int enableRegAdr;
	public int enBit;
	public int flagRegAdr;
	public int flag;
	private Interrupt next;

	public Interrupt() {
		_0Init = _0Init;
	}

	/**
	 * The Method <code>Do</code> is called by a specific interrupt.
	 * Write here the code, that should be handled if a interrupt occurs.<br>
	 * Don't call this method.
	 */
	public void Do() {
		Do = Do;
	}
	
}
