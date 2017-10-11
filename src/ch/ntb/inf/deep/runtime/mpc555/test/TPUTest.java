package ch.ntb.inf.deep.runtime.mpc555.test;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.mpc555.driver.TPU_DIO;
import java.io.*;

public class TPUTest {

	/* Non-working pin */
	static boolean definitionNonWorkingPinTpuAB; // true: TPUA / false: TPUB
	static int definitionNonWorkingPinNo = -1; // Pin-No., initialized as -1.
	static boolean definitionNonWorkingPinInputOutput; // true: output / false: input
	static TPU_DIO nonWorkingPinObject;
	/* Working pin */
	static boolean definitionWorkingPinTpuAB; // true: TPUA / false: TPUB
	static int definitionWorkingPinNo = -1; // Pin-No., initialized as -1.
	static boolean definitionWorkingPinInputOutput; // true: output / false: input
	static TPU_DIO workingPinObject;

	/* Initialize serial output-interface "SCI1" */
	static {
		SCI sci1 = SCI.getInstance(SCI.pSCI1);
		sci1.start(9600, SCI.NO_PARITY, (short) 8);
		// 2) Use SCI1 for standard-output
		System.out = new PrintStream(sci1.out);
	}

	/* Initialize pin objects */
	static void initPin() {
		// Initialize non-working pin object
		nonWorkingPinObject = new TPU_DIO(definitionNonWorkingPinTpuAB, definitionNonWorkingPinNo,
				definitionNonWorkingPinInputOutput);
		if (definitionNonWorkingPinInputOutput == true) { // if set as an output
			setNonWorkingPinLow(); // set state to "LOW"
		}

		/* Initialize working pin object */
		workingPinObject = new TPU_DIO(definitionWorkingPinTpuAB, definitionWorkingPinNo,
				definitionWorkingPinInputOutput);
		if (definitionWorkingPinInputOutput == true) { // if set as an output
			setWorkingPinLow(); // set state to "LOW"
		}
	}

	/* Toggle non-working pin object */
	static void toggleNonWorkingPin() {
		nonWorkingPinObject.set(!nonWorkingPinObject.get());
	}

	/* Toggle working pin object */
	static void toggleWorkingPin() {
		workingPinObject.set(!workingPinObject.get());
	}

	/*
	 * Compare pin state (HIGH/LOW) between non-working pin object and working pin
	 * object. If equal --> print "Equal" on SCI2 If not equal --> print "Not equal"
	 * on SCI2
	 */
	static void compareWorkingPinWithNonWorkingPinValue() {
		System.out.println((workingPinObject.get() == nonWorkingPinObject.get()) ? "Equal" : "Not equal");
	}

	/*
	 * Set state of non-working pin as "LOW" if "nonWorkingPinObject" is set as an
	 * output. Needed to prevent the pin object from being "high-impendant" at its
	 * initial state.
	 */
	static void setNonWorkingPinLow() {
		nonWorkingPinObject.set(false);
	}

	/*
	 * Set state of working pin as "LOW" if "workingPinObject" is set as an output.
	 * Needed to prevent the pin object from being "high-impendant" at its initial
	 * state.
	 */
	static void setWorkingPinLow() {
		workingPinObject.set(false);
	}

} 