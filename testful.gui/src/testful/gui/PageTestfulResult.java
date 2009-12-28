package testful.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

import testful.gui.TestfulImage.IMAGE;
import testful.gui.operator.Result;

public class PageTestfulResult extends WizardPage implements ITestfulWizardPage {

	private Result testResult;
	private Composite parent;
	private Composite cmpMain;
	private GridData gdtHV;
	private GridData gdtH;
	private Label lblResult;
	private Label lblIcon;
	private Text txtResult;

	public PageTestfulResult(Result res) {
		super("TestfulfResult");
		setTitle("Testfulf Result");
		testResult = res;
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
		txtResult.setText("test");

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
		return new Result(true);
	}

	@Override
	public Control getParentControl() {
		return parent;
	}

	@Override
	public void start() {
		if (testResult.isSuccess) {
			lblResult.setText("Testful test generation complited successful!");
			lblResult.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
			lblIcon.setImage(new TestfulImage(parent.getDisplay()).loadImage(IMAGE.TESTFUL));
		} else {
			lblResult.setText(testResult.message);
			lblResult.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
			lblIcon.setImage(new TestfulImage(parent.getDisplay()).loadImage(IMAGE.TESTFUL_WARNING));
		}
		StringBuilder contents = new StringBuilder();
		try {
			BufferedReader input =  new BufferedReader(new FileReader((String)testResult.returned));
			try {
				String line = null;
				while (( line = input.readLine()) != null){
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
				txtResult.setText(contents.toString());
			} catch (Exception e) {
				txtResult.setText("Error occured while reading log file");
			} finally {
				input.close();

			}
		}
		catch (IOException ex){
			ex.printStackTrace();
			txtResult.setText("No log file finded...");
		}

		cmpMain.layout();
	}

	public Result getTestResult() {
		return testResult;
	}

}
