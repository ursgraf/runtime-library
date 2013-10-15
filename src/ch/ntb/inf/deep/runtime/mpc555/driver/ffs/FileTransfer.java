/*
 * Copyright 2011 - 2013 NTB University of Applied Sciences in Technology
 * Buchs, Switzerland, http://www.ntb.ch/inf
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package ch.ntb.inf.deep.runtime.mpc555.driver.ffs;

import java.io.PrintStream;

import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

/** files can be manually uploaded over SCI2, uses interrupt driven SCI driver */

public class FileTransfer extends Task {
	static final int receivingCommands = 0;
	static final int sendingFile = 1;
	static final int receivingFile = 2;
	static final int fieldLen = 10;
	static char[] str;
	static File f;
	static Rider r;
	
	int state = receivingCommands;
	int subState, count, len, i;

	void sendFileDir () {
		File f;
		System.out.println("file directory");
		for (int i = 0; i < FFS.maxFiles; i++) {
			if (FFS.directory(i)) {
				f = FFS.fileTab[i];
				System.out.print("\t"); System.out.print(f.name); System.out.print("\t"); System.out.print("length = "); System.out.println(f.len); 
			}
		}
		System.out.println(); 
	}
	
	public void action () {
		char ch; int res; String name;
		switch (this.state) {
		case receivingCommands:
			if (SCI2.availToRead() > 0) {
				res = SCI2.read();
				ch = (char)res;
				if (ch == 'g') sendFileDir();
				if (ch == 'a') {FFS.formatAll(); System.out.println("ffs formated"); System.out.println();}
				if (ch == 's') {this.state = sendingFile; this.count = 0;}	// transfer to host 
				if (ch == 'p') {this.state = receivingFile; this.subState = 0; this.count = 0;}	// receive from host
			}
			break;
		case sendingFile:
			if (SCI2.availToRead() > 0) {
				res = SCI2.read();
				ch = (char)res;
				if (ch != 0) {str[this.count] = ch; this.count++;}
				else {
					name = new String(str, 0, this.count);
					System.out.print(name);
					System.out.print('\0');
					f = FFS.old(name);
					if (f != null) {
						System.out.print(f.length()); 
						System.out.print(' ');
						r = new Rider();
						r.set(f, 0);
						byte val = r.readByte();
						while (!r.eof) {
							System.out.print((char)val);
							val = r.readByte();
						}
					} else { // file does not exist
						System.out.print(-1); 
						System.out.print(' ');
					}
					this.state = receivingCommands;
				}
			}				
			break;
		case receivingFile:
			if (SCI2.availToRead() > 0) {
				res = SCI2.read();
				ch = (char)res;
				switch (subState) {
				case 0:
					if (ch != 0) {str[this.count] = ch; this.count++;}
					else if (count > 0) {this.subState++; this.len = 0;}
					break;	
				case 1:
					if (ch != ' ') {this.len = ch - '0' + this.len * 10; }
					else {
						this.subState++;
						this.i = 0;
						name = new String(str, 0, this.count);;
						System.out.print("create file: "); System.out.print(name); 
						System.out.print(", len = "); System.out.print(this.len); System.out.println("Bytes");
						f = new File(name);
						r = new Rider();
						r.set(f, 0);
					}
					break;
				case 2:
					r.writeByte((byte)res); this.i++;
					if (this.i == this.len) {
						f.register(); System.out.print(f.name); System.out.println(" registered");
						this.state = receivingCommands;
					}
				}
			}
			break;
		}
	}

	static {
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.out.println("started");
		Task t = new FileTransfer(); 
		Task.install(t);
		FFS.init();
		str = new char[32];
	}
}
