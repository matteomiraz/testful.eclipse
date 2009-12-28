package testful.gui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import testful.Configuration;
import testful.gui.TestfulImage.IMAGE;
import testful.gui.operator.Result;
import testful.gui.operator.SaveXmlModel;
import testful.model.xml.XmlAux;
import testful.model.xml.XmlClass;
import testful.model.xml.XmlConstructor;
import testful.model.xml.XmlMethod;
import testful.model.xml.XmlParameter;
import testful.model.xml.XmlMethod.Kind;

public class PageXMLModel extends WizardPage implements ITestfulWizardPage {

	private boolean createHeader = true;

	private XmlClass xmlClass;
	private Configuration config;

	private Composite parent;
	private Composite cmpModel;
	private Tree tree;
	private Object selectedItem;
	private Button btnParameter;
	private Button btnRemove;
	private Button btnParamUp;
	private Button btnParamDown;
	private Composite cmpProperty;
	private Composite cmpSelectedItem;
	private GridData gdtHV;
	private GridData gdtH;
	private TestfulImage imageLoader;

	public PageXMLModel(Configuration config, XmlClass xmlClass)  {
		super("XMLModel");
		setTitle("XML Model");
		setDescription("Can modify \"" + config.getCut() +  "\" model destrcription");
		setPageComplete(false);
		this.config = config;
		this.xmlClass = xmlClass;
	}

	public PageXMLModel(Configuration config, XmlClass xmlClass, boolean createHeader) {
		this(config, xmlClass);
		this.createHeader = createHeader;
	}

	@Override
	public void createControl(Composite parent) {
		createControl(parent, createHeader);
	}

	public void createControl(Composite parent, boolean createHeader) {

		this.parent = parent;
		initGUI();
		cmpModel = new Composite(parent, SWT.NONE);
		cmpModel.setLayout(new GridLayout(1, true));
		cmpModel.setLayoutData(gdtHV);

		if (createHeader) createHeader(cmpModel);
		createBody(cmpModel);
		setControl(cmpModel);
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

		imageLoader = new TestfulImage(parent.getDisplay());
	}

