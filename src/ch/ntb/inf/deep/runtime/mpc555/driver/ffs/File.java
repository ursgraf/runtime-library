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


/*
 * Changes: 
 * 03.05.2011 NTB/Urs Graf:	creation
 */

/**
 * File, which can be used by the flash file system (FFS)<br>
 */
public class File {

	public String name;	// name of file
	public int len;	// length of file
	byte filler;	// filler for last byte as flash is 2byte organized
	int addr;	// absolute address of first block of file in flash
	int blocks[];	// block numbers occupied by this file
	boolean valid;	// indicates if file in directory is valid

	/**
	 * Returns address of first block of file.
	 * @return Address of file.
	 */
	public int address () {
		return this.addr;
	} 

	/**
	 * Returns length of file in bytes.
	 * @return Length in bytes.
	 */
	public int length () {
		return this.len;
	} 

	/**
	 * Writes file length into directory.
	 */
	public void register () {	// blocking
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
	 * Deletes file in directory.
	 */
	public void delete () {	// blocking
		this.valid = false;
		FFS.writeDir();
		return;
	} 

	/**
	 * Creates new file,
	 * if file with this name already exists, existing file is deleted first 
	 * returns null if maximum number of files is exceeded
	 * @param name Name of the file
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
