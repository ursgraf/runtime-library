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

package ch.ntb.inf.deep.runtime.mpc555.driver.can;

public class EPOS {
	byte driveId;
	int pos;
	
	static final EPOS drive1 = new EPOS((byte)1);

	public void initOutABCD() {	
		CANopen.sendSDO(this.driveId, (short)0x2079, (byte)1, 15, 6);
		CANopen.sendSDO(this.driveId, (short)0x2079, (byte)2, 14, 6);
		CANopen.sendSDO(this.driveId, (short)0x2079, (byte)3, 13, 6);
		CANopen.sendSDO(this.driveId, (short)0x2079, (byte)4, 12, 6);
		CANopen.sendSDO(this.driveId, (short)0x2078, (byte)2, 0xf000, 6);	// set mask
	}
	
	public void setOutAC() {	// led 1 and 3
		CANopen.sendSDO(this.driveId, (short)0x2078, (byte)1, 0x5000, 6);
	}
			
	public void setOutBD() {	// led 2 and 4
		CANopen.sendSDO(this.driveId, (short)0x2078, (byte)1, 0xa000, 6);
	}
			
	public void setPosition(int pos) {	
		CANopen.sendSDO(driveId, (short)0x2062, (byte)0, pos, 8);
	}
						
	public void resetPosition() {	
		pos = 0;
		setPosition(pos);
	}
						
	public int getStatus() {
		CANopen.sendSDO(this.driveId, (short)0x6041, (byte)0, 0, 4);
		byte data[] = CANA.getMsgBuf(CANA.rx1BufNo);
		int status = (((data[11]<<8) & 0xff00) + (data[10] & 0xff));
		return status;
	}
		
	public void enablePower() {	
		int status;
		do status = getStatus(); while ((status & 0x0140) != 0x0140);	// wait for "switch on disabled" state
		CANopen.sendSDO(driveId, (short)0x6040, (byte)0, 0x06, 6);	// set control word shutdown (transition 2)
		do status = getStatus(); while ((status & 0x0121) != 0x0121);	// wait for "ready to switch on" state
		CANopen.sendSDO(driveId, (short)0x6040, (byte)0, 0x07, 6);	// set control word switch on (transition 3)
		do status = getStatus(); while ((status & 0x0123) != 0x0123);	// wait for "switched on" state
		CANopen.sendSDO(driveId, (short)0x6040, (byte)0, 0x0f, 6);	// set control word enable (transition 4)
		do status = getStatus(); while ((status & 0x0137) != 0x0137);	// wait for "operation enable" state
	}
			
	public void setParams() {	
		CANopen.sendSDO(driveId, (short)0x6402, (byte)0, 11, 6);	// set motor type to EC block commutated
		CANopen.sendSDO(driveId, (short)0x6060, (byte)0, 0xff, 5); // set operation to position mode 
		CANopen.sendSDO(driveId, (short)0x6410, (byte)1, 2660, 6);	// set continous current limit
		CANopen.sendSDO(driveId, (short)0x6410, (byte)2, 10000, 6);	// set output current limit
		CANopen.sendSDO(driveId, (short)0x6410, (byte)5, 27, 6);	// set time constant
		CANopen.sendSDO(driveId, (short)0x2210, (byte)1, 500, 6);	// set encoder pulse number
		CANopen.sendSDO(driveId, (short)0x2210, (byte)2, 1, 6);	// set encoder with index
	}
	
	public void setPDOtransmission() {
		CANopen.sendSDO(driveId, (short)0x1800, (byte)2, 1, 5);	// set PDO 1 transmission type to synchronous
		CANopen.sendSDO(driveId, (short)0x1A00, (byte)0, 0, 5);	// disable PDO 1
		CANopen.sendSDO(driveId, (short)0x1A00, (byte)1, 0x60640020, 8);	// set PDO 1 mapping to actual position
		CANopen.sendSDO(driveId, (short)0x1A00, (byte)0, 1, 5);	// enable PDO 1, map 1 object
		CANopen.setMsgBufRxPDO(driveId);
	}

	public void sendSync() {
		CANopen.sendSync();
	}
			
	public void start() {
		CANopen.start(driveId);	// enter pre-operational mode and wait for bootup msg
	}

	public void startNode() {
		CANopen.sendMsg0NMTstartRemoteNode(driveId);	// enter operational mode, PDO's allowed from now
	}

	public EPOS(byte driveId) {
		this.driveId = driveId;
	}
	
	static {
		CANA.init();
		
	}
	
	// methods for debugging purposes
	
	public static void readErrorRegister() {	
		CANopen.sendSDO(drive1.driveId, (short)0x1001, (byte)0, 0, 4);
		CANopen.printSDOAnswer();
	}
			
	public static void readContCurrLimit() {	
		CANopen.sendSDO(drive1.driveId, (short)0x6410, (byte)1, 0, 4);
		CANopen.printSDOAnswer();
	}
			
	public static void readOutCurrLimit() {	
		CANopen.sendSDO(drive1.driveId, (short)0x6410, (byte)2, 0, 4);
		CANopen.printSDOAnswer();
	}
			
	public static void readCurrActVal() {	
		CANopen.sendSDO(drive1.driveId, (short)0x6078, (byte)0, 0, 4);
		CANopen.printSDOAnswer();
	}
			
	public static void readDeviceType() {	
		CANopen.sendSDO(drive1.driveId, (short)0x1000, (byte)0, 0, 4);
		CANopen.printSDOAnswer();
	}
			
	public static void readMotorType() {	
		CANopen.sendSDO(drive1.driveId, (short)0x6402, (byte)0, 0, 4);
		CANopen.printSDOAnswer();
	}

	public static void readTimeConst() {	
		CANopen.sendSDO(drive1.driveId, (short)0x6410, (byte)5, 0, 4);
		CANopen.printSDOAnswer();
	}
			
	public static void readSenseConf() {	
		CANopen.sendSDO(drive1.driveId, (short)0x2210, (byte)1, 0, 4);
		CANopen.printSDOAnswer();
		CANopen.sendSDO(drive1.driveId, (short)0x2210, (byte)2, 0, 4);
		CANopen.printSDOAnswer();
		CANopen.sendSDO(drive1.driveId, (short)0x2210, (byte)4, 0, 4);
		CANopen.printSDOAnswer();
	}
			
	public static void readPositionMode() {	
		CANopen.sendSDO(drive1.driveId, (short)0x6060, (byte)0, 0, 4);
		CANopen.printSDOAnswer();
	}
			
	public static void readStatusword() {
		CANopen.sendSDO(drive1.driveId, (short)0x6041, (byte)0, 0, 4);
		CANA.getMsgBuf(CANA.rx1BufNo);
		int status = drive1.getStatus();
		CANopen.printSDOAnswer();
		System.out.print("status = "); System.out.printHexln(status);
	}

	public static void setHomePosition() {	
		CANopen.sendSDO(drive1.driveId, (short)0x2081, (byte)0, 0, 8);
	}
			
	public static void readPosition() {	
		CANopen.sendSDO(drive1.driveId, (short)0x6064, (byte)0, 0, 4);
		CANopen.printSDOAnswer();
	}

	public static void readPositionDemand() {	
		CANopen.sendSDO(drive1.driveId, (short)0x6062, (byte)0, 0, 4);
		CANopen.printSDOAnswer();
	}
	
	public static void readPDO1mapping() {	
		CANopen.sendSDO(drive1.driveId, (short)0x1A00, (byte)1, 0, 4);
		CANopen.printSDOAnswer();
	}
	
	public static void sync() {
		drive1.sendSync();
	}

}
