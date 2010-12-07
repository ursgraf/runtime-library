package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.Kernel;
import ch.ntb.inf.deep.unsafe.US;

// driver for AM29LV160 Flash, 4MB 
/*
 changes:
 3.3.08	GU, changed to java, file system starts at 2MB  
 21.4.05	GU, flash cell must no longer be -1 prior to write operation,
 single bits can be written, make sure not to change a '0' to a '1'
 */

public class Flash {

	static final int DevAddr = 0;//Kernel.CextRomBase; // ext. flash address, 16MB 	//Todo: fix rom base


	static final int FSOffset = 0x200000; // start of file system in flash, 1MB 

	public static final int SectorSize = 0x20000; // size of sectors, 128K 

	/**
	 * Löscht einen Sektor im Flash
	 * @param sector Sektor der zu löschen ist
	 */
	public static void eraseSector(int sector) { // blocking 
		int sectorAddr = DevAddr + FSOffset + sector * SectorSize;
		FlashAM29LV160.eraseSector(sectorAddr);
	}

	/**
	 * Liefert die Adresse eines Sektors
	 * @param sector Sektor, zu welchem die Adresse bestimmt werden soll
	 */
	public static int getSectorAddr(int sector) {
		return DevAddr + FSOffset + sector * SectorSize;
	}

	/**
	 * Programmiert einen Integer an die Position sector+offset
	 * @param sector Adresse des Basis-Sektors
	 * @param offset Offset
	 * @param val Wert, der programmiert werden soll
	 */
	public static void programInt(int sector, int offset, int val) { // blocking 
		int addr = DevAddr + FSOffset + sector * SectorSize + offset;
		FlashAM29LV160.programInt(addr, val);
	}

	/**
	 * Programmiert einen Short an die Position sector+offset
	 * @param sector Adresse des Basis-Sektors
	 * @param offset Offset
	 * @param shortVal Wert, der programmiert werden soll
	 */
	public static void programShort(int sector, int offset, short val) { // blocking 
		int addr = DevAddr + FSOffset + sector * SectorSize + offset;
		FlashAM29LV160.programShort(addr, val);
	}

	/**
	 * Liest ein Byte aus dem Flash
	 * @param sector Sektor
	 * @param offset Offset
	 */
	public static byte readByte(int sector, int offset) {
		int addr = DevAddr + FSOffset + sector * SectorSize + offset;
		return US.GET1(addr);
	}

	/**
	 * Liest einem Short aus dem Flash
	 * @param sector Sektor
	 * @param offset Offset
	 */
	public static short readShort(int sector, int offset) {
		int addr = DevAddr + FSOffset + sector * SectorSize + offset;
		return US.GET2(addr);
	}

	/**
	 * Liest einen Integer aus dem Flash
	 * @param sector Sektor
	 * @param offset Offset
	 */
	public static int readInt(int sector, int offset) {
		int addr = DevAddr + FSOffset + sector * SectorSize + offset;
		return US.GET4(addr);
	}

}