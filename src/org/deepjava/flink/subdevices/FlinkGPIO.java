package org.deepjava.flink.subdevices;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;

/**
 * The flink GPIO subdevice realizes digital input and output within a flink device.
 * It offers several channels. Each channel drives a single pin.
 * 
 * @author Urs Graf 
 */
public class FlinkGPIO implements FlinkDefinitions {
	
	/** Handle to the subdevice within our flink device */
	public FlinkSubDevice dev;
	private int valAddress;
	
	/**
	 * Creates a GPIO subdevice.
	 * @param dev handle to the subdevice
	 */
	public FlinkGPIO(FlinkSubDevice dev) {
		this.dev = dev;
 		this.valAddress = ((dev.nofChannels-1) / REGISTER_WIDTH_BIT + 1) * REGISTER_WIDTH;
	}
	
	/**
	 * Sets the direction of a single channel within a GPIO subdevice. 
	 * Each channel can work as either digital input or output. Channel number
	 * must be 0 <= channel < nof available channels.
	 * @param channel channel number
	 * @param output false = input, true = output
	 */
	public void setDir(int channel, boolean output) {
		int val = dev.read((channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH);
		if (output) val = val | (1 << (channel % REGISTER_WIDTH_BIT));
		else val = val & ~(1 << (channel % REGISTER_WIDTH_BIT));
		dev.write((channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH, val);
	}
	
	/**
	 * Reads the direction of a single channel within a GPIO subdevice. 
	 * Each channel can work as either digital input or output. Channel number
	 * must be 0 <= channel < nof available channels
	 * @param channel channel number
	 * @return false = input, true = output
	 */
	public boolean getDir(int channel) {
		int val = dev.read((channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH);
		return (val & (1 << (channel % REGISTER_WIDTH_BIT))) != 0;
	}
	
	/**
	 * Reads the value of a single channel within a GPIO subdevice. 
	 * Channel number must be 0 <= channel < nof available channels
	 * @param channel channel number
	 * @return false = low, true = high
	 */
	public boolean getValue(int channel) {
		int val = dev.read(valAddress + (channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH);
		return (val & (1 << (channel % REGISTER_WIDTH_BIT))) != 0;
	}
	
	/**
	 * Sets the logical level of a single channel within a GPIO subdevice. 
	 * Channel number must be 0 <= channel < nof available channels
	 * @param channel channel number
	 * @param value false = low, true = high
	 */
	public void setValue(int channel, boolean value) {
		int val = dev.read(valAddress + (channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH);
		if (value) val |= 1 << (channel % REGISTER_WIDTH_BIT);
		else val &= ~(1 << (channel % REGISTER_WIDTH_BIT));
		dev.write(valAddress + (channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH, val);
	}
}
