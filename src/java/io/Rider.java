package java.io;

import ch.ntb.inf.sts.mpc555.driver.Flash;

/*
	3.3.08 NTB/UG write corrected
*/

/**
 * Rider for reading from and writing to flash file system
 * writing can only happen if rider is at end of file, else no data is written
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
			i = this.offset / File.BlockSize;
			offset = this.offset + this.f.blocks[i] % File.BlocksPerSector * File.BlockSize;
			sector = this.f.blocks[i] / File.BlocksPerSector;
			x = Flash.readByte(sector, offset);
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
			if (this.offset % File.BlockSize == 0) {	// a new block must be allocated 
				i = File.BlocksPerSector;
				while ((i < File.NumOfBlocks) && !File.freeBlocks[i]) i++;
				if (i == File.NumOfBlocks) {this.res = 3; return;}	// no blocks 
				k = this.offset / File.BlockSize;
				if (k == File.MaxBlocksPerFile) {this.res = 4; return;}
				this.f.blocks[k] = i;
				File.usedBlocks[i] = true;	// allocated block is used 
				File.freeBlocks[i] = false;	// allocated block is no longer free
				Flash.programShort(0, 4 + i * 2, (short)0);	// write immediately
				if (this.offset == 0) {	// first block of file, set file addr 
					sector = this.f.blocks[0] / File.BlocksPerSector;
					this.f.addr = Flash.getSectorAddr(sector) + this.f.blocks[0] % File.BlocksPerSector * File.BlockSize;
				}
			}
			if (this.offset % 2 == 1) {
				i = this.offset / File.BlockSize;
				offset = this.offset - 1 + this.f.blocks[i] % File.BlocksPerSector * File.BlockSize;
				sector = this.f.blocks[i] / File.BlocksPerSector;
				data = (short)(x & 0xff);
				data += (short)((this.f.filler << 8));
				Flash.programShort(sector, offset, data);
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
