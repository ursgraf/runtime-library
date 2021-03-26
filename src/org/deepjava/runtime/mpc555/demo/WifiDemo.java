package org.deepjava.runtime.mpc555.demo;

import java.io.PrintStream;

import org.deepjava.runtime.mpc555.driver.MPIOSM_DIO;
import org.deepjava.runtime.mpc555.driver.RN131;
import org.deepjava.runtime.mpc555.driver.SCI;
import org.deepjava.runtime.ppc32.Task;
import org.deepjava.runtime.util.IntPacket;

public class WifiDemo extends Task {
	
	public WifiDemo() {
		period = 500;	
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(115200, SCI.NO_PARITY, (short)8);
		wifi = new RN131(sci.in , sci.out, new MPIOSM_DIO(11, true));
	}
	
	public void action() {
		System.out.print(wifi.getState().toString());
		
		if (wifi.connected())
			System.out.print("\t(connected)\t");
		else
			System.out.print("\t(not connected)\t");
		
		while (true) {
			IntPacket.Type type = wifi.intPacket.readInt();
			if (type == IntPacket.Type.None) break;
			else if (type == IntPacket.Type.Int) {
				System.out.print("int packet=");
				System.out.print(wifi.intPacket.getInt());
			} else if (type == IntPacket.Type.Unknown) {
				System.out.print("unknown(");
				System.out.print(wifi.intPacket.getHeader());
				System.out.print(")=");
				System.out.print(wifi.intPacket.getInt());
			}
		}
		System.out.println();
		if (nofActivations % 20 == 0) wifi.intPacket.writeInt(nofActivations);
	}
	
	public static void reset() {
		task.wifi.reset();
	}
	
	public static void sendCmd() {
		if (task.wifi.connected()) {
			task.wifi.intPacket.writeInt(123);
		}
	}
	
	public static void sendOther() {
		if (task.wifi.connected()) {
			task.wifi.intPacket.writeInt((byte)0xab, 789);
		}
	}
	
	private RN131 wifi;
	
	private static WifiDemo task;
	
	static {
		SCI sci = SCI.getInstance(SCI.pSCI1);
		sci.start(115200, SCI.NO_PARITY, (short)8);
		System.out = new PrintStream(sci.out);
		System.err = System.out;
		System.out.println("WifiDemo");
		task = new WifiDemo();
		Task.install(task);
	}
}
