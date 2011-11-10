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

package ch.ntb.inf.deep.runtime.mpc555.driver;

/*
 * Changes: 
 * 03.05.2011 NTB/Urs Graf:	creation
 */
/**
 * Rider, to be placed onto a file<br>
 * multiple riders might be placed for reading and writing 
 */

public class Rider {
	/**
	 * end of file reached
	 */
	public boolean eof;
	
	/**
	 * result of last operation 
	 * = 0, operation successful
	 * = 1, end of file reached
	 * = 2, if file does not exist
	 * = 3, no blocks left in file system
	 * = 4, max number of blocks for this file is exceeded
	 * = 5, not writing to end of file
	 */
	public int res;
	
	File f;	// file associated with rider
	int offset;	// position of rider
	
	/**
	 * sets rider at position
	 * if pos < 0, sets position to 0
	 * if pos > length of file, sets position to file.len 
	 * 
	 * @param f
	 *            file
	 * @param pos
	 *            position (in number of bytes)
	 */
	public void set (File f, int pos) {	
		int offset;
		if (f != null) {
			if (pos > f.len) pos = f.len;
			else if (pos < 0) pos = 0;
			offset = pos;
		} else offset = 0;
		this.f = f; this.offset = offset; this.eof = false; this.res = 0;
	}
	
	/**
	 * returns position of rider
	 */
	public int pos () {	
		return this.offset;
	}
	
	/**
	 * returns file associated with this rider
	 */
	public File base () {	
		return this.f;
	}
	
	/**
	 * reads byte from rider
	 */
	private byte read () {	
		int offset, sector, i; byte x = 0;
		if (this.f == null) {this.res = 2; return x;}
		if (this.offset < this.f.len) {
			i = this.offset / FFS.blockSize;
			offset = this.offset + this.f.blocks[i] % FFS.BlocksPerSector * FFS.blockSize;
			sector = this.f.blocks[i] / FFS.BlocksPerSector;
			x = FFS.readByte(sector, offset);
			this.offset++;
			this.res = 0; this.eof = false;
		} else {	// end of file 
			x = 0; this.eof = true; this.res = 1;
		}
		return x;
	}

	/**
	 * reads boolean
	 */
	public boolean readBool () {	
		byte x = read();
		return (x == (byte)0xff)? true: false;
	}

	/**
	 * reads byte
	 */
	public byte readByte () {	
		return read();
	}

	/**
	 * reads short
	 */
	public short readShort () {	
		int x = (read() << 8) + (read() & 0xff);
		return (short)x;
	}

	/**
	 * reads int
	 */
	public int readInt () {	
		int x = (read() << 24) + ((read() << 16) & 0xff0000) + ((read() << 8) & 0xff00) + (read() & 0xff);
		return x;
	}

	/**
	 * writes byte to file 
	 */
	private void write (byte x) {	
		int offset, sector, i, k; short data;
		if (this.f == null) {this.res = 2; return;}
		if (this.offset == this.f.len) {	// append at end of file
			if (this.offset % FFS.blockSize == 0) {	// a new block must be allocated 
				i = FFS.BlocksPerSector;
				while ((i < FFS.numOfBlocks) && !FFS.freeBlocks[i]) i++;
				if (i == FFS.numOfBlocks) {this.res = 3; return;}	// no blocks 
				k = this.offset / FFS.blockSize;
				if (k == FFS.maxBlocksPerFile) {this.res = 4; return;}
				this.f.blocks[k] = i;
				FFS.usedBlocks[i] = true;	// allocated block is used 
				FFS.freeBlocks[i] = false;	// allocated block is no longer free
				FFS.programShort(0, 4 + i * 2, (short)0);	// write immediately
				if (this.offset == 0) {	// first block of file, set file addr 
					sector = this.f.blocks[0] / FFS.BlocksPerSector;
					this.f.addr = FFS.getSectorAddr(sector) + this.f.blocks[0] % FFS.BlocksPerSector * FFS.blockSize;
				}
			}
			if (this.offset % 2 == 1) {
				i = this.offset / FFS.blockSize;
				offset = this.offset - 1 + this.f.blocks[i] % FFS.BlocksPerSector * FFS.blockSize;
				sector = this.f.blocks[i] / FFS.BlocksPerSector;
				data = (short)(x & 0xff);
				data += (short)((this.f.filler << 8));
				FFS.programShort(sector, offset, data);
			} else {this.f.filler = x;}
			this.f.len++; this.offset++; this.res = 0;
		} else {this.res = 5; return;}	// not at end of file 
	} 

	/**
	 * writes boolean
	 */
	public void writeBool (boolean x) {	
		if (x) write((byte)0xff); else write((byte)0);
	}

	/**
	 * writes byte
	 */
	public void writeByte (byte x) {	
		write(x); 
	}

	/**
	 * writes short
	 */
	public void writeShort (short x) {	
		write((byte)(x >> 8)); write((byte)(x)); 
	}

	/**
	 * writes int
	 */
	public void writeInt (int x) {	
		write((byte)(x >> 24)); write((byte)(x >> 16)); write((byte)(x >> 8)); write((byte)(x)); 
	}
}
