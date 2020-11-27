package org.deepjava.flink.core;

public interface FlinkBusInterface {
	
	/**
	 * 
	 * @return Memory size in byte
	 */
	public int getMemoryLength();
	
	/**
	 * 
	 * @param address of the register which should be read
	 * @return content of the register 
	 */
	public int read(int address);
	
	/**
	 * 
	 * @param address of the register which should be written
	 * @param data to write
	 */
	public void write (int address, int data);
	
	/**
	 * 
	 * @return True if device has Info device
	 */
	public boolean hasInfoDev();

}
