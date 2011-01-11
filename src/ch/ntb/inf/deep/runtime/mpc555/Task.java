package ch.ntb.inf.deep.runtime.mpc555;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2Plain;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 11.11.10	NTB/GRAU	porting from Pascal
 */

public class Task { //implements ntbMpc555HB {
	public static final int maxNofTasks = 32;
	
	public static boolean done;	/** previous operation successfully completed */

	/**
	 * first error occurred, see this error list <br>
	 * <i> 1</i>: error at installation <br>
	 * <i> 2</i>: too many tasks <br>
	 * <i> 3</i>: task is already or still installed
	 */
	public static int firstErr;
	
	private static int nofPerTasks, nofReadyTasks, curRdyTask, curTask;
//	private static Task[] tasks = new Task[maxNofTasks+2];	// periodic tasks
	private static Task[] tasks = new Task[32+2];	// periodic tasks
	private static Task lowestPrioStub = new Task(); // to be put at the end of the prioQ when dequeueing a task
	private static Task highestPrioStub = new Task(); // to be put at the front of the prioQ (periodic Task[0])
	
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

	public Task() {
	}
	
	static void error(int n) {
		if (firstErr == 0) firstErr = n;
		done = false;
	}

	/**
	 * action to be performed by the task
	 */
	public void action() {
	}

	/**
	 * returns system time in milliseconds, time starts at powerup
	 */
	public static int time() {
//		return (int)(Kernel.time() / 1000);
		return (int)(Kernel.time() >> 10);
	}

	/**
	 * installs a new <i>Task</i>. <br>
	 */
	public static void install(Task task) {
		remove(task);
		if ((task.time < 0) || (task.period < 0)) error(1);
		if (nofPerTasks + nofReadyTasks >= 32) error(2);
//		if (nofPerTasks + nofReadyTasks >= maxNofTasks) error(2);
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
//		done = done && (nofPerTasks + nofReadyTasks < maxNofTasks);
		done = done && (nofPerTasks + nofReadyTasks < 32);
		if (done) {
			nofPerTasks++; 
			int n = nofPerTasks;
			while (task.nextTime < tasks[n >> 1].nextTime) {
				tasks[n] = tasks[n >> 1]; n = n >> 1;
			}
			tasks[n] = task;
		}
	}

	/** removes an installed task */
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
			if (cmd != 0) {
				US.PUTSPR(8, cmd);	// use ntb555HB later
				US.ASM("bclrl always, 0");
				Kernel.cmdAddr = 0;
			}
			long time = Kernel.time();
			currentTask = tasks[1];
			if (currentTask.nextTime < time) {
				curTask = 1;
				currentTask.nofActivations++;
				currentTask.action();
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
				currentTask.action();
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
