package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.DAC7614;
import ch.ntb.inf.deep.runtime.mpc555.driver.QADC_AIN;
import ch.ntb.inf.deep.runtime.mpc555.driver.RTBoard;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Testprogramm für das MPC555 Regelungstechnikboard.<br>
 * 
 * 
 * @author NTB 08.04.2009<br>
 *         Simon Pertschy<br>
 * <strong>Änderungen</strong>
 * 3.5.2011 Roger Millischer: adapted for deep
 */
public class RTBoardTest extends Task {

	// Enable/Disable tests
	private static final boolean enableSpiTest = true; 
	private static final boolean enableShCiTest = true; //IO Short Circuit Test
	private static final boolean enableIoTest = true; 
	private static final boolean enableDacTest = true; 
	private static final boolean enableAdcTest = true;

	// Commands
	private static final byte breakCmd = 0x1B, contCmd = 0x0D;

	// Test variables
	private int actualTest = -1, state = 0;
	private static final int spiTest = 0, shCiTest = 1, ioTest = 2,
			dacTest = 3, adcTest = 4;
	private static final int sendMessage = 0, wait = 1, runTest = 2;

	// Led test variables
	private final static int ledPeriod = 500;
	private int ledActTime = 0, ledCtr = 0;

	// IO test variables
	private int ioCtr = 0;

	// DAC test variables;
	private int dacCtr = 0;
	private int voltage = -10;

	/**
	 * Testet die Leuchtdioden. <br>
	 * Die Periode des Lauflichts kann über die Konstante <code>ledPeriod</code>
	 * geändert werden.
	 */
	private void runningLight() {
		if (Task.time() > ledActTime) {
			ledActTime = Task.time() + ledPeriod;
			RTBoard.ledOut(ledCtr, false);
			ledCtr = (ledCtr + 1) % 4;
			RTBoard.ledOut(ledCtr, true);
		}
	}

	/**
	 * Initialisierung des SPI Ports (CS2) für ein 4 Bit Shiftregister.
	 */
	private void initSpiShiftReg() {
		US.PUT2(SPCR3, 0x01); // Halt SPI
		while ((US.GET1(SPSR) & 0x80) == 0); // Wait for SPIF flag
		US.PUT2(SPCR1, 0x0000); // disable SPI
		US.PUT1(PQSPAR, 0x23); // use PCS2, MOSI, MISO in SPI Mode
		US.PUT1(DDRQS, 0x26); // define PCS2, SCK, MOSI as outputs
		US.PUT1(PORTQS + 1, 0x00); // set default value to low
		US.PUT2(SPCR0, 0x80FF); // Master Mode, lowest baud rate
		US.PUT2(SPCR2, 0x00); // Disable interrupts and wraparound mode
		US.PUT1(COMDRAM, 0x0F); // Eight bits, PCS2 high

	}

	/**
	 * Schreibt ein <code>Byte</code> in das Shiftregister.<br>
	 * 
	 * @param data
	 *            Daten welche in das Shiftregister geschrieben werden.
	 * @return das empfangene <code>Byte</code>.
	 */
	private byte writeShiftReg(byte data) {
		// Clear status register
		US.GET1(SPSR);
		US.PUT1(SPSR, 0x0);
		US.PUT1(TRANRAM + 1, data); // Write data to transmit ram
		US.PUT2(SPCR1, 0x8000); // Enable SPI
		while ((US.GET1(SPSR) & 0x80) == 0); // Wait for SPIF flag
		return US.GET1(RECRAM + 1);
	}

	/**
	 * Testet den SPI Port (CS2) mit einem 4Bit Shift Register.
	 */
	private void spiTest() {
		initSpiShiftReg();
		System.out.print("\nSpi test:\t");
		for (int i = 0; i < 10; i++) {
			byte d = writeShiftReg((byte) 0x55);
			if (d != 0x5) {
				System.out.print(d);
				System.out.println(" Failed\n");
				actualTest = shCiTest;
				return;
			}
		}
		System.out.println("Success\n");
		actualTest = shCiTest;
	}

	/**
	 * Testet die IO's des RTBoards auf Kurzschlüsse.
	 */
	private void shCiTest() {
		switch (state) {
		case sendMessage:
			System.out.println("Short Circuit Test");
			System.out.println("Please remove all connections");
			System.out.println("Continue -> Enter, Break -> ESC");
			state = wait;
			break;
		case wait:
			int cmd = System.in.read();
			if (cmd == breakCmd) {
				System.out.println("Break");
				state = sendMessage;
				actualTest = ioTest;
				break;
			} else if (cmd == contCmd) {
				state = runTest;
			} else
				break;
		case runTest:
			boolean failed = false;
			for (int i = 0; i < 8; i++) {
				RTBoard.dioInit(i, true);
				for (int j = i + 1; j < 8; j++) {
					RTBoard.dioInit(j, false);
					boolean in = RTBoard.dioIn(j);
					RTBoard.dioOut(i, !in);
					if (RTBoard.dioIn(j) != in) {
						System.out.print("\tShort circuit detected ");
						System.out.print(i);
						System.out.print(" -> ");
						System.out.println(j);
						failed = true;
					}
				}
			}
			if (failed)
				System.out.println("Failed\n");
			else
				System.out.println("Success\n");
			state = sendMessage;
			actualTest = ioTest;
			break;
		default:
			state = sendMessage;
			actualTest = dacTest;
			break;
		}
	}

