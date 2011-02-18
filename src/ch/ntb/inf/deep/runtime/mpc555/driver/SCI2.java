package ch.ntb.inf.deep.runtime.mpc555.driver;
import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class SCI2 implements ntbMpc555HB {
	
	public static SCI2OutputStream out;
	public static SCI2InputStream in;

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
		US.PUT2(SCC2R1, 0);	//  TE, RE = 0 
	}
	
	public static void start(int i, short noParity, short s) {
		US.PUT2(SCC2R0,  130); 	// baud rate 
		US.PUT2(SCC2R1, 0x0C);	// no parity, 8 data bits, enable tx and rx 
	}
	
	public static void write(byte b) {	// blocking
		short status;
		do 
			status = US.GET2(SC2SR);
		while ((status & (1<<TDRE)) == 0);
		US.PUT2(SC2DR, b);
	}
		
	public static byte receive() {	// blocking
		short status;
		do 
			status = US.GET2(SC2SR);
		while ((status & (1<<RDRF)) == 0);
		short data = US.GET2(SC2DR);
		return (byte)data;
	}
	
	public static int availToRead() {
		return 1;
	}

	public static int read() {
		int rec = 0;
		
		for(int i = 0; i < 4; i++){
			rec = (rec << 8) | receive();
		}	
		return rec;
	}

	public static int read(byte[] b) {
		for(int i = 0; i < b.length; i++){
			b[i] =  receive();
		}	
		return b.length;
	}

	public static int read(byte[] b, int off, int len) {
		for(int i = 0; i < len; i++){
			b[off + i] =  receive();
		}	
		return len;
	}

	public static int availToWrite() {
		return 128;
	}

	public static int write(byte[] b) {
		for(int i = 0; i < b.length; i++){
			write(b[i]);
		}
		return b.length;
	}

	public static int write(byte[] b, int off, int len) {
		for(int i = 0; i < len; i++){
			write(b[off + i]);
		}
		return len;
	}
	
	static {
		out = new SCI2OutputStream();
		in = new SCI2InputStream();
	}
	
}	
