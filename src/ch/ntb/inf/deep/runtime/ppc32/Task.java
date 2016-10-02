/*
 * Copyright 2011 - 2014 NTB University of Applied Sciences in Technology
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

package ch.ntb.inf.deep.runtime.ppc32;

import ch.ntb.inf.deep.runtime.Kernel;
import ch.ntb.inf.deep.runtime.util.Actionable;
import ch.ntb.inf.deep.unsafe.US;

/* changes:
 * 11.11.10	NTB/Urs Graf	creation
 * 20.3.12	NTB/Urs Graf	Interface Actionable added
 */
/**
 * This class implements a simple non-preemptive tasking system.
 * Each task runs to completion. After this the next task in the ready queue will
 * run. Tasks with a period equal to 0 will be rescheduled immediately after completion 
 * Tasks with a period greater than 0 will be scheduled when their time has come.
 * 
 * @author urs.graf@ntb.ch 
 */
public class Task implements Actionable, Ippc32 {
	public static final int maxNofTasks = 32;
	
	public static boolean done;	/** previous operation successfully completed */

	/**
	 * first error occurred, see this error list <br>
	 * <i> 1</i>: error at installation <br>
	 * <i> 2</i>: too many tasks <br>
	 * <i> 3</i>: task is already or still installed
	 */
	public static int firstErr;
	
	private static int nofPerTasks, nofReadyTasks, curRdyTask, nofActionables;
	private static Task[] tasks = new Task[maxNofTasks+2];	// periodic tasks
	private static Task lowestPrioStub = new Task(); // to be put at the end of the prioQ when dequeueing a task
	private static Task highestPrioStub = new Task(); // to be put at the front of the prioQ (periodic Task[0])
	private static Actionable[] actionables = new Actionable[maxNofTasks];	
	
	/** time:	0 &lt;= time : start time in ms from install time */
	public int time;

	/** 
	 * period:	0 &lt;= period : period time in ms<br>
	 * The period is must be specified before installation of the task by calling <code>install</code>.
	 * Subsequent modifications of this value do not have any effects!
	 */
	public int period;

	/** number of activations */
	public int nofActivations;

	/** safe=FALSE -&gt; task gets removed on trap, currently not used */
	public boolean safe;
	
	private boolean installed;
	private long nextTime;
	private long periodUs;
	private int actionable = -1;
	private static boolean mark = true;	// phase of garbage collection, start with mark phase
	
	/**
	 * Creates a new <i>Task</i>. <br>
	 * It's action method will be called by the task scheduler
	 */
	public Task() {
	}
	
	/**
	 * Creates a new <i>Task</i>. <br>
	 * The action method of the parameter <i>Actionable</i> will be called by the task scheduler
	 * @param act Actionable to be installed.
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
	 * Action to be performed by the task
	 */
	public void action() {
	}

	/**
	 * Returns system time in milliseconds, time starts at powerup
	 * @return Current time in ms.
	 */
	public static int time() {
		return (int)(Kernel.time() / 1000);
	}

	/**
	 * Installs a new <i>Task</i>. <br>
	 * @param task Task to be installed.
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

	/** 
	 * Removes an installed task
 	 * @param task Task to be removed.
	 */
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
			try {
				if (cmd != -1) {
					US.PUTSPR(LR, cmd);	
					US.ASM("bclrl always, 0");
					Kernel.cmdAddr = -1;
				}
			} catch (Exception e) {
				Kernel.cmdAddr = -1;	// stop trying to run the same method
				e.printStackTrace();
				Kernel.blink(1);
			}
			if (Heap.runGC) {
				if (mark) {Heap.mark(); mark = false;}
				else {Heap.sweep(); mark = true; Heap.runGC = false;}
			}
			long time = Kernel.time();
			currentTask = tasks[1];
			if (currentTask.nextTime < time) {
				currentTask.nofActivations++;
				try {
					if (currentTask.actionable < 0)	currentTask.action();
					else actionables[currentTask.actionable].action();
				} catch (Exception e) {
					Kernel.cmdAddr = -1;	// stop trying to run the same method
					e.printStackTrace();
					Kernel.blink(3);
				}
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
		Kernel.loopAddr = US.ADR_OF_METHOD("ch/ntb/inf/deep/runtime/ppc32/Task/loop");
	}
	
}
