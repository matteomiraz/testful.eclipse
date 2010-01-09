package testful.gui.wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import testful.IConfigCut;
import testful.TestfulException;
import testful.IConfigProject.LogLevel;
import testful.evolutionary.ConfigEvolutionary;
import testful.gui.GenerationJob;

public class TestfulWizard extends Wizard {

	private final ConfigEvolutionary config;

	public TestfulWizard(IConfigCut cut) throws TestfulException {
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
	}

	@Override
	public void addPages() {
		try {
			addPage(new PageXmlDescription(config));
			addPage(new PageTestful(config));
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "TestFul", "Cannot start the wizard for " + config.getCut() + ":\n" + e.getMessage());
		}

	}

	@Override
	public boolean performFinish() {
		try {

			GenerationJob test = new GenerationJob(config, getShell());
			test.setUser(true);
			test.schedule();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}