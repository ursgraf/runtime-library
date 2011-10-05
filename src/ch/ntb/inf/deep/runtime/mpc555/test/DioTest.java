package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPWMSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;
import ch.ntb.inf.deep.runtime.mpc555.driver.TPU_DIO;

/* CHANGES:
 * 04.10.2011	NTB/MZ	MPWM test added
 * 26.09.2011	NTB/MZ	Created (based on HLC1395Demo)
 */

public class DioTest extends Task {
	private static final int print = 0, waitForInput = 1, inputTest = 2, outputTest = 3;
	private static final boolean IN = false, OUT = true;
	private static final boolean ON = true, OFF = false;
	private static final boolean TPUA = true, TPUB = false;
	private static final String HI = "HI";
	private static final String LO = "LO";
	private static final int periodTime = 250;
	
	
	private static int action = print;
	private static int timeToToggle = 0;
	private static int counter = 0;
	private static boolean ledStat = OFF;
	
	private static String getState(boolean b) {
		if(b) return HI;
		return LO;
	}
	
	private static void initializeIOs(boolean out) {
		// MPIOSM
		for(int i = 0; i < 16; i++) {
			MPIOSM_DIO.init(i, out);
		}
		
		// TPU-A
		for(int i = 0; i < 16; i++) {
			TPU_DIO.init(TPUA, i, out);
		}
		
		// TPU-B
		for(int i = 0; i < 16; i++) {
			TPU_DIO.init(TPUB, i, out);
		}
		
		// MPWMSM
		for(int i = 0; i < 10; i++) {
			MPWMSM_DIO.init(i, out);
		}
	}
	
	private static void toggleLEDs() {
		// MPIOSM
		for(int i = 0; i < 16; i++) {
			MPIOSM_DIO.set(i, !ledStat);
		}
		
		// TPU-A
		for(int i = 0; i < 16; i++) {
			TPU_DIO.set(TPUA, i, !ledStat);
		}
		
		// TPU-B
		for(int i = 0; i < 16; i++) {
			TPU_DIO.set(TPUB, i, !ledStat);
		}
		
		// MPWMSM
		for(int i = 0; i < 10; i++) {
			MPWMSM_DIO.set(i, !ledStat);
		}
		
		ledStat = !ledStat;
	}
	
	private static void readInputs() {
		// MPIOSM
		System.out.println("MPIOSM:");
		for(int i = 0; i < 16; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(MPIOSM_DIO.get(i)));
			System.out.print('\t');
		}
		System.out.println();
		System.out.println();
		
		// TPU-A
		System.out.println("TPU-A:");
		for(int i = 0; i < 16; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(TPU_DIO.get(TPUA, i)));
			System.out.print('\t');
		}
		System.out.println();
		System.out.println();
		
		// TPU-B
		System.out.println("TPU-B:");
		for(int i = 0; i < 16; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(TPU_DIO.get(TPUB, i)));
			System.out.print('\t');
		}
		System.out.println();
		System.out.println();
		
		// MPWMSM
		System.out.println("MPWMSM:");
		for(int i = 0; i < 10; i++) {
			System.out.print(i);
			System.out.print(": ");
			System.out.print(getState(MPWMSM_DIO.get(i)));
			System.out.print('\t');
		}
	}
	
	private static void inputTest() {
		readInputs();
		action = print;
	}
	
	private static void outputTest() {
		if(Task.time() > timeToToggle) {
			toggleLEDs();
			counter++;
			timeToToggle = Task.time() + periodTime;
		}
		if(counter > 10) {
			counter = 0;
			action = print;
		}
	}
	
	private DioTest() {}
	
	public void action() {
		switch (action) {
		case inputTest:
			inputTest();
			break;
		case outputTest:
			outputTest();
			break;
		case print:
			System.out.println("  Press one of the following keys to start a test:");
			System.out.println("    I for testing inputs");
			System.out.println("    O for testing outputs");
			System.out.println();
			action = waitForInput;
			break;
		case waitForInput:
			char c = (char)System.in.read();
			if(c == 'i') {
				System.out.println("  [I] Running input test...");
				initializeIOs(IN);
				action = inputTest;
			}
			else if(c == 'o') {
				System.out.println("  [O] Running output test...");
				initializeIOs(OUT);
				action = outputTest;
			}
			break;
		default:
			System.out.println("ERROR");
			action = print;
			
		}
	}
	
	static {
		// Initialize SCI1 and set stdout to SCI1
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.in = SCI2.in;
		
		System.out.println("DIO-Test");
		System.out.println();
		System.out.println("  Usage:");
		
		// Create and install task
		Task t = new DioTest();
		t.period = 100;
		Task.install(t);
	}
}
