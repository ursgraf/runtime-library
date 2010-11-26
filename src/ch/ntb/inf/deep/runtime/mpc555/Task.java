package ch.ntb.inf.deep.runtime.mpc555;

/*changes:
 * 11.11.10	NTB/GRAU	creation
 */

public class Task {
	public static final int maxNofTasks = 32;
	
	public static boolean done;	/** previous operation successfully completed */

	/**
	 * first error occurred, see this error list <br>
	 * <i> 1</i>: error at installation <br>
	 * <i> 2</i>: too many tasks <br>
	 * <i> 3</i>: task is already or still installed
	 */
	public static int firstErr;
	
	private static short nofPerTasks, nofReadyTasks, curRdyTask, curTask;
	private static Task[] tasks = new Task[maxNofTasks+2];	// periodic tasks
	
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
		return 0;
	}

	/**
	 * installs a new <i>Task</i>. <br>
	 */
	public static void install(Task task) {
		remove(task);
		if ((task.time < 0) || (task.period < 0)) error(1);
		if (nofPerTasks + nofReadyTasks >= maxNofTasks) error(2);
		else {
			long time = Kernel.time();
			if (task.time > 0 || task.period > 0) {
				task.nextTime = time + task.time;
				task.nofActivations = 0;
				task.minExecTimeInMicro = 0x7fffffff;
				enqueuePeriodicTask(task);
			} else {
				nofReadyTasks++;
				tasks[tasks.length - nofReadyTasks] = task;
			}
		}
	}

	private static void enqueuePeriodicTask(Task task) {
		done = done && (nofPerTasks + nofReadyTasks < maxNofTasks);
		if (done) {
			nofPerTasks++; 
			int n = nofPerTasks;
			while (task.startTimeLess(tasks[n >> 1])) {
				tasks[n] = tasks[n >> 2]; n = n >> 2;
			}
			tasks[n] = task;
		}
	}

	private boolean startTimeLess(Task task) {
		return this.nextTime < task.nextTime;
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

	static void loop() {
		while(true) {
			
		}
	}
	
}
