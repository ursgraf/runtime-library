/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang;

import java.io.*;
import ch.ntb.inf.deep.marker.Modified;

/**
 * The superclass of all classes which can be thrown by the VM. The
 * two direct subclasses are recoverable exceptions ({@code Exception}) and
 * unrecoverable errors ({@code Error}). This class provides common methods for
 * accessing a string message which provides extra information about the
 * circumstances in which the {@code Throwable} was created (basically an error
 * message in most cases), and for saving a stack trace (that is, a record of
 * the call stack at a particular point in time) which can be printed later.
 *
 * <p>A {@code Throwable} can also include a cause, which is a nested {@code
 * Throwable} that represents the original problem that led to this {@code
 * Throwable}. It is often used for wrapping various types of errors into a
 * common {@code Throwable} without losing the detailed original error
 * information. When printing the stack trace, the trace of the cause is
 * included.
 *
 * @see Error
 * @see Exception
 * @see RuntimeException
 */
/* Changes:
 * 27.5.2014	Urs Graf	initial import and modified
 */
public class Throwable implements Serializable, Modified {

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

	public Throwable(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		this.message = message;
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