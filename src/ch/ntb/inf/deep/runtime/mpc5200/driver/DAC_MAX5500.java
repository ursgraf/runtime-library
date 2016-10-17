/*
 * Copyright 2011 - 2013 NTB University of Applied Sciences in Technology
 * Buchs, Switzerland, http://www.ntb.ch/inf
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package ch.ntb.inf.deep.runtime.mpc5200.driver;

import ch.ntb.inf.deep.runtime.mpc5200.Impc5200;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Driver for  a <code>MAX5500</code> DAC connected to a PSC6 on the mpc5200.<br>
 * 
 * @author Urs Graf
 *
 */
public class DAC_MAX5500 implements Impc5200 {

	private static final int FIFO_LENGTH = 512;
	private static final int PSCBase = PSC6Base;

	/**
	 * <p>Initialize the <i>SPI</i> on PSC6.</p>
	 * <p>This method has to be called before using the SPI!</p>
	 */
	public static void init() {
		US.PUT1(PSCBase + PSCCR, 0xa); // disable Tx, Rx
		US.PUT1(PSCBase + PSCCR, 0x20); // reset receiver, clears fifo
		US.PUT1(PSCBase + PSCCR, 0x30); // reset transmitter, clears fifo
		US.PUT4(PSCBase + PSCSICR, 0x0280c000); // select SPI mode, master, 16 bit, msb first
		US.PUT4(CDMPSC6MCLKCR, 0x800f);	// Mclk = 33MHz
		US.PUT4(CDMCER, US.GET4(CDMCER) | 0x10);	// enable Mclk for PSC6
		US.PUT4(PSCBase + PSCCCR, 0x00030000); // DSCKL = 60ns, SCK = 8.25MHz
		US.PUT1(PSCBase + PSCCTUR, 0); // set DTL to 150ns
		US.PUT1(PSCBase + PSCCTLR, 2); 
		US.PUT1(PSCBase + PSCTFCNTL, 0x1); // no frames
		US.PUT1(PSCBase + PSCRFCNTL, 0x1); // no frames
		US.PUT4(GPSPCR, US.GET4(GPSPCR) | 0x00700000);	// use pins on PCS6 for SPI
		US.PUT1(PSCBase + PSCCR, 0x5); // enable Tx, Rx
	}
	
	/**
	 * Sends a value over the SPI to one of the four DAC-channels.
	 * A call of this method is not blocking! A call to this method 
	 * can be followed by another call to this method, as the data 
	 * is written to a hardware fifo.
	 * 
	 * @param ch
	 *            channel of the DAC (0, 1, 2, 3)
	 * @param val
	 *            value
	 */
	public static void send(int ch, short val) {
		US.GET2(PSCBase + PSCRxBuf);	// empty the last entry from the fifo
		US.PUT2(PSCBase + PSCTxBuf, (ch << 14) | 0x3000 | (val & 0xfff)); 
	}

	/**
	 * Sets the digital output pin on the DAC.
	 * A call of this method is not blocking! A call to this method 
	 * can be followed by another call to this method, as the data 
	 * is written to a hardware fifo.
	 * 
	 * @param val
	 *            value
	 */
	public static void setUPO(boolean val) {
		US.GET2(PSCBase + PSCRxBuf);	// empty the last entry from the fifo
		if (val) US.PUT2(PSCBase + PSCTxBuf, 0x6000); else US.PUT2(PSCBase + PSCTxBuf, 0x2000); 
	}

	/**
	 * Returns the number of free bytes available in the transmit fifo.
	 * It is possible, to send the returned number of bytes in one
	 * nonblocking transfer.
	 * 
	 * @return the available free bytes in the transmit fifo.
	 */
	public static int availToWrite() {
		return FIFO_LENGTH - US.GET2(PSCBase + PSCTFNUM);
	}

}
