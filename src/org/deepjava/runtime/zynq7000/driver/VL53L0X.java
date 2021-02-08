/*
 * Copyright 2011 - 2013 NTB University of Applied Sciences in Technology
 * Buchs, Switzerland, http://www.ntb.ch/inf
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.deepjava.runtime.zynq7000.driver;

import org.deepjava.runtime.zynq7000.IrqInterrupt;
import org.deepjava.runtime.zynq7000.Izynq7000;
import org.deepjava.runtime.zynq7000.microzed.Kernel;
import org.deepjava.unsafe.US;

/**
 * <p>Interrupt controlled driver for the <i>VL53L0X TOF Sensor</i> with
 * the <i>SPI1</i> of the Zynq7000.</p>
 * <p><b>Remember:</b><br>
 * The driver runs with a loop time of 1ms. The SPI interface is only accessible through the following pins: </p>
 * <ul>
 *  	<li>MOSI: MIO 10</li>
 *  	<li>MISO: MIO 11</li> 
 *  	<li>CLK : MIO 12</li> 
 *  	<li>CS  : MIO 13</li> 
 *	</ul>
 */
public class VL53L0X extends IrqInterrupt implements Izynq7000{
	
	private static final int MAX_SENSORS = 4;
	private static final byte dataEmpty[] = new byte[] {0,0,0,0,0,0,0,0};
	
	private int numSensors;
	private int writtenBytes = 0;
	private byte[] readBuffer = new byte[16];
	private int[] sensorValues;

	/**
	 * Initialize up to 4 VL53L0X time of flight sensors
	 * @param sensors number of sensors (1..4)
	 */
	public VL53L0X(int sensors) {
		numSensors = sensors;
		// check if the number of sensors is sensible
		if (numSensors > MAX_SENSORS) {
			System.err.println("Error, no more than 4 VL53L0X sensors supported!");
			numSensors = -1;
			return;
		}
		sensorValues = new int[numSensors];
		
		// Initialize the Hardware
		initSPI();
		
		// Reset the STM32 Microcontroller
		byte reset[] = new byte[] {0,1,0,0,0,0,0,0};
		write(reset);
		
		// Set the amount of Sensors used
		byte b[] = new byte[] {0,2,0,(byte)numSensors,0,0,0,0};

		long oldTime = Kernel.timeUs();
		while ((Kernel.timeUs() - oldTime) < 10000) {}; // wait for 10 ms
		write(b);
		
		// Initialize the SPI Interrupts
		IrqInterrupt.install(this, 81);
		// Write first bytes to trigger interrupt loop
		write(dataEmpty);
	}
	
	/**
	 * Task function handling the general state and operations of this driver
	 * Not to be called manually!
	 */
	public void action() {
		// Clear Interrupts
		US.PUT4(SPI1_SR, US.GET4(SPI1_SR));
		// Disable Interrupts
		US.PUT4(SPI1_IDR, 0x07);

		// Read the amount of written Bytes from the RX Buffer
		for (int i = 0; i < writtenBytes; i++) {
			if(i < readBuffer.length)
			{
				readBuffer[i] = US.GET1(SPI1_RXD);
			}
		}
		writtenBytes = 0;
		
		// Write Data again to get the new Values if initialization is already done		
		write(dataEmpty);
	}
	
	/**
	 * Write data into the TX Buffer.
	 * Data gets automatically sent by the SPI Controller
	 * @param data data
	 */
	private void write(byte[] data) {
		// Loop over all Data
		for (int i = 0; i < data.length; i++) {
			// Write Data to TX FIFO
			US.PUT1(SPI1_TXD, data[i]);
			writtenBytes++;
		}
		// Enable Interrupts
		US.PUT4(SPI1_IER, 0x07);
	}
	
	/**
	 * Read last measurement data
	 * @return distances in mm, the returned array has as many elements as sensors were requested in the constructor
	 */
	public int[] read() {	
		for (int i = 0; i < numSensors; i++)
		{
			sensorValues[i] = ((readBuffer[i*2] & 0xff) << 8) | (readBuffer[i*2+1] & 0xff);
			if (sensorValues[i] > 16000)
			{
				sensorValues[i] = -1;
			}
		}
		return sensorValues;
	}
	
	/**
	 * Initiate the SPI Clock and the SPI Controller. 
	 * Configure the pins as SPI pins.
	 */
	private static void initSPI() {
		// Unlock System Level Control Registers
		US.PUT4(SLCR_UNLOCK, 0xdf0d);
		// Reset SPI
		US.PUT4(SPI_RST_CTRL, 0x6);
		US.PUT4(SPI_RST_CTRL, 0x0);
		
		// Program the SPI_Ref_Clk
		US.PUT4(SPI_CLK_CTRL, 0x00003F02);
		
		// Define the MIO Pins for SPI
		// 10: SPI1 MOSI
		US.PUT4(MIO_PIN_10, 0x000026A0);
		// 11: SPI1 MISO
		US.PUT4(MIO_PIN_11, 0x000006A0);
		// 12: SPI1 CLK
		US.PUT4(MIO_PIN_12, 0x000026A0);
		// 13: SPI1 CS
		US.PUT4(MIO_PIN_13, 0x000036A0);
		
		// Lock System Level Control Registers
		US.PUT4(SLCR_LOCK, 0x767b);
		
		// Configure the SPI Controller
		US.PUT4(SPI1_CR, 0x0000003F);
		// Enable the SPI Controller
		US.PUT4(SPI1_ER, 0x00000001);
	}

}
