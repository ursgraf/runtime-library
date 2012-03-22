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
