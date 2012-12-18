/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package ch.ntb.inf.deep.runtime.mpc5200.driver;

import ch.ntb.inf.deep.runtime.mpc5200.IphyCoreMpc5200tiny;
import ch.ntb.inf.deep.unsafe.US;

public class DAC_MAX5500 implements IphyCoreMpc5200tiny{

	private static final int FIFO_LENGTH = 512;
	private static final int PSCBase = PSC6Base;

	/**
	 * <p>Initialize the <i>SPI</i> on PSC6.</p>
	 * <p>This method has to be called before using the SPI!<p>
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