	/**
	 * Testet die IO's des RTBoards.
	 */
	private void ioTest() {
		switch (state) {
		case sendMessage:
			System.out.print("Connect IO ");
			System.out.print(ioCtr);
			System.out.print("to IO ");
			System.out.print(ioCtr + 4);
			System.out.println(" --> Enter");
			state = wait;
			break;
		case wait:
			int cmd = System.in.read();
			if (cmd == breakCmd) {
				System.out.println("Break");
				state = sendMessage;
				actualTest = dacTest;
				break;
			} else if (cmd == contCmd) {
				state = runTest;
			} else
				break;
		case runTest:
			RTBoard.dioInit(ioCtr, true);
			RTBoard.dioInit(ioCtr + 4, false);
			RTBoard.dioOut(ioCtr, false);
			if (RTBoard.dioIn(ioCtr + 4)) {
				System.out.println("Failed\n");
			} else {
				RTBoard.dioOut(ioCtr, true);
				if (!RTBoard.dioIn(ioCtr + 4)) {
					System.out.println("Failed2\n");
				} else {
					RTBoard.dioInit(ioCtr + 4, true);
					RTBoard.dioInit(ioCtr, false);
					RTBoard.dioOut(ioCtr + 4, false);
					if (RTBoard.dioIn(ioCtr)) {
						System.out.println("Failed3");
					} else {
						RTBoard.dioOut(ioCtr + 4, true);
						if (!RTBoard.dioIn(ioCtr)) {
							System.out.println("Failed4\n");
						} else {
							System.out.println("Success\n");
						}
					}
				}
			}

			if (ioCtr < 3) {
				ioCtr++;
			} else {
				ioCtr = 0;
				actualTest = dacTest;
			}
			state = sendMessage;
			break;
		default:
			state = sendMessage;
			actualTest = dacTest;
			break;
		}
	}

	/**
	 * Gibt auf den analog Ausgängen des RTBoards einen Sägezahn mit einer
	 * Frequenz von ca. 1/4Hz aus.
	 */
	private void dacTest() {
		switch (state) {
		case sendMessage:
			state = wait;
			DAC7614.init();
			dacCtr = 0;
			System.out.print("DAC set to ");
			System.out.print(voltage);
			System.out.println("V --> Enter");
			break;
		case wait:
			DAC7614.write(4, dacCtr);
			DAC7614.write(5, (dacCtr));
			DAC7614.write(6, (dacCtr));
			DAC7614.write(7, (dacCtr));
			if(System.in.read() == contCmd){
				dacCtr = (dacCtr + 1024) % 5119;
				if(dacCtr == 4096) dacCtr--;
				voltage += 5;
				if(dacCtr == 0){
					voltage = -10;
					state = runTest;
					System.out.println("DAC test, press ESC to break");
				}else{
					System.out.print("DAC set to ");
					System.out.print(voltage);
					System.out.println("V --> Enter");
				}
			}
			break;
		case runTest:
			DAC7614.write(4, dacCtr);
			DAC7614.write(5, (dacCtr + 1024) % 4096);
			DAC7614.write(6, (dacCtr + 2048) % 4096);
			DAC7614.write(7, (dacCtr + 3072) % 4096);
			dacCtr = (dacCtr + 1) % 4096;
			if (System.in.read() == breakCmd) {
				System.out.println("Break");
				state = sendMessage;
				actualTest = adcTest;
			}
			break;
		default:
			state = sendMessage;
			actualTest = adcTest;
			break;
		}
	}

	/**
	 * Liest die Werte der analog Eingängen ein und gibt sie auf den analogen Ausgänge aus.<br>
	 * <ul>
	 * <li>A-IN0 -> A-OUT0</li>
	 * <li>A-IN1 -> A-OUT1</li>
	 * <li>A-IN2 -> POWER OUT0</li>
	 * <li>A-IN3 -> POWER OUT1</li>
	 * </ul>
	 *  
	 */
	private void adcTest() {
		switch (state) {
		case sendMessage:
			System.out.println("ADC test, press ESC to break");
			state = runTest;
			DAC7614.init();
			break;
		case runTest:
			DAC7614.write(4, QADC_AIN.read(true, 52) * 4);
			DAC7614.write(5, QADC_AIN.read(true, 54) * 4);
			DAC7614.write(6, QADC_AIN.read(true, 56) * 4);
			DAC7614.write(7, QADC_AIN.read(true, 58) * 4);
			if (System.in.read() == breakCmd) {
				System.out.println("Break");
				state = sendMessage;
				actualTest = -1;
			}
			break;
		default:
			state = sendMessage;
			actualTest = -1;
			break;
		}
	}

	public void action() {
		runningLight();
		switch (actualTest) {
		case spiTest:
			if (enableSpiTest)
				spiTest();
			else
				actualTest = shCiTest;
			break;
		case shCiTest:
			if (enableShCiTest)
				shCiTest();
			else
				actualTest = ioTest;
			break;
		case ioTest:
			if (enableIoTest)
				ioTest();
			else
				actualTest = dacTest;
			break;
		case dacTest:
			if (enableDacTest)
				dacTest();
			else
				actualTest = adcTest;
			break;
		case adcTest:
			if (enableAdcTest)
				adcTest();
			else
				actualTest = -1;
			break;
		default:
			System.out.println("\n\n**** RTBoard testprogram *****");
			actualTest = spiTest;
			break;
		}

	}

	static {
		//init SCI2
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		//hook SCI2 on System.out and in
		System.out = new PrintStream(SCI2.out);
		System.in = SCI2.in;
		
		RTBoardTest task = new RTBoardTest();
		task.period = 1;
		Task.install(task);
	}
}
