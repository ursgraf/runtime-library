package ch.ntb.inf.deep.runtime.mpc555.driver;

import ch.ntb.inf.deep.runtime.mpc555.driver.DigitalOutput;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCIInputStream;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCIOutputStream;

public class RN131Config {
	public int channel = 1;
	public String ssid = "SysPNet_TeamXY";
	public String passphrase = "12345678";
	public String localIP = "169.254.1.101";
	public String remoteIP = "169.254.1.102";
	public boolean configure = false;
	public boolean apMode = true;
	public boolean useExternalAntenna = true;
	public boolean autoConnect = false;
	public SCIInputStream in = null;
	public SCIOutputStream out = null;
	public DigitalOutput reset = null;
}
