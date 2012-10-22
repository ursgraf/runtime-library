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
