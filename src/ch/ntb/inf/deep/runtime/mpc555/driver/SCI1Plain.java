package ch.ntb.inf.deep.runtime.mpc555.driver;
import ch.ntb.inf.deep.unsafe.*;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class SCI1Plain {

	static final int SCC1R0 = 0x305008;	// SCI1 control register 0 
	static final int SCC1R1 = 0x30500A; 	// SCI1 control register 1 
	static final int SC1SR = 0x30500C; 	// SCI1 status register 
	static final int SC1DR = 0x30500E; 	// SCI1 data register 
	static final byte TDRE = 8;	// Transmit Data Register Empty Flag in SC1SR 
	static final byte RDRF = 6;	// Receive Data Register Full Flag in SC1SR 
	
	public static void stop() {
		US.PUT2(SCC1R1, 0);	//  TE, RE = 0 
	}
	
	public static void start() {
		US.PUT2(SCC1R0,  130); 	// baud rate 
		US.PUT2(SCC1R1, 0x0C);	// no parity, 8 data bits, enable tx and rx 
	}
	
	public static void send(byte b) {	// blocking
		US.PUT2(SC1DR, b);
		short status;
		do 
			status = US.GET2(SC1SR);
		while ((status & (1<<TDRE)) == 0);
	}
		
	public static byte receive() {	// blocking
		short status;
		do 
			status = US.GET2(SC1SR);
		while ((status & (1<<RDRF)) == 0);
		short data = US.GET2(SC1DR);
		return (byte)data;
	}
	
	
}	
