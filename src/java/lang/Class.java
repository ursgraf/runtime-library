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
/*
 * Copyright (C) 2006-2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang;

import java.io.Serializable;

import ch.ntb.inf.deep.marker.Modified;

/**
 * The in-memory representation of a Java class. 
 * This is maily a stub, as we currently do not support reflection.
 */
/* Changes:
 * 27.5.2014	Urs Graf	initial import and modified
 */
public final class Class<T> implements Serializable, Modified {

    private static final long serialVersionUID = 3206093459760846163L;

    /**
     * Lazily computed name of this class; always prefer calling getName().
     */
    private transient String name;

    private Class() {
        // Prevent this class to be instantiated, instance
        // should be created by JVM only
    }

    /**
     * Returns a {@code Class} object which represents the class with the
     * given name. The name should be the name of a non-primitive class, as described in
     * the {@link Class class definition}.
     * Primitive types can not be found using this method; use {@code int.class} or {@code Integer.TYPE} instead.
     *
     * <p>If the class has not yet been loaded, it is loaded and initialized
     * first. This is done through either the class loader of the calling class
     * or one of its parent class loaders. It is possible that a static initializer is run as
     * a result of this call.
     *
     * @param className Name of the class.
     * @return Class
     * @throws ClassNotFoundException
     *             if the requested class can not be found.
     * @throws LinkageError
     *             if an error occurs during linkage
     * @throws ExceptionInInitializerError
     *             if an exception occurs during static initialization of a
     *             class.
     */
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return null;
    }


    /**
     * Returns the name of the class represented by this {@code Class}. For a
     * description of the format which is used, see the class definition of
     * {@link Class}.
     * @return Name of the class.
     */
    public String getName() {
        return name;
    }

 
    @Override
    public String toString() {
    	return getName();
    }

//    /**
//     * Copies two arrays into one. Assumes that the destination array is large
//     * enough.
//     *
//     * @param result the destination array
//     * @param head the first source array
//     * @param tail the second source array
//     * @return the destination array, that is, result
//     */
//    private static <T extends Object> T[] arraycopy(T[] result, T[] head, T[] tail) {
//        System.arraycopy(head, 0, result, 0, head.length);
//        System.arraycopy(tail, 0, result, head.length, tail.length);
//        return result;
//    }

 
 }
