package testful.gui.operator;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import testful.Configuration;
import testful.IUpdate;
import testful.evolutionary.jMetal.Launcher;
import testful.gui.ShellTestfulResult;

public class TestGenerator extends Job implements ITestfulOperator{

	private int time = IProgressMonitor.UNKNOWN;
	private Configuration config;
	private String[] args;
	private Result result = null;

	/**
	 * Creates a new TestGenerator job with the specified name.
	 * @see Job#Job(String)
	 * @param name the name of the job.
	 */
	public TestGenerator(String name) {
		super(name);
	}

	/**
	 * @param name The name of job
	 * @param config Configuration associated with the class to test
	 * @param args The same arguments to pass to Testful.jar
	 */
	public TestGenerator(String name, Configuration config, String[] args) {
		this(name);
		setConfig(config);
		setArgs(args);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		String logFile = config.getDirGeneratedTests() + File.separator + config.getCut() + System.currentTimeMillis() + ".log";

		try {
			monitor.beginTask("generating test cases: ", time*1000);

			Launcher.run(args, new IUpdate.Callback() {

				private long lastUpdate = -1;

				@Override
				public void update(long start, long current, long end, Map<String, Float> coverage) {
					if(lastUpdate < 0) lastUpdate = start;

					if(current > end) {
						monitor.beginTask("Testful is generating test cases: ", IProgressMonitor.UNKNOWN);
						monitor.subTask("Saving...");
					} else {
						monitor.worked((int) (current-lastUpdate));
						lastUpdate = current;

						final int r = (int) (end - current) / 1000;
						monitor.subTask(
								((r / 60) > 0 ? (r / 60) + " minutes " : "" )
								+ (r % 60) + " seconds remaining");
					}
				}

			});
			result = new Result(true, (Object)logFile);
		} catch (Exception e) {
			result =new Result(false, e.getMessage());
		}


		if (result==null) result = new Result(false, (Object)logFile);
		monitor.done();
		new OpenShellResult(result).start();
		return Status.OK_STATUS;
	}

	/**
	 * @param config Configuration associated with the class to test
	 */
	public void setConfig(Configuration config) {
		this.config = config;
	}

	/**
	 * @return the Configuration associated with the class to test
	 */
	public Configuration getConfig() {
		return config;
	}

	/**
	 * @param args The same args to pass to Testful.jar
	 */
	public void setArgs(String[] args) {
		this.args = args;
	}

	/**
	 * @return the args
	 */
	public String[] getArgs() {
		return args;
	}

	@Override
	public Result Result() {
		return result;
	}

	public void setTime(int totalTime) {
		time = totalTime;
	}

	private class OpenShellResult extends Thread {

		private Result result;

		public OpenShellResult (Result result) {
			this.result = result;
		}

		@Override
		public void run() {
			ShellTestfulResult tr = new ShellTestfulResult(Display.getDefault(), result);
			tr.run();
		}
	}
}
