
package ch.ntb.inf.deep.runtime.mpc555.driver;
import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;


/**
 * @author NTB/UG
 * Driver for the digital analog converter DAC7614.
 * With this driver it is possible to drive to DAC7614.
 * PCS0 and PCS1 is used
 */
/*	changes:
	15.05.07 NTB/SP	porting from component pascal to java
*/
public class DAC7614 implements ntbMpc555HB{
	/**
	 * Initalise the QSPI Port and set the output values of the DAC's to zero.
	 */
	public static void init(){
		US.PUT2(SPCR1, 0x0); 	//disable QSPI 
		US.PUT1(PQSPAR, 0x01B); // use PCS0, PCS1, MOSI, MISO for QSPI 
		US.PUT1(DDRQS, 0x01E); 	//SCK, MOSI, PCS's outputs; MISO is input 
		US.PUT2(PORTQS, 0x0FF); 	//all Pins, in case QSPI disabled, are high 
		US.PUT2(SPCR0, 0x08314); // QSPI is master, 16 bits per transfer, inactive state of SCLK is high (CPOL=1), data changed on leading edge (CPHA=1), clock = 1 MHz 
		US.PUT2(SPCR2, 0x4700); 	// no interrupts, wraparound mode, NEWQP=0, ENDQP=7 
		
		for(int i=0; i<4; i++) US.PUT1(COMDRAM + i,0x6E); //disable chip select after transfer, use bits in SPCR0, use PCS0 
		for(int i=4; i<8; i++) US.PUT1(COMDRAM + i, 0x6D); 		//disable chip select after transfer, use bits in SPCR0, use PCS1 
		for(int i=0; i<8; i++) US.PUT2(TRANRAM + 2 * i, (i % 4) * 0x4000 + 2048);
		
		US.PUT2(SPCR1, 0x08010);	//enable QSPI, delay 13us after transfer
	}

	/**
	 * Write the output value <code>val</code> to the channel <code>chn<code>
	 * @param ch Channel
	 * @param val Value
	 * 
	 */
	public static void write(int ch, int val){
		US.PUT2(TRANRAM + 2 * ch, (ch % 4) * 0x4000 + val);
	}

}
