package ch.ntb.inf.sts.mpc555.driver;

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
			Mpiosm.out(this.dq, (pat & (1 << i)) != 0);
//			for (int k = 0; k < 20; k++);
			Mpiosm.out(this.clk, false);
//			for (int k = 0; k < 20; k++);
			Mpiosm.out(this.clk, true);
//			for (int k = 0; k < 20; k++);
		}
	}
	
	/**
	 * start conversions, must be called once upon power-up
	 */
	public void startConvert () {
		Mpiosm.out(this.rst, true);
		this.outPattern((byte)0xee);
		Mpiosm.out(this.rst, false);
	}
			
	/**
	 * reads temperature, returns value in deg. centigrade times 2
	 */
	public short read () {
		Mpiosm.out(this.rst, true);
		this.outPattern((byte)0xaa);
		Mpiosm.init(this.dq, false);
		Mpiosm.out(this.clk, false);
		short data = 0;
		for (int i = 0; i < 9; i++) {
			if (Mpiosm.in(this.dq)) data |= 1 << i;
//			for (int k = 0; k < 10; k++);
			Mpiosm.out(this.clk, true);
//			for (int k = 0; k < 10; k++);
			Mpiosm.out(this.clk, false);
//			for (int k = 0; k < 10; k++);
		}
		Mpiosm.out(this.clk, true);
		Mpiosm.init(this.dq, true);
		Mpiosm.out(this.rst, false);
		return data;
	}

	/**
	 * configures the sensor for serial connection
	 * must be called only once, not for each power-cycle
	 */
	public void writeConfig () {
		Mpiosm.out(this.rst, true);
		this.outPattern((byte)0x0c);
		this.outPattern((byte)0x0a);
		Mpiosm.out(this.rst, false);
	}
	
	/**
	 * returns configuration data
	 */
	public byte readConfig () {
		Mpiosm.out(this.rst, true);
		this.outPattern((byte)0xac);
		Mpiosm.init(this.dq, false);
		Mpiosm.out(this.clk, false);
		byte data = 0;
		for (int i = 0; i < 8; i++) {
//			for (int k = 0; k < 20; k++);
			if (Mpiosm.in(this.dq)) data |= 1 << i;
//			for (int k = 0; k < 20; k++);
			Mpiosm.out(this.clk, true);
//			for (int k = 0; k < 20; k++);
			Mpiosm.out(this.clk, false);
		}
		Mpiosm.out(this.clk, true);
		Mpiosm.init(this.dq, true);
		Mpiosm.out(this.rst, false);
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
		Mpiosm.init(this.rst, true); Mpiosm.out(this.rst, false);
		Mpiosm.init(this.clk, true); Mpiosm.out(this.clk, true);
		Mpiosm.init(this.dq, true);
	}
}
			
			
