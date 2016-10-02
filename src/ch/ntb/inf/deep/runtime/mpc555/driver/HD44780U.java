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

package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.ppc32.Task;
import ch.ntb.inf.deep.unsafe.US;

/* Changes:
 * 06.05.2011	NTB/RM	driver ported to deep, merged with the CharLCD2x16 driver
 * 29.05.2008	NTB/SP	variable digits changed to global
 * 05.03.2008	NTB/SP	assigned to Java
 * 24.03.2005	NTB/ED	Emulationsanweisungen gelöscht
 */

/**
 * Driver for character display with 2 - 4 rows and 16
 * columns.<br>
 * Display controller: HD44780U<br>
 * <br>
 * 
 * Connected on the system bus of the mpc555.<br>
 * 
 * <pre>
 *  System bus:				Display:
 *  D0..7 (data lines)			D7..0(interchanged!)
 *  R/W'					R/W'
 *  CS2' (Chip Select 2)			E (use a Inverter between!)
 *  A31 (address lines)			RS (Data/Instruction)
 * </pre>
 * 
 * The chip select CS2' is for an 8-Bit port configured. <br>
 * <br>
 * 
 * Base setting: Display On, Cursor On, Blink On, Increment, no shift.<br>
 * <br>
 * 
 */

public class HD44780U extends Task{
	
	private static final int BufLen = 64;

	public boolean done;
	private int status;
	private int cursPos;
	private int adrCmd;
	private boolean lcdDone;
	private boolean lcdStatus;
	private static HD44780U disp;

	private int lcdMaxRows = 2;
	private final int lcdMaxColumns = 16;
	private short[] adrCntOfRow = new short[4];
	private static final int BASE = 0x2000000;
	private static final int BR2 = 0x2FC110;
	private static final int OR2 = 0x2FC114;
	private final boolean lcdIdle = false;
	private final boolean lcdWaiting = true;

	/* Command - Buffer */
	private int lcdCmdBuffLen, lcdCmdBuffOut;
	private short[] lcdCmdBuff = new short[BufLen];

	/**
	 * Sets the cursor on desired destination.
	 * 
	 * @param row
	 *            row: starting with row 0. 
	 * @param column
	 *            column: starting with 0.
	 */
	public void setCursor(int row, int column) {
		done = true;
		row = (row % lcdMaxRows); column = (column % lcdMaxColumns);
		cursPos = (row * lcdMaxColumns + column);
		putCmd((adrCntOfRow[row]+column));
	}

	/**
	 * Writes a character <code>ch</code> on the display at current cursor position.
	 * 
	 * @param ch
	 *            character to write.
	 */
	public void writeChar(char ch) {
		done = true;
		putCmd((0x100 + ch));
		cursPos++;
		if (cursPos % lcdMaxColumns == 0) {
			cursPos = (cursPos % (lcdMaxRows * lcdMaxColumns));
			setCursor((cursPos / lcdMaxColumns), cursPos);
		}
	}

	/**
	 * Writes a line feed.
	 */
	public void writeLn() {
		done = true;
		cursPos =((cursPos / lcdMaxColumns + 1) % lcdMaxRows) * lcdMaxColumns;
		setCursor((cursPos /  lcdMaxColumns), cursPos);
	}

	private static char[] digits = new char[12];
	
	/**
	 * Writes an integer value on the display.
	 * 
	 * @param i
	 *            value to write.
	 * @param fieldLen
	 *            number of characters which should be used to display the value. 
	 */
	public void writeInt(int i, int fieldLen) {
		
		int m, n;
		boolean neg;
		
		done = true;
		if (fieldLen > 64) {
			fieldLen = 64;
		}
		if (i == 0x80000000) {
			digits[0] = '8';
			digits[1] = '4';
			digits[2] = '6';
			digits[3] = '3';
			digits[4] = '8';
			digits[5] = '4';
			digits[6] = '7';
			digits[7] = '4';
			digits[8] = '1';
			digits[9] = '2';
			digits[10] = '-';
			n = 10;
		} else {
			n = -1;
			neg = false;
			if (i < 0) { neg = true; i = -i; }
			do {
				n++;
				digits[n] = (char)((i % 10)+'0');
				i = i / 10;
			} while (i > 0);
			if (neg) {
				n++; 
				digits[n] = '-';
			}
		}
		if (fieldLen <= n) {
			fieldLen = n + 1;
		}
		m = fieldLen - n - 1;
		while (m > 0) {
			writeChar(' ');
			m--;
		}
		
		while (n >= 0) {
			writeChar(digits[n]);
			n--;
		}
	}

