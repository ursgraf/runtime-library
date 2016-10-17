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
	 * = 2, file does not exist
	 * = 3, no blocks left in file system
	 * = 4, max number of blocks for this file is exceeded
	 * = 5, writing not at end of file
	 */
	public int res;
	
	private File f;	// file associated with rider
	private int offset;	// position of rider
	
	/**
	 * sets rider at position
	 * if pos &lt; 0, sets position to 0
	 * if pos &gt; length of file, sets position to file.len 
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
	 * Returns position of rider.
	 * @return Position of rider in byte offset.
	 */
	public int pos () {	
		return this.offset;
	}
	
	/**
	 * Returns file associated with this rider.
	 * @return File where this rider is placed on.
	 */
	public File base () {	
		return this.f;
	}
	
	/**
	 * Reads byte from rider.
	 * @return Byte read.
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
	 * @return Boolean value.
	 */
	public boolean readBool () {	
		byte x = read();
		return (x == (byte)0xff)? true: false;
	}

	/**
	 * reads byte
	 * @return Byte value.
	 */
	public byte readByte () {	
		return read();
	}

	/**
	 * reads short
	 * @return Short value.
	 */
	public short readShort () {	
		int x = (read() << 8) + (read() & 0xff);
		return (short)x;
	}

	/**
	 * reads int
	 * @return Integer value.
	 */
	public int readInt () {	
		int x = (read() << 24) + ((read() << 16) & 0xff0000) + ((read() << 8) & 0xff00) + (read() & 0xff);
		return x;
	}

	/**
	 * Writes byte to file.
	 * @param x Byte to write.
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
	 * @param x Boolean value.
	 */
	public void writeBool (boolean x) {	
		if (x) write((byte)0xff); else write((byte)0);
	}

	/**
	 * writes byte
	 * @param x Byte value.
	 */
	public void writeByte (byte x) {	
		write(x); 
	}

	/**
	 * writes short
	 * @param x Short value.
	 */
	public void writeShort (short x) {	
		write((byte)(x >> 8)); write((byte)(x)); 
	}

	/**
	 * writes int
	 * @param x Integer value.
	 */
	public void writeInt (int x) {	
		write((byte)(x >> 24)); write((byte)(x >> 16)); write((byte)(x >> 8)); write((byte)(x)); 
	}
}
