package ch.ntb.inf.deep.runtime.mpc5200.driver.can;

import ch.ntb.inf.deep.unsafe.US;

public class CANopen {
	private static final byte SDOrObject1Byte = 0x40;
	private static final byte SDOrObject2Byte = 0x40;
	private static final byte SDOrObject4Byte = 0x40;
	private static final byte SDOwObject1Byte = 0x2f;
	private static final byte SDOwObject2Byte = 0x2b;
	private static final byte SDOwObject4Byte = 0x23;
	
	private static final short cs_SDO_COB = 0x600;	// COB-ID for client server SDO
	private static final short sc_SDO_COB = 0x580;	// COB-ID for server client SDO
	private static final short PDO1_COB = 0x180;	// COB-ID for TxPDO 1
	private static final short bootUpMsg = 0x700;	// COB-ID for boot up message
	private static final short sync_COB = 0x080;	// COB-ID for synch signal
	
	private static byte[] data = new byte[8];

	private static void setSDO(byte cmdSpez, short index, byte subIndex, int objVal, int len) {
		data[0] = (byte)cmdSpez;
		data[1] = (byte)index;	// index low byte
		data[2] = (byte)(index >> 8);	// index high byte
		data[3] = (byte)subIndex;	// subindex
		for (int i = 4; i < len; i++) {
			data[i] = (byte)(objVal >> ((i - 4) * 8));	
		}
	}
			
	public static void sendSDO(byte id, short index, byte subIndex, int val, int len) {
		CAN.setMsgBufRx1(0, sc_SDO_COB | id, false, data);	// set receive msg buffer to search for SDO answer
		switch (len) {
		case 4:
			setSDO(SDOrObject1Byte, index, subIndex, val, 4);
			break;
		case 5:
			setSDO(SDOwObject1Byte, index, subIndex, val, 5);
			break;
		case 6:
			setSDO(SDOwObject2Byte, index, subIndex, val, 6);
			break;
		case 8:
			setSDO(SDOwObject4Byte, index, subIndex, val, 8);
			break;
		default:
			break;
		}
		CAN.setMsgBufTx(len, cs_SDO_COB | id, false, data);	
		CAN.waitForTransferComplete(CAN.rx1BufNo);	// wait for answer
	}
	
	// network management: enter pre-operational protocol
	// id: 1..127, 0->all nodes
	public static void sendMsg0NMTenterPreOp(int id) {
		data[0] = (byte)0x80;
		data[1] = (byte)id;
		CAN.setMsgBufTx(2, 0, false, data);	
		CAN.waitForTransferComplete(CAN.txBufNo);	// wait for end of transfer
	}
			
	// network management: reset communication protocol
	// id: 1..127, 0->all nodes
	public static void sendMsg0NMTresetComm(int id) {
		data[0] = (byte)0x82;
		data[1] = (byte)id;
		CAN.setMsgBufTx(2, 0, false, data);	
		CAN.waitForTransferComplete(CAN.txBufNo);	// wait for end of transfer
	}
			
	// network management: reset node protocol
	// id: 1..127, 0->all nodes
	public static void sendMsg0NMTresetNode(int id) {
		data[0] = (byte)0x81;
		data[1] = (byte)id;
		CAN.setMsgBufTx(2, 0, false, data);	
		CAN.waitForTransferComplete(CAN.txBufNo);	// wait for end of transfer
	}
			
	// network management: start remote node protocol
	// id: 1..127, 0->all nodes
	public static void sendMsg0NMTstartRemoteNode(int id) {
		data[0] = (byte)0x01;
		data[1] = (byte)id;
		CAN.setMsgBufTx(2, 0, false, data);	
		CAN.waitForTransferComplete(CAN.txBufNo);	// wait for end of transfer
	}

	// network management: stop remote node protocol
	// id: 1..127, 0->all nodes
	public static void sendMsg0NMTstopRemoteNode(int id) {
		data[0] = (byte)0x02;
		data[1] = (byte)id;
		CAN.setMsgBufTx(2, 0, false, data);	
		CAN.waitForTransferComplete(CAN.txBufNo);	// wait for end of transfer
	}

	public static void start(int id) {	
		CAN.setMsgBufRx1(0, bootUpMsg | id, false, data);	// set receive msg buffer to listen for bootup msg
		sendMsg0NMTresetNode(id);	// reset all nodes
		CAN.waitForTransferComplete(CAN.rx1BufNo);	// wait for end of bootup msg
	}
	
	public static void dispMsgBuf1() {
		System.out.print("Message Buffer 1");
		byte data[] = CAN.getMsgBuf(1);
		int len = data[1] &  0xf;
		System.out.print("\tlength: "); System.out.print(len);
		System.out.print("\tcode: "); System.out.printHexln((data[1] >> 4) & 0xf); 
		for (int i = 0; i < len; i++) {
			System.out.printHex(data[i+6]); System.out.print("\t");
		}
		System.out.println();
	}
						
	public static void dispMsgBuf2() {
		System.out.print("Message Buffer 2");
		byte data[] = CAN.getMsgBuf(2);
		int len = data[1] &  0xf;
		System.out.print("\tlength: "); System.out.print(len);
		System.out.print("\tcode: "); System.out.printHexln((data[1] >> 4) & 0xf); 
		for (int i = 0; i < len; i++) {
			System.out.printHex(data[i+6]); System.out.print("\t");
		}
		System.out.println();
	}
						
	public static void printSDOAnswer() {
		data = CAN.getMsgBuf(CAN.rx1BufNo);
		System.out.print("index: "); System.out.printHex(data[8]*0x100 + data[7]); System.out.print("\t");
		System.out.print("subindex: "); System.out.printHex(data[9]); System.out.print("\t");
		int val = 0xff & data[10]; 
		val += 0xffff & (data[11]*0x100); 
		val += 0xffffff & (data[12]*0x10000); 
		val += data[13]*0x1000000; 
		System.out.print("val: "); System.out.println(val); 
	}

	public static void sendSync() {
		CAN.setMsgBufTx(0, 0x80, false, data);
	}

	public static void setMsgBufRxPDO(int id) {
		CAN.setMsgBufRx2(0, PDO1_COB + id, false, data);
		
	}
			

}
