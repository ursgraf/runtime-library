package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.unsafe.HWD;

/*changes:
 * 14.04.09 NTB/SP Method desiredPosition removed
 * 24.02.09 NTB/SP TPU_A position failure corrected
 * 06.03.08 NTB/SP assigned to java
 * 18.05.06	NTB/HS	stub creation
 */
/**
 * Schrittmotoransteuerung mit der TPU-A oder TPU-B.<br>
 * Der Treiber muss zuerst über die Methode <code>init(...)</code>
 * initialisiert werden.<br>
 * Durch den Parameter <code>tpuA</code> wird bestimmt ob die TPU-A (<code>true</code>)
 * oder die TPU-B (<code>false</code>) benutzt wird.<br>
 * Über den Parameter <code>fullStep</code> wird die Betriebsart bestimmt. Der
 * Schrittmotor kann in zwei verschiedenen Arten betrieben werden: Fullstep- (<code>true</code>)
 * und Halfstep-Modus (<code>false</code>). Für den Betrieb im
 * Fullstep-Modus werden 2 TPU-Pins für Halfstep 4 TPU-Pins benötigt.<br>
 * Mittels dem Parameter <code>stepPeriod</code> wird die maximale
 * Schrittgeschwindigkeit in \u00B5s übergeben. Der Motor wird über eine Rampe
 * beschleunigt. Die Startgeschwindigkeit beträgt <sup>1</sup>/<sub>4</sub>
 * der maximalen Schrittgeschwindigkeit.<br>
 * Der Parameter <code>channel</code> bestimmt den TPU-Startpin.<br>
 * Für den Fullstep-Modus werden die Pins Startpin, Startpin+1 verwendet.<br>
 * Für den Halfstep-Modus werden die Kanäle Startpin, Startpin+1, Startpin+2,
 * Startpin+3 verwendet.<br>
 * Dementsprechend darf der Startpin im Fullstep-Modus im Bereich <em>0 <= Startpin <=
 * 14</em> und im Haltstep-Modus im Bereich <em>0 <= Startpin <= 12</em> gewählt werden.
 * 
 */
public class StepMotor {


	/**
	 * Initialisiert den Schrittmotor-Treiber.<br>
	 * Entsprechend der Klassenbeschreibung kann die gewünschte TPU, der
	 * Betriebsmodus, die maximale Schrittgeschwindigkeit (stepPeriod <= 6553)
	 * und der TPU-Kanal gewählt werden.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param fullStep
	 *            <code>true</code>: Fullstep-Modus. <code>false</code>:
	 *            Halfstep-Modus.
	 * @param stepPeriod
	 *            maximale Schrittgeschwindigkeit (stepPeriod <= 6553)
	 * @param channel
	 *            TPU-Startpin (Bereich ist vom Betriebsmodus abhängig), welcher
	 *            initialisiert wird.
	 */
	public static void init(boolean tpuA, boolean fullStep, int stepPeriod,
			int channel) {
		int shiftl, reg;
		short s;
		if(tpuA){
			int stepPeriodTCR1 = stepPeriod * 1000 / TPU_A.getCycleTime();
			
			//Disable interrupts for first channel
			s = HWD.GET2(TPU_A.CIER);
			s &= ~(1 << channel);
			HWD.PUT2(TPU_A.CIER, s);
			
			//Disable the TPU channels by clearing the priority bits
			//first channel
			shiftl = (channel % 8) * 2;
			reg = TPU_A.CPR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			HWD.PUT2(reg, s);
			//second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = TPU_A.CPR1 - ((channel + 1) / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			HWD.PUT2(reg, s);
			if(!fullStep){
				//second channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_A.CPR1 - ((channel + 2) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg, s);
				//second channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_A.CPR1 - ((channel + 3) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg, s);
			}
			
			//function code (D) for Stepper
			//first channel
			shiftl = ((channel % 4) * 4);
			reg = TPU_A.CFSR3 - (channel / 4) * 2;
			s = HWD.GET2(reg);
			s &= ~(0xF << shiftl);
			s |= (0xD << shiftl);
			HWD.PUT2(reg,s);
			//second channel
			shiftl = (((channel + 1) % 4) * 4);
			reg = TPU_A.CFSR3 - ((channel + 1) / 4) * 2;
			s = HWD.GET2(reg);
			s &= ~(0xF << shiftl);
			s |= (0xD << shiftl);
			HWD.PUT2(reg,s);
			if(!fullStep){
				//third channel
				shiftl = (((channel + 2) % 4) * 4);
				reg = TPU_A.CFSR3 - ((channel + 2) / 4) * 2;
				s = HWD.GET2(reg);
				s &= ~(0xF << shiftl);
				s |= (0xD << shiftl);
				HWD.PUT2(reg,s);
				//fourth channel
				shiftl = (((channel + 3) % 4) * 4);
				reg = TPU_A.CFSR3 - ((channel + 3) / 4) * 2;
				s = HWD.GET2(reg);
				s &= ~(0xF << shiftl);
				s |= (0xD << shiftl);
				HWD.PUT2(reg,s);
			}
			
			//Acceleration table
//			SYS.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1), 0x1902);
//			SYS.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 2, 0x3047);
//			SYS.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 4, 0x755E);
//			SYS.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 6, 0xA38C);
//			SYS.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 8, 0xD1BA);
//			SYS.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 0xA, 0xFFE8);
			
//			Acceleration table
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1), 240 * 0x100 + 250);
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 2, 205 * 0x100 + 225);
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 4, 165 * 0x100 + 185);
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 6, 125 * 0x100 + 145);
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 8, 90 * 0x100 + 105);
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * (channel + 1) + 0xA, 70 * 0x100 + 80);
			
			//Desired Position
