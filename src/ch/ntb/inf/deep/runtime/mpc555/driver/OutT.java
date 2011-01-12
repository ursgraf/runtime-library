package ch.ntb.inf.deep.runtime.mpc555.driver;


/*changes:
 25.6.07	NTB/TB 	add switchToSCI1 & switchToSCI2
 24.8.06	NTB/ED	void print(double val)
 11.7.06	NTB/SH	void print(double val), 
 void print(int val, int base, int minWidth, char fillCh, boolean showBase)
 28.3.05	NTB/ED	+printTab()
 25.6.04	NTB/ED	creation
 */
/**
 * Ausgaben (vom Microcontroller) über die serielle Schnittstelle (SCI1).<br>
 * Für die normale Benutzung muss <code>OutT</code> nicht mittels
 * <code>open()</code> und <code>close()</code> gestartet bzw. beendet werden.
 * Es kann direkt über die Ausgabemethoden geschrieben werden.
 */
public class OutT {

	public static final boolean hideBase = false, showBase = true;

	public static final byte DECIMAL = 10, HEXADECIMAL = 16, OCTAL = 8,
			BINARY = 2;

	private static boolean useSCI2 = false;
	private static final int MIN_INT = (1 << 31);
	private static final int MAX_INT = MIN_INT - 1;

	private static final char NUL = '\0', TAB = '\t', CR = '\r', LF = '\n',
			SPACE = ' ';

	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static char[] valDigits = new char[34];
	// private static int DOUBLE_PREC = 100000;
	private static final int DOUBLE_PREC = 100000;

	static final char[] chars = new char[32];

	private OutT() {
	}

	/**
	 * Wechselt die Ausgabe auf SCI2
	 */
	public static void switchToSCI2() {
		close();
		useSCI2 = true;
		open();
	}

	/**
	 * Wechselt die Ausgabe zurück auf SCI1
	 */
	public static void switchToSCI1() {
		close();
		useSCI2 = false;
		open();
	}

	/**
	 * Oeffnet und initialisiert die serielle Schnittstelle (SCI1/RS-232-C) mit
	 * 9600 Baud, 8 Datenbit, kein Paritätsbit, 1 Stop-Bit
	 */
	public static void open() {
		if (useSCI2) {
			SCI2Plain.start(9600, SCI2Plain.NO_PARITY, (short) 8);
		} else {
			SCI1Plain.start(9600, SCI1Plain.NO_PARITY, (short) 8);
		}
	}

	/**
	 * Schliesst die serielle Schnittstelle
	 */
	public static void close() {
		if (useSCI2) {
			SCI2Plain.stop();
		} else {
			SCI1Plain.stop();
		}
	}

	/**
	 * Gibt ein Zeichen über die serielle Schnittstelle aus.
	 * 
	 * @param ch
	 *            Zeichen, welches ausgegeben werden soll.
	 */
	public static void print(char ch) {
//		if (useSCI2) {
			SCI2Plain.write((byte) ch);
/*		} else {
			SCI1Plain.write((byte) ch);
		}*/
	}

	/**
	 * Gibt den boolschen Wert über die serielle Schnittstelle aus.
	 * 
	 * @param bool
	 *            Boolscher Wert, welcher ausgegeben werden soll.
	 */
/*	public static void print(boolean bool) {
		if (bool)
			print("true");
		else
			print("false");
	}*/

	/**
	 * Gibt einen Literal-String über die serielle Schnittstelle aus.
	 * 
	 * @param str
	 *            Zeichen, welches ausgegeben werden soll.
	 */
/*	public static void print(String str) {
		int len = str.length();
		int pos = 0;
		if (useSCI2) {
			while (pos < len) {
				SCI2Plain.write((byte) str.charAt(pos));
				pos++;
			}
		} else {
			while (pos < len) {
				SCI1Plain.write((byte) str.charAt(pos));
				pos++;
			}
		}
	}*/
	
