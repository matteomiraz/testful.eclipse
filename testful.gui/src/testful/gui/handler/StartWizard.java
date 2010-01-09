package testful.gui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import testful.ConfigCut;
import testful.gui.wizard.TestfulWizard;

public class StartWizard extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);

		try {
			ConfigCut config = ConfigUtils.getConfigCut((IStructuredSelection) HandlerUtil.getActiveMenuSelection(event));

			TestfulWizard wizard = new TestfulWizard(config);

			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.create();
			dialog.open();

		} catch (Exception e) {
			MessageDialog.openError(shell, "TestFul", e.getMessage());

			System.err.println("-");
			System.err.println("-");
			System.err.println("-");
			e.printStackTrace();
			System.err.println("-");
			System.err.println("-");
			System.err.println("-");

		}

		return null;
	}

}
