package testful.gui;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

import testful.Configuration;
import testful.gui.operator.LoadXmlModel;
import testful.gui.operator.Result;
import testful.gui.operator.SaveXmlModel;
import testful.model.xml.XmlAux;
import testful.model.xml.XmlClass;

public class PageAuxilaryClass extends WizardPage implements ITestfulWizardPage {

	
	private Configuration config;
	private XmlClass xmlClass;
		
	private ArrayList<XmlClass> auxilaryClasses;
	
	private Control parent;
	final StackLayout layout = new StackLayout();
	private Composite cmpMain;
	private Composite cmpList;
	private Composite cmpModel;
	private Composite cmpModelEditor;
	private Composite cmpXmlEditor;
	private List lstAux;
	private Button btnModify;
	private GridLayout subGrid;
	private GridData gdtHV;
	private PageCheckerClasses pccChecker;
	
	public PageAuxilaryClass(Configuration config, XmlClass xmlClass) {
		super("AuxilaryClasses");
		setTitle("Auxilary classes");
		setDescription("Can modify auxilary classes models destrcriptions");
		setPageComplete(false);
		this.config = config;
		this.xmlClass = xmlClass;
	}
	
	private void initGUI() {
		gdtHV = new GridData();
		gdtHV.horizontalAlignment = GridData.FILL;
		gdtHV.verticalAlignment = GridData.FILL;
		gdtHV.grabExcessHorizontalSpace = true;
		gdtHV.grabExcessVerticalSpace = true;
		
		subGrid = new GridLayout(1, true);
		subGrid.marginWidth = 0;
		subGrid.marginHeight = 0;
		subGrid.horizontalSpacing = 0;
		subGrid.verticalSpacing = 0;
	}
	
	@Override
	public void createControl(final Composite parent) {
		this.parent = parent;
		
		initGUI();
		
		cmpMain = new Composite(parent, SWT.NONE);
		cmpMain.setLayoutData(gdtHV);
		cmpMain.setLayout(layout);
		 
		cmpList = new Composite(cmpMain, SWT.NONE);
		cmpList.setLayout(new GridLayout(1, true));
		cmpList.setLayoutData(gdtHV);
		new Label(cmpList, SWT.NONE).setText("Auxilary classes:");
		
		Composite cmpListItems = new Composite(cmpList, SWT.NONE);
		cmpListItems.setLayout(new GridLayout(2, true));
		cmpListItems.setLayoutData(gdtHV);
		
		lstAux = new List(cmpListItems, SWT.SINGLE);
		lstAux.setLayoutData(gdtHV);
		lstAux.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnModify.setEnabled(((List)e.getSource()).getSelectionIndex()>-1);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		Composite cmpRight = new Composite(cmpListItems, SWT.NONE);
		cmpRight.setLayout(new GridLayout(1, true));
		cmpRight.setLayoutData(gdtHV);
		
		btnModify = new Button(cmpRight, SWT.PUSH);
		btnModify.setText("Edit class model >>");
		btnModify.setEnabled(false);
		GridData gdtTop = new GridData();
		gdtTop.verticalAlignment = GridData.BEGINNING;
		btnModify.setLayoutData(gdtTop);
		btnModify.addListener(SWT.Selection,new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					cmpXmlEditor.dispose();
					cmpXmlEditor  = new Composite(cmpModelEditor, SWT.NONE);
					cmpXmlEditor.setLayout(subGrid);
					cmpXmlEditor.setLayoutData(gdtHV);
					
					PageXMLModel xmc = new PageXMLModel(config, auxilaryClasses.get(lstAux.getSelectionIndex()));
					xmc.createControl(cmpXmlEditor, true);
					xmc.start();
					cmpModelEditor.layout();
					cmpXmlEditor.layout();
					layout.topControl = cmpModel;
					cmpMain.layout();
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(parent.getShell(), "Testful - Error", e.getMessage());
				}
			}
		});

		pccChecker = new PageCheckerClasses(config);
		pccChecker.createControl(cmpRight);
				
		cmpModel = new Composite(cmpMain, SWT.NONE);
		cmpModel.setLayout(subGrid);
		cmpModel.setLayoutData(gdtHV);
		
		cmpModelEditor = new Composite(cmpModel, SWT.NONE);
		cmpModelEditor.setLayout(new GridLayout(1, true));
		cmpModelEditor.setLayoutData(gdtHV);
			
		cmpXmlEditor  = new Composite(cmpModelEditor, SWT.NONE);
		cmpXmlEditor.setLayout(subGrid);
		cmpXmlEditor.setLayoutData(gdtHV);
		
		Button btnFinish = new Button(cmpModel, SWT.PUSH);
		btnFinish.setText("<< Finish");
		GridData gdtRight = new GridData();
		gdtRight.horizontalAlignment = GridData.END;
		btnFinish.setLayoutData(gdtRight);
		btnFinish.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				layout.topControl = cmpList;
				cmpMain.layout();
			}
		});
		setControl(cmpMain);
	}
	
	@Override
	public Control getParentControl() {
		return parent;
	}

	@Override
	public Result finish() {
		Result result = new Result(true, "");
		try {
			Result tmpRes = null;
			SaveXmlModel saveXmlModel;
			for (XmlClass c : auxilaryClasses) {
				saveXmlModel = new SaveXmlModel(config, c);
				saveXmlModel.run();
				tmpRes = saveXmlModel.Result();
				result = new Result(result.isSuccess&&tmpRes.isSuccess, result.message + "\n" + tmpRes.message);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(false, "Errors occurred while saving");
		}
		return result;
	}

	@Override
	public void start() {
		auxilaryClasses = new ArrayList<XmlClass>();
		lstAux.removeAll();
		
		boolean duplcateValue;
		Result res;
		
		LoadXmlModel loadXmlModel;
		
		for (XmlAux aux : xmlClass.getAux()) {
			if (!aux.getName().equals(xmlClass.getName())) {
				duplcateValue = false;
				for (String  value : lstAux.getItems()) {
					if (value.equals(aux.getName())) {
						duplcateValue = true;
						break;
					}
				}
				if (!duplcateValue) {
					loadXmlModel = new LoadXmlModel(config, aux.getName());
					loadXmlModel.run();
					res = loadXmlModel.Result();
					if (res.isSuccess) {
						auxilaryClasses.add((XmlClass)res.returned);
						lstAux.add(aux.getName());
					}
				}
			}
		}
		layout.topControl = cmpList;
		pccChecker.start();
		cmpMain.layout();
		setPageComplete(true);
	}

}
