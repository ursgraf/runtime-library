package ch.ntb.inf.deep.flink.interfaces.mpc5200;

import ch.ntb.inf.deep.flink.core.BusInterface;
import ch.ntb.inf.deep.runtime.mpc5200.IphyCoreMpc5200tiny;
import ch.ntb.inf.deep.unsafe.US;

//Altera spi to avalon bridge example c code is used for this interface. 

public class SPIBusInterface implements IphyCoreMpc5200tiny, BusInterface{

	private int memLength ;
	private boolean infoDevice;
	

	private static final int SPICR1 = MBAR + 0x0F00;
	private static final int SPICR2 = MBAR + 0x0F01;
	private static final int SPIBDRATE = MBAR + 0x0F04;
	private static final int SPIST = MBAR + 0x0F05;
	private static final int SPIDATA = MBAR + 0x0F09;
	private static final int SPIPORT = MBAR + 0x0F0D;
	private static final int SPIDATADIR = MBAR + 0x0F10;	
	
	
	public SPIBusInterface(){
		int gpspcr = US.GET4(GPSPCR);
		gpspcr = gpspcr & 0xCFFFF0FF; //Clean ALTs bits
		gpspcr = gpspcr | 0x00000C00;// use pins on PCS3 for SPI and UART
		US.PUT4(GPSPCR,gpspcr);	
		US.PUT1(SPICR1, 0x56); //SPI Enable, SPI Master set, CPOL = 0, CPHA = 1, CS enable
		US.PUT1(SPICR2, 0x0);
		US.PUT1(SPIBDRATE, 0x74);
		US.PUT1(SPIDATADIR, 0xE); // enable CS
		US.GET1(SPIST); // clear st
		this.memLength = 0;
		this.infoDevice = true;
	}
	
	
	public SPIBusInterface(int memoryLength){
		this();
		this.memLength = memoryLength;
		this.infoDevice = false;
	}
	
	public int getMemoryLength() {
		return this.memLength;
	}

	public int read(int address) {
		return transferData(address,0,0);
	}

	public void write(int address, int data) {
		transferData(0,address,data);
	    
	}
	private int transferData(int readAddress,int writeAddress,int data){
		byte[] dataToSend = new byte[12];
		dataToSend[0] = (byte) ((readAddress >> 24) & 0xff);
	    dataToSend[1] = (byte) ((readAddress >> 16) & 0xff);
	    dataToSend[2] = (byte) ((readAddress >> 8)  & 0xff);
	    dataToSend[3] = (byte) (readAddress & 0xff);
	    dataToSend[4] = (byte) ((writeAddress >> 24) & 0xff);
	    dataToSend[5] = (byte) ((writeAddress >> 16) & 0xff);
	    dataToSend[6] = (byte) ((writeAddress >> 8)  & 0xff);
	    dataToSend[7] = (byte) (writeAddress & 0xff);
	    dataToSend[8] = (byte) ((data >> 24) & 0xff);
	    dataToSend[9] = (byte) ((data >> 16) & 0xff);
	    dataToSend[10] = (byte) ((data >> 8)  & 0xff);
	    dataToSend[11] = (byte) (data & 0xff);
	    for(int i=0;i<dataToSend.length;i++){
	    	US.PUT1(SPIDATA,dataToSend[i]);
	    	while((US.GET1(SPIST)&0x80)==0x0);
	    	for(int u = 0; u<100;u++){
	    	
	    	}
	    	dataToSend[i] = US.GET1(SPIDATA);
	    }
		int result =  ((((int)dataToSend[8])&0xFF) << 24);
		result =  result | ((((int)dataToSend[9])&0xFF) << 16);
		result =  result | ((((int)dataToSend[10])&0xFF) << 8);
		result =  result | (((int)dataToSend[11])&0xFF);
		return result;
	}

	public boolean hasInfoDev() {
		return infoDevice;
	}
	
}
