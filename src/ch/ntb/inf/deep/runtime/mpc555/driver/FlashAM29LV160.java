package ch.ntb.inf.deep.runtime.mpc555.driver;

// driver for AM29LV160 Flash, 4MB 

public class FlashAM29LV160 {
	private static final int $NATIVE = 0xCedeBead;

	private static byte // native method names, parameter like the public
			// methods in this class
			_0Init, EraseSector, ProgramInt, ProgramShort;

	private FlashAM29LV160() {
		_0Init = _0Init;
	}


	/**
	 * Löscht einen Sektor im Flash
	 * @param address Adresse des Sektors, der zu löschen ist
	 */
	public static void eraseSector(int address) {
		EraseSector = EraseSector;
	}

	/**
	 * Programmiert einen Integer an die Position address
	 * @param address Adresse 
	 * @param val Wert, der programmiert werden soll
	 */
	public static void programInt(int address, int val) { 
		ProgramInt = ProgramInt;
	}

	/**
	 * Programmiert einen Short an die Position address
	 * @param address Adresse 
	 * @param val Wert, der programmiert werden soll
	 */
	public static void programShort(int address, short val) { 
		ProgramShort = ProgramShort;
	}

}