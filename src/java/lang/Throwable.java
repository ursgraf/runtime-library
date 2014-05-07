package java.lang;

import java.io.*;

public class Throwable implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * This field contains a detailed message depending on the exception
	 * actually thrown.
	 */
	public String message;

	/**
	 * This field contains the machine address where the exception was thrown.
	 */
	public int addr;

	public Throwable() {
		
	}

	public Throwable(String message) {
		this.message = message;
	}

	public Throwable(String message, Throwable cause) {
		this.message = message;
	}

	public Throwable(Throwable cause) {
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return (message != null) ? message : "";	
	}

	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/**
	 * Prints the throwable and it's stack trace to a print stream
	 * 
	 * @param s
	 *            PrintWriter to stream the data to.
	 */
	public void printStackTrace(PrintStream s) {
//		synchronized (s) {
//			s.println(this);
//			StackTraceElement[] trace = getCallstack();
//			for (int i = 0; i < trace.length; i++) {
//				s.println("\tat " + trace[i]);
//			}
//		}
		s.print(message);
		s.print(" at addr ");
		s.printHexln(addr);
	}

}