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

package org.deepjava.runtime.mpc555.demo;

import org.deepjava.runtime.ppc32.Task;

/**
 * Demo application for the NTB Robi2. The Robi is driving around and avoids
 * collisions with obstacles.
 * 
 * This class serves only as a root class starting the real application. This
 * has been divided in order to make the demo itself reusable.
 */
public class Robi2ObstacleDemo {
	static {
		// Task initialization
		Task task = new Robi2ObstacleTask();
		Task.install(task);
	}
}