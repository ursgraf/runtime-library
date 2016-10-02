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

public class SPI_FQD implements Impc5200 {
	
//	private static final int FIFO_LENGTH = 512;
	private static final int PSCBase = PSC1Base;
	
	private static short fqdCount0;
	private static short fqdCount1;

	/**
	 * <p>Initialize the <i>SPI</i> on PSC1.</p>
	 * <p>This method has to be called before using the SPI!</p>
	 */
	public static void init() {
		US.PUT1(PSCBase + PSCCR, 0xa); // disable Tx, Rx
		US.PUT1(PSCBase + PSCCR, 0x20); // reset receiver, clears fifo
		US.PUT1(PSCBase + PSCCR, 0x30); // reset transmitter, clears fifo
		US.PUT4(PSCBase + PSCSICR, 0x0f80c000); // select SPI mode, master, 32 bit, msb first
		US.PUT4(CDMPSC1MCLKCR, 0x800f);	// Mclk = 33MHz
		US.PUT4(CDMCER, US.GET4(CDMCER) | 0x20);	// enable Mclk for PSC1
//		US.PUT4(PSCBase + PSCCCR, 0x00030000); // DSCKL = 60ns, SCK = 8.25MHz
        US.PUT4(PSCBase + PSCCCR, 0x000f0000); // DSCKL = 60ns, SCK = 2MHz
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
	 */
	public static void receive() {
		US.PUT4(PSCBase + PSCTxBuf, 0); // start transfer
//		while ((US.GET2(PSCBase + PSCTFSTAT) & 1) == 0); // wait for all transfers to complete
		while (US.GET2(PSCBase + PSCRFNUM) < 4);
		int fqdCount = US.GET4(PSCBase + PSCRxBuf);
		fqdCount0 = (short)(fqdCount >> 16);
		fqdCount1 = (short)fqdCount;
	}

	public static short getEncoder0(){
		return fqdCount0;
	}

	public static short getEncoder1(){
		return fqdCount1;
	}
}
