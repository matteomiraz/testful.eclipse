package testful.gui.operator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import testful.IUpdate;
import testful.evolutionary.IConfigEvolutionary;
import testful.evolutionary.jMetal.Launcher;

public class TestGenerator extends Job implements ITestfulOperator{

	private IConfigEvolutionary config;

	/**
	 * @param config Configuration associated with the class to test
	 */
	public TestGenerator(IConfigEvolutionary config) {
		super("TestFul");
		this.config = config;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {
			monitor.beginTask("Generating test cases: ", config.getTime()*1000);

			Launcher.run(config, new IUpdate.Callback() {

				private long lastUpdate = -1;

				@Override
				public void update(long start, long current, long end) {
					if(lastUpdate < 0) lastUpdate = start;

					if(current > end) {
						monitor.beginTask("Generating test cases: ", IProgressMonitor.UNKNOWN);
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

			try {
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}

			// TODO: MessageDialog.openInformation(shell, "Testful", "Tests generated correctly");

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: MessageDialog.openError(shell, "Testful", e.getMessage());
		}

		monitor.done();

		return Status.OK_STATUS;
	}

	/**
	 * @return the Configuration associated with the class to test
	 */
	public IConfigEvolutionary getConfig() {
		return config;
	}

	@Override
	public Result Result() {
		return null;
	}
}
