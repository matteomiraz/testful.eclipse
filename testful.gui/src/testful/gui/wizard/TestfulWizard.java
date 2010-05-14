package testful.gui.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import testful.IConfigCut;
import testful.IConfigProject.LogLevel;
import testful.TestfulException;
import testful.evolutionary.ConfigEvolutionary;
import testful.gui.Activator;
import testful.gui.GenerationJob;

public class TestfulWizard extends Wizard {

	private final ConfigEvolutionary config;
	private final IResource project;

	public TestfulWizard(IConfigCut cut, IResource projectResource) throws TestfulException {
		project = projectResource;

		config = new ConfigEvolutionary();
		config.setCut(cut.getCut());
		config.setDirBase(cut.getDirBase());
		config.setDirSource(cut.getDirSource());
		config.setDirCompiled(cut.getDirCompiled());
		config.setDirContracts(cut.getDirContracts());
		config.setDirInstrumented(cut.getDirInstrumented());

		config.setQuiet(true);
		config.setLog(null);
		config.setLogLevel(LogLevel.WARNING);

		setDefaultPageImageDescriptor(Activator.getImageDescriptor("testful_wizard_big.png"));
	}

	@Override
	public void addPages() {
		try {
			addPage(new PageXmlDescription(config, project));
			addPage(new PageTestful(config));
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "TestFul", "Cannot start the wizard for " + config.getCut() + ":\n" + e.getMessage());
		}

	}

	@Override
	public boolean performFinish() {
		try {

			GenerationJob test = new GenerationJob(config, project, getShell());
			test.setUser(true);
			test.schedule();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
