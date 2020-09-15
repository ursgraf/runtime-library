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

package ch.ntb.inf.deep.runtime.zynq7000.driver;

import ch.ntb.inf.deep.flink.core.FlinkDefinitions;
import ch.ntb.inf.deep.flink.core.FlinkDevice;
import ch.ntb.inf.deep.flink.core.FlinkSubDevice;
import ch.ntb.inf.deep.flink.subdevices.FlinkGPIO;
import ch.ntb.inf.deep.runtime.arm32.Task;

/* Changes:
 * 28.01.2020	NTB/GRAU	uses flink gpio
 */

/**
 * Driver for character display with 2 - 4 rows and 16 columns.<br>
 * Display controller: HD44780U<br>
 * <br>
 * 
 * Connected to flink gpios of the Zynq7000.<br>
 * 
 * <pre>
 *  GPIO:     Display:
 *  gpio[0]   D0 (data line)
 *  gpio[1]   D1 (data line)
 *  gpio[2]   D2 (data line)
 *  gpio[3]   D3 (data line)
 *  gpio[4]   D4 (data line)
 *  gpio[5]   D5 (data line)
 *  gpio[6]   D6 (data line)
 *  gpio[7]   D7 (data line)
 *  gpio[8]   RS (Data/Instruction)
 *  gpio[9]   E (Enable)
 *  gpio[10]  R/W' (Read/Write)
 * </pre>
 * 
 * <br>
 * Base setting: Display On, Cursor On, Blink On, Increment, no shift.<br>
 * <br>
 */

public class HD44780U extends Task implements FlinkDefinitions {
	
	private static final int BufLen = 64;

	private int cursPos;
	private boolean lcdDone;
	private boolean lcdStatus;
	private static FlinkGPIO gpio;
	private FlinkSubDevice dev;
	private int valAddr;
	private static HD44780U disp;

	private int maxRows = 2;
	private final int maxColumns = 16;
	private short[] adrCntOfRow = new short[4];
	private final static int RS = 8;
	private final static int E = 9;
	private final static int RW = 10;
	private final boolean lcdIdle = false;
	private final boolean lcdWaiting = true;
	private int buffLen, buffOut;
	private short[] buff = new short[BufLen];

	/**
	 * Sets the cursor on desired destination.
	 * 
	 * @param row
	 *            row: starting with row 0. 
	 * @param column
	 *            column: starting with 0.
	 */
	public void setCursor(int row, int column) {
		row = (row % maxRows); column = (column % maxColumns);
		cursPos = (row * maxColumns + column);
		putBuff((adrCntOfRow[row] + column));
	}

	/**
	 * Writes a character <code>ch</code> on the display at current cursor position.
	 * 
	 * @param ch
	 *            character to write.
	 */
	public void writeChar(char ch) {
		putBuff((0x100 + ch));
		cursPos++;
		if (cursPos % maxColumns == 0) {
			cursPos = (cursPos % (maxRows * maxColumns));
			setCursor((cursPos / maxColumns), cursPos);
		}
	}

	/**
	 * Writes a line feed.
	 */
	public void writeLn() {
		cursPos =((cursPos / maxColumns + 1) % maxRows) * maxColumns;
		setCursor((cursPos /  maxColumns), cursPos);
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
		
		if (fieldLen > 64) fieldLen = 64;
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
		cursPos = 0;
		putBuff(1);
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
	public void onOff(boolean displayOn, boolean cursorOn, boolean blinkOn) {
		byte val = 8;
		if (displayOn) { val = (byte)(val + 4); }
		if (cursorOn) { val = (byte)(val + 2); }
		if (blinkOn) { val = (byte)(val + 1); }
		putBuff(val);
	}

	private void putBuff(int addrCmd) {
		if (buffLen < buff.length) {
			buff[(buffOut + buffLen) % BufLen] = (short) addrCmd;
			buffLen++;
			lcdDone = false;
		}
	}
	
	/**
	 *  <b>Do not call this method!</b>
	 */	
	public void action() {
		if ((lcdStatus == lcdIdle) && !lcdDone) {
			int adrCmd = buff[buffOut];
			if ((adrCmd & 0x100) == 0) writeCmd(adrCmd);
			else writeData(adrCmd);
			buffOut = (short)((buffOut + 1) % BufLen); 
			buffLen--;
			lcdStatus = lcdWaiting;
		} else if (lcdStatus == lcdWaiting) {
			if ((readStatus() & 0x80) == 0) {
				lcdStatus = lcdIdle;
				if (buffLen == 0) lcdDone = true;
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
		gpio = FlinkDevice.getGPIO();
		dev = gpio.dev;
		this.valAddr = ((dev.nofChannels-1) / REGISTER_WIDTH_BIT + 1) * REGISTER_WIDTH;

		gpio.setDir(RS, true);
		gpio.setDir(E, true);
		gpio.setDir(RW, true);
		gpio.setValue(RS, false);
		gpio.setValue(E, false);
		gpio.setValue(RW, false);

		if (nofRows < 2) nofRows = 2;
		else if (nofRows > 4) nofRows = 4;
		maxRows = nofRows;
		lcdDone = true;
		lcdStatus = lcdIdle;
		buffOut = 0;
		buffLen = 0;
		cursPos = 0;
		
		adrCntOfRow[0] = 0x80;
		adrCntOfRow[1] = 0xC0;
		adrCntOfRow[2] = 0x90;
		adrCntOfRow[3] = 0xD0;
		
		writeCmd(0x30);
		int start = Task.time();
		do { } while (Task.time() - start < 10);
		writeCmd(0x30);
		start = Task.time();
		do { } while (Task.time() - start < 10);
		writeCmd(0x30);
		do { } while ((readStatus() & 0x80) != 0);
		writeCmd(0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while ((readStatus() & 0x80) != 0);
		writeCmd(0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while ((readStatus() & 0x80) != 0);
		writeCmd(0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while ((readStatus() & 0x80) != 0);
		writeCmd(0x1); 	/* clear display */
		do { } while ((readStatus() & 0x80) != 0);
		writeCmd(0x0F); 	/* display on, cursor on, blink on */
		do { } while ((readStatus() & 0x80) != 0);
		writeCmd(0x06); 	/* increment address, no shift */
		do { } while ((readStatus() & 0x80) != 0);
		writeCmd(0x02); 	/* return home */
		do { } while ((readStatus() & 0x80) != 0);

		Task.install(disp);
	}

	private int readStatus() {
		gpio.setValue(RS, false);
		gpio.setValue(RW, true);
		gpio.setValue(E, true);
		int status = readBus();
		gpio.setValue(E, false);
		return status;
	}

	private void writeCmd(int cmd) {
		gpio.setValue(RS, false);
		gpio.setValue(RW, false);
		gpio.setValue(E, true);
		writeBus(cmd);
		gpio.setValue(E, false);
	}

	private void writeData(int data) {
		gpio.setValue(RS, true);
		gpio.setValue(E, true);
		gpio.setValue(RW, false);
		writeBus(data);
		gpio.setValue(E, false);
	}

	private int readBus() {
		int reg = dev.read(0);
		reg &= ~0xff;
		dev.write(0, reg);
		
		int status = 0;
		for (int i = 0; i < 8; i++) {
			if (gpio.getValue(i)) status |= 1 << i;
		}
		return status;
	}

	private void writeBus(int data) {
		int val = dev.read(0);
		val |= 0xff;
		dev.write(0, val);

		val = dev.read(valAddr);
		val &= ~0xff;
		val |= data & 0xff;
		dev.write(valAddr, val);
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
