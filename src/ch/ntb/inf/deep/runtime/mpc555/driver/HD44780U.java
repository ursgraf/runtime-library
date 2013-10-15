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

import ch.ntb.inf.deep.runtime.mpc555.Task;
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
 * Connected on the system bus of the mpc555:<br>
 * 
 * <pre>
 *  System bus:				Display:
 *  D0..7 (data lines)			D7..0(interchanged!)
 *  R/W'					R/W'
 *  CS2' (Chip Select 2)			E (use a Inverter between!)
 *  A31 (address lines)			RS (Data/Instruction)
 * </pre>
 * 
 * The chip elect CS2' is for an 8-Bit port configured. <br>
 * <br>
 * 
 * Base setting: Display On, Cursor On, Blink On, Increment, no shift.<br>
 * <br>
 * 
 * Instructions are written in the buffer <code>buf</code> and therefrom sends a task it to the display. 
 * The task have the states "IDLE (I)" and "WAITING (W)".<br>
 * 
 * Is that task in the state "Idle" and an instruction is provided in the buffer, so sends the task this to the display. 
 * And the task goes in the state "W". It checks by polling the busy flag in the state register, if the display have finished
 * the instruction. After the task goes back in the state "I".<br>
 * <br>
 * To use this driver, the method <code>init(int nofRows)</code> must be called mandatory.
 * 
 */

public class HD44780U extends Task{
	
	public static boolean done;

	private static int status;
	private static int cursPos;
	private static int adrCmd;
	private static boolean lcdDone;
	private static boolean lcdStatus;
	private static HD44780U transferTask;

	/* Command - Buffer */
	private static int lcdCmdBuffLen, lcdCmdBuffOut;
	private static short[] lcdCmdBuff = new short[64];


	private static final int BufLen = 64;
	private static int lcdMaxRows = 2;
	private static final int lcdMaxColumns = 16;
	private static short[] adrCntOfRow = new short[4];
	private static final int BASE = 0x2000000;
	private static final int BR2 = 0x2FC110;
	private static final int OR2 = 0x2FC114;
	private static final boolean lcdIdle = false;
	private static final boolean lcdWaiting = true;


	/**
	 * Sets the cursor on desired destination.
	 * 
	 * @param row
	 *            on which the cursor is set to. 
	 * @param column
	 *            on which the cursor is set to.
	 */
	public static void setCursor(int row, int column) {
		done = true;
		row = (row % lcdMaxRows); column = (column % lcdMaxColumns);
		cursPos = (row * lcdMaxColumns + column);
		PutCmd((adrCntOfRow[row]+column));
	}

	/**
	 * Write a character <code>ch</code> on the display.
	 * 
	 * @param ch
	 *            character, which is to write.
	 */
	public static void wrChar(char ch) {
		done = true;
		PutCmd((0x100 + ch));
		cursPos++;
		if (cursPos % lcdMaxColumns == 0) {
			cursPos = (cursPos % (lcdMaxRows * lcdMaxColumns));
			setCursor((cursPos / lcdMaxColumns), cursPos);
		}
	}

	/**
	 * Writes a line feed.
	 */
	public static void wrLn() {
		done = true;
		cursPos =((cursPos / lcdMaxColumns + 1) % lcdMaxRows) * lcdMaxColumns;
		setCursor((cursPos /  lcdMaxColumns), cursPos);
	}

	/**
	 * Writes an integer value on the display.
	 * 
	 * @param i
	 *            value to write.
	 * @param fieldLen
	 *            number of characters which should be used to display the value. 
	 */
	private static char[] digits = new char[12];
	public static void wrInt(int i, int fieldLen) {
		
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
			wrChar(' ');
			m--;
		}
		
		while (n >= 0) {
			wrChar(digits[n]);
			n--;
		}
	}

	/**
	 * Clears the display.
	 */
	public static void clearDisplay() {
		done = true;
		cursPos = 0;
		PutCmd(1);
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
	public static void onOff(boolean displayOn, boolean cursorOn,
			boolean blinkOn) {
			byte val = 8;
			if (displayOn) { val = (byte)(val + 4); }
			if (cursorOn) { val = (byte)(val + 2); }
			if (blinkOn) { val = (byte)(val + 1); }
			PutCmd(val);
	}
	
	private static void PutCmd(int addrCmd) {
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
	 * temporary method for internal use only
	 */
	private static boolean BIT(int address, int bitNo){//TODO replace with US.BIT
		int value = US.GET1(address);
		return ((1 << bitNo) & value) > 0;
	}
	
	/**
	 * Initialisation of the display.<br>
	 * To use this driver, this method must be called mandatory.
	 * 
	 */
	public static void init(int nofRows) {
		//allowed nofRows 2..4
		if(nofRows < 2){
			nofRows = 2;
		}else if(nofRows > 4){
			nofRows = 4;
		}
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
		/*Dreimaliges Initalisieren, da Display z.T. nicht korrekt funktioniert*/
		US.PUT1(BASE, 0x30);
		do { } while (BIT(BASE, 7));

		US.PUT1(BASE, 0x30);
		do { } while (BIT(BASE, 7));

		US.PUT1(BASE, 0x30);
		do { } while (BIT(BASE, 7));

		
		US.PUT1(BASE, 0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while (BIT(BASE, 7));

		US.PUT1(BASE, 0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while (BIT(BASE, 7));

		US.PUT1(BASE, 0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while (BIT(BASE, 7));

		
		US.PUT1(BASE, 0x1); 	/* clear display */
		do { } while (BIT(BASE, 7));


		US.PUT1(BASE, 0x0F); 	/* display on, cursor on, blink on */
		do { } while (BIT(BASE, 7));


		US.PUT1(BASE, 0x06); 	/* increment address, no shift */
		do { } while (BIT(BASE, 7));


		US.PUT1(BASE, 0x02); 	/* return home */
		do { } while (BIT(BASE, 7));

		
		transferTask = new HD44780U(); Task.install(transferTask);
	}
}