	/**
	 * Gibt einen String über die serielle Schnittstelle aus. Der Wert
	 * <code>value</code> kann an beliebigen Position im String eingefügt
	 * werden.<br>
	 * <ul>
	 * <li>%d wird ersetzt durch den Wert <code>value</code> in der dezimalen
	 * Darstellung</li>
	 * <li>%o wird ersetzt durch den Wert <code>value</code> in der octalen
	 * Darstellung</li>
	 * <li>%h wird ersetzt durch den Wert <code>value</code> in der
	 * hexadezimalen Darstellng</li>
	 * </ul>
	 * 
	 * @param str
	 *            String der ausgegeben wird.
	 * @param value
	 *            Wert der im String eingefügt wird.
	 */
/*	public static void printf(String str, int value) {
		int strlen = str.length();
		for (int i = 0; i < strlen; i++) {
			char c = str.charAt(i);
			if ((c == '%') && ((i + 1) < strlen)) {
				c = str.charAt(i + 1);
				switch (c) {
				case 'd':
					i++;
					print(value, DECIMAL, 0, SPACE, false);
					break;
				case 'o':
					i++;
					print('0');
					print(value, OCTAL, 0, SPACE, false);
					break;
				case 'h':
					i++;
					print("0x");
					print(value, HEXADECIMAL, 0, SPACE, false);
					break;
				default:
					if (useSCI2)
						SCI2Plain.write((byte) '%');
					else
						SCI1Plain.write((byte) '%');
					break;
				}
			} else {
				if (useSCI2)
					SCI2Plain.write((byte) c);
				else
					SCI1Plain.write((byte) c);
			}
		}
	}*/
	
	/**
	 * Gibt einen String über die serielle Schnittstelle aus. Der Werte
	 * <code>values</code> können an beliebigen Position im String eingefügt
	 * werden. Die Reihenfolge der ausgegeben Werte entspricht der Reihenfolge
	 * der übergebenen Werte.<br>
	 * <ul>
	 * <li>%d wird ersetzt durch den Wert <code>value</code> in der dezimalen
	 * Darstellung</li>
	 * <li>%o wird ersetzt durch den Wert <code>value</code> in der octalen
	 * Darstellung</li>
	 * <li>%h wird ersetzt durch den Wert <code>value</code> in der
	 * hexadezimalen Darstellng</li>
	 * </ul>
	 * 
	 * @param str
	 *            String der ausgegeben wird.
	 * @param value
	 *            Wert der im String eingefügt wird.
	 */
/*	public static void printf(String str, int... values) {
		int strlen = str.length();
		int vallen = values.length;
		int valctr = 0;
		for (int i = 0; i < strlen; i++) {
			char c = str.charAt(i);
			if ((c == '%') && ((i + 1) < strlen && valctr < vallen)) {
				c = str.charAt(i + 1);
				switch (c) {
				case 'd':
					i++;
					print(values[valctr], DECIMAL, 0, SPACE, false);
					valctr++;
					break;
				case 'o':
					print('0');
					print(values[valctr], OCTAL, 0, SPACE, false);
					i++;
					valctr++;
					break;
				case 'h':
					print("0x");
					print(values[valctr], HEXADECIMAL, 0, SPACE, false);
					i++;
					valctr++;
					break;
				default:
					if (useSCI2)
						SCI2Plain.write((byte) '%');
					else
						SCI1Plain.write((byte) '%');
					break;
				}
			} else {
				if (useSCI2)
					SCI2Plain.write((byte) c);
				else
					SCI1Plain.write((byte) c);
			}
		}
	}*/

	/**
	 * Gibt eine Integer-Zahl als Dezimalzahl über die serielle Schnittstelle
	 * aus.
	 * 
	 * @param val
	 *            Integer-Zahl, welche ausgegeben werden soll.
	 */
	public static void print(int val) {
		/*
		 * if (val < 0) { SCI1.write((byte)'-'); val = - val; if (val ==
		 * MIN_INT) { SCI1.write((byte)'2'); val = 147483648; } } if (val >= 10)
		 * print(val / 10); SCI1.write((byte)(val % 10 + '0'));
		 */
		print(val, DECIMAL, 0, SPACE, false);

	}

	/**
	 * Gibt eine Float-Zahl über die serielle Schnittstelle aus.<br>
	 * 
	 * @param val
	 *            Float-Zahl, welche ausgegeben werden soll.
	 */
/*	public static void print(float val) {
		int nofChars = Double.doubleToChars(val, 6, chars);
		int n = 0;
		while (n < nofChars) {
			OutT.print(chars[n]);
			n++;
		}
	}*/




