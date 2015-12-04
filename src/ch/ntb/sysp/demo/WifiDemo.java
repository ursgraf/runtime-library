package ch.ntb.sysp.demo;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.util.CmdInt;
import ch.ntb.inf.deep.runtime.mpc555.driver.RN131;
import ch.ntb.inf.deep.runtime.mpc555.driver.RN131Config;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.ppc32.Task;

public class WifiDemo extends Task {
	
	public WifiDemo() throws Exception {
		period = 500;
		
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(115200, SCI.NO_PARITY, (short)8);
		
		config = new RN131Config();
		config.in = sci.in;
		config.out = sci.out;
		config.reset = new MPIOSM_DIO(11, true);
		
		config.ssid = "SysPNet_TeamXY";
		config.localIP = "169.254.1.1";
		config.remoteIP = "169.254.1.2";
		config.autoConnect = false;
		config.apMode = true;
		config.configure = false;
		
		wifi = new RN131(config);
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
	
	private RN131Config config;
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Task.install(task);
	}
}
