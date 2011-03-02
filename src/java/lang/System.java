package java.lang;

import java.io.InputStream;
import java.io.PrintStream;

public class System {
	public static PrintStream err;
	public static PrintStream out;
	public static InputStream in;

	private System() {

	}
	
//	public static void arraycopy(Object src, int srcPos, Object dest,
//			int destPos, int length) {
//		System.arraycopy(src, srcPos, dest, destPos, length);
//	}
	@Deprecated
	public static void chararraycopy(char[] src, int srcPos, char[] dest,
			int destPos, int length) {

		if (src.length - 1 >= srcPos && src.length >= srcPos + length
				&& dest.length - 1 >= destPos
				&& dest.length >= destPos + length) {
			for (int i = 0; i < length; i++) {
				dest[destPos + i] = src[srcPos + i];
			}
		}
	}
	
	public static void setErr(PrintStream err){
		System.err = err;
	}
	public static void setIn(InputStream in){
		System.in = in;
	}
	public static void setOut(PrintStream out){
		System.out = out;
	}
	
	static{
		out = new PrintStream(new DummyOutputStream());
		err = new PrintStream(new DummyOutputStream());
		in = new DummyInputStream();
		
	}
}