//			SYS.PUT2(TPU_A.TPURAM0 + 0x10 * channel, 0x0);
			
			//Current position, first channel
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel + 2,0x0);
			s = HWD.GET2(TPU_A.TPURAM0 + 0x10 * channel + 2);
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel, s);
			
			//Acceleration table size (12) and initializing table index
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel + 4, 0xC00);
			//Slew period and initializeng bit s = 0 (bit 0 = 0)
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel + 6, stepPeriodTCR1 *2);
			if(fullStep){
				//Start period and bit a= 0 => two channel mode (bit 0 = 0)
				HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel + 8,4 * stepPeriodTCR1 * 2);
				
				//Pin sequence
				HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel + 0xA, 0x9999);
				
				//Operating mode, only first channel
				//Local table mode (no spare TPU channels), rotate pin sequence once => %00
				shiftl = (channel % 8) * 2;
				reg = TPU_A.HSQR1 - (channel / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg, s);
				
				//Request initialization (Host Service Request) both channels
				//For pin pattern 0x9999, first channel = %10 (pin high), second channel = %10 (pin high)
				//first channel
				shiftl = (channel % 8) * 2;
				reg = TPU_A.HSRR1 - (channel / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
				//second channel
				shiftl = ((channel + 1) % 8) * 2;
				reg = TPU_A.HSRR1 - ((channel + 1) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
				

			}
			else{
				//Start period and bit a = 1 => four channel mode (bit 0 = 1
				HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel + 8,4 * stepPeriodTCR1 * 2 + 1);
				
				//Pin sequence
				HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel + 0xA, 0xE0E0);
				
				//Operating mode, only first channel
				//Local table mode (no spare TPU channels), rotate pin sequence twice => %10
				shiftl = (channel % 8) * 2;
				reg = TPU_A.HSQR1 - (channel / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
				
				//Request initialization (Host Service Request) both channels
				//For pin pattern 0x0E0E0, first channel = %10 (pin high), second channel = %01 (pin low)
				//third channel = %01 (pin low) fourth channel = %10 (pin high)
				//first channel
				shiftl = (channel % 8) * 2;
				reg = TPU_A.HSRR1 - (channel / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
				//second channel
				shiftl = ((channel + 1) % 8) * 2;
				reg = TPU_A.HSRR1 - ((channel + 1) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg, s);
				//third channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_A.HSRR1 - ((channel + 2) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg, s);
				//fourth channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_A.HSRR1 - ((channel + 3) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
			}
			
			//Set priority low
			//First channel
			shiftl = (channel % 8) * 2;
			reg = TPU_A.CPR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			HWD.PUT2(reg, s);
			//Second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = TPU_A.CPR1 - ((channel + 1)  / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			HWD.PUT2(reg, s);
			
			if(!fullStep){
				//Third channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_A.CPR1 - ((channel + 2)  / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg, s);
				//Fourth channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_A.CPR1 - ((channel + 3)  / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg, s);
			}
		}else{
			int stepPeriodTCR1 = stepPeriod * 1000 / TPU_A.getCycleTime();
			
			//Disable interrupts for first channel
			s = HWD.GET2(TPU_B.CIER);
			s &= ~(1 << channel);
			HWD.PUT2(TPU_B.CIER, s);
			
			//Disable the TPU channels by clearing the priority bits
			//first channel
			shiftl = (channel % 8) * 2;
			reg = TPU_B.CPR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			HWD.PUT2(reg, s);
			//second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = TPU_B.CPR1 - ((channel + 1) / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			HWD.PUT2(reg, s);
			if(!fullStep){
				//second channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_B.CPR1 - ((channel + 2) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg, s);
				//second channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_B.CPR1 - ((channel + 3) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg, s);
			}
			
			//function code (D) for Stepper
			//first channel
			shiftl = ((channel % 4) * 4);
			reg = TPU_B.CFSR3 - (channel / 4) * 2;
			s = HWD.GET2(reg);
			s &= ~(0xF << shiftl);
			s |= (0xD << shiftl);
			HWD.PUT2(reg,s);
			//second channel
			shiftl = (((channel + 1) % 4) * 4);
			reg = TPU_B.CFSR3 - ((channel + 1) / 4) * 2;
			s = HWD.GET2(reg);
			s &= ~(0xF << shiftl);
			s |= (0xD << shiftl);
			HWD.PUT2(reg,s);
			if(!fullStep){
				//third channel
				shiftl = (((channel + 2) % 4) * 4);
				reg = TPU_B.CFSR3 - ((channel + 2) / 4) * 2;
				s = HWD.GET2(reg);
				s &= ~(0xF << shiftl);
				s |= (0xD << shiftl);
				HWD.PUT2(reg,s);
				//fourth channel
				shiftl = (((channel + 3) % 4) * 4);
				reg = TPU_B.CFSR3 - ((channel + 3) / 4) * 2;
				s = HWD.GET2(reg);
				s &= ~(0xF << shiftl);
				s |= (0xD << shiftl);
				HWD.PUT2(reg,s);
			}
			
			//Acceleration table
//			SYS.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1), 0x1902);
//			SYS.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 2, 0x3047);
//			SYS.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 4, 0x755E);
//			SYS.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 6, 0xA38C);
//			SYS.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 8, 0xD1BA);
//			SYS.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 0xA, 0xFFE8);
			
//			Acceleration table
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1), 240 * 0x100 + 250);
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 2, 205 * 0x100 + 225);
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 4, 165 * 0x100 + 185);
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 6, 125 * 0x100 + 145);
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 8, 90 * 0x100 + 105);
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * (channel + 1) + 0xA, 70 * 0x100 + 80);
			
			//Desired Position
