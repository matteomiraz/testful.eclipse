package testful.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import testful.ConfigCut;
import testful.gui.TestfulImage.IMAGE;
import testful.gui.operator.CheckClasses;
import testful.gui.operator.Result;

public class PageCheckerClasses implements ITestfulWizardPage {

	private ConfigCut config;

	private Composite parent;
	private Composite cmpMain;
	private Label lblIcon;
	private Label lblResult;
	private Text txtResult;

	private boolean enableMsgSuccess = true;

	public PageCheckerClasses(ConfigCut config) {
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		this.parent = parent;

		GridData gdtHV = new GridData();
		gdtHV.horizontalAlignment = GridData.FILL;
		gdtHV.verticalAlignment = GridData.FILL;
		gdtHV.grabExcessHorizontalSpace = true;
		gdtHV.grabExcessVerticalSpace = true;

		GridData gdtH = new GridData();
		gdtH.horizontalAlignment = GridData.FILL;
		gdtH.grabExcessHorizontalSpace = true;

		GridData gdtV = new GridData();
		gdtV.verticalAlignment = GridData.END;

		cmpMain = new Composite(parent, SWT.NONE);
		cmpMain.setLayout(new GridLayout(1, true));
		cmpMain.setLayoutData(gdtHV);

		Composite cmpHeader = new Composite(cmpMain, SWT.NONE);
		cmpHeader.setLayout(new GridLayout(2, false));
		cmpHeader.setLayoutData(gdtH);
		lblIcon = new Label(cmpHeader, SWT.NONE);
		lblResult = new Label(cmpHeader, SWT.NONE);
		lblResult.setLayoutData(gdtV);

		txtResult = new Text(cmpMain, SWT.MULTI|SWT.READ_ONLY|SWT.V_SCROLL);
		txtResult.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		txtResult.setLayoutData(gdtHV);
	}


	@Override
	public Result finish() {
		return new Result(true);
	}

	@Override
	public Control getParentControl() {
		return parent;
	}

	@Override
	public void start() {
		CheckClasses checkClasses = new CheckClasses(config);
		checkClasses.run();
		Result res = checkClasses.Result();
		if (!res.isSuccess) {
			lblIcon.setImage(new TestfulImage(parent.getDisplay()).loadImage(IMAGE.TESTFUL_WARNING));
			lblResult.setText("Warning!");
			lblResult.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
			txtResult.setVisible(true);
			txtResult.setText(res.message);
		} else if(enableMsgSuccess){
			lblIcon.setImage(new TestfulImage(parent.getDisplay()).loadImage(IMAGE.TESTFUL));
			lblResult.setText(res.message);
			lblResult.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
			txtResult.setVisible(false);
		}
		cmpMain.layout();
	}

}