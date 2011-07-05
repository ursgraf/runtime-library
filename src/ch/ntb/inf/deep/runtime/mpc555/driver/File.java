package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.PrintStream;

/*
 * Changes: 
 * 03.05.2011 NTB/Urs Graf:	creation
 */
/**
 * File, which can be used by the flash file system (FFS)<br>
 */

public class File {

	boolean valid;	// indicates if file in directory is valid
	byte filler;	// filler for last byte as flash is 2byte organized
	public String name;	// name of file
	int addr;	// absolute address of first block of file in flash
	public int len;	// length of file
	public int blocks[];	// block numbers occupied by this file

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
		FFS.writeDir();
		return;
	} 

	/**
	 * deletes file in directory
	 */
	public void delete () {	// blocking
		if (this == null) return;
		this.valid = false;
		FFS.writeDir();
		return;
	} 

	/**
	 * creates new file
	 * if file with this name already exists, existing file is deleted first 
	 * returns null if maximum number of files is exceeded
	 */
	public File (String name) { 
		File f = FFS.old(name); 
		if (f != null) f.delete();
		int i = 0; f = FFS.fileTab[i];	// search for empty place in fileTab
		while ((i < FFS.maxFiles) && (f != null) && f.valid) {i++; f = FFS.fileTab[i];}
		if (i == FFS.maxFiles) return;
		this.name = name;
		this.valid = true;
		this.filler = 0;
		this.addr = 0;
		this.len = 0;
		this.blocks = new int[FFS.maxBlocksPerFile];
		for (int k = 0; k < FFS.maxBlocksPerFile; k++) this.blocks[k] = - 1;
		FFS.fileTab[i] = this;
	}


}
