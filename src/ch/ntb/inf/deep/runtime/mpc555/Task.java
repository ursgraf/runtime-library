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

package ch.ntb.inf.deep.runtime.mpc555;

import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 * 20.3.12	NTB/Urs Graf	Interface Actionable added
 */

public class Task implements Actionable, ntbMpc555HB {
	public static final int maxNofTasks = 32;
	
	public static boolean done;	/** previous operation successfully completed */

	/**
	 * first error occurred, see this error list <br>
	 * <i> 1</i>: error at installation <br>
	 * <i> 2</i>: too many tasks <br>
	 * <i> 3</i>: task is already or still installed
	 */
	public static int firstErr;
	
	private static int nofPerTasks, nofReadyTasks, curRdyTask, curTask, nofActionables;
	private static Task[] tasks = new Task[maxNofTasks+2];	// periodic tasks
	private static Task lowestPrioStub = new Task(); // to be put at the end of the prioQ when dequeueing a task
	private static Task highestPrioStub = new Task(); // to be put at the front of the prioQ (periodic Task[0])
	private static Actionable[] actionables = new Actionable[maxNofTasks];	
	
	/** time:	0 <= time : start time in ms from install time */
	public int time;

	/** period:	0 <= period : period time in ms */
	public int period;

	/** number of activations */
	public int nofActivations;

	/** safe=FALSE -> task gets removed on trap, currently not used */
	public boolean safe;
	
	private boolean installed;
	private long nextTime;
	private long periodUs;
	private int actionable = -1;

	/**
	 * Creates a new <i>Task</i>. <br>
	 * It's action method will be called by the task scheduler
	 */
	public Task() {
	}
	
	/**
	 * Creates a new <i>Task</i>. <br>
	 * The action method of the parameter <i>Actionable<i> will be called by the task scheduler
	 */
	public Task(Actionable act) {
		actionable = nofActionables;
		actionables[nofActionables++] = act;
	}
	
	static void error(int n) {
		if (firstErr == 0) firstErr = n;
		done = false;
	}

	/**
	 * Aaction to be performed by the task
	 */
	public void action() {
	}

	/**
	 * Returns system time in milliseconds, time starts at powerup
	 */
	public static int time() {
		return (int)(Kernel.time() / 1000);
	}

	/**
	 * Installs a new <i>Task</i>. <br>
	 */
	public static void install(Task task) {
		remove(task);
		if ((task.time < 0) || (task.period < 0)) error(1);
		if (nofPerTasks + nofReadyTasks >= maxNofTasks) error(2);
		else {
			long time = Kernel.time();
			if (task.time > 0 || task.period > 0) {
				task.nextTime = time + task.time*1000;
				task.nofActivations = 0;
				task.periodUs = (long)(task.period) * 1000;
				enqueuePeriodicTask(task);
			} else {
				nofReadyTasks++;
				tasks[tasks.length - nofReadyTasks] = task;
			}
		}
		task.installed = true;
	}

	private static void enqueuePeriodicTask(Task task) {
		done = done && (nofPerTasks + nofReadyTasks < maxNofTasks);
		if (done) {
			nofPerTasks++; 
			int n = nofPerTasks;
			while (task.nextTime < tasks[n >> 1].nextTime) {
				tasks[n] = tasks[n >> 1]; n = n >> 1;
			}
			tasks[n] = task;
		}
	}

	/** Removes an installed task */
	public static void remove(Task task) {
		int remTaskNo = tasks.length - nofReadyTasks;
		while ((remTaskNo < tasks.length) && (tasks[remTaskNo] != task)) remTaskNo++;
		if (remTaskNo < tasks.length) {
			tasks[remTaskNo] = tasks[tasks.length - nofReadyTasks]; 
			nofReadyTasks--;
		} else
			removePeriodicTask(task);
		task.installed = false;
	}
	
