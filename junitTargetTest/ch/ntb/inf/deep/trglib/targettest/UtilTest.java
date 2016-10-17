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

import ch.ntb.inf.deep.runtime.util.Matrix;
import ch.ntb.inf.deep.runtime.util.Vector;
import ch.ntb.inf.deep.runtime.util.Vector3;
import ch.ntb.inf.deep.runtime.util.Vector4;
import ch.ntb.inf.junitTarget.Assert;
import ch.ntb.inf.junitTarget.CmdTransmitter;
import ch.ntb.inf.junitTarget.MaxErrors;
import ch.ntb.inf.junitTarget.Test;

/**
 * NTB 17.12.2013, Adam Bajric
 * 
 *         Changes:
 */

@MaxErrors(100)
public class UtilTest {
	
	@Test
	public static void vector1() {
		Vector v1 = new Vector(5);
		v1.set(3, 3.6);
		Assert.assertEquals("Test1", 3.6, v1.get(3), 0);
		Vector v2 = new Vector(5, true);
		v2.set(3, 3.6);
		Assert.assertEquals("Test2", 3.6, v2.get(3), 0);
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void vector2() {
		Vector v1 = new Vector(2, true);
		v1.set(1, 1.5); v1.set(2, 10.5);
		Vector res = new Vector(2);
		Vector v2 = new Vector(2);
		v2.set(1, 3); v2.set(2, 21);
		Vector v3 = new Vector(2);
		v3.set(1, 4); v3.set(2, -1);
		Assert.assertFalse("Test1", res.add(v1, v1, false));
		Assert.assertTrue("Test2", res.add(v1, v1, true));
		Assert.assertTrue("Test3", v2.isEqual(res));
		Assert.assertEquals("Test4", -4.5, v3.dot(v1), 0);
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void matrix1() {
		double[][] d1 = {{1, 2}, {3, 4}, {5, 6}};
		Matrix m1 = new Matrix(d1);
		double[][] d2 = {{1, 2}, {3, 4}, {5, 6}};
		Matrix m2 = new Matrix(d2);
		Matrix m3 = new Matrix(3, 2);
		Assert.assertTrue("Test1", m2.isEqual(m1));
		Assert.assertTrue("Test2", m1.isEqual(m2));
		Assert.assertFalse("Test3", m3.isEqual(m2));
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void matrix2() {
		double[][] d1 = {{1, 2, 3}, {4, 5, 6}};
		Matrix m1 = new Matrix(d1);
		double[][] d2 = {{1}, {2}, {3}};
		Matrix m2 = new Matrix(d2);
		Matrix m3 = new Matrix(2, 1);
		double[][] d4 = {{14}, {32}};
		Matrix m4 = new Matrix(d4);
		Assert.assertTrue("Test1", m3.multiply(m1, m2));
		Assert.assertEquals("Test2", 2, m1.getRowCount());
		Assert.assertEquals("Test3", 3, m1.getColumnCount());
		Assert.assertEquals("Test4", 3, m2.getRowCount());
		Assert.assertEquals("Test5", 1, m2.getColumnCount());
		Assert.assertEquals("Test6", 2, m3.getRowCount());
		Assert.assertEquals("Test7", 1, m3.getColumnCount());
		Assert.assertEquals("Test8", 14, m3.get(1, 1), 0);
		Assert.assertEquals("Test9", 32, m3.get(2, 1), 0);
		Assert.assertTrue("Test10", m4.isEqual(m3));
		CmdTransmitter.sendDone();
	}	

	@Test
	public static void matrix3() {
		double[][] d1 = {{1, 2, 3}, {4, 5, 6}};
		Matrix m1 = new Matrix(d1);
		double[][] d2 = {{2, 4, 6}, {8, 10, 12}};
		Matrix m2 = new Matrix(d2);
		Assert.assertTrue("Test1", m1.add(m1, m1));
		Assert.assertTrue("Test2", m2.isEqual(m1));
		CmdTransmitter.sendDone();
	}	

	@SuppressWarnings("unused")
	@Test
	public static void matrix4(){
		Matrix A = Matrix.rot3x(30);
        Matrix At = new Matrix(3, 3);
        Vector x = new Vector(3, true);
        x.set(1, 1);
        x.set(2, 2);
        x.set(3, 3);
        Vector y = new Vector(3, true);
        
        y.multiply(A, x);

        At.transpose(A);
        At.transpose();
        Assert.assertTrue("equals", At.isEqual(A));
        
        Matrix B = new Matrix(2, 2);
        B.set(1, 1, 1);
        B.set(1, 2, 1);
        B.set(1, 3, 1);
        
        @SuppressWarnings("unused")
		Vector v1 = new Vector(3);
        Vector3 v2 = new Vector3(1, 2, 3);
        Vector4 v4 = new Vector4(4, 3, 2, 1);
        Vector v5 = new Vector(4);
        
        Assert.assertFalse("multiply", v4.multiply(v2, v5));
        
        Vector3 a = new Vector3(7, 8, 9);
        Vector3 b = new Vector3(-1, 1, -2);
        Vector3 c = new Vector3();
        c.add(a, b);

        CmdTransmitter.sendDone();
	}
}