	private void createHeader(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		comp.setLayoutData(gridData);

		Label lblClass = new Label(comp, SWT.NONE);
		lblClass.setText("Class: " + xmlClass.getName());
		lblClass.setLayoutData(gdtH);

		final Button btnInstrument = new Button(comp, SWT.CHECK);
		btnInstrument.setText("Is instrumented");
		btnInstrument.setSelection(xmlClass.isInstrument());
		btnInstrument.setLayoutData(gdtH);
		btnInstrument.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				xmlClass.setInstrument(btnInstrument.getSelection());
			}
		});
	}

	private void createBody(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));

		comp.setLayoutData(gdtHV);

		new Label(comp, SWT.NONE).setText("Elements:");
		new Label(comp, SWT.NONE).setText("Properties:");

		Composite cmpTree = new Composite(comp, SWT.NONE);
		cmpTree.setLayout(new GridLayout(1, true));
		cmpTree.setLayoutData(gdtHV);

		createMenuElemens(cmpTree);
		createTree(cmpTree);
		createProperty(comp);

	}

	private void createMenuElemens(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		comp.setLayout(new RowLayout());
		comp.setLayoutData(gdtH);

		Button btnAux = new Button(comp, SWT.PUSH);
		btnAux.setImage(imageLoader.loadImage(IMAGE.MODEL_ADD_AUX));
		btnAux.setToolTipText("Add an Auxilary Class");
		btnAux.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					xmlClass.getAux().add(new XmlAux(xmlClass.getName()));
					update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Button btnConstructor = new Button(comp, SWT.PUSH);
		btnConstructor.setImage(imageLoader.loadImage(IMAGE.MODEL_ADD_CONSTRUCTOR));
		btnConstructor.setToolTipText("Add a Costructor");
		btnConstructor.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					try {
						xmlClass.getConstructor().add(new XmlConstructor());
						update();
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Button btnMethod = new Button(comp, SWT.PUSH);
		btnMethod.setImage(imageLoader.loadImage(IMAGE.MODEL_ADD_METHOD));
		btnMethod.setToolTipText("Add a Method");
		btnMethod.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					XmlMethod xmlMethod = new XmlMethod();
					xmlMethod.setName("NewMethod");
					xmlMethod.setKind(Kind.OBSERVER);
					xmlMethod.setExposeState(false);
					xmlClass.getMethod().add(xmlMethod);
					update();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		btnParameter = new Button(comp, SWT.PUSH);
		btnParameter.setImage(imageLoader.loadImage(IMAGE.MODEL_ADD_PARAMETER));
		btnParameter.setToolTipText("Add a Parameter");
		btnParameter.setEnabled(false);
		btnParameter.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					XmlParameter xmlParameter = new XmlParameter();
					xmlParameter.setType("Object");
					xmlParameter.setArray(false);
					xmlParameter.setCaptured(false);
					xmlParameter.setMutated(false);
					xmlParameter.setExposedByReturn(false);
					xmlParameter.setExchangeStateWith("");

					if (selectedItem instanceof XmlMethod) {
						XmlMethod xmlMethod = (XmlMethod) selectedItem;
						xmlMethod.getParameter().add(xmlParameter);
					}
					if (selectedItem instanceof XmlConstructor) {
						XmlConstructor xmlConstructor = (XmlConstructor) selectedItem;
						xmlConstructor.getParameter().add(xmlParameter);
					}
					update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnParamUp = new Button(comp, SWT.PUSH);
		btnParamUp.setImage(imageLoader.loadImage(IMAGE.ARROW_UP));
		btnParamUp.setToolTipText("Parameter Up");
		btnParamUp.setEnabled(false);
		btnParamUp.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					Object parent =  tree.getSelection()[0].getParentItem().getData();
					if (parent instanceof XmlConstructor) {
						XmlConstructor xmlConstructor = (XmlConstructor)parent;
						int index = xmlConstructor.getParameter().indexOf(selectedItem);
						if (index>0) {
							XmlParameter clone = ((XmlParameter)selectedItem).clone();
							xmlConstructor.getParameter().remove(index);
							xmlConstructor.getParameter().add(index-1, clone);
							selectedItem = clone;
							updateTree();
						}
					}
					else if (parent instanceof XmlMethod) {
						XmlMethod xmlMethod = (XmlMethod)parent;
						int index = xmlMethod.getParameter().indexOf(selectedItem);
						if (index>0) {
							XmlParameter clone = ((XmlParameter)selectedItem).clone();
							xmlMethod.getParameter().remove(index);
							xmlMethod.getParameter().add(index-1, clone);
							selectedItem = clone;
							updateTree();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnParamDown = new Button(comp, SWT.PUSH);
		btnParamDown.setImage(imageLoader.loadImage(IMAGE.ARROW_DOWN));
		btnParamDown.setToolTipText("Parameter Down");
		btnParamDown.setEnabled(false);
		btnParamDown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					Object parent =  tree.getSelection()[0].getParentItem().getData();
					if (parent instanceof XmlConstructor) {
						XmlConstructor xmlConstructor = (XmlConstructor)parent;
						int index = xmlConstructor.getParameter().indexOf(selectedItem);
						if (index<xmlConstructor.getParameter().size()-1) {
							XmlParameter clone = ((XmlParameter)selectedItem).clone();
							xmlConstructor.getParameter().remove(index);
							xmlConstructor.getParameter().add(index+1, clone);
							selectedItem = clone;
							updateTree();
						}
					}
					else if (parent instanceof XmlMethod) {
						XmlMethod xmlMethod = (XmlMethod)parent;
						int index = xmlMethod.getParameter().indexOf(selectedItem);
						if (index<xmlMethod.getParameter().size()-1) {
							XmlParameter clone = ((XmlParameter)selectedItem).clone();
							xmlMethod.getParameter().remove(index);
							xmlMethod.getParameter().add(index+1, clone);
							selectedItem = clone;
							updateTree();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnRemove = new Button(comp, SWT.PUSH);
		btnRemove.setImage(imageLoader.loadImage(IMAGE.REMOVE));
		btnRemove.setToolTipText("Remove");
		btnRemove.setEnabled(false);
		btnRemove.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					if (selectedItem instanceof XmlAux) xmlClass.getAux().remove(selectedItem);
					else if (selectedItem instanceof XmlConstructor) xmlClass.getConstructor().remove(selectedItem);
					else if (selectedItem instanceof XmlMethod) xmlClass.getMethod().remove(selectedItem);
					else if (selectedItem instanceof XmlParameter ) {
						Object parent =  tree.getSelection()[0].getParentItem().getData();
						if (parent instanceof XmlConstructor) ((XmlConstructor)parent).getParameter().remove(selectedItem);
						else if (parent instanceof XmlMethod) ((XmlMethod)parent).getParameter().remove(selectedItem);
						selectedItem = parent;
					}
					if (!(selectedItem instanceof XmlParameter)) selectedItem = null;
					update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void createProperty(Composite parent) {
		cmpProperty = new Composite(parent, SWT.NONE);
		cmpProperty.setLayout(new GridLayout(1, true));
		cmpProperty.setLayoutData(gdtHV);
		cmpSelectedItem = new Composite(cmpProperty,  SWT.NONE);

	}

	private void createTree(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		//comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		comp.setLayout(new FillLayout(SWT.VERTICAL));
		comp.setLayoutData(gdtHV);

		tree = new Tree(comp,SWT.SINGLE);
		tree.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedItem = ((TreeItem)e.item).getData();
				btnParameter.setEnabled(selectedItem instanceof XmlConstructor || selectedItem instanceof XmlMethod);
				btnParamUp.setEnabled(selectedItem instanceof XmlParameter);
				btnParamDown.setEnabled(selectedItem instanceof XmlParameter);
				btnRemove.setEnabled(true);
				updateProperty();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}

	public void update() {
		updateTree();
		updateProperty();
	}

	private void updateTree() {

		tree.removeAll();

		for (XmlAux xmlAux : xmlClass.getAux()) {
			final TreeItem item = new TreeItem(tree,SWT.NULL);
			item.setImage(imageLoader.loadImage(IMAGE.MODEL_AUX));
			try {
				item.setText("Aux: " + xmlAux.getName());
			} catch (Exception e) {item.setText("Aux: Invalid data");}
			item.setData(xmlAux);
		}

		for (XmlConstructor xmlConstructor : xmlClass.getConstructor()) {
			final TreeItem item = new TreeItem(tree,SWT.NULL);
			item.setImage(imageLoader.loadImage(IMAGE.MODEL_CONSTRUCTOR));
			item.setText("Constructor");
			item.setData(xmlConstructor);
			for (XmlParameter xmlParameter : xmlConstructor.getParameter()) {
				final TreeItem par = new TreeItem(item,SWT.NULL);
				par.setImage(imageLoader.loadImage(IMAGE.MODEL_PARAMETER));
				par.setData(xmlParameter);
				try {
					par.setText(xmlParameter.getType());
				} catch (Exception e) {
					par.setText("Invalid data");
				}
			}
		}

		for (XmlMethod xmlMethod : xmlClass.getMethod()) {
			final TreeItem item = new TreeItem(tree,SWT.NULL);
			item.setImage(imageLoader.loadImage(IMAGE.MODEL_METHOD));
			item.setText("Method: " + xmlMethod.getName());
			item.setData(xmlMethod);
			for (XmlParameter xmlParameter : xmlMethod.getParameter()) {
				final TreeItem par = new TreeItem(item,SWT.NULL);
				par.setImage(imageLoader.loadImage(IMAGE.MODEL_PARAMETER));
				par.setData(xmlParameter);
				try {
					par.setText(xmlParameter.getType());
				} catch (Exception e) {
					par.setText("Invalid data");
				}
			}
		}

		if (selectedItem != null)
			for (TreeItem item : tree.getItems()) {
				if (item.getData() == selectedItem) {
					tree.select(item);
					item.setExpanded(true);
					break;
				} else if (item.getItemCount()>0) {
					for (TreeItem child: item.getItems()) {
						if(child.getData() == selectedItem) {
							tree.select(child);
							item.setExpanded(true);
						}
					}
				}
			}
	}

	private void updateProperty() {

		cmpSelectedItem.dispose();
		if (selectedItem == null) return;

		cmpSelectedItem = new Composite(cmpProperty, SWT.NONE);
		GridLayout gl = new GridLayout(2, true);
		gl.horizontalSpacing = 1;
		gl.verticalSpacing = 1;
		cmpSelectedItem.setLayout(gl);
		cmpSelectedItem.setLayoutData(gdtHV);
		cmpSelectedItem.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));

		if (selectedItem instanceof XmlAux) {

			XmlAux xmlAux = (XmlAux) selectedItem;
			new ControlLabel(cmpSelectedItem, "Name:");
			new ControlText(cmpSelectedItem, xmlAux.getName()!=null?xmlAux.getName():"", true)
			.addListener(SWT.Modify, new Listener() {
				@Override
				public void handleEvent(Event e) {
					XmlAux xmlAux = (XmlAux) selectedItem;
					Text txt = ((Text)e.widget);
					if (!txt.getText().isEmpty()) {
						xmlAux.setName(txt.getText());
						updateTree();
					}
				}
			});

		} else if (selectedItem instanceof XmlMethod) {

			XmlMethod xmlMethod = (XmlMethod)selectedItem;

			new ControlLabel(cmpSelectedItem, "Name:");
			new ControlText(cmpSelectedItem, xmlMethod.getName()!=null?xmlMethod.getName():"", true)
			.addListener(SWT.Modify, new Listener() {
				@Override
				public void handleEvent(Event e) {
					XmlMethod xmlMethod = (XmlMethod) selectedItem;
					Text txt = ((Text)e.widget);
					if (!txt.getText().isEmpty()) {
						xmlMethod.setName(txt.getText());
						updateTree();
					}
				}
			});

			new ControlLabel(cmpSelectedItem, "Kind:");
			new ControlCombo(cmpSelectedItem, XmlMethod.Kind.values(), xmlMethod.getKind()!=null?xmlMethod.getKind().name():"")
			.addListener(SWT.Selection  , new Listener() {
				@Override
				public void handleEvent(Event e) {
					Combo cmb = (Combo)e.widget;
					if (cmb.getSelectionIndex() > -1){
						XmlMethod xmlMethod = (XmlMethod) selectedItem;
						//xmlMethod.setKind(Kind.fromValue(cmb.getItem(cmb.getSelectionIndex())));
						for(Kind c : Kind.values())
							if(c.name().equals(cmb.getItem(cmb.getSelectionIndex()))) {
								xmlMethod.setKind(c);
								break;
							}
					}
				}
			});

			new ControlLabel(cmpSelectedItem, "Expose State:");
			Object[] boolValue = {"False", "True"};
			new ControlCombo(cmpSelectedItem, boolValue, xmlMethod.isExposeState()!=null?xmlMethod.isExposeState().toString():"false")
			.addListener(SWT.Selection  , new Listener() {
				@Override
				public void handleEvent(Event e) {
					Combo cmb = (Combo)e.widget;
					XmlMethod xmlMethod = (XmlMethod) selectedItem;
					xmlMethod.setExposeState(cmb.getSelectionIndex() == 1);
				}
			});

		}else if (selectedItem instanceof XmlParameter) {

			XmlParameter xmlParameter = (XmlParameter) selectedItem;

			new ControlLabel(cmpSelectedItem, "Type:");
			new ControlText(cmpSelectedItem, xmlParameter.getType()!=null?xmlParameter.getType():"", true)
			.addListener(SWT.Modify, new Listener() {
				@Override
				public void handleEvent(Event e) {
					XmlParameter xmlParameter = (XmlParameter) selectedItem;
					Text txt = ((Text)e.widget);
					if (!txt.getText().isEmpty()) {
						xmlParameter.setType(txt.getText());
						updateTree();
					}
				}}
			);

			new ControlLabel(cmpSelectedItem, "Is array:");
			new ControlBoolean(cmpSelectedItem, xmlParameter.isArray())
			.addListener(SWT.Selection  , new Listener() {
				@Override
				public void handleEvent(Event e) {
					Combo cmb = (Combo)e.widget;
					XmlParameter xmlParameter = (XmlParameter) selectedItem;
					xmlParameter.setArray(cmb.getSelectionIndex() == 1);
				}
			});

			new ControlLabel(cmpSelectedItem, "Is captured:");
			new ControlBoolean(cmpSelectedItem, xmlParameter.isCaptured())
			.addListener(SWT.Selection  , new Listener() {
				@Override
				public void handleEvent(Event e) {
					Combo cmb = (Combo)e.widget;
					XmlParameter xmlParameter = (XmlParameter) selectedItem;
					xmlParameter.setCaptured(cmb.getSelectionIndex() == 1);
				}
			});

			new ControlLabel(cmpSelectedItem, "Is mutated:");
			new ControlBoolean(cmpSelectedItem, xmlParameter.isMutated())
			.addListener(SWT.Selection  , new Listener() {
				@Override
				public void handleEvent(Event e) {
					Combo cmb = (Combo)e.widget;
					XmlParameter xmlParameter = (XmlParameter) selectedItem;
					xmlParameter.setMutated(cmb.getSelectionIndex() == 1);
				}
			});

			new ControlLabel(cmpSelectedItem, "Is exposed by return:");
			new ControlBoolean(cmpSelectedItem, xmlParameter.isExposedByReturn())
			.addListener(SWT.Selection  , new Listener() {
				@Override
				public void handleEvent(Event e) {
					Combo cmb = (Combo)e.widget;
					XmlParameter xmlParameter = (XmlParameter) selectedItem;
					xmlParameter.setExposedByReturn(cmb.getSelectionIndex() == 1);
				}
			});

			new ControlLabel(cmpSelectedItem, "Exchange State With:");
			new ControlText(cmpSelectedItem, xmlParameter.getExchangeStateWith()!=null?xmlParameter.getExchangeStateWith():"", false)
			.addListener(SWT.Modify, new Listener() {
				@Override
				public void handleEvent(Event e) {
					XmlParameter xmlParameter = (XmlParameter) selectedItem;
					Text txt = ((Text)e.widget);
					if (!txt.getText().isEmpty()) {
						xmlParameter.setExchangeStateWith(txt.getText());
						updateTree();
					}
				}
			});

		}

		cmpProperty.layout();

	}

	@Override
	public Result finish() {
		SaveXmlModel saver = new SaveXmlModel(config, xmlClass);
		saver.run();
		return saver.Result();
	}

	@Override
	public Control getParentControl() {
		return parent;
	}

	@Override
	public void start() {
		update();
		setPageComplete(true);
	}

}