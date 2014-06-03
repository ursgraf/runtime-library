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
 * NTB 27.5.2014, Urs Graf
 * 
 * Tests for primitive type classes
 */
@MaxErrors(100)
public class BaseClassTest {
	
	@Test
	public static void testByte() {
		Byte b1 = -100;
		Byte b2 = 100;
		Assert.assertEquals("test1", Byte.compare(b1, b2), -1);
		Assert.assertEquals("test2", Byte.compare(b2, b1), 1);
		Assert.assertEquals("test3", Byte.compare(b1, new Byte((byte) -100)), 0);
		
		b2 = new Byte("50");
		Assert.assertEquals("test4", b2.byteValue(), 50);
		b2 = new Byte("-128");
		Assert.assertEquals("test5", b2.byteValue(), -128);
		Assert.assertTrue("test6", b2.equals(new Byte((byte) -128)));
		Assert.assertFalse("test7", b2.equals(b1));
		
		Assert.assertEquals("test10", b2.floatValue(), -128.0, 0);
		Assert.assertEquals("test11", b2.intValue(), -128);
		Assert.assertEquals("test12", b2.longValue(), -128);
		
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testInt() {
		Integer i1 = -10000000;
		Integer i2 = 334556;
		Assert.assertEquals("test1", Integer.compare(i1, i2), -1);
		Assert.assertEquals("test2", Integer.compare(i2, i1), 1);
		Assert.assertEquals("test3", Integer.compare(i1, new Integer(-10000000)), 0);
		
		i2 = new Integer("8493564");
		Assert.assertEquals("test4", i2.intValue(), 8493564);
//		i2 = new Integer("0x80");
//		Assert.assertEquals("test5", i2.intValue(), 128);
		i2 = new Integer("-9800");
		Assert.assertEquals("test7", i2.intValue(), -9800);
		
		Assert.assertEquals("test10", i2.floatValue(), -9800.0, 0);
		Assert.assertEquals("test11", i2.shortValue(), -9800);
		Assert.assertEquals("test12", i2.longValue(), -9800);
		
		int i3 = 0b1;
		Assert.assertEquals("test20", Integer.reverse(i3), 0x80000000);
		i3 = 0b01111101001110000111111100000110;
		Assert.assertEquals("test21", Integer.reverse(i3), 0b01100000111111100001110010111110);
		i3 = 100;
		Assert.assertEquals("test22", Integer.signum(i3), 1);
		i3 = -12;
		Assert.assertEquals("test23", Integer.signum(i3), -1);
		i3 = 0;
		Assert.assertEquals("test24", Integer.signum(i3), 0);
		i3 = 0x22334455;
		Assert.assertEquals("test25", Integer.reverseBytes(i3), 0x55443322);
		Assert.assertEquals("test26", Integer.rotateRight(i3, 4), 0x52233445);
		Assert.assertEquals("test27", Integer.rotateRight(i3, 32), 0x22334455);
		Assert.assertEquals("test28", Integer.rotateLeft(i3, 4), 0x23344552);
		Assert.assertEquals("test29", Integer.highestOneBit(i3), 0x20000000);
		
		Assert.assertTrue("test40", Integer.toString(i2).equals("-9800"));
		Assert.assertTrue("test41", i2.toString().equals("-9800"));
		Assert.assertFalse("test42", i2.toString().equals("9800"));
		
		Assert.assertTrue("test43", Integer.toHexString(i3).equals("0x22334455"));
		Assert.assertFalse("test44", Integer.toHexString(0).equals("0x0"));
		Assert.assertFalse("test45", Integer.toHexString(-0).equals("0x0"));
		Assert.assertTrue("test46", Integer.toHexString(0).equals("0"));
		Assert.assertTrue("test47", Integer.toHexString(0x80000000).equals("0x80000000"));
		Assert.assertTrue("test48", Integer.toHexString(0x93).equals("0x93"));
		
		Assert.assertTrue("test50", Integer.toBinaryString(0).equals("0"));
		Assert.assertTrue("test51", Integer.toBinaryString(1).equals("0b1"));
		Assert.assertTrue("test52", Integer.toBinaryString(0b1111).equals("0b1111"));
		Assert.assertTrue("test53", Integer.toBinaryString(0b00001111).equals("0b1111"));
		Assert.assertTrue("test54", Integer.toBinaryString(0b11110000111100001111000000001111).equals("0b11110000111100001111000000001111"));
		
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testLong() {
		Long l1 = -1000000000000L;
		Long l2 = 334556L;
		Assert.assertEquals("test1", Long.compare(l1, l2), -1);
		Assert.assertEquals("test2", Long.compare(l2, l1), 1);
		Assert.assertEquals("test3", Long.compare(l1, new Long(-1000000000000L)), 0);
		
		l2 = new Long("84935640004356");
		Assert.assertEquals("test4", l2.longValue(), 84935640004356L);
		l2 = new Long("-9800445567500");
		Assert.assertEquals("test7", l2.longValue(), -9800445567500L);
		
		Assert.assertEquals("test10", l2.floatValue(), -9800445567500.0, 1e6);
		Assert.assertEquals("test11", l2.shortValue(), 24052);
		Assert.assertEquals("test12", l2.intValue(), 669801972);
		
		long l3 = 0b1;
		Assert.assertEquals("test20", Long.reverse(l3), 0x8000000000000000L);
		l3 = 0b01111101001110000111111100000110001100110011001100110011101010000L;
		Assert.assertEquals("test21", Long.reverse(l3), 0b0000101011100110011001100110011000110000011111110000111001011111L);
		l3 = 34545003333333333L;
		Assert.assertEquals("test22", Long.signum(l3), 1);
		l3 = -12;
		Assert.assertEquals("test23", Long.signum(l3), -1);
		l3 = 0;
		Assert.assertEquals("test24", Long.signum(l3), 0);
		l3 = 0x2233445566778899L;
		Assert.assertEquals("test25", Long.reverseBytes(l3), 0x9988776655443322L);
		Assert.assertEquals("test26", Long.rotateRight(l3, 4), 0x9223344556677889L);
		Assert.assertEquals("test27", Long.rotateRight(l3, 64), 0x2233445566778899L);
		Assert.assertEquals("test28", Long.rotateLeft(l3, 4), 0x2334455667788992L);
		Assert.assertEquals("test29", Long.highestOneBit(l3), 0x2000000000000000L);
		
		Assert.assertTrue("test40", Long.toString(l2).equals("-9800445567500"));
		Assert.assertTrue("test41", l2.toString().equals("-9800445567500"));
		Assert.assertFalse("test42", l2.toString().equals("9800445567500"));
		
		Assert.assertTrue("test43", Long.toHexString(l3).equals("0x2233445566778899"));
		Assert.assertFalse("test44", Long.toHexString(0).equals("0x0"));
		Assert.assertFalse("test45", Long.toHexString(-0).equals("0x0"));
		Assert.assertTrue("test46", Long.toHexString(0).equals("0"));
		Assert.assertTrue("test47", Long.toHexString(0x8000000000000000L).equals("0x8000000000000000"));
		Assert.assertTrue("test48", Long.toHexString(0x93).equals("0x93"));
		
		Assert.assertTrue("test50", Long.toBinaryString(0).equals("0"));
		Assert.assertTrue("test51", Long.toBinaryString(1).equals("0b1"));
		Assert.assertTrue("test52", Long.toBinaryString(0b1111).equals("0b1111"));
		Assert.assertTrue("test53", Long.toBinaryString(0b00001111).equals("0b1111"));
		Assert.assertTrue("test54", Long.toBinaryString(0b111101011100110011001100110011000110000011111110000111001011111L).equals("0b111101011100110011001100110011000110000011111110000111001011111"));
		
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testDouble() {
		Double d1 = -8724.456;
		Double d2 = 3.22334e-28;
		Assert.assertEquals("test1", Double.compare(d1, d2), -1);
		Assert.assertEquals("test2", Double.compare(d2, d1), 1);
		Assert.assertEquals("test3", Double.compare(d1, new Double(-8724.456)), 0);
		d2 = new Double("8493564");
		Assert.assertEquals("test4", d2.intValue(), 8493564);
		d2 = new Double("-9800");
		Assert.assertEquals("test5", d2.intValue(), -9800);
		
		Assert.assertEquals("test10", Double.valueOf("1.2e2"), 1.2e2, 0);
		Assert.assertEquals("test11", Double.valueOf("-1.2e2"), -1.2e2, 0);
		Assert.assertEquals("test12", Double.valueOf("1.2e-2"), 1.2e-2, 0);
		Assert.assertEquals("test13", Double.valueOf("-1.2e-2"), -1.2e-2, 0);
		Assert.assertEquals("test14", Double.valueOf("34580E004"), 3.458e8, 0);
		Assert.assertEquals("test15", Double.valueOf("-434596954.345473995"), -4.34596954345473995e8, 0);
		Assert.assertEquals("test16", Double.valueOf("0E-0"), 0, 0);
		Assert.assertEquals("test17", Double.valueOf("0E-1"), 0, 0);
		Assert.assertEquals("test18", Double.valueOf("1.2f"), 1.2, 0);
		Assert.assertEquals("test19", Double.valueOf("-0.3e-1F"), -0.03, 0);
		
		d2 = new Double(2.49853498e3);
		Assert.assertEquals("test20", d2.floatValue(), 2.49853498e3f, 0e-4);
		Assert.assertEquals("test21", d2.shortValue(), 2498);
		Assert.assertEquals("test22", d2.longValue(), 2498);

		Assert.assertTrue("test30", Double.toString(d2).equals("2.498534980000000E+003"));

		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testFloat() {
		Float f1 = -8724.456f;
		Float f2 = 3.22334e-28f;
		Assert.assertEquals("test1", Float.compare(f1, f2), -1);
		Assert.assertEquals("test2", Float.compare(f2, f1), 1);
		Assert.assertEquals("test3", Float.compare(f1, new Float(-8724.456)), 0);
		f2 = new Float("8493564");
		Assert.assertEquals("test4", f2.intValue(), 8493564);
		f2 = new Float("-9800");
		Assert.assertEquals("test5", f2.intValue(), -9800);
		
		Assert.assertEquals("test10", Float.valueOf("1.2e2"), 1.2e2, 1e-6);
		Assert.assertEquals("test11", Float.valueOf("-1.2e2f"), -1.2e2, 1e-6);
		Assert.assertEquals("test12", Float.valueOf("1.2e-2"), 1.2e-2, 1e-6);
		Assert.assertEquals("test13", Float.valueOf("-1.2e-2"), -1.2e-2, 1e-6);
		Assert.assertEquals("test14", Float.valueOf("34580E004"), 3.458e8, 1e-6);
		Assert.assertEquals("test15", Float.valueOf("-434596954.345473995"), -4.34596954345473995e8, 1e3);
		Assert.assertEquals("test16", Float.valueOf("0E-0"), 0, 1e-6);
		Assert.assertEquals("test17", Float.valueOf("0E-1"), 0, 1e-6);
		Assert.assertEquals("test18", Float.valueOf("1.2f"), 1.2, 1e-6);
		Assert.assertEquals("test19", Float.valueOf("-0.3e-1f"), -0.03, 1e-6);
		
		f2 = new Float(2.49853498e3f);
		Assert.assertEquals("test20", f2.doubleValue(), 2.49853498e3, 1e-3);
		Assert.assertEquals("test21", f2.shortValue(), 2498, 1e-6);
		Assert.assertEquals("test22", f2.longValue(), 2498, 1e-6);

		Assert.assertTrue("test30", Float.toString(f2).equals("2.4985349E+003"));

		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testBoolean() {
		Boolean b1 = true;
		Boolean b2 = false;
		Assert.assertEquals("test1", Boolean.compare(b1, b2), 1);
		Assert.assertEquals("test2", Boolean.compare(b2, b1), -1);
		Assert.assertEquals("test3", Boolean.compare(b1, new Boolean(true)), 0);
		
		b2 = new Boolean("true");
		Assert.assertTrue("test4", b2.booleanValue());
		b2 = new Boolean("false");
		Assert.assertFalse("test5", b2.booleanValue());
		Assert.assertTrue("test6", b2.equals(new Boolean("false")));
		Assert.assertFalse("test7", b2.equals(new Boolean("true")));
		
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void testCharacter() {
		Character c1 = 'a';
		Character c2 = 'A';
		Assert.assertEquals("test1", Character.compare(c1, c2), 'a'-'A');
		Assert.assertEquals("test2", Character.compare(c2, c1), 'A'-'a');
		Assert.assertEquals("test3", Character.compare(c1, new Character('a')), 0);
		
		c2 = new Character('b');
		Assert.assertEquals("test4", c2.charValue(), 'b');
		Assert.assertEquals("test5", Character.toUpperCase(c2), 'B');
		Assert.assertEquals("test6", Character.toLowerCase('X'), 'x');
		Assert.assertTrue("test7", Character.isDigit('3'));
		Assert.assertFalse("test8", Character.isDigit('d'));
		
		Assert.assertFalse("test10", c2.toString().equals('b'));
		Assert.assertTrue("test11", c2.toString().equals("b"));
		Assert.assertFalse("test12", c2.toString().equals("ABC"));
		
		CmdTransmitter.sendDone();
	}	


	
}

