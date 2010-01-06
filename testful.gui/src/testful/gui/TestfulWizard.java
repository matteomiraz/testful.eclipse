package testful.gui;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import testful.ConfigCut;
import testful.TestfulException;
import testful.gui.TestfulImage.IMAGE;
import testful.model.xml.Parser;
import testful.model.xml.XmlClass;

public class TestfulWizard extends Wizard {

	private boolean isFirstButtonUpdate = false;
	private IWizardPage currentPage;

	private PageXMLModel pXml;
	private PageAuxilaryClass pAux;
	private PageInstrumenter pInst;
	private PageTestful pTest;

	public TestfulWizard() {
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(Activator.getDefault().getBundle(), new Path(IMAGE.TESTFUL_WIZARD_BIG.path()),null)));
	}

	public TestfulWizard(ConfigCut config) {
		this();
		setWindowTitle("Testful - " + config.getCut() + " - Wizard");
		try {

			XmlClass xmlClass = new XmlClass();
			xmlClass = Parser.singleton.parse(config, config.getCut());
			xmlClass.setInstrument(true);

			pXml = new PageXMLModel(config, xmlClass, false);
			pAux = new PageAuxilaryClass(config, xmlClass);
			pInst = new PageInstrumenter(config);
			pTest = new PageTestful(config);

		} catch (JAXBException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Testful - Error", "Impossible to open Xml Model!\n" + e.getMessage());
		} catch (TestfulException e) {
			MessageDialog.openError(getShell(), "Testful - Error", e.getMessage());
		}
	}

	@Override
	public void addPages() {
		addPage(pXml);
		addPage(pAux);
		addPage(pInst);
		addPage(pTest);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		// the default behavior is to create all the pages controls
		for (int i = 0; i < getPages().length; i++) {
			IWizardPage page = getPages()[i];
			page.createControl(pageContainer);
			// page is responsible for ensuring the created control is
			// accessable
			// via getControl.
			Assert.isNotNull(page.getControl());
		}
		((ITestfulWizardPage)getPages()[0]).start();
		currentPage = getPages()[0];
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage =  super.getNextPage(page);
		checkNextPage(page, nextPage);
		return nextPage;
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage prevPage =  super.getPreviousPage(page);
		checkPrevPage(page);
		return prevPage;
	}

	@Override
	public boolean performFinish() {
		if (currentPage == pXml || currentPage == pAux) {
			pInst.finish();
		}
		pTest.finish();
		return true;
	}

	private void checkNextPage(IWizardPage page, IWizardPage nextPage) {
		if (isFirstButtonUpdate) {
			if (page == currentPage) {
				((ITestfulWizardPage)currentPage).finish();
				currentPage = nextPage;
				((ITestfulWizardPage)currentPage).start();
				isFirstButtonUpdate = false;
			} else currentPage = page;
		} else isFirstButtonUpdate = true;
	}

	private void checkPrevPage(IWizardPage page) {
		if (isFirstButtonUpdate) {
			currentPage = page;
			isFirstButtonUpdate = false;
		}
	}

}
