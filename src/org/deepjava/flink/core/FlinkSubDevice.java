package org.deepjava.flink.core;

/**
 * A flink subdevice realizes a special function in a {@link org.deepjava.flink.core FlinkDevice}.
 * 
 * @author Urs Graf 
 */
public class FlinkSubDevice implements FlinkDefinitions {
	public FlinkSubDevice next;
	public int function;
	public int subType; 
	public int version; 
	public int memSize;
	public int nofChannels;
	public int baseAddress;
	public int uniqueID;
	public int id;
	public FlinkBusInterface busInterface; 
	
	/**
	 * Base method to read one register from a flink device.
	 * @param address of the register which should be read
	 * @return content of the register 
	 */
	public int read(int address) {
		return busInterface.read(this.baseAddress + TOTAL_HEADER_SIZE + address);
	}
	
	/**
	 * Base method to write one register of a flink device.
	 * @param address of the register which should be written
	 * @param data to write
	 */
	public void write(int address, int data ) {
		busInterface.write(this.baseAddress + TOTAL_HEADER_SIZE + address, data);
	}
	
	/**
	 * Returns the configuration register of a flink subdevice.
	 * @return content of configuration register
	 */
	public int getConfigReg() {
		return busInterface.read(this.baseAddress + MOD_CONF_OFFSET);
	}
	
	/**
	 * Returns the status register of a flink subdevice.
	 * @return content of status register
	 */
	public int getStatusReg() {
		return busInterface.read(this.baseAddress + MOD_STATUS_OFFSET);
	}

	/**
	 * Writes the configuration register of a flink subdevice.
	 * @param confReg content of configuration register
	 */
	public void setConfigReg(int confReg) {
		busInterface.write(this.baseAddress + MOD_CONF_OFFSET, confReg);
	}
}
