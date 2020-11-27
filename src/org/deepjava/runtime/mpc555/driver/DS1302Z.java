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

package org.deepjava.runtime.mpc555.driver;

/* Changes:
 * 14.09.2009	NTB/SP	creation
 */

/**
 * Driver for the DS1302Z real time clock.<br>
 * Pin 13, 14 and 15 of the TPUB are used.<br>
 * Before the first write disable the write protection with {@link #setWriteProtection(boolean)}.
 *
 */
public class DS1302Z {

	private static final boolean tpuB = false;
	
	private static final int RST = 13;
	private static final int SCKL = 14;
	private static final int IO = 15;
	
	private static final byte sec = (byte) 0x80;
	private static final byte min = (byte) 0x82;
	private static final byte hour = (byte) 0x84;
	private static final byte date =  (byte)0x86;
	private static final byte month = (byte) 0x88;
	private static final byte day =  (byte)0x8A;
	private static final byte year =  (byte)0x8C;
	private static final byte ctrl = (byte) 0x8E;
	
	private static TPU_DIO rst, sckl, io;
	
	/**
	 * Write a value to the RTC.
	 * 
	 * @param type The type (sec, min, ...) to write.
	 * @param val The value to write.
	 */
	private static void write(byte type, byte val){
		int w = ((val << 8) | (0xFF & type));
		rst.set(true);
		io.dir(true);
		for(int i = 0x1; i < 0x10000; i<<=1){
			sckl.set(false);
			if((w & i) != 0) {
				io.set(true);
			}
			else {
				io.set(false);
			}
			sckl.set(true);
		}
		sckl.set(false);
		rst.set(false);
	}
	
	
	/**
	 * Read a value from the RTC.
	 * @param type The type to read (sec, min, ...).
	 * @return The read value.
	 */
	private static int read(byte type){
		type |= 0x01;
		rst.set(true);
		io.dir(true);
		for(int i = 0x1; i < 0x100; i<<=1){
			sckl.set(false);
			if((type & i) != 0) io.set(true);
			else io.set(false);
			sckl.set(true);
		}
		int val = 0;
		io.dir(false);
		for(int i = 0x1; i < 0x100; i<<=1){
			sckl.set(true);		
			sckl.set(false);	
			if(io.get()) val |= i;	
		}
		io.dir(false);	
		rst.set(false);
		return val;
	}
	
	/**
	 * Read the actual second value from the RTC.
	 * @return The actual seconds.
	 */
	public static int getSec(){
		int val = read(sec);
		return (val & 0xF) + ((val >> 4) & 0x7) * 10;
		
	}
	
	/**
	 * Read the actual minute value from the RTC.
	 * @return The actual minutes.
	 */
	public static int getMin(){
		int val = read(min);
		return (val & 0xF) + (val >> 4) * 10;
	}
	
	/**
	 * Read the actual hour value from the RTC.
	 * @return The actual hours.
	 */
	public static int getHour(){
		int val = read(hour);
		return (val & 0xF) + ((val >> 4) & 0x3) * 10;
	}
	
	/**
	 * Reads the actual date value from the RTC.
	 * @return The actual date.
	 */
	public static int getDate(){
		int val = read(date);
		return (val & 0xF) + (val >> 4) * 10;
	}
	
	/**
	 * Reads the actual month value from the RTC.
	 * @return The actual month.
	 */
	public static int getMonth(){
		int val = read(month);
		return (val & 0xF) + (val >> 4) * 10;
	}
	
	/**
	 * Reads the actual day value from the RTC.
	 * @return The actual day of the week.
	 */
	public static int getDay(){
		return read(day);
	}
	
	/**
	 * Reads the actual year value from the RTC.
	 * @return The actual year.
	 */
	public static int getYear(){
		int val = read(year);
		return (val & 0xF) + ((val >> 4) & 0xF)* 10 + 2000;
	}
	
	/**
	 * Set the actual second value to the RTC.
	 * @param val The actual seconds.
	 */
	public static void setSec(int val){
		int v = val % 10;
		v |= (val / 10) << 4;
		write(sec,(byte) v);
	}
	
	/**
	 * Set the actual minute value to the RTC.
	 * @param val The actual minutes.
	 */
	public static void setMin(int val){
		int v = val % 10;
		v |= (val / 10) << 4;
		write(min,(byte) v);
	}
	
	/**
	 * Set the actual hour value to the RTC.
	 * @param val The actual hour.
	 */
	public static void setHour(int val){
		int v = val % 10;
		v |= ((val / 10) << 4);
		write(hour,(byte) v);
	}
	
	/**
	 * Set the actual date value to the RTC.
	 * @param val The actual date.
	 * 
	 */
	public static void setDate(int val){
		int v = val % 10;
		v |= (val / 10) << 4;
		write(date,(byte) v);
	}
	
	/**
	 * Set the actual month value to the RTC.
	 * @param val The actual month.
	 */
	public static void setMonth(int val){
		int v = val % 10;
		v |= (val / 10) << 4;
		write(month,(byte) v);
	}
	
	/**
	 * Set the actual Day value to the RTC
	 * @param val The actual Day of the week.
	 */
	public static void setDay(int val){
		write(day,(byte)val);
	}
	
	/**
	 * Set the actual year value to the RTC.
	 * @param val The actual year.
	 */
	public static void setYear(int val){
		if(val > 2000) val -= 2000;
		int v = val % 10;
		v |= (val / 10) << 4;
		write(year,(byte) v);
	}
	
	/**
	 * Returns the write protection state.
	 * @return <code>true</code> if the write protection is enabled, <code>false</code> otherwise.
	 */
	public static boolean writeProtected(){		
		return (read(ctrl) & 0x80) != 0;
	}
	
	/**
	 * Enable or disable the write protection.
	 * @param enable <code>true</code> to enable, <code>false</code> to disable.
	 */
	public static void setWriteProtection(boolean enable){
		if(enable) write(ctrl,(byte) 0xFF);
		else write(ctrl, (byte)0 );
	}
	
	static{
		rst = new TPU_DIO(tpuB, RST , true);
		sckl = new TPU_DIO(tpuB, SCKL, true);
		io = new TPU_DIO(tpuB, IO, false);
		rst.set(false);
		sckl.set(false);
	}
}
