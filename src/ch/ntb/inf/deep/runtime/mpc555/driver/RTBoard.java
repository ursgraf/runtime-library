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

package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.IntbMpc555HB;
import ch.ntb.inf.deep.unsafe.US;

/**
 * Driver for the mpc555 board for control applications.
 * This board comprises two regular analog outputs and two analog outputs with
 * 1A current supply. Further there are 4 analog input channels as well as 
 * 8 digital in/output channels.
 * 
 * @author Graf Urs
 */

/* Changes:
 * 01.05.2011 Urs Graf: Fehler in DACinit korrigiert
 * 18.02.2010 M. Zueger: Treiber fuer DAC und ADC angepasst und direkt integriert
 */

public class RTBoard implements IntbMpc555HB {
  
  private static final int ADDR_OFFSET = 64;
  private static final int CCW_INIT = 0x0000;
  private static final int END_OF_QUEUE = 0x003F;
  private static TPU_DIO[] led, dio;
  private static TPU_FQD[] fqd;

  /**
   * Returns the value of an analog input channel.<br>
   * The analog signal will be read from <code>channel</code>.
   * The channels carry the names <code>A-In0..3 bezeichnet</code>. The range 
   * of the return value is between -10..+10 corresponding to Volts.
   * The resolution of the ADC is 10 bit.
   * 
   * @param channel
   *            Channel with analog signal.
   * @return Value in Volts (-10..+10).
   */
  public static float analogIn(int channel) {
    return ((US.GET2(RJURR_A + ADDR_OFFSET + channel * 2)) - 511.5f) / 511.5f * 10f;
  }

  /**
   * Writes a value to an regular analog output <code>channel</code>
   * The channels are denoted with <code>A-Out0</code> and <code>A-Out1</code>. 
   * The range of <code>val</code> is between -10..+10 corresponding to Volts
   * The resolution of the DAC is 12 bit.
   * 
   * @param channel
   *            Channel with analog signal.
   * @param val
   *            Value in Volts (-10..+10).
   */
  public static void analogOut(int channel, float val) {
    US.PUT2(TRANRAM + 2 * channel, (channel % 4) * 0x4000 + ((int)(val / 10 * 2047.5f + 2047.5f) & 0xfff));
}

	/**
	 * Writes a value to an analog output <code>channel</code> with 1A current drive capability.
	 * The channels are denoted with <code>Power-Out0</code> and <code>Power-Out1</code>. 
	 * The range of <code>val</code> is between -10..+10 corresponding to Volts
	 * The resolution of the DAC is 12 bit.
	 * 
	 * @param channel
	 *            Channel with analog signal.
	 * @param val
	 *            Value in Volts (-10..+10).
	 */
  public static void analogPowerOut(int channel, float val) {
	  channel += 2;
	  US.PUT2(TRANRAM + 2 * channel, (channel % 4) * 0x4000 + ((int)(val / 10 * 2047.5f + 2047.5f) & 0xfff));
  }


	/**
	 * Initializes a digital <code>channel</code> as input or output. Channels are numbered 0..7.
	 * 
	 * @param channel
	 *            Channel to be initialized. 
	 * @param out
	 *            If <code>true</code> the channel will be an output, otherwise it will be an input.
	 */
	public static void dioInit(int channel, boolean out) {
		if (channel >= 0 && channel < 8) dio[channel] = new TPU_DIO(true, channel, out);
	}


	/**
	 * The digital input at <code>channel</code> is read. Channels are numbered
	 * 0..7. The value <code>true</code> corresponds to the logical signal <code>1</code>.
	 * 
	 * @param channel
	 *            Channel to be read.
	 * @return Digital signal at <code>channel</code>.
	 */
	public static boolean dioIn(int channel) {
		return dio[channel].get();
	}


	/**
	 * Write a digital output to <code>channel</code>.
	 * Channels are numbered <code>0..7</code>.
	 * 
	 * @param channel
	 *            Channel to write.
	 * @param level
	 *            Digital signal, <code>true</code> corresponds to the logical signal <code>1</code>.
	 */
	public static void dioOut(int channel, boolean level) {
		dio[channel].set(level);
	}


