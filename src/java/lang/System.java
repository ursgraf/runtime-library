package java.lang;

import java.io.InputStream;
import java.io.PrintStream;

public class System {
	public static PrintStream err;
	public static PrintStream out;
	public static InputStream in;

	private System() {

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
