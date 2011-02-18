package ch.ntb.inf.deep.runtime.mpc555.driver;
import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.*;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class SCI1Plain implements ntbMpc555HB {
	
	public static SCI1OutputStream out;
	public static SCI1InputStream in;

	public static final byte NO_PARITY = 0, ODD_PARITY = 1, EVEN_PARITY = 2;

	// Driver states
	public static final int PORT_OPEN = 9, TX_EMPTY = 8, TX_COMPLETE = 7,
			RX_RDY = 6, RX_ACTIVE = 5;

	// Error states
	public static final int IDLE_LINE_DET = 4, OVERRUN_ERR = 3, NOISE_ERR = 2,
			FRAME_ERR = 1, PARITY_ERR = 0, LENGTH_NEG_ERR = -1,
			OFFSET_NEG_ERR = -2, NULL_POINTER_ERR = -3;

	static final byte TDRE = 8;	// Transmit Data Register Empty Flag in SC1SR 
	static final byte RDRF = 6;	// Receive Data Register Full Flag in SC1SR 
	
	public static void stop() {
		US.PUT2(SCC1R1, 0);	//  TE, RE = 0 
	}
	
	public static void start(int i, byte noParity, short s) {
		US.PUT2(SCC1R0,  130); 	// baud rate 
		US.PUT2(SCC1R1, 0x0C);	// no parity, 8 data bits, enable tx and rx 
	}
	
	public static void write(byte b) {	// blocking
		short status;
		do 
			status = US.GET2(SC1SR);
		while ((status & (1<<TDRE)) == 0);
		US.PUT2(SC1DR, b);
	}
		
	public static byte receive() {	// blocking
		short status;
		do 
			status = US.GET2(SC1SR);
		while ((status & (1<<RDRF)) == 0);
		short data = US.GET2(SC1DR);
		return (byte)data;
	}
	
	static {
		out = new SCI1OutputStream();
		in = new SCI1InputStream();
	}
}	
