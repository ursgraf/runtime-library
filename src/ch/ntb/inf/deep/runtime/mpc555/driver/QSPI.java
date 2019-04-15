import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

public class QSPI implements IntbMpc555HB {
	private static int txPtr = 0;
	private static boolean sending = false;
	
	public static void init()
	{
		US.PUT2(SPCR1, 0x0000); // disable SPI	0x0000
		US.PUT1(PQSPAR, 0x23); 	// use PCS2, MOSI, MISO in SPI Mode 0x23
		US.PUT1(DDRQS, 0x26); 	// define PCS2, SCK, MOSI as outputs 0x26
		US.PUT2(PORTQS, 0x0020);// 0x00 set default value to low, except PCS2 //0x20 --> Converters CS = active low
		US.PUT2(SPCR0, 0xA320); // Master Mode, baud rate test, 8 bits sent/transfer 0x80FF		--> x3xx = clockpolarity inactive high
		US.PUT2(SPCR2, 0x0200); // Disable interrupts and wraparound mode 0x00 || enable wraparound 0x40
		US.PUT1(COMDRAM, 0x8B); // Eight bits, PCS2 low 0x0B 	// 0x8B = keep chip select after transfer
	}
	
	/**
	 * initialise the SPI command ram for n transfers
	 * @param numTransfers number of 16bit transfers
	 */
	private static void initCmdRam(int numTransfers)
	{
		// the last tansfer is added after the loop, so substract 1
		int addr = COMDRAM;
		if (numTransfers > 1)
		{
			numTransfers--; 
			for (int i = 0; i < numTransfers; i++)
			{
				US.PUT1(addr++, 0b11111011 );
			}
		}
		// no continue flag on last transfer
		US.PUT1(addr++, 0b11111011 );
		US.PUT1(addr++, 0b01111111 );
	}
	
	/**
	 * set receive Ram to data
	 * @param data data as 16bit integers
	 */
	public static void appendTXRam(int[] data)
	{
		for (int datum : data)
		{
			US.PUT2(TRANRAM+txPtr, datum);
			txPtr += 2; // 2 bytes per write -> need to increment twice
		}
		
	}
	
	/**
	 * append a value to TX Ram
	 * @param datum
	 */
	public static void appendTXRam(int datum)
	{
		US.PUT2(TRANRAM+txPtr, datum);
		txPtr += 2; // 2 bytes per write -> need to increment twice
	}
	
	/**
	 * read receive ram
	 * @param numHalfWords number of 16bit integers to read
	 * @return data as 16bit integer array
	 */
	public static int[] readRxRam(int numHalfWords)
	{
		int[] data = new int[numHalfWords];
		for (int i = 0; i < numHalfWords; i++)
		{
			data[i] = US.GET2(RECRAM+2*i);
		}
		return data;
	}
	
	/**
	 * read receive ram starting at offset (in half words)
	 * @param numHalfWords number of 16bit integers to read
	 * @return data as 16bit integer array
	 */
	public static void readRxRam(int offset, int[] buffer)
	{
		for (int i = 0; i < buffer.length; i++)
		{
			buffer[i] = US.GET2(RECRAM+2*(i+offset));
		}
	}
	
	/**
	 * read receive ram starting at offset (in half words)
	 * @param buffer buffer to read into, will read buffer.length halfWords
	 * @return data as 16bit integer array
	 */
	public static void readRxRam(int[] buffer, int offset)
	{
		for (int i = 0; i < buffer.length; i++)
		{
			buffer[i] = US.GET2(RECRAM+2*(i+offset));
		}
	}
	
	/**
	 * send data in transfer ram and receive into receive ram
	 * blocks until transfer is done
	 * SPI is full duplex meaning data is transmitted and received simultaneously.
	 * @param numHalfWords number of 16bit integers to send
	 */
	public static void sendBlocking()
	{
		while(!send());
	}
	
	/**
	 * initialise command ram and start spi transaction
	 * @return true if transaction spi done, false if busy
	 */
	public static boolean send()
	{
		// only start new transmission if there is something to transmit
		// send might be called multiple times for a single transaction to check for completion
		if (txPtr > 0)
		{
//			System.out.println("sending");
			initCmdRam(txPtr>>1);
			// write txPtr/2 to end queue pointer
			US.PUT2(SPCR2, (txPtr/2 -1) << 8);
			txPtr = 0;
			US.PUT2(SPCR1, (1 << 15));
		}
		if ((US.GET1(SPSR) & (1 << 15)) > 0)
		{
			US.PUT1(SPSR, 0);
			return true;
		}
		return false;
	}
}
