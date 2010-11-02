package ch.ntb.inf.sts.mpc555;

/*changes:
 24.3.05	NTB/ED	Emulationsanweisungen gelöscht
 10.5.04	NTB/ED	new native interface
 19.3.04	NTB/sz	creation
 */
/**
 <ul>
 <li><b>Kategorie 1: </b>&nbsp;<i>nextActivation &lt;= timeInMs() &amp;&amp; period == 0</i><br>
 <i>Tasks</i> die immer bereit sind, werden zyklisch aktiviert, bis zu 50'000 Taks pro 
 Sekunde.</li>
 <li><b>Kategorie 2:</b>&nbsp; <i>nextActivation &gt; timeInMs()</i><br>
 Nächste Aktivierung erfolgt frühestens zum Zeitpunkt nextActivation == 
 timeInMs().<br>
 Nach der Aktivierung entscheidet die Instanzvariable <i>period</i>, ob er in die 
 Kategorie 1 oder 3 wechselt.</li>
 <li><b>Kategorie 3: </b>&nbsp;<i>period &gt; 0</i> (periodische Tasks)<br>
 <i>Tasks</i> werden periodisch aktiviert, die Zustandsvariable&nbsp; <i>nextActivation</i>&nbsp; legt 
 den Zeitpunkt der nächsten Aktivierung fest, beim gerade aktiven <i>Task</i> die 
 geplante Startzeit. <i>&nbsp;nextActivation</i>&nbsp; wird am Ende der 
 Aktivierung um den Wert von&nbsp; <i>period</i>&nbsp; erhöht.</li>
 <li><b>Kategorie 4:</b>&nbsp; <i>stop == true</i> (gestoppte Tasks)<br>
 <i>Tasks</i> die gestoppt wurden werden nicht mehr aktiviert und aus der Liste der 
 installierten <i>Taks</i> entfernt.<br>
 Wird ein <i>Task</i> während der aktiven Zeit gestoppt, ist es ihm überlassen, ob er 
 die gegenwärtige Aufgabe zu Ende bringt oder an geeigneter Stelle abbricht.</li>
 </ul>
 */
/**
 * Tasking System - Verwaltung von <i>Tasks</i>. <br>
 * Unter einem Task wird aus Sicht des Andwenders (Programmierers) eine Klasse
 * verstanden, die als Erweiterung der Klasse Task deklariert wurde: <i> class
 * MeinTask extends Task</i> Diese Klasse muss eine Methode <i>Do()</i> mit
 * der Signatur <i>void Do()</i> anbieten (siehe Beispiele in der
 * Fachausbildung).
 */
public class Task {
	private static final int $NATIVE = 0xCedeBead;

	private static byte // native method names, parameter like the public
			// methods in this class
			_0Init, Do, setStop, Install, Start, Time, Remove;

	// ---- read only class fields
	/**
	 * Zustandsvariable: zeigt an, ob schon Fehler aufgetreten sind. <br>
	 * Done == <i>true</i>: bis jetzt sind keine Fehler aufgetreten <br>
	 * Done == <i>false</i>: es sind Fehler aufgetreten: die Nummer des ersten
	 * Fehlers wurde in der Variablen <i>firstErr</i> registriert.
	 */
	public static boolean Done;

	/**
	 * Enthält die Nummer des ersten registrierten Fehlers. <br>
	 * <i> 1</i>: Fehler bei der Installation, z.B. negative Periodenlänge
	 * (period) <br>
	 * <i> 2</i>: Zu viele Task <br>
	 * <i> 3</i>: task ist bereits - oder noch immer - installiert
	 */
	public static short firstErr;

	// ---- Instanzvariablen
	/**
	 * Task-Startverzögerung [ms].
	 */
	public int time;

	/**
	 * Task-Periode [ms]. Wird die Periode während des Betriebs geändert, muss nach der 
	* Änderung von <code>Task.period</code> <code>Task.install(Task task)</code> aufgerufen werden. 
	* Ansonsten werden die Änderungen erst nach der nächsten Ausführung des Tasks übernommen.
	 */
	public int period;

	/**
	 * Anzahl Aktivierungen dieses Tasks. <br>
	 * wird jedesmal erhöht, wenn die Methode <i>Do()</i> vom Tasking
	 * aufgerufen wurde.
	 */
	public int nofActivations;

	public int minExecTimeInMicro;

	public int maxExecTimeInMicro;

	public boolean safe, getTime, coalesce;

	/**
	 * Erzeugung eines Tasks
	 */
	public Task() {
		_0Init = _0Init;
	}

	/**
	 * Aufgabe des Tasks: <i>Do()</i> beschreibt das Verhalten (die
	 * Funktionalität) eines Tasks.
	 */
	public void Do() {
		Do = Do;
	}

	/**
	 * Liefert die aktuelle Systemzeit in Millisekunden. Zeitmessung beginnt bei
	 * Systemstart.
	 */
	public static int time() {
		Time = Time;
		return 0;
	}

	/**
	 * Installiert (registriert) einen <i>Task</i>. <br>
	 * Wurde der Task durch einen anderen Task gestoppt, so kann er erst wieder
	 * installiert weren, nachdem der vom Taksking System aus der Liste der
	 * registrierten entfernt wurde (siehe auch <i>setStop()</i> ).
	 */
	public static void install(Task task) {
		Install = Install;
	}

	/**
	 * Entfernt einen bereits installierten <i>Task</i>.
	 * 
	 * @param task
	 *            Der zu entfernende Task.
	 */
	public static void remove(Task task) {
		Remove = Remove;
	}
}
