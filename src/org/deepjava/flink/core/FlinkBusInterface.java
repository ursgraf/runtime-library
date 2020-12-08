package org.deepjava.flink.core;

/**
 * This interface class provides for the basic functionality
 * each bus interface must implement.
 * 
 * @author Urs Graf 
 */
public interface FlinkBusInterface {
	
	/**
	 * A flink device occupies a contiguous block in the memory. 
	 * This method returns the size of the memory block.
	 * @return Memory size in byte
	 */
	public int getMemoryLength();
	
	/**
	 * Base method to read one register from a flink device.
	 * @param address of the register which should be read
	 * @return content of the register 
	 */
	public int read(int address);
	
	/**
	 * Base method to write one register of a flink device.
	 * @param address of the register which should be written
	 * @param data to write
	 */
	public void write (int address, int data);
	
	/**
	 * A flink device usually incorporates an info device. 
	 * However, this is not a precondition.
	 * @return True if device has Info device
	 */
	public boolean hasInfoDev();

}
