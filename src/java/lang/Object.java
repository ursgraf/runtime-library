/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang;

import ch.ntb.inf.deep.unsafe.US;

/**
 * The root class of the Java class hierarchy. All non-primitive types
 * (including arrays) inherit either directly or indirectly from this class.
 *
 * This class is only partly implemented as we currently have no reflection and synchronization
 */
/* Changes:
 * 2.6.2014	Urs Graf	initial import and modified
 */
public class Object {

    /**
     * Constructs a new instance of {@code Object}.
     */
    public Object() {
    }

    /**
     * Returns an integer hash code for this object. By contract, any two
     * objects for which {@link #equals} returns {@code true} must return
     * the same hash code value. This means that subclasses of {@code Object}
     * usually override both methods or neither method.
     *
     * <p>Note that hash values must not change over time unless information used in equals
     * comparisons also changes.
     *
     * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_hashCode">Writing a correct
     * {@code hashCode} method</a>
     * if you intend implementing your own {@code hashCode} method.
     *
     * @return this object's hash code.
     * @see #equals
     */
    public int hashCode() {
    	return US.REF(this);
    }
	
    /**
     * Returns the unique instance of {@link Class} that represents this
     * object's class. Note that {@code getClass()} is a special case in that it
     * actually returns {@code Class<? extends Foo>} where {@code Foo} is the
     * erasure of the type of the expression {@code getClass()} was called upon.
     * <p>
     * As an example, the following code actually compiles, although one might
     * think it shouldn't:
     * <pre>{@code
     *   List<Integer> l = new ArrayList<Integer>();
     *   Class<? extends List> c = l.getClass();}</pre>
     *
     * @return this object's {@code Class} instance.
     */
    public final Class<?> getClass() {
		return null;
	}

    /**
     * Returns a string containing a concise, human-readable description of this
     * object. Subclasses are encouraged to override this method and provide an
     * implementation that takes into account the object's type and data. 
     *
     * @return a printable representation of this object.
     */
    public String toString() {
        return Integer.toHexString(hashCode());
    }

    /**
     * Compares this instance with the specified object and indicates if they
     * are equal. In order to be equal, {@code o} must represent the same object
     * as this instance using a class-specific comparison. The general contract
     * is that this comparison should be reflexive, symmetric, and transitive.
     * Also, no object reference other than null is equal to null.
     *
     * <p>The default implementation returns {@code true} only if {@code this ==
     * o}. See <a href="{@docRoot}reference/java/lang/Object.html#writing_equals">Writing a correct
     * {@code equals} method</a>
     * if you intend implementing your own {@code equals} method.
     *
     * <p>The general contract for the {@code equals} and {@link
     * #hashCode()} methods is that if {@code equals} returns {@code true} for
     * any two objects, then {@code hashCode()} must return the same value for
     * these objects. This means that subclasses of {@code Object} usually
     * override either both methods or neither of them.
     *
     * @param o
     *            the object to compare this instance with.
     * @return {@code true} if the specified object is equal to this {@code
     *         Object}; {@code false} otherwise.
     * @see #hashCode
     */
    public boolean equals(Object o) {
        return this == o;
    }

}
