package ch.ntb.inf.sts.mpc555.driver;
import ch.ntb.inf.sts.internal.SYS;
//© NTB/UG 
/*changes:
*	21.07.07	NTB/SP	porting to java
*	08.02.06	NTB/HS	stub creation
*
*/
/**
 * Treiber für den AD-Wandler TLC549.<br>
 * Der AD-Wandler ist wie folgt anzuschliessen:<br>
 * MPC555 => TLC549:<br>
 * PCS0 => CS<br>
 * MISO => DATA OUT<br>
 * SCK => I/O CLOCK<br>
 * Für den Anschluss des AD-Wandlers wird die QSPI-Schnittstelle verwendet. Es
 * darf keine weitere extrene Peripherie angeschlossen werden, welche ebenfalls
 * die QSPI benötigt.
 */
public class TLC549 {



	/**
	 * Initialisiert die QSPI für den Betrieb des TLC549.
	 */
	public static void init() {
		SYS.PUT2(QSMCM.SPCR1, 0x0); 	// disable QSPI 
		SYS.PUT1(QSMCM.PQSPAR, 0xB); 	//use PCS0, MOSI, MISO for QSPI //
		SYS.PUT1(QSMCM.DDRQS, 0xE); 	//SCK, MOSI, PCS0 outputs; MISO is input
		SYS.PUT2(QSMCM.PORTQS, 0xFF); 	//all Pins, in case QSPI disabled, are high 
		SYS.PUT2(QSMCM.SPCR0, 0xA028); 	//QSPI is master, 8 bits per transfer, inactive state of 
															//SCLK is LOW (CPOL=0), data captured on leading edge (CPHA=0), clock = 0.5 MHz 
		SYS.PUT2(QSMCM.SPCR2, 0x4000); 	// no interrupts, wraparound mode, NEWQP=0, ENDQP=0
		SYS.PUT1(QSMCM.CR, 0x3E); 	//disable chip select after transfer, 8 bits,  DT=1, DSCK=1, use PCS0 
		SYS.PUT2(QSMCM.SPCR1, 0xB816);	// enable QSPI, 1.4usec delay from PCS to SCK,
															//wait 17 usec for conversion after transfer 
	}

	/**
	 * Liest den gewandelten Wert aus dem AD-Wandler.
	 * 
	 * @return Gewandelter Wert.
	 */
	public static short read() {
			return SYS.GET2(QSMCM.RR);
	}
	
	static{
		init();
	}
}