	/**
	 * Gibt eine Double-Zahl über die serielle Schnittstelle aus.<br>
	 * 
	 * @param val
	 *            Double-Zahl, welche ausgegeben werden soll.
	 */
/*	public static void print(double val) {
		int nofChars = Double.doubleToChars(val, 15, chars);
		int n = 0;
		while (n < nofChars) {
			OutT.print(chars[n]);
			n++;
		}
	}*/

	private static final char esc = 0x1B, ecPrintForm = 0xF0, // print format
			// for double
			// and float
			// numbers
			ecFloat = 0xF1, // float
			ecDouble = 0xF2; // double

	/**
	 * Gibt die 8 bytes einer Double-Zahl über die serielle Schnittstelle aus.<br>
	 * Format:<br>
	 * byte 1: 0x1B ESC byte 2: 0xF2 double-Zahl byte 3, .. ,10: die acht bytes
	 * der Double-Zahl (MSB zuerst)
	 * 
	 * @param val
	 *            Wert, der ausgegeben werden soll.
	 */
/*	public static void printRaw(double val) {
		int nofChars = Double.doubleToRawBytes(val, chars);
		OutT.print(esc);
		OutT.print(ecDouble);
		for (int n = 0; n < nofChars; n++)
			OutT.print(chars[n]);
	}*/

	/**
	 * Gibt die 4 bytes einer Float-Zahl als ESC-Sequnz über die serielle
	 * Schnittstelle aus.<br>
	 * Format:<br>
	 * byte 1: 0x1B ESC byte 2: 0xF1 float-Zahl byte 3,4,5,6: die vier bytes der
	 * Float-Zahl (MSB zuerst)
	 * 
	 * @param val
	 *            Wert, der ausgegeben werden soll.
	 */
/*	public static void printRaw(float val) {
		int nofChars = Double.floatToRawBytes(val, chars);
		OutT.print(esc);
		OutT.print(ecFloat);
		for (int n = 0; n < nofChars; n++)
			OutT.print(chars[n]);
	}*/

	/**
	 * Sendet das Printformat für double- und float-Zahlen an den Empfänger.<br>
	 * Alle Werte die im Raw-Format gesendet werden, werden vom Empfänger ab
	 * diesem Zeitpunkt in diesem Fromat dargestellt.
	 * 
	 * @param precision
	 *            precision denotes the number of valid decimal places (usually
	 *            7 for short reals and 16 for reals).
	 * 
	 * @param minW
	 *            denotes the minimal length in characters. If necessary,
	 *            preceding <code>fillCh</code> will be inserted. Numbers are
	 *            always rounded to the last valid and visible digit.
	 * 
	 * @param expW
	 *            expW > 0: exponential format (scientific) with at least expW
	 *            digits in the exponent.<br>
	 *            expW = 0: fixpoint or floatingpoint format, depending on x.
	 *            expW < 0: fixpoint format with -expW digits after the decimal
	 *            point.
	 * 
	 * @param fillChar
	 *            If necessary, preceding <code>fillCh</code> will be inserted.
	 */
/*	public static void setRealPrintFormat(int precision, int minW, int expW,
			char fillChar) {
		OutT.print(esc);
		OutT.print(ecPrintForm);
		OutT.print((char) precision);
		OutT.print((char) minW);
		OutT.print((char) expW);
		OutT.print(fillChar);
	}

	public static void setRealDefaultFormat() {
		setRealPrintFormat(16, 0, 0, ' ');
	}*/

	/**
	 * Gibt einen Zeilenumbruch (carriage return '\r' , line feed '\n´) über die
	 * serielle Schnittstelle aus
	 */
	public static void println() {
		if (useSCI2) {
			SCI2Plain.write((byte) CR);
		} else {
			SCI1Plain.write((byte) CR); // SCI1.write((byte)lf);
		}
	}

	/**
	 * Gibt ein Zeichen über die serielle Schnittstelle aus.<br>
	 * Die Zeile wird mit einem Zeilenumbruch abgeschlossen.
	 * 
	 * @param ch
	 *            Zeichen, welches ausgegeben werden soll.
	 */
	public static void println(char ch) {
		print(ch);
		println();
	}

	/**
	 * Gibt den boolschen Wert über die serielle Schnittstelle aus.<br>
	 * Die Zeile wird mit einem Zeilenumbruch abgeschlossen.
	 * 
	 * @param bool
	 *            Boolscher Wert, welcher ausgegeben werden soll.
	 */
/*	public static void println(boolean bool) {
		print(bool);
		println();
	}*/

