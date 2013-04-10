package ch.ntb.inf.deep.runtime.mpc555.test;

import java.io.PrintStream;
import ch.ntb.inf.deep.runtime.mpc555.Task;
import ch.ntb.inf.deep.runtime.mpc555.driver.SCI2;

public class EnumTest1 extends Task {

	static State state = State.STARTING;

	public void action() {
		switch (state) {
		case STARTING: state = State.RUNNING; break;
		case RUNNING: state = State.STOPPING; break;
		case STOPPING: state = State.STOPPED; break;
		case STOPPED: state = State.STARTING; break;
		}

		if (this.nofActivations % 101 == 0) {
			System.out.println(state.name());
		}
	}
	
	static void test1() {
		System.out.println(State.STOPPING.name());
		System.out.println(State.STOPPING.toString());
		System.out.println(State.valueOf("STOPPING").name());
		System.out.print("ordinal of STOPPING = "); System.out.println(State.STOPPING.ordinal());
	}

	static {	
		SCI2.start(9600, SCI2.NO_PARITY, (short)8);
		System.out = new PrintStream(SCI2.out);
		System.out.println("test started");
		test1();
		System.out.println("test method done");
		Task t = new EnumTest1();	
		t.period = 10;
		Task.install(t);
	}
}

enum State {STARTING, RUNNING, STOPPING, STOPPED}
