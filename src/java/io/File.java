package java.io;
import ch.ntb.inf.deep.runtime.mpc555.driver.Flash;



/*
	25.06.07 NTB/SP Prints removed
*/

/**
 * Flash File System<br>
 * Works if code runs out of RAM or flash
 */

public class File {
	/**
	 * indicates if file in directory is valid
	 */
	boolean valid; 	
	/**
	 * filler for last byte as flash is 2byte organized
	 */
	byte filler;	
	/**
	 * name of file
	 */
	public String name;	
	/**
	 * absolute address of first block of file in flash
	 */
	int addr;	
	/**
	 * length of file
	 */
	public int len;	
	/**
	 * block numbers occupied by this file
	 */
	int blocks[]; 
	
	/**
	 * maximum number of files in file system
	 */
	static public final int MaxFiles = 16;
	/**
	 * maximum number of blocks per file
	*/
	static public final int MaxBlocksPerFile = 16; 	 
	/**
	 * block size (8 kB)
	 */
	static public final int BlockSize = 0x2000; 
	/**
	 * number of blocks per sector (16)
	 */
	static public final int BlocksPerSector = Flash.SectorSize / BlockSize;
	/**
	 * total number of sectors for file system
	 */
	static public final int NumOfSectors = 16; 	 
	/**
	 * total number of blocks for file system
	 */
	static public final int NumOfBlocks = NumOfSectors * BlocksPerSector; 	// 
	
	static final int key = 0x12345678; 	// key for file system 
	
	static public File[] fileTab; 	//  table of files
	static boolean[] freeBlocks;	// free and erased blocks in flash 
	static boolean[] usedBlocks; 	// blocks containing files
	static boolean fileDirExists; 	// indicates whether file system exists in flash and could be read 
	
	/**
	 * returns address of first block of file
	 */
	public int address () {
		return this.addr;
	} 

	/**
	 * returns length of file in bytes
	 */
	public int length () {
		if (this == null) return 0;
		return this.len;
	} 

	/**
	 * writes file length into directory
	 */
	public void register () {	// blocking
		if (this == null) return;
		if (this.len % 2 == 1) {
			Rider r = new Rider();
			r.set(this, this.len);
			r.writeByte((byte)0); 	// write dummy to program last word into flash
			this.len--;
		}
		writeDir();
		return;
	} 

	/**
	 * deletes file in directory
	 */
	public void delete () {	// blocking
		if (this == null) return;
		this.valid = false;
		writeDir();
		return;
	} 

