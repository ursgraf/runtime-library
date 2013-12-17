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
	public static void testMatrix(){
		Matrix A = Matrix.Rot3x(30);
        Matrix At = new Matrix(3, 3);
        Vector x = new Vector(3, true);
        x.set(1, 1);
        x.set(2, 2);
        x.set(3, 3);
        Vector y = new Vector(3, true);
        
        y.multiply(A, x);
        // TODO @Adam
       
    
        At.transpose(A);
        At.transpose();
        Assert.assertTrue("equals", At.isEqual(A));
        
        
        Matrix B = new Matrix(2, 2);
        B.set(1, 1, 1);
        B.set(1, 2, 1);
        B.set(1, 3, 1);
        
        Vector v1 = new Vector(3);
        Vector3 v2 = new Vector3(1, 2, 3);
        Vector4 v4 = new Vector4(4, 3, 2, 1);
        Vector v5 = new Vector(4);
        
        Assert.assertFalse("multiply", v4.multiply(v2, v5));
        
        Vector3 a = new Vector3(7, 8, 9);
        Vector3 b = new Vector3(-1, 1, -2);
        Vector3 c = new Vector3();
        c.add(a, b);

        // TODO @Adam
        CmdTransmitter.sendDone();
	}
}