//			SYS.PUT2(TPU_B.TPURAM0 + 0x10 * channel, 0x0);
			
			//Current position, first channel
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel + 2,0x0);
			s = HWD.GET2(TPU_B.TPURAM0 + 0x10 * channel + 2);
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel, s);
			
			//Acceleration table size (12) and initializing table index
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel + 4, 0xC00);
			//Slew period and initializeng bit s = 0 (bit 0 = 0)
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel + 6, stepPeriodTCR1 *2);
			if(fullStep){
				//Start period and bit a= 0 => two channel mode (bit 0 = 0)
				HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel + 8,4 * stepPeriodTCR1 * 2);
				
				//Pin sequence
				HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel + 0xA, 0x9999);
				
				//Operating mode, only first channel
				//Local table mode (no spare TPU channels), rotate pin sequence once => %00
				shiftl = (channel % 8) * 2;
				reg = TPU_B.HSQR1 - (channel / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg, s);
				
				//Request initialization (Host Service Request) both channels
				//For pin pattern 0x9999, first channel = %10 (pin high), second channel = %10 (pin high)
				//first channel
				shiftl = (channel % 8) * 2;
				reg = TPU_B.HSRR1 - (channel / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
				//second channel
				shiftl = ((channel + 1) % 8) * 2;
				reg = TPU_B.HSRR1 - ((channel + 1) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
				

			}
			else{
				//Start period and bit a = 1 => four channel mode (bit 0 = 1
				HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel + 8,4 * stepPeriodTCR1 * 2 + 1);
				
				//Pin sequence
				HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel + 0xA, 0xE0E0);
				
				//Operating mode, only first channel
				//Local table mode (no spare TPU channels), rotate pin sequence twice => %10
				shiftl = (channel % 8) * 2;
				reg = TPU_B.HSQR1 - (channel / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
				
				//Request initialization (Host Service Request) both channels
				//For pin pattern 0x0E0E0, first channel = %10 (pin high), second channel = %01 (pin low)
				//third channel = %01 (pin low) fourth channel = %10 (pin high)
				//first channel
				shiftl = (channel % 8) * 2;
				reg = TPU_B.HSRR1 - (channel / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
				//second channel
				shiftl = ((channel + 1) % 8) * 2;
				reg = TPU_B.HSRR1 - ((channel + 1) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg, s);
				//third channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_B.HSRR1 - ((channel + 2) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg, s);
				//fourth channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_B.HSRR1 - ((channel + 3) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (2 << shiftl);
				HWD.PUT2(reg, s);
			}
			
			//Set priority low
			//First channel
			shiftl = (channel % 8) * 2;
			reg = TPU_B.CPR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			HWD.PUT2(reg, s);
			//Second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = TPU_B.CPR1 - ((channel + 1)  / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			HWD.PUT2(reg, s);
			
			if(!fullStep){
				//Third channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_B.CPR1 - ((channel + 2)  / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg, s);
				//Fourth channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_B.CPR1 - ((channel + 3)  / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg, s);
			}
		}
	}

	/**
	 * Bewegt den Motor um die in <code>steps</code> angegebenen Schritte.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param steps
	 *            Anzahl Schritte, um welche sich der Motor bewegen soll.
	 *            Mittels einem negaitven Wert lässt sich der Motor in die
	 *            Gegenrichtung bewegen.
	 * @param channel
	 *            TPU-Startpin.
	 */
	public static void move(boolean tpuA, int steps, int channel) {
		int shiftl, reg;
		short s;
		if(tpuA){
			//Get current position
			short pos = HWD.GET2(TPU_A.TPURAM0 + 0x10 * channel + 2);
			//Set disired position
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel,(short) pos + steps);
			
			//Move Request (Host Service Request) (Master only)
			shiftl = (channel % 8) * 2;
			reg = TPU_A.HSRR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s |= (3 << shiftl);
			HWD.PUT2(reg,s);
		}else{
			//Get current position
			short pos = HWD.GET2(TPU_B.TPURAM0 + 0x10 * channel + 2);
			//Set disired position
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel,(short) pos + steps);
			
			//Move Request (Host Service Request) (Master only)
			shiftl = (channel % 8) * 2;
			reg = TPU_B.HSRR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s |= (3 << shiftl);
			HWD.PUT2(reg,s);
		}
	}

	/**
	 * Startet den Schrittmotor.<br>
	 * Diese Methode wird nur benötigt, wenn der Motor vorgängig mittels der
	 * Methode <code>stop(...)</code> gestoppt wurde.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param fullStep
	 *            <code>true</code>: Fullstep-Modus. <code>false</code>:
	 *            Halfstep-Modus.
	 * @param channel
	 *            TPU-Startpin.
	 */
	public static void start(boolean tpuA, boolean fullStep, int channel) {
		if(tpuA){
			int shiftl,reg;
			short s;
			//Set priority low
			//first channel
			shiftl = (channel % 8) * 2;
			reg = TPU_A.CPR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			HWD.PUT2(reg, s);
			//second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = TPU_A.CPR1 - ((channel + 1) / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			HWD.PUT2(reg,s);
			if(!fullStep){
				//Third channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_A.CPR1 - ((channel + 2) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg,s);
				//fourth channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_A.CPR1 - ((channel + 3) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg,s);
			}
		}else{
			int shiftl,reg;
			short s;
			//Set priority low
			//first channel
			shiftl = (channel % 8) * 2;
			reg = TPU_B.CPR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			HWD.PUT2(reg, s);
			//second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = TPU_B.CPR1 - ((channel + 1) / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			s |= (1 << shiftl);
			HWD.PUT2(reg,s);
			if(!fullStep){
				//Third channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_B.CPR1 - ((channel + 2) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg,s);
				//fourth channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_B.CPR1 - ((channel + 3) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				s |= (1 << shiftl);
				HWD.PUT2(reg,s);
			}
		}
	}

	/**
	 * Stoppt den Schrittmotor.<br>
	 * Der Motor kann anschliessend über die Methode <code>start(...)</code>
	 * wieder gestartet werden.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param fullStep
	 *            <code>true</code>: Fullstep-Modus. <code>false</code>:
	 *            Halfstep-Modus.
	 * @param channel
	 *            TPU-Startpin.
	 */
	public static void stop(boolean tpuA, boolean fullStep, int channel) {
		if(tpuA){
			int shiftl, reg;
			short s;
			//Set priority null
			//first channel
			shiftl = (channel % 8) * 2;
			reg = TPU_A.CPR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			HWD.PUT2(reg,s);
			//second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = TPU_A.CPR1 - ((channel + 1) / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			HWD.PUT2(reg,s);
			if(!fullStep){
				//third channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_A.CPR1 - ((channel + 2) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg,s);
				//fourth channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_A.CPR1 - ((channel + 3) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg,s);
			}
		}else{
			int shiftl, reg;
			short s;
			//Set priority null
			//first channel
			shiftl = (channel % 8) * 2;
			reg = TPU_B.CPR1 - (channel / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			HWD.PUT2(reg,s);
			//second channel
			shiftl = ((channel + 1) % 8) * 2;
			reg = TPU_B.CPR1 - ((channel + 1) / 8) * 2;
			s = HWD.GET2(reg);
			s &= ~(3 << shiftl);
			HWD.PUT2(reg,s);
			if(!fullStep){
				//third channel
				shiftl = ((channel + 2) % 8) * 2;
				reg = TPU_B.CPR1 - ((channel + 2) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg,s);
				//fourth channel
				shiftl = ((channel + 3) % 8) * 2;
				reg = TPU_B.CPR1 - ((channel + 3) / 8) * 2;
				s = HWD.GET2(reg);
				s &= ~(3 << shiftl);
				HWD.PUT2(reg,s);
			}
			
		}
	}

	/**
	 * Gibt zurück, ob der Motor die angesteuerte Position bereits erreicht hat.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Startpin.
	 * @return <code>true</code> wenn der Motor seine angesteuerte Position
	 *         erreicht hat.
	 */
	public static boolean finished(boolean tpuA, int channel) {
		if(tpuA){
			short desPos = HWD.GET2(TPU_A.TPURAM0 + 0x10 * channel);
			short curPos = HWD.GET2(TPU_A.TPURAM0 + 0x10 * channel + 2);
			return desPos == curPos;
		}else{
			short desPos = HWD.GET2(TPU_B.TPURAM0 + 0x10 * channel);
			short curPos = HWD.GET2(TPU_B.TPURAM0 + 0x10 * channel + 2);
			return desPos == curPos;
		}
	}

	/**
	 * Gibt die momentane Position des Motors zurück.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Startpin.
	 * @return Momentane Position in Schritten.
	 */
	public static int position(boolean tpuA, int channel) {
		if(tpuA) return HWD.GET2(TPU_A.TPURAM0 + 0x10 * channel + 2);
		else return HWD.GET2(TPU_B.TPURAM0 + 0x10 * channel + 2);
	}
	
		
	/**
	 * Resetet den Schrittmotor-Treiber.<br>
	 * Die akuelle Position wird als die gewünschte Position gesetzt.
	 * 
	 * @param tpuA
	 *            <code>true</code>: benutzen der TPU-A. <code>false</code>:
	 *            benutzen der TPU-B.
	 * @param channel
	 *            TPU-Startpin.
	 */
	public static void reset(boolean tpuA, int channel) {
		if(tpuA){
			//Get current position
			short pos = HWD.GET2(TPU_A.TPURAM0 + 0x10 * channel + 2);
			//Set desired position
			HWD.PUT2(TPU_A.TPURAM0 + 0x10 * channel, pos);	
		}else{
			//Get current position
			short pos = HWD.GET2(TPU_B.TPURAM0 + 0x10 * channel + 2);
			//Set desired position
			HWD.PUT2(TPU_B.TPURAM0 + 0x10 * channel, pos);	
		}
	}
	

}