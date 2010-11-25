package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.unsafe.HWD;

/*changes:
 * 29.5.2008 NTB/SP variable digits changed to global
 * 05.03.08	NTB/SP	assigned to Java
 24.3.05,ED	Emulationsanweisungen gelöscht
 */
/**
 * Treiber für das alphanummerische Display-Steckmodul mit 4 Zeilen à 16
 * Zeichen.<br>
 * Display controller: HD44780U<br>
 * <br>
 * 
 * Anschluss am Systembus des mpc555:<br>
 * 
 * <pre>
 *  Systembus:					Display:
 *  D0..7	(Datenleitungen)			D7..0	(Vertauscht!)
 *  R/W'						R/W'
 *  CS2'	(Chip Select 2)				E	(Inverter dazwischen!)
 *  A31	(Adressleitung)				RS	(Data/Instruction)
 * </pre>
 * 
 * Das Chip Select CS2' wird für einen 8-Bit Port konfiguriert. <br>
 * <br>
 * 
 * Grundeinstellung: Display On, Cursor On, Blink On, Increment, no shift.<br>
 * <br>
 * 
 * Die Aufträge werden in den Puffer <code>buf</code> eingefügt und von dort von einem Task
 * zum Display gesendet. Der Task besitzt die Zustände "IDLE (I)" und "WAITING
 * (W)".<br>
 * Ist dieser Task im Ruhezustand "Idle" und befindet sich ein Auftrag im
 * Puffer, so wird dieser vom Task zum Display gesandt. Der Task geht dabei in
 * den Zustand "W" über. Er prüft durch Polling das Busy Flag im Statusregister,
 * bis dieses die Beendigung des Auftrags durch das Display signalisiert. Danach
 * geht der Task wieder in den Zustand "I".<br>
 * <br>
 * Für den Gebrauch dieses Treibers muss zuerst zwingend die Methode
 * <code>init()</code> aufgerufen werden.
 */

public class CharLCD extends Task{
	
	public static boolean done;

	private static int status;
	private static int cursPos;
	private static int adrCmd;
	private static boolean lcdDone;
	private static boolean lcdStatus;
	private static CharLCD transferTask;

	/* Command - Buffer */
	private static int lcdCmdBuffLen, lcdCmdBuffOut;
	private static short[] lcdCmdBuff = new short[64];


	private static final int BufLen = 64;
	private static final int lcdMaxRows = 4;
	private static final int lcdMaxColumns = 16;
	private static short[] adrCntOfRow = new short[4];
	private static final int BASE = 0x2000000;
	private static final int BR2 = 0x2FC110;
	private static final int OR2 = 0x2FC114;
	private static final boolean lcdIdle = false;
	private static final boolean lcdWaiting = true;


	/**
	 * Setzt den Cursor auf die entsprechende Position.
	 * 
	 * @param row
	 *            Zeile, auf welche der Cursor gesetzt werden soll (Bereich
	 *            0..3)
	 * @param column
	 *            Spalte, in welche der Cursor gesetzt werden soll.
	 */
	public static void setCursor(int row, int column) {
		done = true;
		row = (row % lcdMaxRows); column = (short)(column % lcdMaxColumns);
		cursPos = (row * lcdMaxColumns + column);
		PutCmd((adrCntOfRow[row]+column));
	}

	/**
	 * Schreibt das Zeichen <code>ch</code> auf das Display.
	 * 
	 * @param ch
	 *            Zeichen, welches geschrieben werden soll.
	 */
	/**
	 * Schreibt das Zeichen ch auf das Display
	 */
	public static void wrChar(char ch) {
		done = true;
		PutCmd((short)(0x100 + ch));
		cursPos++;
		if (cursPos % lcdMaxColumns == 0) {
			cursPos = (byte)(cursPos % (lcdMaxRows * lcdMaxColumns));
			setCursor((short)(cursPos / lcdMaxColumns), cursPos);
		}
	}