	/**
	 * Write a digital output to a led.
	 * leds are numbered <code>0..3</code>.
	 * 
	 * @param channel
	 *            Led channel.
	 * @param level
	 *            <code>true</code> corresponds to the led lightening up.
	 */
	public static void ledOut(int channel, boolean level) {
		led[channel].set(!level);
	}


	/**
	 * Initializes two digital input channels as encoder input.<br>
	 * <code>channel</code> can be in the range of <code>0..3</code>.
	 * <br>
	 * <b>Important:</b><br>
	 * As two digital inputs are necessary for a single encoder input,
	 * <code>channel+1</code> will be reserved and used as well.
	 * 
	 * @param channel
	 *            <code>channel</code> and <code>channel+1</code> will be used for encoder input signals.
	 */
	public static void encInit(int channel) {
		fqd[channel] = new TPU_FQD(true, channel);
		fqd[channel].setPosition(0);
	}


	/**
	 * Reads the encoder position.<br>
	 * 
	 * @param channel Channel of encoder input.
	 * @return Position.
	 */
	public static short getEncCount(int channel) {
		return fqd[channel].getPosition();
	}


	/**
	 * Set the encoder position.<br>
	 * 
	 * @param channel Channel of encoder input.
	 * @param pos Position to initialize encoder.
	 */
	public static void setEncCount(int channel, short pos) {
		fqd[channel].setPosition(pos);
	}


	private static void initDAC() {
		US.PUT2(SPCR1, 0x0);     //disable QSPI 
		US.PUT1(PQSPAR, 0x013);  // use PCS1, MOSI, MISO for QSPI 
		US.PUT1(DDRQS, 0x016);   //SCK, MOSI, PCS1 output; MISO is input 
		US.PUT2(PORTQS, 0x0FF);  //all Pins, in case QSPI disabled, are high 
		US.PUT2(SPCR0, 0x08302); // QSPI is master, 16 bits per transfer, inactive state of SCLK is high (CPOL=1), data changed on leading edge (CPHA=1), clock = 10MHz 
		US.PUT2(SPCR2, 0x4300);  // no interrupts, wraparound mode, NEWQP=0, ENDQP=03 
		for(int i=0; i<4; i++) US.PUT1(COMDRAM + i, 0x6D); //disable chip select after transfer, use bits in SPCR0, use PCS1 
		for(int i=0; i<4; i++) US.PUT2(TRANRAM + 2 * i, i * 0x4000 + 2048);
		US.PUT2(SPCR1, 0x08010);	//enable QSPI, delay 13us after transfer
	}


  private static void initADC() {
    // user access
    US.PUT2(QADC64MCR_A, 0);
    
    // internal multiplexing, use ETRIG1 for queue1, QCLK = 40 MHz / (11+1 + 7+1) = 2 MHz
    US.PUT2(QACR0_A, 0x00B7);
    
    // queue2:
    // Software triggered continuous-scan mode
    // Resume execution with the aborted CCW
    // queue2 begins at CCW + 2*32 = 64 ( = ADDR_OFFSET)
    US.PUT2(QACR2_A, 0x31A0);
    
    // CCW for AN48 - AN59, max sample time
    // ADDR_OFFSET: Using queue2
    for (int i = 52; i <= 58; i += 2) {
    	US.PUT2(CCW_A + ADDR_OFFSET + (i-52), CCW_INIT + i);
    }
    
    // end of queue
    US.PUT2(CCW_A + ADDR_OFFSET + 4 * 2, END_OF_QUEUE);
  }


  static {
	  /* 1) Initialize DAC */
	  initDAC();

	  /* 2) Initialize digital I/Os */
	  led = new TPU_DIO[4];
	  for (int i = 0; i < 4; i++) led[i] = new TPU_DIO(false, i * 2 + 1, true);
	  dio = new TPU_DIO[8];
	  fqd = new TPU_FQD[8];

	  /* 3) Initialize ADC */
	  initADC();
  }
}
