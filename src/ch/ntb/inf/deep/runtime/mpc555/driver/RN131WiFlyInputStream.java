package ch.ntb.inf.deep.runtime.mpc555.driver;

import java.io.InputStream;

public class RN131WiFlyInputStream extends InputStream{

	public int available() {
		return RN131WiFly.availToRead();
	}
	public int read() {
		return RN131WiFly.read();
	}
	
	public int read(byte b[], int len){
		return RN131WiFly.read(b, len);
	}

	public int read(byte b[], int off, int len){
		return RN131WiFly.read(b, off, len);
	}
}
