package testful.gui;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import testful.ConfigCut;
import testful.gui.TestfulImage.IMAGE;
import testful.gui.operator.LoadXmlModel;
import testful.gui.operator.Result;
import testful.model.xml.XmlClass;

public class EditorXMLModel {

	private XmlClass xmlClass;
	private ConfigCut config;
	private PageXMLModel gui;
	private Shell shell;

	private EditorXMLModel(String baseDir, String cut) throws Exception  {
		config = new ConfigCut();
		config.setDirBase(new File(baseDir));
		config.setCut(cut);
		LoadXmlModel loadXmlModel = new LoadXmlModel(config);
		loadXmlModel.run();
		Result res =  loadXmlModel.Result();
		if (!res.isSuccess) throw new Exception(res.message);
		else xmlClass = (XmlClass)res.returned;
	}

	public EditorXMLModel(ConfigCut config, XmlClass xmlClass) {
		this.config = config;
		this.xmlClass = xmlClass;
	}

	public static void main(String[] args) {
		Display display = new Display();
		EditorXMLModel editor;
		try {
			editor = new EditorXMLModel(args[0], args[1]);
			editor.openShell(display);
			display.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openShell(Display display) {

		shell = new Shell(display, SWT.TITLE | SWT.CLOSE);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		shell.setText(xmlClass.getName());
		shell.setMinimumSize(600, 400);
		shell.setImage(new  TestfulImage(display).loadImage(IMAGE.TESTFUL_MODIFY));

		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new GridLayout(1, true));

		gui = new PageXMLModel(config, xmlClass);
		gui.createControl(comp, true);
		gui.start();
		createFooter(comp);

		shell.pack();
		shell.open();
		while( !shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}

	}

	private void createFooter(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new RowLayout());

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		comp.setLayoutData(gridData);

		Button btnCancel = new Button(comp, SWT.PUSH);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				shell.close();
			}
		});

		Button btnApply = new Button(comp, SWT.PUSH);
		btnApply.setText("Apply");
		btnApply.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Result res = gui.finish();
				if (!res.isSuccess)
					MessageDialog.openWarning(shell, "Testful",
							"Impossible to save Xml Model.\n" + res.message);
			}
		});

		Button btnSave = new Button(comp, SWT.PUSH);
		btnSave.setText("Save");
		btnSave.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Result res = gui.finish();
				if (res.isSuccess) shell.close();
				else MessageDialog.openWarning(shell, "Testful",
						"Impossible to save Xml Model.\n" + res.message);
			}
		});
	}

}
