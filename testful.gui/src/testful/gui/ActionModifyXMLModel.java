package testful.gui;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import testful.Configuration;
import testful.gui.operator.LoadXmlModel;
import testful.gui.operator.Result;
import testful.model.xml.XmlClass;

public class ActionModifyXMLModel implements IObjectActionDelegate {

	private Shell shell;
	private ISelection selection;

	public ActionModifyXMLModel() {
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
		if (!selection.isEmpty()) {
			try {
				String path = Util.getISelectionPath(((IStructuredSelection)selection).getFirstElement());
				String baseDir = Util.WORKSPACEDIR + Util.getSourceFolderPath(path);
				String cut = Util.getClassName(Util.getClassPath(path));

				Configuration config = new Configuration(baseDir);
				config.setCut(cut);

				LoadXmlModel loadXmlModel = new LoadXmlModel(config);
				loadXmlModel.run();
				Result res = loadXmlModel.Result();
				if (res.isSuccess) {
					EditorXMLModel xme = new EditorXMLModel(config, (XmlClass)res.returned);
					xme.openShell(shell.getDisplay());
				} else MessageDialog.openWarning(shell, "Testful", res.message);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else
			MessageDialog.openWarning (shell, "Testful", "No class selected!");
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		try {
			if (selection.isEmpty()) action.setEnabled(false);
			else {
				IStructuredSelection iss = (IStructuredSelection) selection;
				String 	path = Util.getISelectionPath(iss.getFirstElement()),
				xmlFullPath = Util.WORKSPACEDIR+path.replace(File.separatorChar+Util.SOURCEFOLDER+File.separator, File.separatorChar+Util.BINARYFOLDER+File.separator).replace(".java", ".xml");
				action.setEnabled(new File(xmlFullPath).exists());
			}
		} catch (Exception e) {
			e.printStackTrace();
			action.setEnabled(false);
		}

	}

}
