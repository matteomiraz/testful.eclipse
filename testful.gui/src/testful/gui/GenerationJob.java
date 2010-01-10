package testful.gui;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.IProgressConstants;

import testful.IUpdate;
import testful.TestfulException;
import testful.core.Instrument;
import testful.evolutionary.ConfigEvolutionary;
import testful.evolutionary.jMetal.Launcher;
import testful.utils.FileUtils;

public class GenerationJob extends Job {

	private final IResource projectResource;
	private final ConfigEvolutionary config;
	private Shell shell;

	/**
	 * @param config Configuration associated with the class to test
	 */
	public GenerationJob(ConfigEvolutionary config, IResource projectResource, Shell shell) {
		super("TestFul");
		this.config = config;
		this.shell = shell;
		this.projectResource= projectResource;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Generating tests for " + config.getCut(), 100);


		File tmp = null;
		try {
			tmp = FileUtils.createTempDir("testful-", null);

			config.setDirInstrumented(tmp);

			instrument(new SubProgressMonitor(monitor, 5));
			generate(new SubProgressMonitor(monitor, 95));

			try {
				projectResource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}

			if (isModal(this)) {
				showResults();
			} else {
				setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
				setProperty(IProgressConstants.ACTION_PROPERTY,
						getGenerationCompleteAction());
			}


			return new Status(IStatus.OK, Activator.PLUGIN_ID, "TestFul successfully generated tests for " + config.getCut());

		} catch (Exception e) {
			e.printStackTrace();
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage());
		} finally {
			monitor.done();
			FileUtils.deleteRecursive(tmp);
		}
	}

	private void generate(final IProgressMonitor monitor) throws TestfulException {
		monitor.beginTask("TestFul", config.getTime() * 1000);
		monitor.subTask("starting the evolutionary engine...");

		Launcher.run(config, new IUpdate.Callback() {

			private long lastUpdate = -1;

			@Override
			public void update(long start, long current, long end) {
				if(lastUpdate < 0) lastUpdate = start;

				if(current > end) {
					monitor.subTask("saving...");
				} else {
					monitor.worked((int) (current-lastUpdate));
					lastUpdate = current;

					final int r = (int) (end - current) / 1000;
					monitor.subTask("generating tests (" +
							((r / 60) > 0 ? (r / 60) + " minutes " : "" )
							+ (r % 60) + " seconds remaining)");
				}
			}
		});
	}

	private void instrument(final IProgressMonitor monitor) throws Exception {
		monitor.beginTask("TestFul", IProgressMonitor.UNKNOWN);
		monitor.subTask("Instrumenting...");
		try {

			StringBuilder msg = new StringBuilder();
			boolean ok = Instrument.instrument(
					config.getCut(),
					config.getDirSource().getAbsolutePath(),
					config.getDirCompiled().getAbsolutePath(),
					config.getDirContracts().getAbsolutePath(),
					config.getDirInstrumented().getAbsolutePath(), msg);

			if(!ok) throw new Exception(msg.toString());

		} finally {
			monitor.done();
		}
	}

	public boolean isModal(Job job) {
		Boolean isModal = (Boolean)job.getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
		if(isModal == null) return false;
		return isModal.booleanValue();
	}

	protected Action getGenerationCompleteAction() {
		return new Action("TestFul: Generation Complete") {
			@Override
			public void run() {
				MessageDialog.openInformation(shell,
						"TestFul: Generation Complete",
						"TestFul has successfully generated tests for " + config.getCut());
			}
		};
	}

	protected void showResults() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				getGenerationCompleteAction().run();
			}
		});
	}
}
