package org.deepjava.unsafe;

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
	 * Read a bit at hardware address: return BIT(mem[address](byte), bitNr) 
	 * @param address Memory address.
	 * @param bitNr Bit number (0 .. 31). 
	 * @return {@code true} if bit set. 
	 */
	public static boolean BIT(int address, int bitNr) {
		return false;
	}

	/** 
	 * Insert single machine code instruction 
	 * @param instr Machine instruction as string.
	 */
	public static void ASM(String instr) {
	}

	/** 
	 * Get absolute address of class method 
	 * @param name Name of method.
	 * @return Memory address of method. 
	 */
	public static int ADR_OF_METHOD(String name) {
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

}
