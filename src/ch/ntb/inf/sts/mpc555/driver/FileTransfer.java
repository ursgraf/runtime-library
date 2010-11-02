package ch.ntb.inf.sts.mpc555.driver;

import java.io.File;
import java.io.Rider;

import ch.ntb.inf.sts.mpc555.Task;

public class FileTransfer extends Task {
	static final int receivingCommands = 0;
	static final int sendingFile = 1;
	static final int receivingFile = 2;
	static final int fieldLen = 5;
	static char[] str;
	static File f;
	static Rider r;
	
	int state = receivingCommands;
	int subState, count, field, len, i;

	void sendFileDir () {
		File f;
		OutT.println(); OutT.println("File Directory");
		for (int i = 0; i < File.MaxFiles; i++) {
			if (File.directory(i)) {
				f = File.fileTab[i];
				OutT.printTab();OutT.print(f.name); OutT.printTab(); OutT.print("length = "); OutT.println(f.len); 
			}
		}
	}
	
	public void Do () {
		char ch; int res; int len; String name; byte val;
		switch (this.state) {
		case receivingCommands:
			res = SCI1.read();
			if (res > 0) {
				ch = (char)res;
				if (ch == 'g') sendFileDir();
				if (ch == 'a') File.formatAll();
				if (ch == 's') {OutT.println("sending file"); this.state = sendingFile; this.count = 0;}	// transfer to host 
				if (ch == 'p') {this.state = receivingFile; this.subState = 0; this.count = 0;}	// receive from host
			}
			break;
	case sendingFile:
			res = SCI1.read();
			if (res >= 0) {
				ch = (char)res;
				if (ch != 0) {str[this.count] = ch; this.count++;}
				else {
					str[this.count] = 0; 
					name = new String(str);
					OutT.println();
					OutT.print(name);
					f = File.old(name);
					if (f != null) {
						OutT.print(f.length()); OutT.println(" Bytes"); 
						r = new Rider();
						r.set(f, 0);
					int	val1 = r.readInt();
						while (!r.eof) {
							OutT.print(val1);
							val = r.readByte();
							OutT.printTab();
							short val2 = r.readShort();
							OutT.println(val2);
							val = r.readByte();
							val = r.readByte();
							val1 = r.readInt();
						}
					} else OutT.println(" does not exist");
					this.state = receivingCommands;
				}
			}				
			break;
		case receivingFile:
			if (SCI1.availToRead() > 0) {
				res = SCI1.read();
				ch = (char)res;
				switch (subState) {
				case 0:
					if (ch != 0) {str[this.count] = ch; this.count++;}
					else {str[this.count] = 0; this.subState++; this.field = 0; this.len = 0;}
					break;	
				case 1:
					if (ch != ' ') {this.len = ch - '0' + this.len * 10; }
					this.field++;		
					if (this.field == fieldLen) {
						this.subState++;
						this.i = 0;
						name = new String(str);
						OutT.print("create file "); OutT.println(name);
						OutT.print("length "); OutT.print(this.len);OutT.println(" Bytes");
						f = new File(name);
						r = new Rider();
						r.set(f, 0);
					}
					break;
				case 2:
					r.writeByte((byte)res); this.i++;
					if (this.i == this.len) {
						f.register(); OutT.print(f.name); OutT.println(" created");
						this.state = receivingCommands;
					}
				}
			}
			break;
		 }
	}

	static {
		FileTransfer t = new FileTransfer(); Task.install(t);
		str = new char[32];
	}
}
