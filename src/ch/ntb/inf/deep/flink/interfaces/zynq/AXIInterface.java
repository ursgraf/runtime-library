package ch.ntb.inf.deep.flink.interfaces.zynq;

import ch.ntb.inf.deep.flink.core.FlinkBusInterface;
import ch.ntb.inf.deep.flink.core.FlinkDefinitions;
import ch.ntb.inf.deep.unsafe.US;

public class AXIInterface implements FlinkBusInterface, FlinkDefinitions {
	
	int base = 0x7aa00000;

	@Override
	public int getMemoryLength() {
		return US.GET4(base + 0x20);
	}

	@Override
	public int read(int address) {
		return US.GET4(base + address);
	}

	@Override
	public void write(int address, int data) {
		US.PUT4(base + address, data);
	}

	@Override
	public boolean hasInfoDev() {
		int function = US.GET4(base);	// info device must be at memory location 0
		return function >> 16 == 0;	// no info device
	}	

}