package testful.gui;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import testful.ConfigCut;
import testful.gui.operator.CreateXmlModel;
import testful.gui.operator.Result;

public class ActionStartWizard implements IObjectActionDelegate {

	private Shell shell;
	private ISelection selection;

	public ActionStartWizard() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	@Override
	public void run(IAction action) {
		try {

			Result result;
			String 	elemPath = Util.getISelectionPath(((IStructuredSelection)selection).getFirstElement()),
			subDir = Util.getSourceFolderPath(elemPath),
			cut = Util.getClassName(Util.getClassPath(elemPath));

			//If not exist XML Model, it will be create
			if (!(new File(Util.WORKSPACEDIR+elemPath.replace(".java", ".xml")).exists())) {
				String[] s = {subDir};
				String[] c = {cut};
				CreateXmlModel createXmlModel = new CreateXmlModel(s, c);
				createXmlModel.run();
				result = createXmlModel.Result();
				if (!result.isSuccess)
					throw new Exception("Unable to create the Testful XML model.\n" + result.message);
			}
			//new TestfulWizardOld(shell.getDisplay(), Util.WORKSPACEDIR + subDir, cut);
			ConfigCut config = new ConfigCut();
			config.setDirBase(new File(Util.WORKSPACEDIR + subDir));
			config.setCut(cut);
			TestfulWizard wizard = new TestfulWizard(config);

			WizardDialog dialog = new WizardDialog(new Shell(SWT.TITLE | SWT.CLOSE), wizard){
				@Override
				protected void configureShell(Shell newShell) {
					super.configureShell(newShell);
					newShell.setSize(600, 500);

				}
			};
			dialog.setBlockOnOpen(true);
			int returnCode = dialog.open();
			if(returnCode == Dialog.OK)
				System.out.println("Ok");
			else
				System.out.println("Cancelled");

		} catch (Exception e) {
			MessageDialog.openError(shell, "Testful", "Unable to complete wizard!");
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
