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
 * NTB 2.4.2013, Urs Graf
 * 
 */
@MaxErrors(100)
public class SystemTest {
	
//	@Ignore
	@Test
	public static void testByteArray() {
		byte[] a1 = {10, 20, 30, 40, 50};
		Object a2 = new byte[8];
		System.arraycopy(a1, 0, a2, 0, 3);
		Assert.assertEquals("test1", 10, ((byte[])a2)[0]);
		Assert.assertEquals("test2", 30, ((byte[])a2)[2]);
		Assert.assertEquals("test3", 0, ((byte[])a2)[4]);
		System.arraycopy(a1, 2, a2, 1, 3);
		Assert.assertEquals("test4", 30, ((byte[])a2)[1]);
	
		CmdTransmitter.sendDone();
	}	
	
	@Test
	public static void testBooleanArray() {
		boolean[] a1 = {true, false, true};
		Object a2 = new boolean[4];
		System.arraycopy(a1, 0, a2, 0, 2);
		Assert.assertTrue("test1", ((boolean[])a2)[0]);
		Assert.assertFalse("test2", ((boolean[])a2)[1]);
		Assert.assertFalse("test3", ((boolean[])a2)[2]);
	
		CmdTransmitter.sendDone();
	}	
	
	@Test
	public static void testShortArray() {
		short[] a1 = {100, 200, 300, 400, 500};
		short[] a2 = new short[8];
		System.arraycopy(a1, 0, a2, 0, 3);
		Assert.assertEquals("test1", 100, a2[0]);
		Assert.assertEquals("test2", 300, a2[2]);
		Assert.assertEquals("test3", 0, a2[4]);
		System.arraycopy(a1, 1, a2, 5, 1);
		Assert.assertEquals("test4", 200, a2[5]);
	
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testCharArray() {
		char[] a1 = {'A', 'B', 'C'};
		char[] a2 = new char[8];
		System.arraycopy(a1, 0, a2, 1, 3);
		Assert.assertEquals("test1", 'A', a2[1]);
		Assert.assertEquals("test2", 'B', a2[2]);
		Assert.assertEquals("test3", 'C', a2[3]);
	
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testIntArray() {
		int[] a1 = {1000, 2000, 3000, 4000, 5000};
		int[] a2 = new int[8];
		System.arraycopy(a1, 0, a2, 0, 3);
		Assert.assertEquals("test1", 1000, a2[0]);
		Assert.assertEquals("test2", 3000, a2[2]);
		Assert.assertEquals("test3", 0, a2[4]);
		System.arraycopy(a1, 2, a2, 5, 2);
		Assert.assertEquals("test4", 3000, a2[5]);
	
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testLongArray() {
		long[] a1 = {-1000, -2000, -3000, -4000, -5000};
		long[] a2 = new long[3];
		System.arraycopy(a1, 3, a2, 0, 2);
		Assert.assertEquals("test1", -4000, a2[0]);
		Assert.assertEquals("test2", -5000, a2[1]);
		Assert.assertEquals("test3", 0, a2[2]);
	
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testFloatArray() {
		float[] a1 = {1e23f, -2.345345e-12f, -3};
		float[] a2 = new float[3];
		System.arraycopy(a1, 0, a2, 1, 2);
		Assert.assertEquals("test1", 0, a2[0], 0);
		Assert.assertEquals("test2", 1e23f, a2[1], 0);
		Assert.assertEquals("test3", -2.345345e-12f, a2[2], 0);
	
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testDoubleArray() {
		double[] a1 = {-3.453e234, -1.0, 2.3455e-123};
		double[] a2 = new double[3];
		System.arraycopy(a1, 1, a2, 0, 2);
		Assert.assertEquals("test1", -1.0, a2[0], 0);
		Assert.assertEquals("test2", 2.3455e-123, a2[1], 0);
		Assert.assertEquals("test3", 0, a2[2], 0);
	
		CmdTransmitter.sendDone();
	}	
}
