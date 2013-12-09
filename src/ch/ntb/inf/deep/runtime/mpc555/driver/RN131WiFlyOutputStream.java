package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.OutputStream;

public class RN131WiFlyOutputStream extends OutputStream {

	public void write(byte b) {
		RN131WiFly.write(b);
	}
	
	public int write(byte[] b){
		return RN131WiFly.write(b);
	}
	
	public int write(byte[] b, int len){
		return RN131WiFly.write(b, len);
	}
	
	public int write(byte[] b, int off, int len){
		return RN131WiFly.write(b, off, len);
	}
	
	public int freeSpace() {
		return RN131WiFly.availToWrite();
	}

	public void reset() {
		RN131WiFly.clear();
	}

}
