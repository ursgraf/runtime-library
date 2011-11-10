/*
 * Copyright (c) 2011 NTB Interstate University of Applied Sciences of Technology Buchs.
 * All rights reserved.
 *
 * http://www.ntb.ch/inf
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.ntb.inf.deep.runtime.mpc555.test;
import ch.ntb.inf.deep.runtime.mpc555.*;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 */
class BlinkerApplication {
	static int res;
	static Blinker blinker14, blinker13, blinker12, blinker11;
	static Task task1;
	
	static void getNofBlinkers () {
		res = Blinker.getNofBlinkers();
	}
	
	static void changePeriod14to100 () {
		Task.remove(blinker14);
		blinker14.changePeriod(100);
		Task.install(blinker14);
	}
	static void changePeriod14to1000 () {
		Task.remove(blinker14);
		blinker14.changePeriod(1000);
		Task.install(blinker14);
	}
	
	static void changePeriod13 () {
		if (task1 instanceof Blinker) ((Blinker)task1).changePeriod(2000);
	}
	
	static {
		blinker14 = new Blinker(14, 500); 
		blinker12 = new Blinker(12, 1000, 5); 
		blinker13 = new Blinker(13, 100, 20); 
		blinker11 = new Blinker(11, 500, 30); 
		task1 = blinker13;
	}
}