package ch.ntb.sysp.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.util.CmdInt;
import ch.ntb.inf.deep.runtime.mpc555.driver.RN131;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.ppc32.Task;

public class WifiDemo extends Task {
	
	public WifiDemo() throws Exception {
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
			CmdInt.Type type = wifi.cmd.readCmd();
			if (type == CmdInt.Type.None) break;
			if (type == CmdInt.Type.Cmd) {
				System.out.print("command=");
				System.out.print(wifi.cmd.getInt());
			}
			else if (type == CmdInt.Type.Code) {
				System.out.print("code=");
				System.out.print(wifi.cmd.getInt());
			}
			else if (type == CmdInt.Type.Unknown) {
				System.out.print("unknown(");
				System.out.print(wifi.cmd.getHeader());
				System.out.print(")=");
				System.out.print(wifi.cmd.getInt());
			}
		}
		System.out.println();
	}
	
	public static void reset() {
		task.wifi.reset();
	}
	
	public static void sendCmd() {
		if (task.wifi.connected()) {
			task.wifi.cmd.writeCmd(123);
		}
	}
	
	public static void sendCode() {
		if (task.wifi.connected()) {
			task.wifi.cmd.writeCmd(CmdInt.Type.Code, 456);
		}
	}
	
	public static void sendOther() {
		if (task.wifi.connected()) {
			task.wifi.cmd.writeCmd((byte)0xab, 789);
		}
	}
	
	private RN131 wifi;
	
	private static WifiDemo task;
	
	static {
		SCI sci = SCI.getInstance(SCI.pSCI1);
		sci.start(115200, SCI.NO_PARITY, (short)8);
		
		System.out = new PrintStream(sci.out);
		System.err = new PrintStream(sci.out);
		System.out.println("WifiDemo");
		
		try {
			task = new WifiDemo();
			Task.install(task);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
