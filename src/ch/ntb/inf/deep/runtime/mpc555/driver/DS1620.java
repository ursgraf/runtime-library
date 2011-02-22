package ch.ntb.inf.deep.runtime.mpc555.driver;

/*
	12.3.08 NTB/UG created
*/

/**
	Driver for the temperature sensor DS1620
	connected to MPIOSM-pins
*/

public class DS1620 {
	byte rst, clk, dq;
	
	void outPattern (byte pat) {
		for (int i = 0; i < 8; i++) {
			MPIOSM_DIO.out(this.dq, (pat & (1 << i)) != 0);
//			for (int k = 0; k < 20; k++);
			MPIOSM_DIO.out(this.clk, false);
//			for (int k = 0; k < 20; k++);
			MPIOSM_DIO.out(this.clk, true);
//			for (int k = 0; k < 20; k++);
		}
	}
	
	/**
	 * start conversions, must be called once upon power-up
	 */
	public void startConvert () {
		MPIOSM_DIO.out(this.rst, true);
		this.outPattern((byte)0xee);
		MPIOSM_DIO.out(this.rst, false);
	}
			
	/**
	 * reads temperature, returns value in deg. centigrade times 2
	 */
	public short read () {
		MPIOSM_DIO.out(this.rst, true);
		this.outPattern((byte)0xaa);
		MPIOSM_DIO.init(this.dq, false);
		MPIOSM_DIO.out(this.clk, false);
		short data = 0;
		for (int i = 0; i < 9; i++) {
			if (MPIOSM_DIO.in(this.dq)) data |= 1 << i;
//			for (int k = 0; k < 10; k++);
			MPIOSM_DIO.out(this.clk, true);
//			for (int k = 0; k < 10; k++);
			MPIOSM_DIO.out(this.clk, false);
//			for (int k = 0; k < 10; k++);
		}
		MPIOSM_DIO.out(this.clk, true);
		MPIOSM_DIO.init(this.dq, true);
		MPIOSM_DIO.out(this.rst, false);
		return data;
	}

	/**
	 * configures the sensor for serial connection
	 * must be called only once, not for each power-cycle
	 */
	public void writeConfig () {
		MPIOSM_DIO.out(this.rst, true);
		this.outPattern((byte)0x0c);
		this.outPattern((byte)0x0a);
		MPIOSM_DIO.out(this.rst, false);
	}
	
	/**
	 * returns configuration data
	 */
	public byte readConfig () {
		MPIOSM_DIO.out(this.rst, true);
		this.outPattern((byte)0xac);
		MPIOSM_DIO.init(this.dq, false);
		MPIOSM_DIO.out(this.clk, false);
		byte data = 0;
		for (int i = 0; i < 8; i++) {
//			for (int k = 0; k < 20; k++);
			if (MPIOSM_DIO.in(this.dq)) data |= 1 << i;
//			for (int k = 0; k < 20; k++);
			MPIOSM_DIO.out(this.clk, true);
//			for (int k = 0; k < 20; k++);
			MPIOSM_DIO.out(this.clk, false);
		}
		MPIOSM_DIO.out(this.clk, true);
		MPIOSM_DIO.init(this.dq, true);
		MPIOSM_DIO.out(this.rst, false);
		return data;
	}
			
	/**
	 * creates new sensor
	 * @param rst
	 *            pin number (MPIOSM) for rst signal
	 * @param clk
	 *            pin number (MPIOSM) for clk signal
	 * @param dq
	 *            pin number (MPIOSM) for dq signal
	 */
	public DS1620 (byte rst, byte clk, byte dq) {
		this.rst = rst;
		this.clk = clk;
		this.dq = dq;
		MPIOSM_DIO.init(this.rst, true); MPIOSM_DIO.out(this.rst, false);
		MPIOSM_DIO.init(this.clk, true); MPIOSM_DIO.out(this.clk, true);
		MPIOSM_DIO.init(this.dq, true);
	}
}
			
			
