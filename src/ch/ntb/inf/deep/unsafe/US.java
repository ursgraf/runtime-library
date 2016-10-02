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

package ch.ntb.inf.deep.unsafe;

/**
 * This class allows for accessing absolute memory locations and machine registers. 
 * <strong>Warning: The inherent safety of Java is avoided. Use these methods with special care and only in 
 * low-level drivers!</strong>
 * The methods of this class will be translated by inserting machine code instructions
 * directly in the code without a method call.
 */
/* changes:
 * 06.06.06	NTB/ED	creation
 */

public class US {

	/** 
	 * Write 1 byte to hardware address: mem[address] = (byte)value
	 * @param address Memory address to write to.
	 * @param value Byte value. 
	 */
	public static void PUT1(int address, int value) {
	}

	/** 
	 * Write 2 byte to hardware address: mem[address] = (short)value 
	 * @param address Memory address to write to.
	 * @param value Two byte value. 
	 */
	public static void PUT2(int address, int value) {
	}

	/** 
	 * Write 4 byte to hardware address: mem[address] = value 
	 * @param address Memory address to write to.
	 * @param value Four byte value. 
	 */
	public static void PUT4(int address, int value) {
	}

	/** 
	 * Write 8 byte to hardware address: mem[address] = value 
	 * @param address Memory address to write to.
	 * @param value Eight byte value. 
	 */
	public static void PUT8(int address, long value) {
	}

	/** 
	 * Read a bit at hardware address: return BIT(mem[address](byte), bitNr) 
	 * @param address Memory address.
	 * @param bitNr Bit number (0 .. 7). 
	 * @return {@code true} if bit set. 
	 */
	public static boolean BIT(int address, int bitNr) {
		return false;
	}

	/** 
	 * Read 1 byte from hardware address: return mem[address] 
	 * @param address Memory address to read from.
	 * @return Byte value. 
	 */
	public static byte GET1(int address) {
		return 0;
	}

	/** 
	 * Read 2 byte from hardware address: return mem[address] 
	 * @param address Memory address to read from.
	 * @return Two byte value. 
	 */
	public static short GET2(int address) {
		return 0;
	}

	/** 
	 * Read 4 byte from hardware address: return mem[address] 
	 * @param address Memory address to read from.
	 * @return Four byte value. 
	 */
	public static int GET4(int address) {
		return 0;
	}

	/** 
	 * Read 8 byte from hardware address: return mem[address] 
	 * @param address Memory address to read from.
	 * @return Eight byte value. 
	 */
	public static long GET8(int address) {
		return 0;
	}

	/** 
	 * Returns object reference as address 
	 * @param ref Object reference.
	 * @return Memory address of object. 
	 */
	public static int REF(Object ref) {
		return 0;
	}

	/** 
	 * Read content of general purpose register 
	 * @param reg Register number.
	 * @return Register content. 
	 */
	public static int GETGPR(int reg) {
		return 0;
	}

	/** 
	 * Read content of floating point register 
	 * @param reg Register number.
	 * @return Register content. 
	 */
	public static double GETFPR(int reg) {
		return 0;
	}

	/** 
	 * Read content of special purpose register 
	 * @param reg Register number.
	 * @return Register content. 
	 */
	public static int GETSPR(int reg) {
		return 0;
	}

	/** 
	 * Write to general purpose register 
	 * @param reg Register number.
	 * @param value Register content. 
	 */
	public static void PUTGPR(int reg, int value) {
	}

	/** 
	 * Write to floating point register 
	 * @param reg Register number.
	 * @param value Register content. 
	 */
	public static void PUTFPR(int reg, double value) {
	}

	/** 
	 * Write to special purpose register 
	 * @param reg Register number.
	 * @param value Register content. 
	 */
	public static void PUTSPR(int reg, int value) {
	}

	/** 
	 * Insert single machine code instruction 
	 * @param instr Machine instruction as string.
	 */
	public static void ASM(String instr) {
	}

	/** 
	 * Get absolute hardware address of class method 
	 * @param name Name of method.
	 * @return Memory address of method. 
	 */
	public static int ADR_OF_METHOD(String name) {
		return 0;
	}
	
	/** 
	 * Halt exception: program termination, 20 &lt;= haltNr &lt; 256 
	 * @param haltNr User definable halt number.
	 */
	public static void HALT(int haltNr) {
	}

	/**
	 * Saves FPSCR and all temporary FPRs (Floating Point Registers) and sets
	 * the FP flag in MSR (usingGPR0). This method must be called before any other statement 
	 * in a method. 
	 */
	public static void ENABLE_FLOATS() {
	}
	
}