	private static void removePeriodicTask(Task task) {
		int remTaskNo = nofPerTasks;
		int fath, son;
		while (remTaskNo > 0 && tasks[remTaskNo] != task) remTaskNo--;
		if (remTaskNo > 0) {
			if (remTaskNo == 1) {
				dequeuePeriodicTask();
			} else {
				Task last = tasks[nofPerTasks];
				tasks[nofPerTasks] = lowestPrioStub;
				if (remTaskNo == nofPerTasks) nofPerTasks--;
				else if (remTaskNo > 1) {
					nofPerTasks--;
					fath = remTaskNo + 0;
					while (last.nextTime < tasks[fath >> 1].nextTime) { // propagate to root
						tasks[fath] = tasks[fath >> 1]; fath = fath >> 1;
					}
					if (fath == remTaskNo) { // propagate to leaf
						while (true) {
							son = fath << 1;
							if (son > nofPerTasks) break;
							if (tasks[son].nextTime > tasks[son + 1].nextTime) son++; // son = right son
							if (last.nextTime < tasks[son].nextTime) break;
							else {tasks[fath] = tasks[son]; fath = son;}
						}
					}
					tasks[fath] = last;
				}
			}
		}
	}

	private static void dequeuePeriodicTask() {
		if (nofPerTasks > 1) {
			Task last = tasks[nofPerTasks];
			tasks[nofPerTasks] = lowestPrioStub;
			int fath = 1;
			nofPerTasks--;
			while (true) {
				int son = fath << 1;
				if (son > nofPerTasks) break;
				if (tasks[son].nextTime > tasks[son + 1].nextTime) son++; // son = right son
				if (last.nextTime < tasks[son].nextTime) break;
				else {tasks[fath] = tasks[son]; fath = son;}
			}
			tasks[fath] = last;
		} else if (nofPerTasks == 1) {
			tasks[1] = lowestPrioStub;
		}
	}

	private static void requeuePerTask() {
		if (nofPerTasks > 1) {
			Task head = tasks[1];
			int fath = 1, son;
			while (true) {
				son = fath << 1;
				if (son > nofPerTasks) break;
				if (tasks[son].nextTime > tasks[son + 1].nextTime) son++; // son = right son
				if (head.nextTime < tasks[son].nextTime) break;
				else {tasks[fath] = tasks[son]; fath = son;}
			}
			tasks[fath] = head;
		}
	}

	static void loop() {
		int cmd;
		Task currentTask;
		while(true) {
			cmd = Kernel.cmdAddr;
			if (cmd != -1) {
				US.PUTSPR(LR, cmd);	
				US.ASM("bclrl always, 0");
				Kernel.cmdAddr = -1;
			}
			long time = Kernel.time();
			currentTask = tasks[1];
			if (currentTask.nextTime < time) {
				curTask = 1;
				currentTask.nofActivations++;
				if (currentTask.actionable < 0)	currentTask.action();
				else actionables[currentTask.actionable].action();
				if (currentTask.installed) {
					if (currentTask.period == 0) {
						nofReadyTasks++;
						tasks[tasks.length - nofReadyTasks] = tasks[1];
						dequeuePeriodicTask();
					} else {
						currentTask.nextTime += currentTask.periodUs;
						requeuePerTask();
					}
				}
			} else if (nofReadyTasks > 0) {
				curRdyTask++;
				if (curRdyTask >= tasks.length) curRdyTask = tasks.length - nofReadyTasks;
				currentTask = tasks[curRdyTask];
				currentTask.nofActivations++;
				if (currentTask.actionable < 0)	currentTask.action();
				else actionables[currentTask.actionable].action();
			}
		}
	}
	
	static {
		done = true; 
		firstErr = 0;
		nofPerTasks = 0;
		nofReadyTasks = 0;
		curRdyTask = tasks.length;
		lowestPrioStub.nextTime = Long.MAX_VALUE;
		highestPrioStub.nextTime = Long.MIN_VALUE;
		tasks[0] = highestPrioStub;
		for (int i = 1; i < tasks.length; i++) tasks[i] = lowestPrioStub;
		Kernel.loopAddr = US.ADR_OF_METHOD("ch/ntb/inf/deep/runtime/mpc555/Task/loop");
	}
	
}
