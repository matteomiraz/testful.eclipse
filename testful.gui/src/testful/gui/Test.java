package testful.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import testful.Configuration;
import testful.TestfulException;
import testful.gui.operator.Result;

@SuppressWarnings("unused")
public class Test {

	private static String  	baseDir = "D:\\Shared\\TestFul\\runtime-EclipseApplication\\draw2",
	cut = "draw.Triangolo";

	public static void main(String[] args) {
		//TestXMLEditor();
		//TestInstrumenter();
		//new Test().TestWizard();
		TestResult();
		//TestGif();
	}

	private static void TestResult() {
		ShellTestfulResult tr = new ShellTestfulResult(new Display(), new Result(false, (Object)"D:\\Shared\\TestFul\\runtime-EclipseApplication\\draw2\\test\\draw.Triangolo1260873514775.log"));
		tr.run();
	}


	private static void TestDisplay() {
		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite comp = new Composite(shell, SWT.NONE);
		GridLayout grid = new GridLayout(1, true);
		comp.setLayout(grid);
		Composite group = new Composite(comp, SWT.NONE);
		group.setLayout(new FillLayout(SWT.VERTICAL));

		Label lblName = new Label(group, SWT.NONE);
		lblName.setText("Class: ");

		//text.setLayoutData(new RowData(300, 300));
		shell.pack();
		shell.open();
		while( !shell.isDisposed())
		{
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void TestInstrumenter()  {
		try {
			//Process p = Runtime.getRuntime().exec("java -jar instrumenter.jar -baseDir " + baseDir + " -cut " + cut);
			//Runtime.getRuntime().exec("cmd /c java instrumenter.jar -baseDir D:\\Shared\\TestFul\\runtime-EclipseApplication\\draw2 -cut draw.Triangolo");
			Configuration config = new Configuration(baseDir);
			config.setCut(cut);
			//new Operator().instrument(config);
		} catch(Exception e) {
			System.out.println("Error - " + e);
		}
	}

	private static void TestXMLEditor() {
		try {
			//Operator op = new Operator();
			Configuration config = new Configuration(baseDir);
			config.setCut(cut);
			//Result res = op.loadXmlModel(config, config.getCut());
			//if (res.isSuccess) {
			//	EditorXMLModel xme = new EditorXMLModel(config, (XmlClass)res.returned);
			//	xme.openShell(new Display());
			//} else MessageDialog.openWarning(new Shell(), "Testful", res.message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void TestOLDWizard() {
		//new TestfulWizardOld(new Display(), baseDir, cut);
	}

	public class MyApplication extends ApplicationWindow {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected Control createContents(Composite parent) {
			Button button = new Button(parent, SWT.PUSH);
			button.setText("Start Wizard");
			button.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					Configuration config = new Configuration(baseDir);
					try {
						config.setCut(cut);
					} catch (TestfulException e) {

						e.printStackTrace();
					}
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
				}
			});
			return button;
		}


		public MyApplication(Shell parentShell) {
			super(parentShell);
		}
	}

	public void TestWizard() {
		MyApplication app = new MyApplication(null);
		app.setBlockOnOpen(true);
		app.open();
	}

	private static void TestChecker() {
		try {
			Configuration config = new Configuration(baseDir);
			config.setCut(cut);
			//new Operator().checkClasses(config);
		} catch (TestfulException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void TestGif() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		Composite c = new Composite(shell, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		shell.setSize(300, 300);
		shell.open();
		new ControlAnimatedGif(c, "./images/wait.gif");

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}

}
