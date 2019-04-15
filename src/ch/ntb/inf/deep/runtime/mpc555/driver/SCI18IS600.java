import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.ppc32.Task;
import ch.ntb.inf.deep.unsafe.US;

public class SCI18IS600 {
	private static final int CMD_WR_REG = 0x20;
	private static final int CMD_RD_REG = 0x21;
	
	public static final int REG_I2C_STAT = 4;
	public static final int REG_GPIO_CFG = 0;
	public static final int REG_GPIO_VAL = 1;
	
	private enum I2CStates {IDLE, SPI_TRANS, SPI_RECV, I2C_TRANS};
	
	private I2CStates i2cstate;
	private Integer intBuffer = new Integer(0);
	
	private MPIOSM_DIO resetPin;
	
	/**
	 * create new SCI18SI600
	 * Note that <code>QSPI.init()</code> needs to be called beforehand
	 * @param resetPinNum
	 */
	public SCI18IS600( int resetPinNum)
	{
		resetPin = new MPIOSM_DIO(resetPinNum, true);
		// HW reset (blocking)
		resetPin.set(false);
		int ct = Task.time();
		ct+= 100; // shorten
		while(Task.time() < ct);
		resetPin.set(true);
		
		
		QSPI.appendTXRam(0x18); // configure spi
		QSPI.appendTXRam(0x42); // set MSB first
		QSPI.sendBlocking();
		
		// set default I2C address to 0
		QSPI.appendTXRam(CMD_WR_REG);
		QSPI.appendTXRam(5);
		QSPI.appendTXRam(0);
		QSPI.sendBlocking();
		
		// set GPIO to push-pull
		QSPI.appendTXRam(CMD_WR_REG);
		QSPI.appendTXRam(0);
		QSPI.appendTXRam(0xAA);
		QSPI.sendBlocking();
		
		// set I2C clock
		QSPI.appendTXRam(CMD_WR_REG);
		QSPI.appendTXRam(2);
		QSPI.appendTXRam(19);
		QSPI.sendBlocking();
		
		// set all GPIO low
		QSPI.appendTXRam(CMD_WR_REG);
		QSPI.appendTXRam(1);
		QSPI.appendTXRam(0);
		QSPI.sendBlocking();
		i2cstate = I2CStates.IDLE;
//		while(true);
	}
	
	/**
	 * write to SCI18IS600 register
	 * blocks until transfer is done
	 * @param regAddr register address
	 * @param value value to write
	 */
	public void writeRegBlocking(int regAddr, int value)
	{
		QSPI.appendTXRam(CMD_WR_REG);
		QSPI.appendTXRam(regAddr);
		QSPI.appendTXRam(value);
		QSPI.sendBlocking();
	}
	
	/**
	 * read SCI18IS600 register
	 * blocks until tranfser is done
	 * @param regAddr register address
	 * @return value in register
	 */
	public int readRegBlocking(int regAddr)
	{
		QSPI.appendTXRam(CMD_RD_REG);
		QSPI.appendTXRam(regAddr);
		QSPI.appendTXRam(0);
		QSPI.sendBlocking();
		return QSPI.readRxRam(3)[2];
	}
	
	/**
	 * read SCI18IS600
	 * non-blocking
	 * @param regAddr register address
	 * @param result buffer to return result in
	 * @return true if transfer is done, false if still in progress
	 */
	private boolean readReg(int regAddr, Integer result)
	{
		if (!QSPI.send())
		{
			QSPI.appendTXRam(CMD_RD_REG);
			QSPI.appendTXRam(regAddr);
			QSPI.appendTXRam(0);
			QSPI.send();
			result = QSPI.readRxRam(3)[2];
		}
		else
		{
			return true;
		}
		return QSPI.send();
	}
	
	/**
	 * write to I2C bus, then read
	 * first write all data in writeData to I2C bus, then read recBuffer.length bytes into readBuffer
	 * note that all values are transmitted as bytes
	 * blocks until transaction is done
	 * @param addr I2C slave address (7bits, without R/W bit)
	 * @param writeData data to write
	 * @param recBuffer buffer for receive data
	 */
	public void I2CWriteReadBlocking(int addr, int[] writeData, int[] recBuffer)
	{
		QSPI.appendTXRam(2); // read after write
		QSPI.appendTXRam(writeData.length); // number of bytes to write
		QSPI.appendTXRam(recBuffer.length); // number of bytes to read
		QSPI.appendTXRam(addr << 1); // slave addr (write)
		for (int cmd : writeData) {
			QSPI.appendTXRam(cmd);
		}
		QSPI.appendTXRam(addr << 1); // slave addr (read)
		QSPI.sendBlocking();
		
		//wait for transfer to finish
		while (I2CBusy());
		
		readBufferBlocking(recBuffer);
	}
	
