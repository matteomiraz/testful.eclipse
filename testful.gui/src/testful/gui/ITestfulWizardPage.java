package testful.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import testful.gui.operator.Result;

public interface ITestfulWizardPage {

	public void start();
	public Result finish();
	public Control getParentControl();
	public void createControl(Composite parent);
	
}
