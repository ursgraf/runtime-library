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

package org.deepjava.unsafe.arm;

/**
 * This class allows for accessing absolute memory locations and machine registers on a ARM processor. 
 * <strong>Warning: The inherent safety of Java is avoided. Use these methods with special care and only in 
 * low-level drivers!</strong>
 * The methods of this class will be translated by inserting machine code instructions
 * directly in the code without a method call.
 */
/* changes:
 * 16.05.2018	NTB/Urs Graf	creation
 */

public class US extends org.deepjava.unsafe.US {

	/** 
	 * Read content of general purpose register 
	 * @param reg Register number.
	 * @return Register content. 
	 */
	public static int GETGPR(int reg) {
		return 0;
	}

	/** 
	 * Read content of extension double precision register (SIMD and VFP)
	 * @param reg Register number.
	 * @return Register content. 
	 */
	public static double GETEXTRD(int reg) {
		return 0;
	}

	/** 
	 * Read content of extension single precision register (SIMD and VFP)
	 * @param reg Register number.
	 * @return Register content. 
	 */
	public static float GETEXTRS(int reg) {
		return 0;
	}

	/** 
	 * Read content of coprocessor register 
	 * @param coproc Coprocessor number.
	 * @param CRn CRn field. 
	 * @param opc1 opc1 field. 
	 * @param CRm CRm field. 
	 * @param opc2 opc2 field. 
	 * @return Register content. 
	 */
	public static int GETCPR(int coproc, int CRn, int opc1, int CRm, int opc2) {
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
	 * Write to extension double precision register (SIMD and VFP)
	 * @param reg Register number.
	 * @param value Register content. 
	 */
	public static void PUTEXTRD(int reg, double value) {
	}

	/** 
	 * Write to extension single precision register (SIMD and VFP)
	 * @param reg Register number.
	 * @param value Register content. 
	 */
	public static void PUTEXTRS(int reg, float value) {
	}

	/** 
	 * Write to special purpose register 
	 * @param coproc Coprocessor number.
	 * @param CRn CRn field. 
	 * @param opc1 opc1 field. 
	 * @param CRm CRm field. 
	 * @param opc2 opc2 field. 
	 * @param value Register content. 
	 */
	public static void PUTCPR(int coproc, int CRn, int opc1, int CRm, int opc2, int value) {
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