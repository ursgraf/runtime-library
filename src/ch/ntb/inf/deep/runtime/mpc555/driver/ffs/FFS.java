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

package ch.ntb.inf.deep.runtime.mpc555.driver.ffs;

import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/*
 * Changes: 
 * 03.05.2011 NTB/Urs Graf:	creation
 */

/**
 * Flash File System<br>
 * Works if code runs out of RAM or flash
 * Sets aside part of the onboard flash for a file system
 */
public class FFS implements IntbMpc555HB {
	private static final boolean dbg = false;
	private static final int devAddr = extFlashBase; // external flash address, 16MB 
	private static final int fsOffset = 0x200000; // start of file system in flash, 2MB 
	private static final int key = 0x12345678; 	// key for file system 
	private static final int sectorSize = 0x20000; // size of sectors, 128K 
	private static final int NumOfSectors = 16;	// total number of sectors for file system
	static final int maxFiles = 16;	// maximum files in file system
	static final int maxBlocksPerFile = 16;	// maximum number of blocks per file
	static final int blockSize = 0x2000;	// block size (8 kB)
	static final int BlocksPerSector = FFS.sectorSize / blockSize;	// number of blocks per sector (16)
	static final int numOfBlocks = NumOfSectors * BlocksPerSector;	// total number of blocks for file system

	static File[] fileTab; 	//  table of files
	static boolean[] freeBlocks;	// free and erased blocks in flash 
	static boolean[] usedBlocks; 	// blocks containing files
	@SuppressWarnings("unused")
	private static boolean fileDirExists; 	// indicates whether file system exists in flash and could be read 

	/**
	 * erases a sector in the flash
	 * @param sector sector to be deleted
	 */
	public static void eraseSector(int sector) { // blocking 
		int sectorAddr = devAddr + fsOffset + sector * sectorSize;
		AM29LV160.eraseSector(sectorAddr);
	}

	/**
	 * Returns address of sector.
	 * @param sector Sector, of which address is returned.
	 * @return Address of sector.
	 */
	public static int getSectorAddr(int sector) {
		return devAddr + fsOffset + sector * sectorSize;
	}

	/**
	 * Programs an integer value.
	 * @param sector address of sector.
	 * @param offset offset.
	 * @param val value to be programmed.
	 */
	public static void programInt(int sector, int offset, int val) { // blocking 
		int addr = devAddr + fsOffset + sector * sectorSize + offset;
		AM29LV160.programInt(addr, val);
	}

	/**
	 * programs an short value
	 * @param sector address of sector
	 * @param offset offset
	 * @param val value to be programmed
	 */
	public static void programShort(int sector, int offset, short val) { // blocking 
		int addr = devAddr + fsOffset + sector * sectorSize + offset;
		AM29LV160.programShort(addr, val);
	}

	/**
	 * reads a byte from flash file system
	 * @param sector sector
	 * @param offset offset
	 * @return Byte read.
	 */
	public static byte readByte(int sector, int offset) {
		int addr = devAddr + fsOffset + sector * sectorSize + offset;
		return US.GET1(addr);
	}

	/**
	 * reads a short from flash file system
	 * @param sector sector
	 * @param offset offset
	 * @return Short read.
	 */
	public static short readShort(int sector, int offset) {
		int addr = devAddr + fsOffset + sector * sectorSize + offset;
		return US.GET2(addr);
	}

	/**
	 * reads an integer from flash file system
	 * @param sector sector
	 * @param offset offset
	 * @return Integer read.
	 */
	public static int readInt(int sector, int offset) {
		int addr = devAddr + fsOffset + sector * sectorSize + offset;
		return US.GET4(addr);
	}

	/**
	 * reads directory from flash 
	 */
	private static void readDir () {
		int  sector, offset; 
		if (dbg) System.out.println("searching for file directory"); 
		offset = 0; sector = 0;
		int val = FFS.readInt(sector, offset);
		if (val == key) {	// FS exists 
			if (dbg) System.out.println("file directory exists"); 
			offset += 4;	
			for (int i = 0; i < numOfBlocks; i++) {
				short data = FFS.readShort(sector, offset);	
				freeBlocks[i] = (data == 0)? false: true;
				offset += 2;
			}
			char[] ch = new char[32];
			for (int i = 0; i < maxFiles; i++) {
				boolean valid = (FFS.readShort(sector, offset) == 0)? false: true; offset += 2;
				if (valid) {	// file is valid in directory
					if (dbg) {System.out.print("file "); System.out.print(i); System.out.print(" is valid: ");} 
					byte filler = (byte)FFS.readShort(sector, offset); offset += 2;
						for (int k = 0; k < 32; k++) {
							ch[k] = (char)FFS.readShort(sector, offset);
							offset += 2;
						}
					int n;
					for (n = 0; ch[n] != 0 && n < 32; n++);
					String name = new String(ch, 0, n);
					if (dbg) System.out.println(name);
					File f = new File(name);	// file does not yet exist and will be created in FileTab 
					// file info is read from flash
					f.valid = valid;
					f.filler = filler;
					f.addr = FFS.readInt(sector, offset); 
					offset += 4;
					f.len = FFS.readInt(sector, offset); 
					offset += 4;
					for (int k = 0; k < maxBlocksPerFile; k++) {
						f.blocks[k] = FFS.readInt(sector, offset);
						int num = f.blocks[k];
						if ((num >= 0) && (num < numOfBlocks)) usedBlocks[num] = true;
						offset += 4;
					}	
				} else {
					offset += 74 + 4 * maxBlocksPerFile;
				}
			}
			fileDirExists = true;
		} else {	// no existing FS 
			if (dbg) System.out.println("no directory found"); 
			formatAll();
			fileDirExists = false;
		} 
	}

