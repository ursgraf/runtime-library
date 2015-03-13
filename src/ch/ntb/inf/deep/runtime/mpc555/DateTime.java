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

import ch.ntb.inf.deep.runtime.mpc555.driver.DS1302Z;
import ch.ntb.inf.deep.runtime.ppc32.Task;

/**
 * Date and time class. This class reads on initialization the date and time
 * from the {@link ch.ntb.inf.deep.runtime.mpc555.driver.DS1302Z} real time clock. After that the time is
 * updated with a normal {@link ch.ntb.inf.deep.runtime.ppc32.Task}. Each 24 hours at 24:00 the time will be
 * updated from the real time clock.<br>
 * 
 */
public class DateTime extends Task {

	private int sec, min, hour, date, month, year, millisec;

	public static int maxStrLen = 21;

	private int lastCallTime;
	private static DateTime dateTime;

	private DateTime() {
		this.period = 100;
		sec = DS1302Z.getSec();
		min = DS1302Z.getMin();
		hour = DS1302Z.getHour();
		date = DS1302Z.getDate();
		month = DS1302Z.getMonth();
		year = DS1302Z.getYear();
		millisec = 0;
		lastCallTime = Task.time();
		Task.install(this);
	}

	/**
	 * Get an instance from this class.
	 * @return The instance of this class.
	 */
	public static DateTime getInstance() {
		if (dateTime == null)
			dateTime = new DateTime();
		return dateTime;
	}

	/*
	 * @see mpc555.Task#action()
	 */
	public void action() {
		millisec += Task.time() - lastCallTime;
		lastCallTime = Task.time();
		if (millisec > 999) {
			sec++;
			millisec -= 1000;
			if (sec > 59) {
				min++;
				sec -= 60;
				if (min > 59) {
					min -= 60;
					hour++;
					if (hour > 23)
						update();
				}
			}
		}
	}

	/**
	 * Update the time from the {@link ch.ntb.inf.deep.runtime.mpc555.driver.DS1302Z} real time clock.
	 */
	private void  update() {
		sec = DS1302Z.getSec();
		min = DS1302Z.getMin();
		hour = DS1302Z.getHour();
		date = DS1302Z.getDate();
		month = DS1302Z.getMonth();
		year = DS1302Z.getYear();
		millisec = 0;
		lastCallTime = Task.time();
	}

	/**
	 * Read the actual seconds.
	 * @return the actual seconds.
	 */
	public int getSec() {
		return sec;
	}

	/**
	 * Read the actual minutes.
	 * @return the actual minutes.
	 */
	public int getMin() {
		return min;
	}

	/**
	 * Reads the actual hour.
	 * The hour is represented with the 24 hours format.
	 * @return the actual hour.
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Reads the actual date of the month.
	 * @return the actual date.
	 */
	public int getDate() {
		return date;
	}

	/**
	 * Reads the actual month.
	 * @return the actual month.
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * Reads the actual year.
	 * @return the actual year.
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Sets the actual time and writes it to the {@link ch.ntb.inf.deep.runtime.mpc555.driver.DS1302Z} real time clock.
	 * @param sec the actual seconds.
	 * @param min the actual minutes.
	 * @param hour the actual hour.
	 * @param date the actual date.
	 * @param month the actual month.
	 * @param year the actual year.
	 */
	public void setTime(int sec, int min, int hour, int date, int month,
			int year) {
		DS1302Z.setWriteProtection(false);
		DS1302Z.setSec(sec);
		DS1302Z.setMin(min);
		DS1302Z.setHour(hour);
		DS1302Z.setDate(date);
		DS1302Z.setMonth(month);
		DS1302Z.setYear(year);
		DS1302Z.setWriteProtection(true);
		this.sec = sec;
		this.min = min;
		this.hour = hour;
		this.date = date;
		this.month = month;
		this.year = year;
		this.millisec = 0;
	}

	/**
	 * Returns the actual Time as an integer.
	 * bits 0 - 4 = seconds / 2
	 * bits 5 - 10 = minutes
	 * bits 11 - 15 = hour
	 * bits 16 - 20 = date
	 * bits 21 - 24 = month
	 * bits 25 - 31 = year - 1980
	 * @return actual Time as an integer
	 */
	public int getPackedTime() {
		int time = (year - 1980) << 25;
		time |= month << 21;
		time |= date << 16;
		time |= hour << 11;
		time |= min << 5;
		time |= sec >> 1;
		return time;

	}

	/**
	 * Returns the date and time as a string.
	 * The format <i>dd.mm.yyyy hh:mm:ss</i>.
	 * @param str
	 * @return date and time as string
	 */
	public int getString(char[] str) {
		int off = 0;
		if (date < 10) {
			str[off] = '0';
			off++;
			str[off] = (char) ('0' + date);
			off++;
		} else {
			off = Integer.toCharArray(str, 0, date);
		}
		str[off] = '.';
		off++;
		if (month < 10) {
			str[off] = '0';
			off++;
			str[off] = (char) ('0' + month);
			off++;
		} else {
			off = Integer.toCharArray(str, off, month);
		}
		str[off] = '.';
		off++;
		off = Integer.toCharArray(str, off, year);
		str[off] = ' ';
		off++;
		if (hour < 10) {
			str[off] = '0';
			off++;
			str[off] = (char) ('0' + hour);
			off++;
		} else {
			off = Integer.toCharArray(str, off, hour);
		}
		str[off] = ':';
		off++;
		if (min < 10) {
			str[off] = '0';
			off++;
			str[off] = (char) ('0' + min);
			off++;
		} else {
			off = Integer.toCharArray(str, off, min);
		}
		str[off] = ':';
		off++;
		if (sec < 10) {
			str[off] = '0';
			off++;
			str[off] = (char) ('0' + sec);
			off++;
		} else {
			off = Integer.toCharArray(str, off, sec);
		}
		return off;
	}

}
