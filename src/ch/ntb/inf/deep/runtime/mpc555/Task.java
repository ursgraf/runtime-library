package ch.ntb.inf.deep.runtime.mpc555;

import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2Plain;
import ch.ntb.inf.deep.unsafe.US;

/*changes:
 * 11.11.10	NTB/GRAU	porting from Pascal
 */

public class Task {
//	public static final int maxNofTasks = 32;
	
	public static boolean done;	/** previous operation successfully completed */

	/**
	 * first error occurred, see this error list <br>
	 * <i> 1</i>: error at installation <br>
	 * <i> 2</i>: too many tasks <br>
	 * <i> 3</i>: task is already or still installed
	 */
	public static int firstErr;
	
	private static int nofPerTasks, nofReadyTasks, curRdyTask, curTask;
	private static Task[] tasks = new Task[32+2];	// periodic tasks
	private static Task lowestPrioStub = new Task(); // to be put at the end of the prioQ when dequeueing a task
	private static Task highestPrioStub = new Task(); // to be put at the front of the prioQ (periodic Task[0])
	
	/** time:	0 <= time : start time in ms from install time */
	public int time;

	/** period:	0 <= period : period time in ms */
	public int period;

	/** number of activations */
	public int nofActivations;

	public int minExecTimeInMicro;

	public int maxExecTimeInMicro;

	/** safe=FALSE -> task gets removed on trap, currently not used */
	public boolean safe;
	
	/** getTime=TRUE -> nofActivations, minExecTimeInMicro, maxExecTimeInMicro get updated */
	public boolean getTime;
	
	/**  for periodic tasks only: 
	 * 		if ((period > 0) && coalesce) 
	 *			if (nextTime < currentTime) nextTime = startTime+period
	 */ 
	public boolean coalesce;
	
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
		else {
			long time = Kernel.time();
			if (task.time > 0 || task.period > 0) {
				task.nextTime = time + task.time*1000;
				task.nofActivations = 0;
				task.periodUs = (long)(task.period) * 1000;
				task.minExecTimeInMicro = 0x7fffffff;
//			US.ASM("b 0");
				enqueuePeriodicTask(task);
			} else {
				nofReadyTasks++;
				tasks[tasks.length - nofReadyTasks] = task;
			}
		}
		task.installed = true;
	}

	private static void enqueuePeriodicTask(Task task) {
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
		// TODO Auto-generated method stub
		
	}

	private static void requeuePerTask() {
		if (nofPerTasks > 1) {
			Task head = tasks[1];
			int fath = 1, son;
			while (true) {
				son = fath * 2;
				if (son > nofPerTasks) break;
				if (tasks[son].nextTime > tasks[son + 1].nextTime) son++; // son = right son
				if (head.nextTime < tasks[son].nextTime) break;
				else {tasks[fath] = tasks[son]; fath = son;}
			}
			tasks[fath] = head;
		}
	}

	static void loop() {
		SCI2Plain.write((byte)'2');
		Task currentTask;
		while(true) {
			long time = Kernel.time();
			currentTask = tasks[1];
				int temp1 = US.GETGPR(31);
			if (currentTask.nextTime < time) {
				SCI2Plain.write((byte)'3');
				curTask = 1;
				currentTask.nofActivations++;
				SCI2Plain.write((byte)'a');
				int temp2 = US.GETGPR(31);
				US.ASM("b 0");
				currentTask.action();
				if (currentTask.installed) {
					currentTask.nextTime += currentTask.periodUs;
					requeuePerTask();
				}
			}
		}
	}
	
	static {
		done = true; 
		firstErr = 0;
		nofPerTasks = 0;
		nofReadyTasks = 0;
		curRdyTask = tasks.length;
		lowestPrioStub.nextTime = 0x7fffffff;
		highestPrioStub.nextTime = 0x80000000;
		tasks[0] = highestPrioStub;
		for (int i = 1; i < tasks.length; i++) tasks[i] = lowestPrioStub;
		Kernel.loopAddr = 0x803324;
	}
	
}
