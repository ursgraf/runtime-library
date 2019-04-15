package ch.inf.deep.runtime.mpc555.driver;
import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.runtime.ppc32.Task;
import ch.ntb.inf.deep.unsafe.US;

public class VL53L0X extends Task implements IntbMpc555HB {
	private static final int INIT_TIME = 1500;
	private static final int RESTART_TIME = 1500;
	private static final int MAX_SENSORS = 4;
	
	private enum States { RESET, WAIT_SEND, WAIT_RESTART, WAIT_INIT, NORMAL};
	private boolean sending = false;
	private States state;
	private int initTime;
	
	private int numSensors;
	private short[] data = new short[4];	
	private short[] cmdData= new short[4];
	
	/**
	 * Initialise up to 4 VL53L0X time of flight sensors
	 * @param sensors number of sensors (1..4)
	 */
	public VL53L0X(int sensors)
	{
		// check if the number of sensors is sensible
		if (numSensors > MAX_SENSORS) {
			System.err.println("Error, no more than 4 VL53L0X sensors supported!");
			numSensors = -1;
			return;
		}
		// initialise hardware
		numSensors = sensors;
		this.period=50;
		state = States.RESET;
		initSPI();
		Task.install(this);
	}
	
	/**
	 * Task function handling the general state and operations of this driver
	 * Not to be called manually!
	 */
	public void action()
	{
		switch(state)
		{
		// normal operation, transmit (and receive) to update sensor values in RX Ram
		case NORMAL:
			zeroTXRam(4);
			send(4);
			break;
			
		// initiate reset of SPI->I2C microcontroller
		case RESET:
			// if not enough time has passed since the last reset, do nothing
			// (prevents reset loops)
			if (Task.time() < initTime + INIT_TIME)
			{
				break;
			}
			// put -1 into RX Ram to erase old values and avoid confusion
			// otherweise reading RX while in reset will result in normal looking values
			// and look like everything works normally
			int addr = RECRAM;
			for (int i = 0; i < 4; i++)
			{
				US.PUT2(addr++, -1);
				addr++; //2 bytes -> need to increment twice
			}
			// record current time to make sure we are not trying to reset again while resetting
			initTime = Task.time();
			// set end qeue pointer 
			US.PUT2(SPCR2, (3 << 8));
			initCmdRam(4);
			
			// command: reset, no args
			cmdData[0] = 1;
			cmdData[1] = 0;
			cmdData[2] = 0;
			cmdData[3] = 0;
			setTXRam(cmdData);
			send(4);
			reset();
			state = States.WAIT_SEND;
			break;
			
			// wait for SPI transfer to finish so we can clear RxRam
		case WAIT_SEND:
			// check if SPIF flag is set = transfer done
			if ((US.GET1(SPSR) & (1 << 7)) > 0)
			{
				zeroRXRam(4);
				zeroTXRam(4);
				initCmdRam(4);
				state = States.WAIT_RESTART;
			}
			break;
			// wait for SPI->I2C microcontroller to restart (reset)
		case WAIT_RESTART:
			// Adapter micro controller sends back 0xAAAA when sending the init command
			// 0xAAAA = -21846 (int16), comparing to 0xAAAA always returns false, even if RxRam is 0xAAAA
			if (readRxRam(1)[0] == -21846)
			{
				// send number of sensors
				initCmdRam(4);
				// command: 2 (init), n sensors
				cmdData[0] = 2;
				cmdData[1] = (short)numSensors;
				setTXRam(cmdData);
				send(4);				
				initTime = Task.time();
				state = States.WAIT_INIT;
			} 
			else {
				// if there was no answer, try again
				send(4);
			}
			break;
			
		case WAIT_INIT:
			if (Task.time() > initTime + INIT_TIME)
			{
				// micro controller & sensors should be fully operational at this point
				// prepare for normal operation
				zeroTXRam(4);
				// set end qeue pointer
				US.PUT2(SPCR2, (3 << 8));
				// enable SPI (again)
				US.PUT2(SPCR1, (1 << 15));
				
				state = States.NORMAL;
			}
			break;
		}
	}
	
	/**
	 * Read last measurement data
	 * @return Distances in mm, the returned array has as many elements as sensors were requested in the constructor
	 */
	public short[] read()
	{
		return readRxRam(numSensors);
	}
	
	/**
	 * reset the sensor micro controller and sensors
	 * Not: This takes some time.
	 * Using read() directly after will return incorrect measurements until reset is done.
	 */
	public void reset()
	{
		state = States.RESET;		
	}
	
	/**
	 * Check if driver and hardware are operating normally or in the process of resetting
	 * @return true when in normal operation, false while resetting
	 */
	public boolean resetDone()
	{
		return state == States.NORMAL;
	}
	
	private static void initSPI()
	{
		//disable SPI
		US.PUT2(SPCR1, 0);
		// set up pins: use MISO, MOSI, SCK and CS2
		US.PUT1(PQSPAR, (1 << 5) | 0b11);
		// set up pins: CS2, SCK, MOSI output (rest=MISO input)
		US.PUT1(DDRQS, (1 << 5) | (1 << 2) | (1 << 1));
		//set up pins: set initial output values 
		US.PUT2(PORTQS, 0x20);
		//Master, 16bit transfer, baud rate
		US.PUT2(SPCR0, (1 << 15) | (0x0 << 10) | (0x0 << 8) | 0xFF);
		// set delay CS -> SCK and delay after transfer
//		US.PUT2(SPCR1, (127 << 8) | 1); // has no effect?
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
	}
	
	/**
	 * set receive Ram to data
	 * @param data data as 16bit integers
	 */
	private void setTXRam(short[] data)
	{
		int addr = TRANRAM;
		for (short datum : data)
		{
			US.PUT2(addr++, (int)datum);
			addr++; //2 bytes per write -> need to increment twice
		}
	}
	
	/**
	 * zero transfer ram to 0
	 * @param numHalfWords number of 16bit words to zero
	 */
	private void zeroTXRam(int numHalfWords)
	{
		int addr = TRANRAM;
		for (int i = 0; i < numHalfWords; i++)
		{
			US.PUT2(addr++, 0);
			addr++; //2 bytes -> need to increment twice
		}
	}
	
	/**
	 * zero receive ram to 0
	 * @param numHalfWords number of 16bit words to zero
	 */
	private void zeroRXRam(int numHalfWords)
	{
		int addr = RECRAM;
		for (int i = 0; i < numHalfWords; i++)
		{
			US.PUT2(addr++, 0);
			addr++; //2 bytes -> need to increment twice
		}
	}
		
	/**
	 * read receive ram
	 * @param numHalfWords number of 16bit integers to read
	 * @return data as 16bit integer array
	 */
	private short[] readRxRam(int numHalfWords)
	{
		for (int i = 0; i < numHalfWords; i++)
		{
			data[i] = US.GET2(RECRAM+2*i);
		}
		return data;
	}
	
	/**
	 * send data in transfer ram and receive into receive ram
	 * SPI is full duplex meaning data is transmitted and received simultaneously.
	 * @param numHalfWords number of 16bit integers to send
	 */
	private void send(int numHalfWords)
	{
		// enable SPI (SPI starts operation) if transmission is not already in progress
		if (!sending)
		{
			
			US.PUT2(SPCR1, (1 << 15));
		}
	}

}
