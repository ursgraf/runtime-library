package org.deepjava.flink.subdevices;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;

/**
 * The flink info subdevice is used the deliver a description string 
 * for a flink device together with the total amount of used memory.
 * 
 * @author Urs Graf 
 */
public class FlinkInfo implements FlinkDefinitions {

	/** Handle to the subdevice within our flink device */
	public FlinkSubDevice dev;
	
	/**
	 * Creates a info subdevice.
	 * @param dev handle to the subdevice
	 */
	public FlinkInfo(FlinkSubDevice dev) {
		this.dev = dev;
	}
	
	/**
	 * Returns the total amount of memory mapped onto the AXI bus.
	 * This memory covers the memory blocks of all subdevices within a flink device.
	 * @return total memory size in bytes
	 */
	public int getMemLength() {
		return dev.read(0);
	}
	
	/**
	 * A info device holds a description string which can describe a flink device.
	 * @return description string
	 */
	public char[] getDescription() {
		int len = 28;
		char res[] = new char[len];
		for (int i = 0; i < len; i += 4) {
			int reg = dev.read(4 + i);
			res[i + 3] = (char) (reg & 0xff);
			res[i + 2] = (char) ((reg>>8) & 0xff);
			res[i + 1] = (char) ((reg>>16) & 0xff);
			res[i] = (char) ((reg>>24) & 0xff);
		}
		return res;
	}
}