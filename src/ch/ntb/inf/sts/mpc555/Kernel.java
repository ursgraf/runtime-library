package ch.ntb.inf.sts.mpc555;

/*changes:
 * 18.05.06	NTB/HS	stub creation
 */
/**
 * Kernel des mpc555 Betriebssystems.<br>
 * Das unterliegende Module Kernel.otd enthält die Grundfunktionen des
 * Betriebssystems.
 */
public class Kernel {
	private static final int $NATIVE = 0xCedeBead;
	
	/**
	 * Internal memory base
	 */
	public static final int ISB = 0x0;
	
	/**
	 * Unified system interface unit
	 */
	public static final int USIU = ISB + 0x2FC000;
	
	/**
	 * U-BUS to IMB3 bus interface
	 */
	public static final int UIMB = USIU + 0x4000;
	
	/**
	 * UIMB configuration
	 */
	public static final int UMCR = UIMB + 0x7F80;
	
	/**
	 * Interrupt request pending
	 */
	public static final int UIPEND = UIMB + 0x7FA0;
	
	/**
	 * MPIOSM data
	 */
	public static final int MPIOSMDR = UIMB + 0x6100;
	
	
	/**
	 * MPIOSM data direction
	 */
	public static final int MPIOSMDDR = UIMB + 0x6102;
	
	/**
	 * base address of external rom
	 */
	public static final int CextRomBase = 0x1000000;

	private static byte // native method names, parameter list equals the
			// corresponding methods in this class
			_0Init, // Constructor value
			GetInterruptState,
			SetInterruptState,
			DisableInts,
			Time; // PROCEDURE Time* (): INTEGER;

	private Kernel() {
		_0Init = _0Init;
	}

	/**
	 * reads MSR into R30, make sure local copy is located in R30 
	*/
	public static void getInterruptState() {
		GetInterruptState = GetInterruptState;
	}
	
	/**
	 * writes R30 into MSR, make sure local copy is located in R30 
	*/
	public static void setInterruptState() {
		SetInterruptState = SetInterruptState;
	}
	
	/**
	 * clears EE-Bit in MSR
	*/
	public static void disableInts() {
		DisableInts = DisableInts;
	}
	
	/**
	 * Gibt die aktuelle Systemzeit in \u00b5s zurück.
	 * 
	 * @return Aktuelle Systemzeit.
	 */
	public static int time() {
		Time = Time;
		return 0;
	}

}