	/**
	 * write to I2C bus, then read
	 * first write all data in writeData to I2C bus, then read recBuffer.length bytes into readBuffer
	 * note that all values are transmitted as bytes
	 * @param addr I2C slave address (7bits, without R/W bit)
	 * @param writeData data to write
	 * @param buffer buffer for data to be received in
	 * @return true if transfer is done, false if still in progress
	 */
	public boolean I2CWriteRead(int addr, int[] writeData, int[] buffer)
	{
		switch(i2cstate)
		{
		case IDLE:
			QSPI.appendTXRam(2); // read after write
			QSPI.appendTXRam(writeData.length); // number of bytes to write
			QSPI.appendTXRam(buffer.length); // number of bytes to read
			QSPI.appendTXRam((addr << 1)); // slave addr (write)
			for (int cmd : writeData) {
				QSPI.appendTXRam(cmd);
			}
			QSPI.appendTXRam(addr << 1); // slave addr (read)
			i2cstate = I2CStates.SPI_TRANS;
			break;
		case SPI_TRANS:
			if (QSPI.send())
			{
				i2cstate = I2CStates.I2C_TRANS;
			}
			break;
		case I2C_TRANS:
			if(readReg(REG_I2C_STAT, intBuffer))
			{
				switch(intBuffer & 0xF)
				{
				case 0:
					// start transaction to read buffer
					i2cstate = I2CStates.SPI_RECV;
					QSPI.appendTXRam(6);
					for (int i = 0; i < buffer.length; i++)
					{
						QSPI.appendTXRam(0);
					}
					QSPI.send();
					break;
				case 3:
					// i2c busy, transaction still in progress
					break;
				default:
					System.out.println("i2c error");
					i2cstate = I2CStates.IDLE;
				}
			}
			break;
		case SPI_RECV:
			if (QSPI.send())
			{
				i2cstate = I2CStates.IDLE;
				QSPI.readRxRam(1, buffer);
				return true;
			}
			break;
		}
		return false;
	}
	
	/**
	 * write data to I2C bus
	 * blocks until transfer is done
	 * @param addr I2C slave address (7bit, without R/W bit)
	 * @param data data to write
	 */
	public void I2CWriteBlocking(int addr, int[] data)
	{
		QSPI.appendTXRam(0); // write
		QSPI.appendTXRam(data.length);
		QSPI.appendTXRam(addr << 1);
		for ( int datum : data)
		{
			QSPI.appendTXRam(datum);
		}
		QSPI.sendBlocking();
		while (I2CBusy());
	}
	
	/**
	 * write data to I2C bus
	 * non-blocking
	 * @param addr I2C slave address (7bit, without R/W bit)
	 * @param data data to write
	 * @return true if transfer is done, false if still in progress
	 */
	public boolean I2CWrite(int addr, int[] data)
	{
		switch(i2cstate)
		{
		case IDLE:
			QSPI.appendTXRam(0); // write
			QSPI.appendTXRam(data.length);
			QSPI.appendTXRam(addr << 1);
			for ( int datum : data)
			{
				QSPI.appendTXRam(datum);
			}
			if (QSPI.send())
			{
				i2cstate = I2CStates.I2C_TRANS;
			}
			else
			{
				i2cstate = I2CStates.SPI_TRANS;
			}
			break;
		case SPI_TRANS:
			if (QSPI.send())
			{
				i2cstate = I2CStates.I2C_TRANS;
			}
			break;
		case I2C_TRANS:
			if(!readReg(REG_I2C_STAT, intBuffer))
			{
				if((intBuffer & 0xFF) != 0xF3)
				{
					i2cstate = I2CStates.IDLE;
					return true;
				}
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * read SCI18SI600 I2C receive buffer
	 * blocks until transfer is done
	 * @param buffer buffer to return data in
	 */
	public void readBufferBlocking(int[] buffer)
	{
		QSPI.appendTXRam(6);
		for (int i = 0; i < buffer.length; i++)
		{
			QSPI.appendTXRam(0);
		}
		QSPI.sendBlocking();
		QSPI.readRxRam(1, buffer);
	}
	
	/**
	 * check if I2C bus is idle
	 * blocking
	 * @return true if idle, false if busy or error
	 */
	public boolean I2COk()
	{
		return readRegBlocking(REG_I2C_STAT) == 0xF0;
	}
	
	/**
	 * check if I2C bus is busy
	 * blocking
	 * @return true if busy, false if idle or error
	 */
	public boolean I2CBusy()
	{
		return readRegBlocking(REG_I2C_STAT) == 0xF3;
	}
	
	/**
	 * print current I2C bus status to stdout
	 * blocking
	 */
	public void printI2CStatus()
	{
		System.out.print("I2C status: ");
		switch(readRegBlocking(REG_I2C_STAT) & 0xF)
		{
		case 0:
			System.out.println("ok");
			break;
		case 1:
			System.out.println("no ack from slave");
			break;
		case 3:
			System.out.println("busy");
			break;
		case 8:
			System.out.println("bus time out");
			break;
		case 9:
			System.out.println("invalid data count");
		}
	}
	
	/**
	 * print current state of the internal state machine
	 */
	public void printState()
	{
		switch(i2cstate)
		{
		case IDLE:
			System.out.println("idle");
			break;
		case SPI_TRANS:
			System.out.println("spi trans");
			break;
		case I2C_TRANS: 
			System.out.println("i2c trans");
			break;
		case SPI_RECV:
			System.out.println("spi rec");
			break;
		}
	}
}
