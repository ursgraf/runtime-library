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

import ch.ntb.inf.deep.runtime.mpc5200.phyCoreMpc5200tiny;
import ch.ntb.inf.deep.unsafe.US;

public class SPI_FQD implements phyCoreMpc5200tiny{
	
	private static final int FIFO_LENGTH = 512;
	private static final int PSCBase = PSC1Base;

	/**
	 * <p>Initialize the <i>SPI</i> on PSC1.</p>
	 * <p>This method has to be called before using the SPI!<p>
	 */
	public static void init() {
		US.PUT1(PSCBase + PSCCR, 0xa); // disable Tx, Rx
		US.PUT1(PSCBase + PSCCR, 0x20); // reset receiver, clears fifo
		US.PUT1(PSCBase + PSCCR, 0x30); // reset transmitter, clears fifo
		US.PUT4(PSCBase + PSCSICR, 0x0280c000); // select SPI mode, master, 16 bit, msb first
		US.PUT4(CDMPSC1MCLKCR, 0x800f);	// Mclk = 33MHz
		US.PUT4(CDMCER, US.GET4(CDMCER) | 0x20);	// enable Mclk for PSC1
		US.PUT4(PSCBase + PSCCCR, 0x00030000); // DSCKL = 60ns, SCK = 8.25MHz
		US.PUT1(PSCBase + PSCCTUR, 0); // set DTL to 150ns
		US.PUT1(PSCBase + PSCCTLR, 2); 
		US.PUT1(PSCBase + PSCTFCNTL, 0x1); // no frames
		US.PUT1(PSCBase + PSCRFCNTL, 0x1); // no frames
		US.PUT4(GPSPCR, US.GET4(GPSPCR) | 0x7);	// use pins on PCS1 for SPI
		US.PUT1(PSCBase + PSCCR, 0x5); // enable Tx, Rx	
	}
	
	/**
	 * Reads a given data word (16bit) from the receive buffer.
	 * A call of this method is blocking!
	 * 
	 * @return 
	 *         value from SPI
	 */
	public static short receive() {
		US.PUT2(PSCBase + PSCTxBuf, 0); // start transfer
		while ((US.GET2(PSCBase + PSCTFSTAT) & 1) == 0); // wait for all transfers to complete
		return US.GET2(PSCBase + PSCRxBuf);
	}

}
