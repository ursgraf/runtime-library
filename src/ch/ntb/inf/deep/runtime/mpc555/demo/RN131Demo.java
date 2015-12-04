package ch.ntb.inf.deep.runtime.mpc555.demo;

import ch.ntb.inf.deep.runtime.util.CmdInt;
import ch.ntb.inf.deep.runtime.mpc555.driver.RN131;
import ch.ntb.inf.deep.runtime.mpc555.driver.RN131Config;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.driver.DigitalInput;
import ch.ntb.inf.deep.runtime.mpc555.driver.MPIOSM_DIO;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI;
import ch.ntb.inf.deep.runtime.ppc32.Task;

public class RN131Demo extends Task {
	
	public RN131Demo() throws Exception {
		period = 500;
		
		SCI sci = SCI.getInstance(SCI.pSCI2);
		sci.start(115200, SCI.NO_PARITY, (short)8);
		
		reset = new MPIOSM_DIO(6, false);
		configure = new MPIOSM_DIO(7, false);
		apMode = new MPIOSM_DIO(5, false);
		autoConnect = new MPIOSM_DIO(8, false);
		
		config = new RN131Config();
		config.ssid = "SysPNet_TeamXY";
		config.passphrase = "12345678";
		config.in = sci.in;
		config.out = sci.out;
		config.reset = new MPIOSM_DIO(11, true);
		config.autoConnect = false;
		setConfig();
		
		wifi = new RN131(config);
	}
	
	private void setConfig() {
		config.apMode = apMode.get();
		config.configure = configure.get();
		config.autoConnect = autoConnect.get();
		if (config.apMode) {
			config.localIP = "169.254.1.101";
			config.remoteIP = "169.254.1.102";
		}
		else {
			config.localIP = "169.254.1.102";
			config.remoteIP = "169.254.1.101";
		}
	}
	
	public void action() {
		if (reset.get()) {
			setConfig();
			wifi.reset();
			counter = 0;
		}
		
		if (wifi.connected())
			System.out.print('+');
		else
			System.out.print('-');
		
		System.out.print(' ');
		System.out.print(wifi.getState().toString());
		System.out.print(' ');

		if (wifi.connected()) {
			
			if (!wifi.cmd.writeCmd(counter++)) {
				System.out.print("! ");
			}
		}
		
		while (true) {
			CmdInt.Type type = wifi.cmd.readCmd();
			if (type == CmdInt.Type.None) break;
			if (type == CmdInt.Type.Cmd) {
				if (!wifi.cmd.writeCmd(CmdInt.Type.Code, wifi.cmd.getInt()))
					System.out.print("? ");
			}
			System.out.print('[');
			System.out.print(type.toString());
			System.out.print(':');
			System.out.print(wifi.cmd.getInt());
			System.out.print("] ");
		}
		
		System.out.println();
	}
	
	private RN131Config config;
	private RN131 wifi;
	private DigitalInput reset;
	private DigitalInput configure;
	private DigitalInput apMode;
	private DigitalInput autoConnect;
	
	private int counter = 0;
	
	static {
		SCI sci = SCI.getInstance(SCI.pSCI1);
		sci.start(115200, SCI.NO_PARITY, (short)8);
		
		System.out = new PrintStream(sci.out);
		System.err = new PrintStream(sci.out);
		System.out.println("rn131 test");
		
		try {
			Task.install(new RN131Demo());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
