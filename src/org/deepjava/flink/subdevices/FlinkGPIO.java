package org.deepjava.flink.subdevices;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;

public class FlinkGPIO implements FlinkDefinitions {
	
	public FlinkSubDevice dev;
	private int valAddress;
	
	public FlinkGPIO(FlinkSubDevice dev) {
		this.dev = dev;
 		this.valAddress = ((dev.nofChannels-1) / REGISTER_WIDTH_BIT + 1) * REGISTER_WIDTH;
	}
	
	public void setDir(int channel, boolean output) {
		int val = dev.read((channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH);
		if (output) val = val | (1 << (channel % REGISTER_WIDTH_BIT));
		else val = val & ~(1 << (channel % REGISTER_WIDTH_BIT));
		dev.write((channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH, val);
	}
	
	public boolean getDir(int channel) {
		int val = dev.read((channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH);
		return (val & (1 << (channel % REGISTER_WIDTH_BIT))) != 0;
	}
	
	public boolean getValue(int channel) {
		int val = dev.read(valAddress + (channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH);
		return (val & (1 << (channel % REGISTER_WIDTH_BIT))) != 0;
	}
	
	public void setValue(int channel, boolean value) {
		int val = dev.read(valAddress + (channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH);
		if (value) val |= 1 << (channel % REGISTER_WIDTH_BIT);
		else val &= ~(1 << (channel % REGISTER_WIDTH_BIT));
		dev.write(valAddress + (channel / REGISTER_WIDTH_BIT) * REGISTER_WIDTH, val);
	}
}