	/**
	 * writes directory to flash
	 */
	static void writeDir () {
		int offset, sector, i, k;
		FFS.eraseSector(0);
		offset = 4; sector = 0;
		for (i = 0; i < numOfBlocks; i++) {
			FFS.programShort(sector, offset, freeBlocks[i]? (short) 0xff: (short) 0);
			offset += 2;
		}
		for (i = 0; i < maxFiles; i++) {
			File f = fileTab[i];
			if (f == null) {
				FFS.programShort(sector, offset, (short) 0); offset += 2;
				offset += 74 + 4 * maxBlocksPerFile;
			} else {
				FFS.programShort(sector, offset, f.valid? (short) 0xff: (short) 0); offset += 2;
				if (f.valid) {
					FFS.programShort(sector, offset, f.filler); offset += 2;
					byte[] b = f.name.getBytes();
					for (k = 0; k < f.name.length(); k++) {
						FFS.programShort(sector, offset, b[k]);
						offset += 2;
					}
					for (int n = k; n < 32; n++) {
						FFS.programShort(sector, offset, (short)0);
						offset += 2;
					}	
					FFS.programInt(sector, offset, f.addr); offset += 4;
					FFS.programInt(sector, offset, f.len); offset += 4;
					for (k = 0; k < maxBlocksPerFile; k++) {
						FFS.programInt(sector, offset, f.blocks[k]);
						offset += 4;
					}	
				} else offset += 74 + 4 * maxBlocksPerFile;
			}
		}
		FFS.programInt(sector, 0, key); 	// FS identifier 
	}

	/**
	 * Returns file with filename,
	 * null if file does not exist.
	 * @param filename Name of file to be opened.
	 * @return Opened file.
	 */
	public static File old (String filename) {	// searches file table for file name
		File f = null; int i = 0, len, len1; boolean eq;
		len = filename.length();
		if (filename.length() == 0) {return null;}
		else {
			do {
				eq = true;
				f = fileTab[i];
				if ((f != null) && (f.valid))  {	// file exists, compare names
					byte[] b1 = f.name.getBytes();
					byte[] b2 = filename.getBytes();
					len = filename.length(); len1 = f.name.length();
					if (len > len1) len = len1;
					for (int k = 0; k < len; k++) if (b1[k] != b2[k]) eq = false;
				} else eq = false;
				i++; 
			} while ((i < maxFiles) && !eq); 
			if (i == maxFiles) return null; else return f;
		}
	}

	/**
	 * Returns true if directory at index has a valid entry.
	 * @param index Index into file directory.
	 * @return {@code true} if directory contains valid entry at this index.
	 */
	public static boolean directory (int index) {	
		if ((index < 0) || (index >= maxFiles)) return false;
		return (fileTab[index] != null)? fileTab[index].valid: false;
	}

	/**
	 * erases flash, initializes file system 
	 */
	public static void formatAll () {
		freeBlocks[0] = false; usedBlocks[0] = false; 	// reserved for file directory 
		for (int i = 1; i < BlocksPerSector; i++) {
			freeBlocks[i] = false; usedBlocks[i] = true;	// reserved for defragmentation method 
		}
		for (int i = BlocksPerSector; i < numOfBlocks; i++) {
			freeBlocks[i] = true; usedBlocks[i] = false;
		}
		for (int i = 0; i < NumOfSectors; i++) {
			FFS.eraseSector(i);
		}
		for (int i = 0; i < maxFiles; i++) {
			fileTab[i] = null;
		}
	}

	/**
	 * reads directory from flash
	 */
	public static void init() { 
		fileTab = new File[maxFiles];
		freeBlocks = new boolean[numOfBlocks];
		usedBlocks = new boolean[numOfBlocks];
		for (int i = 0; i < BlocksPerSector; i++) usedBlocks[i] = true;
		for (int i = BlocksPerSector; i < numOfBlocks; i++) usedBlocks[i] = false;
		readDir();
	}
	
	
	// methods for debugging purposes *
	public static void showFreeBlocks () {
		System.out.println("free Blocks"); 
		for (int k = 0; k < numOfBlocks; k++) {
			if (freeBlocks[k]) {System.out.print(k); System.out.print("\t");} 
		}
		System.out.println(); 
	}

	public static void showUsedBlocks () {
		System.out.println("used Blocks"); 
		for (int k = 0; k < numOfBlocks; k++) {
			if (usedBlocks[k]) {System.out.print(k); System.out.print("\t");} 
		}
		System.out.println(); 
	}
	public static void outDir () {
		System.out.println("output directory");
		for (int i = 0; i < maxFiles; i++) {
			if (directory(i)) {
				File f = fileTab[i];
				System.out.print("file no ");System.out.println(i); 
				System.out.print("\tfiller ");System.out.println(f.filler); 
				System.out.print("\t"); System.out.println(f.name); 
				System.out.print("\taddr = "); System.out.printHex(f.addr); System.out.println();
				System.out.print("\tlength = "); System.out.println(f.len); 
				System.out.print("\tused blocks: "); 
				for (int k = 0; k < maxBlocksPerFile; k++)
					if (fileTab[i].blocks[k] != -1) {
						System.out.print(fileTab[i].blocks[k]); System.out.print(',');
					}
				System.out.println();
			}
		}
	}


}
