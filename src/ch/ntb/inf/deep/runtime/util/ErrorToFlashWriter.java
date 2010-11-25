package ch.ntb.inf.deep.runtime.util;

import ch.ntb.inf.deep.runtime.mpc555.driver.Flash;

/**
 *
 * Class to write error identifications <code>(shorts)</code> to the flash memory.
 * This i useful to write program errors to the flash memory which will cause an exception.
 * 
 * @author 18.12.2009 simon.pertschy@ntb.ch
 */
public class ErrorToFlashWriter {

	/**
	 * Standard flash sector to write the errors
	 */
	public static final int stdSector = 5;
	private static int sector;
	private static int writeOff = 0;
	
	
	/**
	 * Writes an error to the flash memory.
	 * @param id The error id.
	 */
	public static void write(short id){
		if(writeOff < Flash.SectorSize){
			Flash.programShort(sector, writeOff, id);
			writeOff+=2;
		}
	}
	
	/**
	 * Resets (erases) the error sector.
	 */
	public static void reset(){
		Flash.eraseSector(sector);
		writeOff = 0;
	}
	
	/**
	 * Reads an error from the flash. 
	 * @param ptr the position of the error.
	 * @return the error id.
	 */
	public static short get(int ptr){
		short val = -1;
		if(ptr >= 0 && ptr < avaiable())
			val = Flash.readShort(sector, ptr << 1);
		return val;
	}
	
	/**
	 * Initializes this class.
	 * If the sector is full, it will be erased.
	 * @param sector the sector of the flash on which the errors will be written.
	 */
	public static void init(int sector){
		ErrorToFlashWriter.sector = sector;
		writeOff = 0;
		for(int i = 0; i < Flash.SectorSize; i+=2){
			if(Flash.readShort(sector, i) == -1) return;
			writeOff+=2;
		}
		reset();
	}
	
	/**
	 * Initializes this class with the standard sector {@link #stdSector}.
	 * If the sector is full, it will be erased.
	 */
	public static void init(){
		sector = stdSector;
		writeOff = 0;
		for(int i = 0; i < Flash.SectorSize; i+=2){
			if(Flash.readShort(sector, i) == -1) return;
			writeOff+=2;
		}
		reset();
	}
	
	/**
	 * Returns the available errors.
	 * @return the available errors.
	 */
	public static int avaiable(){
		return writeOff  >> 1;
	}
	
}