	/**
	 * Clears the display.
	 */
	public void clearDisplay() {
		done = true;
		cursPos = 0;
		putCmd(1);
	}

	/**
	 * Manages the displays and the cursor functions.
	 * 
	 * @param displayOn
	 *            <code>true</code>: switch on the display.
	 * @param cursorOn
	 *            <code>true</code>: The position of the cursor are displayed with an underline
	 * @param blinkOn
	 *            <code>true</code>: Cursor is blinking
	 */
	public void onOff(boolean displayOn, boolean cursorOn,
			boolean blinkOn) {
			byte val = 8;
			if (displayOn) { val = (byte)(val + 4); }
			if (cursorOn) { val = (byte)(val + 2); }
			if (blinkOn) { val = (byte)(val + 1); }
			putCmd(val);
	}
	
	private void putCmd(int addrCmd) {
		if (lcdCmdBuffLen >= lcdCmdBuff.length) {
			done = false;
		} else {
			lcdCmdBuff[(lcdCmdBuffOut + lcdCmdBuffLen) % BufLen] = (short) addrCmd;
			lcdCmdBuffLen++;
			lcdDone = false;
		}
	}
	
	/**
	 *  <b>Do not call this method!</b>
	 */	
	public void action() {
		if ((lcdStatus == lcdIdle) && !lcdDone) {
			adrCmd = lcdCmdBuff[lcdCmdBuffOut];
			US.PUT1(BASE + adrCmd / 0x100, adrCmd);
			lcdCmdBuffOut = (short)((lcdCmdBuffOut + 1) % BufLen); 
			lcdCmdBuffLen--;
			lcdStatus = lcdWaiting;
		} else if (lcdStatus == lcdWaiting) {
			status = US.GET1(BASE);
			if (status >= 0) {
				lcdStatus = lcdIdle;
				if (lcdCmdBuffLen == 0) {
					lcdDone = true;
				}
			}
		}
	}
	
	/**
	 * Initialisation of the display.<br>
	 * 
	 * @param nofRows
	 *            number of rows present in the display: 2, 3 or 4.
	 */
	public void init(int nofRows) {
		if (nofRows < 2) nofRows = 2;
		else if (nofRows > 4) nofRows = 4;

		lcdMaxRows = nofRows;
		done = true;
		lcdDone = true;
		lcdStatus = lcdIdle;
		lcdCmdBuffOut = 0;
		lcdCmdBuffLen = 0;
		cursPos = 0;
		
		adrCntOfRow[0] = 0x80;
		adrCntOfRow[1] = 0xC0;
		adrCntOfRow[2] = 0x90;
		adrCntOfRow[3] = 0xD0;
		
		/* Chip Select */
		US.PUT4(BR2, 0x2000403);
		/* base address: 2000000H, 8 bit port, RW, internal transfer ack, no bursts */
		US.PUT4(OR2, 0x0FFFF8EB1);
		/* address mask: 0FFFF8CF1H für CS endet 1/4 clock früher, CS startet 1/2 clock nach Adresse,  15+2 wait states, Timing relaxed => 2 * wait states */
		
		/* init LCD */
		US.PUT1(BASE, 0x30);
		do { } while (US.BIT(BASE, 7));
		US.PUT1(BASE, 0x30);
		do { } while (US.BIT(BASE, 7));
		US.PUT1(BASE, 0x30);
		do { } while (US.BIT(BASE, 7));

		US.PUT1(BASE, 0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while (US.BIT(BASE, 7));

		US.PUT1(BASE, 0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while (US.BIT(BASE, 7));

		US.PUT1(BASE, 0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while (US.BIT(BASE, 7));

		US.PUT1(BASE, 0x1); 	/* clear display */
		do { } while (US.BIT(BASE, 7));

		US.PUT1(BASE, 0x0F); 	/* display on, cursor on, blink on */
		do { } while (US.BIT(BASE, 7));

		US.PUT1(BASE, 0x06); 	/* increment address, no shift */
		do { } while (US.BIT(BASE, 7));

		US.PUT1(BASE, 0x02); 	/* return home */
		do { } while (US.BIT(BASE, 7));

		Task.install(disp);
	}

	/**
	 * Returns an instance of <i>Display Driver HD44780U</i> 
	 * @return Instance of display driver HD44780U
	 */
	public static HD44780U getInstance() {
		if (disp == null) {
			disp = new HD44780U();
		}
		return disp;
	}
	
	private HD44780U() {}

}