	/**
	 * returns file with filename
	 * null if file does not exist
	 */
	public static File old (String filename) {	// searches file table for file name
		File f = null; int i = 0, len, len1; boolean eq;
		if (filename.length() == 0) return null;
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
			} while ((i < MaxFiles) && !eq); 
			if (i == MaxFiles) return null; else return f;
		}
	}

	/**
	 * returns true if directory at index has a valid entry
	 */
	public static boolean directory (int index) {	
		if ((index < 0) || (index >= MaxFiles)) return false;
		return (fileTab[index] != null)? fileTab[index].valid: false;
	}
	
	/**
	 * erases flash, initialises file system 
	 */
	public static void formatAll () {
		freeBlocks[0] = false; usedBlocks[0] = false; 	// reserved for file directory 
		for (int i = 1; i < BlocksPerSector; i++) {
			freeBlocks[i] = false; usedBlocks[i] = true;	// reserved for defragmentation procedure 
		}
		for (int i = BlocksPerSector; i < NumOfBlocks; i++) {
			freeBlocks[i] = true; usedBlocks[i] = false;
		}
		for (int i = 0; i < NumOfSectors; i++) {
			Flash.eraseSector(i);
		}
		for (int i = 0; i < MaxFiles; i++) {
			fileTab[i] = null;
		}
	}
	
	/**
	 * writes directory to flash
	 */
	private static void writeDir () {
		int offset, sector, i, k;
		byte val;
		short data;
		Flash.eraseSector(0);
		offset = 4; sector = 0;
		for (i = 0; i < NumOfBlocks; i++) {
			Flash.programShort(sector, offset, freeBlocks[i]? (short) 0xff: (short) 0);
			offset += 2;
		}
		for (i = 0; i < MaxFiles; i++) {
			File f = fileTab[i];
			if (f == null) {
				Flash.programShort(sector, offset, (short) 0); offset += 2;
				offset += 74 + 4 * MaxBlocksPerFile;
			} else {
				Flash.programShort(sector, offset, f.valid? (short) 0xff: (short) 0); offset += 2;
				if (f.valid) {
					Flash.programShort(sector, offset, f.filler); offset += 2;
					byte[] b = f.name.getBytes();
					for (k = 0; k < f.name.length(); k++) {
						Flash.programShort(sector, offset, b[k]);
						offset += 2;
					}
					for (int n = k; n < 32; n++) {
						Flash.programShort(sector, offset, (short)0);
						offset += 2;
					}	
					Flash.programInt(sector, offset, f.addr); offset += 4;
					Flash.programInt(sector, offset, f.len); offset += 4;
					for (k = 0; k < MaxBlocksPerFile; k++) {
						Flash.programInt(sector, offset, f.blocks[k]);
						offset += 4;
					}	
				} else offset += 74 + 4 * MaxBlocksPerFile;
			}
		}
		Flash.programInt(sector, 0, key); 	// FS identifier 
	}
	
	/**
	 * reads directory from flash 
	 */
	private static void readDir () {
		int  sector, offset, i, k; File f; char[] ch;
//		OutT.println("searching for file directory"); for (int a = 0; a < 1000000; a++);

		offset = 0; sector = 0;
		int val = Flash.readInt(sector, offset);
		if (val == key) {	// FS exists 
//			OutT.println("file directory exists"); for (int a = 0; a < 1000000; a++);
			offset += 4;	
			for (i = 0; i < NumOfBlocks; i++) {
				short data = Flash.readShort(sector, offset);	
				freeBlocks[i] = (data == 0)? false: true;
				offset += 2;
			}
			for (i = 0; i < BlocksPerSector; i++) usedBlocks[i] = true;
			for (i = BlocksPerSector; i < NumOfBlocks; i++) usedBlocks[i] = false;
			ch = new char[32];
			for (i = 0; i < MaxFiles; i++) {
				boolean valid = (Flash.readShort(sector, offset) == 0)? false: true; offset += 2;
				if (valid) {	// file is valid in directory
					byte filler = (byte)Flash.readShort(sector, offset); offset += 2;
					for (k = 0; k < 32; k++) {
						ch[k] = (char)Flash.readShort(sector, offset);
						offset += 2;
					}
					String name = new String(ch);
					f = new File(name);	// file does not yet exist and will be created in FileTab 
					// file info is read from flash
					f.valid = valid;
					f.filler = filler;
					f.addr = Flash.readInt(sector, offset); offset += 4;
					f.len = Flash.readInt(sector, offset); offset += 4;
					for (k = 0; k < MaxBlocksPerFile; k++) {
						f.blocks[k] = Flash.readInt(sector, offset);
						int num = f.blocks[k];
						if ((num >= 0) && (num < NumOfBlocks)) usedBlocks[num] = true;
						offset += 4;
					}	
				} else offset += 74 + 4 * MaxBlocksPerFile;
			}
			fileDirExists = true;
		} else {	// no existing FS 
//			OutT.println("no directory found"); for (int a = 0; a < 1000000; a++);
			formatAll();
			fileDirExists = false;
		} 
	}
	
	/**
	 * creates new file
	 * if file with this name already exists, existing file is deleted first 
	 * returns null if maximum number of files is exceeded
	 */
	public File (String name) { 
		int i, k, num; File f;
		f = old(name); 
		if (f != null) f.delete();
		i = 0; f = fileTab[i];	// search for empty place in fileTab
		while ((i < MaxFiles) && (f != null) && f.valid) {i++; f = fileTab[i];}
		if (i == MaxFiles) return;
		this.name = new String(name);
		this.valid = true;
		this.filler = 0;
		this.addr = 0;
		this.len = 0;
		this.blocks = new int[MaxBlocksPerFile];
		for (k = 0; k < MaxBlocksPerFile; k++) this.blocks[k] = - 1;
		fileTab[i] = this;
	}

	
	/* methods for debugging purposes */
/*
	public static void showFreeBlocks () {
		OutT.println("free Blocks"); 
		for (int k = 0; k < NumOfBlocks; k++) {
			if (freeBlocks[k]) {OutT.print(k); OutT.printTab();} 
		}
		OutT.println(); 
	}
	
	public static void showUsedBlocks () {
		OutT.println("used Blocks"); 
		for (int k = 0; k < NumOfBlocks; k++) {
			if (usedBlocks[k]) {OutT.print(k); OutT.printTab();} 
		}
		OutT.println(); 
	}
	public static void outDir () {
		OutT.println("output directory");
		for (int i = 0; i < MaxFiles; i++) {
			if (directory(i)) {
			File f = fileTab[i];
				OutT.print("file no ");OutT.println(i); 
				OutT.printTab(); OutT.print("filler ");OutT.println(f.filler); 
				OutT.printTab(); OutT.println(f.name); 
				OutT.printTab(); OutT.print("addr = "); OutT.printHex(f.addr); OutT.println();
				OutT.printTab(); OutT.print("length = "); OutT.println(f.len); 
				OutT.printTab(); OutT.print("used blocks: "); 
				for (int k = 0; k < MaxBlocksPerFile; k++)
					if (fileTab[i].blocks[k] != -1) {
						OutT.print(fileTab[i].blocks[k]); OutT.print(',');
					}
				OutT.println();
			}
		}
	}
*/	


	
	/**
	 * reads directory from flash
	 */
	static { 
		fileTab = new File[MaxFiles];
		freeBlocks = new boolean[NumOfBlocks];
		usedBlocks = new boolean[NumOfBlocks];
		readDir();
	}
}

