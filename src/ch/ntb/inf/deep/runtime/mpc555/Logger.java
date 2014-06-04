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

package ch.ntb.inf.deep.runtime.mpc555;

import java.io.PrintStream;

/**
 * Logger utility to write log messages to a {@link PrintStream}. It is
 * possible to set six different log levels.
 * <ul>
 * <li>{@link Logger#none} Logging disabled</li>
 * <li>{@link Logger#error} Only error messages will be logged</li>
 * <li>{@link Logger#warning} Error and warning messages will be logged</li>
 * <li>{@link Logger#config} Error, warning and config messages will be logged</li>
 * <li>{@link Logger#info} All messages except debug messages will be logged</li>
 * <li>{@link Logger#debug} All messages will be logged</li>
 * </ul>
 * 
 */
public class Logger {

	/**
	 * If the log level is none no message will be logged. Don't use this level
	 * to indicate a message by a <code>log</code> method call, because this
	 * level is only used to disable all messages.
	 */
	public static final int none = -1;

	/**
	 * Indicates a error message.
	 */
	public static final int error = 0;

	/**
	 * Indicates a warning message.
	 */
	public static final int warning = 1;

	/**
	 * Indicates a system configuration message.
	 */
	public static final int config = 2;

	/**
	 * Indicates a information message.
	 */
	public static final int info = 3;

	/**
	 * Indicates a debug message.
	 */
	public static final int debug = 4;

	private int level; // The actual log level

	private String levelDis[] = { "[Erro] ", "[Warn] ", "[Conf] ", "[Info] ",
			"[Debu] " };

	private PrintStream stream;
	private DateTime time;
	private char[] str;

	/**
	 * Creates a new Logger object which write log messages to the
	 * {@link PrintStream} <code>stream</code>. The standard log level is
	 * {@link #warning}.
	 * 
	 * @param stream
	 *            The output stream of the logger.
	 */
	public Logger(PrintStream stream) {
		time = DateTime.getInstance();
		str = new char[128];
		this.stream = stream;
		level = warning;
	}

	/**
	 * Set the log level. Possible levels are:
	 * <ul>
	 * <li>{@link none}</li>
	 * <li>{@link error}</li>
	 * <li>{@link warning}</li>
	 * <li>{@link config}</li>
	 * <li>{@link info}</li>
	 * <li>{@link debug}</li>
	 * </ul>
	 * 
	 * @param level
	 *            the level of the logger.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Returns the actual level of the logger.
	 * 
	 * @return the atual level.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Writes a log message to the <code>PrintStream</code> if the
	 * parameter <code>level</code> is greater than the logger level.<br>
	 * With % it is possible to add some arguments to the message.
	 * <ul>
	 * <li>%t adds the actual date and time to the message</li>
	 * <li>%l adds the level description to the message</li>
	 * </ul>
	 * <b>Example:</b><br>
	 * <code>log(Logger.error,"%l My log message %t",12);</code> will log
	 * following message.<br>
	 * <i>[Erro] My log message 12.11.2009 08:57:13</i>.
	 * 
	 * @param level
	 *            the log level of the message.
	 * @param msg
	 *            the message. <code>%d</code>.
	 */
	public void log(int level, String msg) {
		if (this.level >= level) {
			int strlen = msg.length();
			for (int i = 0; i < strlen; i++) {
				char c = msg.charAt(i);
				if ((c == '%') && ((i + 1) < strlen)) {
					c = msg.charAt(i + 1);
					switch (c) {
					case 't':
						int len = time.getString(str);
						stream.print(str, 0, len);
						i++;
						break;
					case 'l':
						if (level >= 0 && level < levelDis.length)
							stream.print(levelDis[level]);
						i++;
						break;
					default:
						stream.write(c);
						break;
					}
				} else {
					stream.write(c);
				}
			}
			stream.write('\r');
		}
	}

	/**
	 * Writes a log message to the <code>PrintStream</code> if the
	 * parameter <code>level</code> is greater than the logger level.<br>
	 * With % it is possible to add some arguments to the message.
	 * <ul>
	 * <li>%t adds the actual date and time to the message</li>
	 * <li>%l adds the level description to the message</li>
	 * <li>%d adds the parameter <code>val</code> to the message</li>
	 * </ul>
	 * <b>Example:</b><br>
	 * <code>log(Logger.error,"%l My log message with parameter value: %d",12);</code>
	 * will log following message.<br>
	 * <i>[Erro] My log message with parameter value: 12</i>.
	 * 
	 * @param level
	 *            the log level of the message.
	 * @param msg
	 *            the message.
	 * @param val
	 *            an integer value which can be added to the message with
	 *            <code>%d</code>.
	 */
	public void log(int level, String msg, int val) {
		if (this.level >= level) {
			int strlen = msg.length();
			for (int i = 0; i < strlen; i++) {
				char c = msg.charAt(i);
				if ((c == '%') && ((i + 1) < strlen)) {
					c = msg.charAt(i + 1);
					switch (c) {
					case 't':
						int len = time.getString(str);
						stream.print(str, 0, len);
						i++;
						break;
					case 'l':
						if (level >= 0 && level < levelDis.length)
							stream.print(levelDis[level]);
						i++;
						break;
					case 'd':
						stream.write(val);
						i++;
						break;
					default:
						stream.write(c);
					}
				} else {
					stream.write(c);
				}
			}
			stream.write('\r');
		}
	}

	/**
	 * Writes a log message to the <code>PrintStream</code> if the
	 * parameter <code>level</code> is greater than the logger level.<br>
	 * With % it is possible to add some arguments to the message.
	 * <ul>
	 * <li>%t adds the actual date and time to the message</li>
	 * <li>%l adds the level description to the message</li>
	 * <li>%d adds the parameters <code>values</code> to the message</li>
	 * </ul>
	 * Please use this method only if you really need to add more than one value
	 * to the message. This because every call will allocate new integer array.
	 * <b>Example:</b><br>
	 * <code>log(Logger.error,"%l My log message with parameter values: %d, %d, %d,",12,13,14);</code>
	 * will log following message.<br>
	 * <i>[Erro] My log message with parameter value: 12, 13, 14</i>.
	 * 
	 * @param level
	 *            the log level of the message.
	 * @param msg
	 *            the message.
	 * @param values
	 *            n integer values which can be added to the message with
	 *            <code>%d</code>.
	 */
	public void log(int level, String msg, int... values) {
		if (this.level >= level) {
			int strlen = msg.length();
			int valctr = 0;
			for (int i = 0; i < strlen; i++) {
				char c = msg.charAt(i);
				if ((c == '%') && ((i + 1) < strlen && valctr < values.length)) {
					c = msg.charAt(i + 1);
					switch (c) {
					case 't':
						int len = time.getString(str);
						stream.print(str, 0, len);
						i++;
						break;
					case 'l':
						if (level >= 0 && level < levelDis.length)
							stream.print(levelDis[level]);
						i++;
						break;
					case 'd':
						stream.write(values[valctr]);
						valctr++;
						i++;
						break;
					default:
						stream.write(c);
					}
				} else {
					stream.write(c);
				}
			}
			stream.write('\r');
		}
	}

}
