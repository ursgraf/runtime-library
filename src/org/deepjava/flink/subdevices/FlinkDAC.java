package org.deepjava.flink.subdevices;

import org.deepjava.flink.core.FlinkDefinitions;
import org.deepjava.flink.core.FlinkSubDevice;

/**
 * The flink DAC subdevice realizes analog outputs in a flink device.
 * Its number of channels depend on the actual dac chip used. 
 * 
 * @author Urs Graf 
 */
public class FlinkDAC implements FlinkDefinitions {
	
	/** Handle to the subdevice within our flink device */
	public FlinkSubDevice dev;
	private static int RESOLUTION_ADDRESS = 0;
	private static int VALUE_0_ADDRESS = RESOLUTION_ADDRESS + REGISTER_WIDTH;
	private int resolution;
	private int bit_mask;
	
	/**
	 * Creates a DAC subdevice.
	 * @param dev handle to the subdevice
	 */
	public FlinkDAC(FlinkSubDevice dev){
		this.dev = dev;
		this.resolution = dev.read(RESOLUTION_ADDRESS);
		for(int i = 0;i<resolution;i++){
			bit_mask = bit_mask | (0x1<<i);
		}
		
	}
	
	/** 
	 * Reads the resolution field of the subdevice. The field denotes
	 * the number of resolvable steps, e.g. a 12 bit converter delivers 
	 * 4096 steps.
	 * @return number of resolvable steps
	 */
	public int getResolution(){
		return resolution;
	}
	
	/**
	 * Reads the digital value of a channel. Channel number must be 
	 * smaller than the total number of channels.
	 * @param channel channel number
	 * @return digital value
	 */
	public int getValue(int channel) {
		if (channel<dev.nofChannels) {
			return dev.read(VALUE_0_ADDRESS + channel * REGISTER_WIDTH);
		} else {
			return 0;
		}
	}
	
	/**
	 * Sets the digital value for a channel. Channel number must be 
	 * smaller than the total number of channels.
	 * @param channel channel number
	 * @param value digital output value
	 */
	public void setValue(int channel,int value) {
		if(channel < dev.nofChannels) {
			dev.write(VALUE_0_ADDRESS + channel * REGISTER_WIDTH , value & bit_mask);
		}
	}
	
	
}