	/**
	 * Schreibt einen Linefeed.
	 */
	public static void wrLn() {
		done = true;
		cursPos =((cursPos / lcdMaxColumns + 1) % lcdMaxRows) * lcdMaxColumns;
		setCursor((cursPos /  lcdMaxColumns), cursPos);
	}

	/**
	 * Schreibt einen Integerwert auf das Display.
	 * 
	 * @param i
	 *            Integerwert, welcher ausgegeben werden soll.
	 * @param fieldLen
	 *            Anzahl Zeichen, welche für die Ausgabe der Zahl verwendet
	 *            werden sollen.
	 */
	/**
	 * Schreibt einen Integerwert auf das Display
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
	 * Löscht alle Zeichen auf dem Display.
	 */
	public static void clearDisplay() {
		done = true;
		cursPos = 0;
		PutCmd((short)1);
	}

	/**
	 * Verwalten des Displays und der Cursorfunktionen.
	 * 
	 * @param displayOn
	 *            <code>true</code>: schaltet das Display ein.
	 * @param cursorOn
	 *            <code>true</code>: Die Position des Cursors wird über einen
	 *            Unterstrich angezeigt.
	 * @param blinkOn
	 *            <code>true</code>: Die Position des Cursors wird blinkend
	 *            hevorgehoben.
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
	
	public void Do() {
		if ((lcdStatus == lcdIdle) && !lcdDone) {
			adrCmd = lcdCmdBuff[lcdCmdBuffOut];
			HWD.PUT1(BASE + adrCmd / 0x100, adrCmd);
			lcdCmdBuffOut = (short)((lcdCmdBuffOut + 1) % BufLen); 
			lcdCmdBuffLen--;
			lcdStatus = lcdWaiting;
		} else if (lcdStatus == lcdWaiting) {
			status = HWD.GET1(BASE);
			if (status >= 0) {
				lcdStatus = lcdIdle;
				if (lcdCmdBuffLen == 0) {
					lcdDone = true;
				}
			}
		}
	}

	/**
	 * Initialisiert das Display.<br>
	 * Diese Methode muss vor dem Gebrauch des Displays zwingend aufgerufen
	 * werden.
	 */
	public static void init() {
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
		HWD.PUT4(BR2, 0x2000403);
		/* base address: 2000000H, 8 bit port, RW, internal transfer ack, no bursts */

		HWD.PUT4(OR2, 0x0FFFF8EF1);
		/* address mask: 0FFFF8CF1H für CS endet 1/4 clock früher, CS startet 1/2 clock nach Adresse,  15+2 wait states, Timing relaxed => 2 * wait states */
		
		/* init LCD */
		/*Dreimaliges Initalisieren, da Display z.T. nicht korrekt funktioniert*/
		HWD.PUT1(BASE, 0x30);
		do { } while (HWD.BIT(BASE, 7));
		HWD.PUT1(BASE, 0x30);
		do { } while (HWD.BIT(BASE, 7));
		HWD.PUT1(BASE, 0x30);
		do { } while (HWD.BIT(BASE, 7));

		HWD.PUT1(BASE, 0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while (HWD.BIT(BASE, 7));
		HWD.PUT1(BASE, 0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while (HWD.BIT(BASE, 7));
		HWD.PUT1(BASE, 0x38); 	/* set 8 Bit, 2 lines, 5*7 dots */
		do { } while (HWD.BIT(BASE, 7));
		
		HWD.PUT1(BASE, 0x1); 	/* clear display */
		do { } while (HWD.BIT(BASE, 7));

		HWD.PUT1(BASE, 0x0F); 	/* display on, cursor on, blink on */
		do { } while (HWD.BIT(BASE, 7));

		HWD.PUT1(BASE, 0x06); 	/* increment address, no shift */
		do { } while (HWD.BIT(BASE, 7));

		HWD.PUT1(BASE, 0x02); 	/* return home */
		do { } while (HWD.BIT(BASE, 7));
		
		transferTask = new CharLCD(); Task.install(transferTask);
	}
}
