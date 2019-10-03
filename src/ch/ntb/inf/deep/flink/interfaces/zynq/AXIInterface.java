package ch.ntb.inf.deep.flink.interfaces.zynq;

import ch.ntb.inf.deep.flink.core.BusInterface;
import ch.ntb.inf.deep.flink.core.Definitions;
import ch.ntb.inf.deep.unsafe.US;

public class AXIInterface implements BusInterface, Definitions {
	
	int offset = 0x7aa00000;

	@Override
	public int getMemoryLength() {
		return 10;
	}

	@Override
	public int read(int address) {
		return US.GET4(offset + address);
	}

	@Override
	public void write(int address, int data) {
		US.PUT4(offset + address, data);
	}

	@Override
	public boolean hasInfoDev() {
		return true;
	}	

}