	/**
	 * Gibt einen Literal-String über die serielle Schnittstelle aus.<br>
	 * Die Zeile wird mit einem Zeilenumbruch abgeschlossen.
	 * 
	 * @param str
	 *            Zeichen, welches ausgegeben werden soll.
	 */
/*	public static void println(String str) {
		print(str);
		println();
	}*/
	
	/**
	 * Gibt einen String über die serielle Schnittstelle aus. Der Wert
	 * <code>value</code> kann an beliebigen Position im String eingefügt
	 * werden.<br>
	 * Die Zeile wird mit einem Zeilenumbruch abgeschlossen.
	 * <ul>
	 * <li>%d wird ersetzt durch den Wert <code>value</code> in der dezimalen
	 * Darstellung</li>
	 * <li>%o wird ersetzt durch den Wert <code>value</code> in der octalen
	 * Darstellung</li>
	 * <li>%h wird ersetzt durch den Wert <code>value</code> in der
	 * hexadezimalen Darstellng</li>
	 * </ul>
	 * 
	 * @param str
	 *            String der ausgegeben wird.
	 * @param value
	 *            Wert der im String eingefügt wird.
	 */
/*	public static void printfln(String str, int value) {
		printf(str, value);
		println();
	}*/



	/**
	 * Gibt einen String über die serielle Schnittstelle aus. Der Werte
	 * <code>values</code> können an beliebigen Position im String eingefügt
	 * werden. Die Reihenfolge der ausgegeben Werte entspricht der Reihenfolge
	 * der übergebenen Werte.<br>
	 * <ul>
	 * <li>%d wird ersetzt durch den Wert <code>value</code> in der dezimalen
	 * Darstellung</li>
	 * <li>%o wird ersetzt durch den Wert <code>value</code> in der octalen
	 * Darstellung</li>
	 * <li>%h wird ersetzt durch den Wert <code>value</code> in der
	 * hexadezimalen Darstellng</li>
	 * </ul>
	 * Die Zeile wird mit einem Zeilenumbruch abgeschlossen.
	 * 
	 * @param str
	 *            String der ausgegeben wird.
	 * @param value
	 *            Wert der im String eingefügt wird.
	 */
/*	public static void printfln(String str, int... values) {
		printf(str, values);
		println();
	}*/


	/**
	 * Gibt eine Integer-Zahl als Dezimalzahl über die serielle Schnittstelle
	 * aus.<br>
	 * Die Zeile wird mit einem Zeilenumbruch abgeschlossen.
	 * 
	 * @param val
	 *            Integer-Zahl, welche ausgegeben werden soll.
	 */
	public static void println(int val) {
		print(val);
		println();
	}

	/**
	 * Gibt eine Float-Zahl über die serielle Schnittstelle aus.<br>
	 * Die Zeile wird mit einem Zeilenumbruch abgeschlossen.
	 * 
	 * @param val
	 *            Float-Zahl, welche ausgegeben werden soll.
	 */
/*	public static void println(float val) {
		print(val);
		println();
	}*/

	/**
	 * Gibt eine Double-Zahl über die serielle Schnittstelle aus.<br>
	 * Die Zeile wird mit einem Zeilenumbruch abgeschlossen.
	 * 
	 * @param val
	 *            Double-Zahl, welche ausgegeben werden soll.
	 */
/*	public static void println(double val) {
		print(val);
		println();
	}*/

	/**
	 * Gibt ein Tabulator-Zeichen ('\t') über die serielle Schnittstelle aus
	 */
	public static void printTab() {
		if (useSCI2) {
			SCI2Plain.write((byte) TAB);
		} else {
			SCI1Plain.write((byte) TAB);
		}
	}

	/**
	 * Gibt eine Integer-Zahl als Hexadezimalzahl über die serielle
	 * Schnittstelle aus.
	 * 
	 * @param val
	 *            Integer-Zahl, welche ausgegeben werden soll.
	 */
	public static void printHex(int val) {
		/*
		 * if (val > 16 || val < 0) printHex(val >>> 4);
		 * SCI1.write((byte)DIGITS[val & 15]);
		 */
		print(0);
		print('x');
		print(val, HEXADECIMAL, 0, SPACE, false);

	}

