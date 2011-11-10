/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.unsafe;

/* changes:
 * 06.06.06	NTB/ED	creation
 */

public class US {

	/** put 1 byte: mem[address] = (byte)value */
	public static void PUT1(int address, int value) {
	}

	/** put 2 bytes: mem[address] = (short)value */
	public static void PUT2(int address, int value) {
	}

	/** put 4 bytes: mem[address] = value */
	public static void PUT4(int address, int value) {
	}

	/** put 8 bytes: mem[address] = value */
	public static void PUT8(int address, long value) {
	}

	/** get bit: return BIT(mem[address](byte), bitNr) */
	public static boolean BIT(int address, int bitNr) {
		return false;
	}

	/** get 1 byte: return mem[address] */
	public static byte GET1(int address) {
		return 0;
	}

	/** get 2 bytes: return mem[address] */
	public static short GET2(int address) {
		return 0;
	}

	/** get 4 bytes: return mem[address] */
	public static int GET4(int address) {
		return 0;
	}

	/** get 8 bytes: return mem[address] */
	public static long GET8(int address) {
		return 0;
	}

	/** get content of general purpose register */
	public static int GETGPR(int reg) {
		return 0;
	}

	/** get content of floating point register */
	public static double GETFPR(int reg) {
		return 0;
	}

	/** get content of special purpose register */
	public static int GETSPR(int reg) {
		return 0;
	}

	/** write to general purpose register */
	public static void PUTGPR(int reg, int value) {
	}

	/** write to floating point register */
	public static void PUTFPR(int reg, double value) {
	}

	/** write to special purpose register */
	public static void PUTSPR(int reg, int value) {
	}

	/** insert machine code */
	public static void ASM(String instr) {
	}

	/** get address of method */
	public static int ADR_OF_METHOD(String name) {
		return 0;
	}
	
	/** halt exception: program termination, 20 <= haltNr < 256 */
	public static void HALT(int haltNr) {
	}

	// ---- methods which must be called before any other statement of caller
	// (-method)

	/**
	 * saves FPSCR and all temporary FPRs (Floating Point Registers) and sets
	 * the FP flag in MSR (usingGPR0)
	 */
	public static void ENABLE_FLOATS() {
	}
}