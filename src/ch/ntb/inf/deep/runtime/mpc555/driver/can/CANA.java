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

package ch.ntb.inf.deep.runtime.mpc555.driver.can;

import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

public class CANA implements IntbMpc555HB {
	// EPOS unterstützt nur Standard Frame Format (11Bit Itentifier)
	// Message buffer 0 für transmit
	// Message buffer 1 für receive
	
	static final int MsgBuf0 = 0x307100;
	static final int TCNMCR = 0x307080;
	static final int CANCTRL0 = 0x307086;
	static final int CANCTRL1 = 0x307087;
	static final int PRESDIV = 0x307088;
	static final int CANCTRL2 = 0x307089;
	static final int TIMER = 0x30708a;
	static final int IFLAG = 0x3070a4;
	static final int RX15MSKHI = 0x307098;

	static final int msgBufRxEmpty = 4;
	static final int msgBufTxOnce = 0xc;
	static final int txBufNo = 0;
	static final int rx1BufNo = 1;
	static final int rx2BufNo = 2;
	
	private static byte[] data = new byte[14];

	public static void setMsgBufTx(int len, int id, boolean rtr, byte data[]) {
		setMsgBuf(txBufNo, msgBufTxOnce, len, id, rtr, data);
	}
	
	public static void setMsgBufRx1(int len, int id, boolean rtr, byte data[]) {
		setMsgBuf(rx1BufNo, msgBufRxEmpty, len, id, rtr, data);
	}
	
	public static void setMsgBufRx2(int len, int id, boolean rtr, byte data[]) {
		setMsgBuf(rx2BufNo, msgBufRxEmpty, len, id, rtr, data);
	}
	
	public static void setMsgBuf(int bufNr, int code, int len, int id, boolean rtr, byte data[]) {
		US.PUT2(MsgBuf0 + bufNr * 0x10, 0);	// deactivate first
		if (rtr) US.PUT2(MsgBuf0 + bufNr * 0x10 + 2, (id<<5) + 0x10);	// remote frame
		else US.PUT2(MsgBuf0 + bufNr * 0x10 + 2, (id<<5));	// data frame
		for (int i = 0; i < len; i++) US.PUT1(MsgBuf0 + bufNr * 0x10 + 6 + i, data[i]);
		// clear flag by reading and then writing
		short flags = US.GET2(IFLAG);
		US.PUT2(IFLAG, flags & ~(1<<bufNr));
		US.PUT2(MsgBuf0 + bufNr * 0x10, (code<<4) + len);
	}

	public static void waitForTransferComplete(int bufNr) {
		int flags;
		do flags = US.GET2(IFLAG);
		while ((flags & (1 << bufNr)) == 0);	// wait for end of transfer
		US.PUT2(IFLAG, flags & ~(1<<bufNr));
	}

	public static byte[] getMsgBuf(int bufNr) {
		for (int i = 0; i < 14; i++) {
			data[i] = US.GET1(MsgBuf0 + bufNr * 0x10 + i);
		}
		US.GET2(TIMER);
		return data;
	}

	public static void setMsgInactive(int bufNr) {
		US.PUT2(MsgBuf0 + bufNr * 0x10, 0);
		// clear flag by reading and then writing
		short flags = US.GET2(IFLAG);
		US.PUT2(IFLAG, flags & ~(1<<bufNr));
	}

	public static void setAllMsgInactive() {
		for (int buf=0; buf<16; buf++) setMsgInactive(buf);
	}

	public static void setBusOn() {
		US.GET2(TIMER);	//read timer to release last buffer read
		US.PUT2(TCNMCR, US.GET2(TCNMCR) & ~(0x5000));	// clear HALT bit
	}

	public static void init() {
		US.PUT2(TCNMCR, 0x1200);	// force soft reset
		while ((US.GET2(TCNMCR) & 0x200) != 0);	// wait
		US.PUT1(CANCTRL0, 0);	// positive polarity on RX and TX
		US.PUT1(CANCTRL1, 0x96);	// 3 samples, lowest buf transmitted first, propsegment=6
		US.PUT1(PRESDIV, 1);	// S-clock = 20MHz
		US.PUT1(CANCTRL2, (3<<6) | (5<<3) | 5);	// bit timing

		setAllMsgInactive();	
		setBusOn();
	}
}
