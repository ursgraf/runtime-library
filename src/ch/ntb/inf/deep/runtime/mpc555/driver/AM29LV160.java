package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.ntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/*
 * Changes: 
 * 03.05.2011 NTB/Urs Graf:	creation
 */
/**
 * Driver for AM29LV160 Flash, 2MB
 * Utilities for reading from and writing to flash AM29LV160.
 * Code of procedures are moved into ram space to be able to run out of flash.
 * Single bits can be written, make sure not to change a '0' to a '1'
 */

public class AM29LV160 implements ntbMpc555HB {

	static final int devAddr = extFlashBase; 	// external flash address, 16MB 
	
	//TODO move to ram space if run out of flash
	public static void programInt(int addr, int val) {	// blocking
		US.PUTSPR(EID, 0);
		int expData;
		expData = US.GET4(addr);
		US.PUT4(devAddr + 0x1554, 0x55005500);
		US.PUT4(devAddr + 0x0AA8, 0xAA00AA00);
		US.PUT4(devAddr + 0x1554, 0x05000500);
		int data = val & expData;
		US.PUT4(addr, data);
		do expData = US.GET4(addr); while (expData != data);
		US.PUTSPR(EIE, 0);
	}

	//TODO move to ram space if run out of flash
	public static void programShort(int addr, short val) {	// blocking
		US.PUTSPR(EID, 0);
		short expData;
		expData = US.GET2(addr);
		if ((addr & 3) == 0) { // address is multiple of 4
			US.PUT2(devAddr + 0x1554, 0x5500);
			US.PUT2(devAddr + 0x0AA8, 0xAA00);
			US.PUT2(devAddr + 0x1554, 0x0500);
		} else {
			US.PUT2(devAddr + 0x1556, 0x5500);
			US.PUT2(devAddr + 0x0AAA, 0xAA00);
			US.PUT2(devAddr + 0x1556, 0x0500);			
		}
		int data = val & expData;
		US.PUT2(addr, data);
		do expData = US.GET2(addr); while (expData != data);
		US.PUTSPR(EIE, 0);
	}

	//TODO move to ram space if run out of flash
	public static void eraseSector(int addr) {	// blocking
		US.PUTSPR(EID, 0);
		US.PUT4(devAddr + 0x1554, 0x55005500);
		US.PUT4(devAddr + 0x0AA8, 0xAA00AA00);
		US.PUT4(devAddr + 0x1554, 0x01000100);
		US.PUT4(devAddr + 0x1554, 0x55005500);
		US.PUT4(devAddr + 0x0AA8, 0xAA00AA00);
		US.PUT4(addr, 0x0C000C00);
		int data;
		do data = US.GET4(addr); while (data != -1);
		US.PUTSPR(EIE, 0);
	}
}