	/**
	 * Gibt eine Integer-Zahl als Zahl mit der Basis <code>base</code> aus.
	 * 
	 * @param val
	 *            Integer-Zahl, welche ausgegeben werden soll.
	 * @param base
	 *            Basis der darzustellenden Zahl.
	 * @param minWidth
	 *            Minimale Breite der Zahl. Falls die Zahl kleiner ist, werden
	 *            die vorderen Stellen mit dem in <code>fillCh</code>
	 *            definierten Zeichen aufgefüllt.
	 * @param fillCh
	 *            Zeichen, welches die nicht benutzen Stellen in
	 *            <code>minWidth</code> füllt.
	 * @param showBase
	 *            Bool'scher Wert, über welchen definiert werden kann, ob die
	 *            Zahlenbasis am Schluss der dargestellten Zahl angezeigt werden
	 *            soll.
	 */
	public static void print(int val, int base, int minWidth, char fillCh,
			boolean showBase) {
		short maxNofDigits = 0, bitPerDigit = 0;
		int digMask = 0;
		if (minWidth > 64)
			minWidth = 40;
		short n = 0;
		boolean neg = false;
		if (base == DECIMAL) {
			maxNofDigits = 10;
			if (val == 0x80000000) {
				neg = true;
				if (showBase) {
					valDigits[0] = '0';
					valDigits[1] = '1';
					valDigits[2] = '%';
					valDigits[3] = '8';
					valDigits[4] = '4';
					valDigits[5] = '6';
					valDigits[6] = '3';
					valDigits[7] = '8';
					valDigits[8] = '4';
					valDigits[9] = '7';
					valDigits[10] = '4';
					valDigits[11] = '1';
					valDigits[12] = '2';
					n = 13;
				} else {
					valDigits[0] = '8';
					valDigits[1] = '4';
					valDigits[2] = '6';
					valDigits[3] = '3';
					valDigits[4] = '8';
					valDigits[5] = '4';
					valDigits[6] = '7';
					valDigits[7] = '4';
					valDigits[8] = '1';
					valDigits[9] = '2';
					n = 10;
				}
			} else {
//				print('T');
				if (showBase) {
					valDigits[0] = '0';
					valDigits[1] = '1';
					valDigits[2] = '%';
					n = 3;
					maxNofDigits = 13;
				}
				if (val < 0) {
					neg = true;
					val = -val;
				}
				print('E');	// NIX Päng
				do {
					valDigits[n] = DIGITS[val % 10];
					n++;
					val = val / 10;
				} while (val > 0); // UNTIL val <= 0;
//				print('Q');
			}
		} else {
			bitPerDigit = 4;
			digMask |= 0xf; // digMask = {0..3};
			maxNofDigits = 8;
			if (showBase) {
				valDigits[0] = '6';
				valDigits[1] = '1';
				valDigits[2] = '%';
				n = 3;
			}
			if (base == OCTAL) {
				bitPerDigit = 3;
				digMask = 0x7; // digMask = {0..2};
				maxNofDigits += 11 - 8; // INC(maxNofDigits, 11 - 8);
				if (showBase) {
					valDigits[0] = '8';
					valDigits[1] = '%';
					n = 2;
				}
			} else if (base == BINARY) {
				bitPerDigit = 1;
				digMask = 1;
				maxNofDigits += 32 - 8; // INC(maxNofDigits, 32 - 8);
				if (showBase) {
					valDigits[0] = '2';
					valDigits[1] = '%';
					n = 2;
				}
			}
			if (showBase) {
				maxNofDigits += n;
			}

			do {
				valDigits[n] = DIGITS[val & digMask]; // digits[ORD(BITS(val)
				// * digMask)];
				n++;
				val = val >>> bitPerDigit; // val = SYS.LSH(val, -
				// bitPerDigit)
			} while (val > 0); // UNTIL val <= 0;
		}

		if (minWidth <= maxNofDigits) {
			maxNofDigits = (short) minWidth;
			if (neg) {
				maxNofDigits--; // DEC(maxNofDigits);
			}
		}
		if (fillCh == '0') {
			while (n < maxNofDigits) {
				valDigits[n] = '0';
				n++;
			}
		}
		if (neg) {
			valDigits[n] = '-';
			n++;
		}
		if (minWidth < n) {
			minWidth = n;
		}
		int m = n;
		while (m < minWidth) {
			print(SPACE);
			m++;
		}
		while (n > 0) {
			n--;
			print(valDigits[n]);
		}
	}

	static {
		open();
	}
}
