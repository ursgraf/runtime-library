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

package java.lang;

/**
 * The <code>Actionable</code> interface should be implemented by any
 * class whose instances are intended to be executed by a task. The
 * class must define a method of no arguments called <code>action</code>.
 * <p>
 * This interface is designed to provide a common protocol for objects that
 * wish to execute code while they are active. For example,
 * <code>Actionable</code> is implemented by class <code>Task</code>.
 * Being active simply means that a task has been started and has not
 * yet been stopped.
 * <p>
 * In addition, <code>Actionable</code> provides the means for a class to be
 * active while not subclassing <code>Task</code>. A class that implements
 * <code>Actionable</code> can run without subclassing <code>Task</code>
 * by instantiating a <code>Task</code> instance and passing itself in
 * as the target.  In most cases, the <code>Actionable</code> interface should
 * be used if you are only planning to override the <code>action()</code>
 * method and no other <code>Task</code> methods.
 * This is important because classes should not be subclassed
 * unless the programmer intends on modifying or enhancing the fundamental
 * behavior of the class.
 *
 * @author  Urs Graf
 * @see     jch.ntb.inf.deep.runtime.mpc555.Task
 */
public interface Actionable {
    /**
     * When an object implementing interface <code>Actionable</code> is used
     * to create a task, starting the task causes the object's
     * <code>action</code> method to be called in that separately executing
     * action.
     * <p>
     * The general contract of the method <code>action</code> is that it may
     * take any action whatsoever.
     *
     */
    public abstract void action();
}
