package testful.gui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import testful.Configuration;
import testful.gui.TestfulImage.IMAGE;
import testful.gui.operator.Instrumenter;
import testful.gui.operator.Result;

public class PageInstrumenter extends WizardPage implements ITestfulWizardPage {

	private Configuration config;
	private Result res;

	private Composite parent;
	private Composite cmpMain;
	private GridData gdtHV;
	private GridData gdtH;
	private Label lblResult;
	private Label lblIcon;
	private Text txtResult;

	public PageInstrumenter(Configuration config) {
		super("Intrumenter");
		setTitle("Intrumenter");
		setDescription("Testful instruments your classes");
		setPageComplete(false);
		this.config = config;
	}

	@Override
	public void createControl(final Composite parent) {
		this.parent = parent;
		initGUI();

		cmpMain = new Composite(parent, SWT.NONE);
		cmpMain.setLayout(new GridLayout(1, true));
		cmpMain.setLayoutData(gdtHV);

		Composite cmpHeader = new Composite(cmpMain, SWT.NONE);
		cmpHeader.setLayout(new GridLayout(2, false));
		cmpHeader.setLayoutData(gdtH);
		lblIcon = new Label(cmpHeader, SWT.NONE);
		lblResult = new Label(cmpHeader, SWT.NONE);
		GridData gdtV = new GridData();
		gdtV.verticalAlignment = GridData.END;
		lblResult.setLayoutData(gdtV);

		Button btnDetails = new Button(cmpMain, SWT.PUSH);
		btnDetails.setText("More details >>");
		btnDetails.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Button btnDetails = (Button) event.widget;
				if (txtResult.getVisible()) {
					txtResult.setVisible(false);
					btnDetails.setText("More details >>");
				} else {
					txtResult.setVisible(true);
					btnDetails.setText("<< Close");
				}
				cmpMain.layout();
			}
		});

		txtResult = new Text(cmpMain, SWT.READ_ONLY|SWT.MULTI|SWT.V_SCROLL);
		txtResult.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		txtResult.setLayoutData(gdtHV);
		txtResult.setVisible(false);
		txtResult.setText("");
		setControl(cmpMain);
	}

	private void initGUI() {
		gdtHV = new GridData();
		gdtHV.horizontalAlignment = GridData.FILL;
		gdtHV.verticalAlignment = GridData.FILL;
		gdtHV.grabExcessHorizontalSpace = true;
		gdtHV.grabExcessVerticalSpace = true;

		gdtH = new GridData();
		gdtH.horizontalAlignment = GridData.FILL;
		gdtH.grabExcessHorizontalSpace = true;

	}

	@Override
	public Result finish() {
		return res;
	}

	@Override
	public Control getParentControl() {
		return parent;
	}

	@Override
	public void start() {

		try {
			Instrumenter inst = new Instrumenter(config);
			getContainer().run(true, true, inst);

			res = inst.Result();
			if (res.isSuccess) {
				lblResult.setText("Intrumenting complited successful!");
				lblResult.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
				lblIcon.setImage(new TestfulImage(parent.getDisplay()).loadImage(IMAGE.TESTFUL));
			} else {
				lblResult.setText(res.message);
				lblResult.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
				lblIcon.setImage(new TestfulImage(parent.getDisplay()).loadImage(IMAGE.TESTFUL_WARNING));
			}
			if (res.returned != null) {
				txtResult.setText((String) res.returned);
			} else txtResult.setText("No log present...");
			cmpMain.layout();
			setPageComplete(true);
			return;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setPageComplete(false);
	}

}
