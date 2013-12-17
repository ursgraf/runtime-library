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

package ch.ntb.inf.deep.trglib.targettest;

import ch.ntb.inf.junitTarget.Assert;
import ch.ntb.inf.junitTarget.CmdTransmitter;
import ch.ntb.inf.junitTarget.MaxErrors;
import ch.ntb.inf.junitTarget.Test;

/**
 * 17.12.2013	NTB/AK	atan2, acos test added
 * 30.06.2011	NTB/Urs Graf
 * 
 *         Changes:
 */

@MaxErrors(100)
public class MathTest {
	
	
	@Test
	public static void testPower1() {
		Assert.assertEquals("Test1", 4.0, Math.powIntExp(2.0, 2), 1e-10);
		Assert.assertEquals("Test2", 1024.0, Math.powIntExp(2.0, 10), 1e-10);
		Assert.assertEquals("Test3", -172.10368, Math.powIntExp(-2.8, 5), 1e-10);
		Assert.assertEquals("Test4", 4.1007644162774e-7, Math.powIntExp(134.6, -3), 1e-10);
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testSqrt() {
		Assert.assertEquals("Test1", 4.0, Math.sqrt(16.0), 1e-10);
		Assert.assertEquals("Test2", Double.NaN, Math.sqrt(-2.3), 1e-10);
		Assert.assertEquals("Test3", 1.531553013969807e89, Math.sqrt(2.3456546346e178), 1e-8);
		Assert.assertEquals("Test4", 1.53155301396980e-89, Math.sqrt(2.3456546346e-178), 1e-8);
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testSin() {
		Assert.assertEquals("Test1", 0.0, Math.sin(0.0), 1e-5);
		Assert.assertEquals("Test2", 0.5, Math.sin(Math.PI/6), 1e-5);
		Assert.assertEquals("Test3", 1.0, Math.sin(Math.PI/2), 1e-5);
		Assert.assertEquals("Test4", -0.5, Math.sin(-Math.PI/6), 1e-5);
		Assert.assertEquals("Test5", -1.0, Math.sin(Math.PI + Math.PI/2), 1e-5);
		Assert.assertEquals("Test6", 0.5, Math.sin(4 * Math.PI + Math.PI/6), 1e-5);
		Assert.assertEquals("Test7", -0.5, Math.sin(5 * Math.PI + Math.PI/6), 1e-5);
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testCos() {
		Assert.assertEquals("Test1", 1.0, Math.cos(0.0), 1e-5);
		Assert.assertEquals("Test2", 0.5, Math.cos(Math.PI/3), 1e-5);
		Assert.assertEquals("Test3", 0.0, Math.cos(Math.PI/2), 1e-5);
		Assert.assertEquals("Test4", 0.5, Math.cos(-Math.PI/3), 1e-5);
		Assert.assertEquals("Test5", -0.5, Math.cos(Math.PI + Math.PI/3), 1e-5);
		Assert.assertEquals("Test6", 0.5, Math.cos(2 * Math.PI + Math.PI/3), 1e-5);
		Assert.assertEquals("Test7", -0.5, Math.cos(3 * Math.PI + Math.PI/3), 1e-5);
		CmdTransmitter.sendDone();
	}
	
	@Test
	public static void testArcCos(){
		Assert.assertEquals("Test1", Math.PI/2, Math.acos(0), 1e-5);
		Assert.assertEquals("Test2", 0.0, Math.acos(1), 1e-5);
		Assert.assertEquals("Test3", Math.PI, Math.acos(-1), 1e-5);
		Assert.assertEquals("Test4", Math.PI/4, Math.acos(1/Math.sqrt(2)), 1e-5);
		Assert.assertEquals("Test5", 3*Math.PI/4, Math.acos(-1/Math.sqrt(2)), 1e-5);
		Assert.assertEquals("Test6", Math.PI/6, Math.acos(Math.sqrt(3)/2), 1e-5);
		Assert.assertEquals("Test7", 0.863211890069541, Math.acos(0.65), 1e-5);
		CmdTransmitter.sendDone();
	}
	
	@Test
	public static void testArcTan2(){
		Assert.assertEquals("Test1", Double.NaN, Math.atan2(0,0), 1e-5);
		Assert.assertEquals("Test2", Math.PI/2, Math.atan2(1,0), 1e-5);
		Assert.assertEquals("Test3", -Math.PI/2, Math.atan2(-1,0), 1e-5);
		Assert.assertEquals("Test4", Math.atan(1.5/2), Math.atan2(1.5,2), 1e-5);
		Assert.assertEquals("Test5", Math.atan(1.5/-2)+Math.PI, Math.atan2(1.5,-2), 1e-5);
		Assert.assertEquals("Test6", Math.atan(-1.5/-2)-Math.PI, Math.atan2(-1.5,-2), 1e-5);
		CmdTransmitter.sendDone();
	}
}
