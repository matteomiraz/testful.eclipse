package testful.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import testful.gui.operator.CreateXmlModel;
import testful.gui.operator.Result;

public class ActionCreateXMLModel implements IObjectActionDelegate {

	private Shell shell;
	private ISelection selection;

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public void run(IAction action) {
		if (!selection.isEmpty()) {
			try {
				List<String> classes = new ArrayList<String>();
				List<String> sourceFolders = new ArrayList<String>();
				Iterator elements = ((IStructuredSelection)selection).iterator();

				while(elements.hasNext())
					addClass(sourceFolders, classes, Util.getISelectionPath(elements.next()));

				CreateXmlModel createXmlModel = new CreateXmlModel(sourceFolders.toArray(new String[0]), classes.toArray(new String[0]));
				createXmlModel.run();
				Result result = createXmlModel.Result();
				if (!result.isSuccess)
					throw new Exception(result.message);
				MessageDialog.openInformation(shell, "Testful", result.message);

			} catch (Exception e) {
				MessageDialog.openError(
						shell,
						"Testful",
						"Impossible to complete operation!\n" + e.getMessage()
				);
				e.printStackTrace();
			}

		} else
			MessageDialog.openWarning (shell, "Testful", "No class selected!");
	}

	private boolean addClass(List<String> sourceFolders, List<String> classes, String path) {
		try {

			String	classPath = Util.getClassPath(path),
			sourceFolder = Util.getSourceFolderPath(path),
			binClassFullPath = Util.WORKSPACEDIR+path.replace(File.separatorChar+Util.SOURCEFOLDER+File.separator, File.separatorChar+Util.BINARYFOLDER+File.separator).replace(".java", ".class");

			if (!(new File(binClassFullPath)).exists()) {
				MessageDialog.openWarning(
						shell,
						"Testful - XML model",
						"To create the Testful XML model of \""+ Util.getClassName(classPath) +"\" you must first compile it!"
				);
				return false;
			}

			if (new File(Util.WORKSPACEDIR+path.replace(".java", ".xml")).exists()) {
				MessageBox msgBox =new MessageBox(shell, SWT.YES|SWT.NO|SWT.ICON_WARNING);
				msgBox.setText("Testful - XML model");
				msgBox.setMessage("\""+Util.getClassName(classPath)+"\" has already a Testful XML model.\nDo you want to create a new model?\n(The existing model will be overwrite)");
				if (msgBox.open() == SWT.NO) {
					return false;
				}
			}

			sourceFolders.add(sourceFolder);
			classes.add(Util.getClassName(classPath));
			return true;


		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}