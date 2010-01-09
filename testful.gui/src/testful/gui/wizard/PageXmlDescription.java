package testful.gui.wizard;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import testful.IConfigCut;
import testful.model.TestCluster;
import testful.model.TestCluster.MissingClassException;
import testful.model.xml.Parser;
import testful.model.xml.XmlAux;
import testful.model.xml.XmlClass;
import testful.model.xml.XmlConstructor;
import testful.model.xml.XmlMethod;
import testful.model.xml.XmlParameter;
import testful.model.xml.XmlMethod.Kind;

public class PageXmlDescription extends WizardPage {

	private static final String[] KIND = { Kind.MUTATOR.toString(),
		Kind.WORKER.toString(), Kind.OBSERVER.toString(),
		Kind.STATIC.toString() };

	private Shell shell;

	private final IResource project;

	private Composite properties;
	private TreeViewer treeViewer;
	private final XmlContentProvider contentProvider;

	private IConfigCut config;
	private ClassLoader classLoader;

	public PageXmlDescription(IConfigCut iconfig, IResource projectResource) throws MalformedURLException {
		super("Xml Description");
		setTitle("Class Descriptor");
		setDescription("Describe the classes involved in the test");

		project = projectResource;

		config = iconfig;
		contentProvider = new XmlContentProvider(iconfig);
		classLoader = new URLClassLoader(new URL[] {
				config.getDirContracts().toURI().toURL(),
				config.getDirCompiled().toURI().toURL()
		});
	}

	@Override
	public void createControl(Composite parent) {
		shell = parent.getShell();

		TestCluster testCluster = null;
		try {
			testCluster = new TestCluster(classLoader, config);
			checkPage(testCluster);
		} catch (ClassNotFoundException exc) {
			setErrorMessage(exc.getMessage());
			setPageComplete(false);
		}

		final Composite base = new Composite(parent, SWT.BORDER);
		base.setLayout(new GridLayout());
		setControl(base);


		{
			SashForm sashForm = new SashForm(base, SWT.HORIZONTAL);
			sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

			{
				treeViewer = new TreeViewer(sashForm, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
				treeViewer.setContentProvider(contentProvider);
				treeViewer.setLabelProvider(new XmlLabelProvider());
				treeViewer.setInput(testCluster != null ? testCluster : XmlContentProvider.EMPTY_TEST_CLUSTER);
				treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {

						if (!event.getSelection().isEmpty()
								&& event.getSelection() instanceof IStructuredSelection) {

							Object selection = ((IStructuredSelection) event.getSelection()).iterator().next();

							if (selection instanceof String) {
								showPropertyClass((String) selection);
							} else if (selection instanceof XmlConstructor) {
								showPropertyConstructor((XmlConstructor) selection);
							} else if (selection instanceof XmlMethod) {
								showPropertyMethod((XmlMethod) selection);
							} else if (selection instanceof XmlParameter) {
								showPropertyParameter((XmlParameter) selection);
							}
						}
					}
				});

			}

			{
				properties = new Composite(sashForm, SWT.NONE);
				properties.setLayout(new GridLayout(2, false));
			}
		}

		{
			Composite actions = new Composite(base, SWT.NONE);
			actions.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

			FillLayout layout = new FillLayout();
			layout.spacing = 5;
			actions.setLayout(layout);

			Button save = new Button(actions, SWT.PUSH);
			save.setText("Save");
			save.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						save();
						reload();
					} catch (JAXBException exc) {
						MessageDialog.openError(shell, "TestFul", "Cannot save the XML description:\n" + exc.getMessage());
					}
				}
			});

			Button reload = new Button(actions, SWT.PUSH);
			reload.setText("Reload");
			reload.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					reload();
				}
			});
		}
	}

	public void save() throws JAXBException {
		contentProvider.save();

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private void reload() {
		try {
			for (Control c : properties.getChildren())
				c.dispose();

			properties.layout();

			TestCluster testCluster = new TestCluster(classLoader, config);

			contentProvider.reset();
			treeViewer.setInput(testCluster);

			checkPage(testCluster);

		} catch (ClassNotFoundException exc) {
			MessageDialog.openError(shell, "TestFul", exc.getMessage());
			treeViewer.setInput(XmlContentProvider.EMPTY_TEST_CLUSTER);
		}
	}

	private void checkPage(TestCluster testCluster) {
		try {
			testCluster.isValid();

			setErrorMessage(null);
			setMessage(null, WARNING);
			setPageComplete(true);

		} catch (MissingClassException e) {
			if(e.fatal) {
				setErrorMessage("Missing: " + e.missing);
				setPageComplete(false);
			} else {
				setErrorMessage("");
				setMessage("Missing: " + e.missing, WARNING);
				setPageComplete(true);
			}
		}
	}

	private void showPropertyClass(final String className) {
		final XmlClass xmlClass = contentProvider.getClassXml(className);

		if (xmlClass == null) {
			for (Control c : properties.getChildren())
				c.dispose();

			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("class name: ");

			Text t = new Text(properties, SWT.BORDER);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			t.setEditable(false);
			t.setText(className);

			l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("");

			Button b = new Button(properties, SWT.PUSH);
			b.setText("Create new description");
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						XmlClass xmlClass = Parser.singleton.createClassModel(classLoader.loadClass(className));
						Parser.singleton.encode(xmlClass, config);

						TestCluster testCluster = new TestCluster(classLoader, config);
						treeViewer.setInput(testCluster);

						showPropertyClass(className);

						checkPage(testCluster);
					} catch (Exception exc) {
						MessageDialog.openError(shell, "TestFul", "Error during the creation of the XML description:\n" + exc.getMessage());
					}

				}
			});

			properties.layout();

		} else {
			for (Control c : properties.getChildren())
				c.dispose();

			{
				Label l = new Label(properties, SWT.NONE);
				l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
				l.setText("class name: ");

				Text t = new Text(properties, SWT.BORDER);
				t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				t.setEditable(false);
				t.setText(className);
			}

			{
				// instrument
				Label l = new Label(properties, SWT.NONE);
				l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
				l.setText("to instrument");

				final Button b = new Button(properties, SWT.CHECK);
				b.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				b.setSelection(xmlClass.isInstrument());
				b.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						xmlClass.setInstrument(b.getSelection());
					}
				});
			}

			{
				Label l = new Label(properties, SWT.NONE);
				l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
				l.setText("also include: ");

				Composite c = new Composite(properties, SWT.NONE);
				c.setLayoutData(new GridData(GridData.FILL_BOTH));
				GridLayout gl = new GridLayout();
				gl.marginLeft = 0;
				gl.marginBottom = 0;
				gl.marginRight = 0;
				gl.marginTop = 0;
				gl.marginWidth = 0;
				gl.marginHeight = 0;
				gl.verticalSpacing = 0;
				c.setLayout(gl);

				Composite header = new Composite(c, SWT.NONE);
				header.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				gl = new GridLayout(3, false);
				gl.marginLeft = 0;
				gl.marginBottom = 0;
				gl.marginRight = 0;
				gl.marginTop = 0;
				gl.marginWidth = 0;
				gl.marginHeight = 0;
				header.setLayout(gl);

				final Text text = new Text(header, SWT.BORDER);
				text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				final Button add = new Button(header, SWT.PUSH);
				add.setText("add");
				add.setEnabled(false);

				final Button remove = new Button(header, SWT.PUSH);
				remove.setText("remove");
				remove.setEnabled(false);

				final java.util.List<XmlAux> aux = xmlClass.getAux();
				final List list = new List(c, SWT.BORDER | SWT.V_SCROLL);
				list.setLayoutData(new GridData(GridData.FILL_BOTH));
				for (XmlAux a : aux)
					list.add(a.getName());

				text.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						int idx = list.getSelectionIndex();
						if (idx < 0) {
							add.setEnabled(!text.getText().isEmpty());
						} else {
							if (text.getText().isEmpty()) {
								remove.setEnabled(false);
								add.setText("add");
								add.setToolTipText("");

								aux.remove(idx);
								list.remove(idx);
							} else {
								aux.get(idx).setName(text.getText());
								list.setItem(idx, text.getText());
							}
						}
					}
				});

				add.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (list.getSelectionIndex() < 0) {
							aux.add(new XmlAux(text.getText()));
							list.add(text.getText());
							add.setEnabled(false);
							add.setToolTipText("");
							text.setText("");
						} else {
							add.setText("add");
							list.setSelection(new int[0]);
							remove.setEnabled(false);
						}
					}
				});

				list.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int idx = list.getSelectionIndex();
						if (idx >= 0) {
							add.setEnabled(true);
							add.setText("un");
							add.setToolTipText("Unselect the element of the list");
							remove.setEnabled(true);
							text.setText(list.getItem(idx));
						}
					}
				});
			}

			properties.layout();
		}
	}

	private void showPropertyConstructor(XmlConstructor cns) {
		for (Control c : properties.getChildren())
			c.dispose();

		{
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("");

			Label p = new Label(properties, SWT.NONE);
			p.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			p.setText("Constructor");
		}

		if (cns.getParameter().isEmpty()) {
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("");

			Label p = new Label(properties, SWT.NONE);
			p.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			p.setText("no parameters");
		} else {
			for (int i = 0; i < cns.getParameter().size(); i++) {
				Label l = new Label(properties, SWT.NONE);
				l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
				l.setText("param " + i + ": ");

				Label p = new Label(properties, SWT.NONE);
				p.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				p.setText(cns.getParameter().get(i).getType());
			}
		}

		properties.layout();
	}

	private void showPropertyMethod(final XmlMethod meth) {
		for (Control c : properties.getChildren())
			c.dispose();

		{
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("name: ");

			Text t = new Text(properties, SWT.BORDER);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			t.setEditable(false);
			t.setText(meth.getName());
		}

		{
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("type: ");

			final Combo combo = new Combo(properties, SWT.READ_ONLY);
			combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			combo.setItems(KIND);
			combo.setText(meth.getKind().toString());
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					meth.setKind(Kind.valueOf(KIND[combo.getSelectionIndex()]));
				}
			});
		}

		{
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("expose state: ");

			final Button b = new Button(properties, SWT.CHECK);
			b.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			b.setSelection(meth.isExposeState());
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					meth.setExposeState(b.getSelection());
				}
			});
		}

		properties.layout();
	}

	private void showPropertyParameter(final XmlParameter par) {
		for (Control c : properties.getChildren())
			c.dispose();

		{
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("type: ");

			Text t = new Text(properties, SWT.BORDER);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			t.setEditable(false);
			t.setText(par.getType() + (par.isArray() ? " (array)" : ""));
		}

		{
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("mutated: ");

			final Button b = new Button(properties, SWT.CHECK);
			b.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			b.setSelection(par.isMutated());
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					par.setMutated(b.getSelection());
				}
			});
		}

		{
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("captured: ");

			final Button b = new Button(properties, SWT.CHECK);
			b.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			b.setSelection(par.isCaptured());
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					par.setCaptured(b.getSelection());
				}
			});
		}

		{
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("expose state: ");

			final Button b = new Button(properties, SWT.CHECK);
			b.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			b.setSelection(par.isExposedByReturn());
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					par.setExposedByReturn(b.getSelection());
				}
			});
		}

		{
			Label l = new Label(properties, SWT.NONE);
			l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			l.setText("exchange state with: ");

			final Text t = new Text(properties, SWT.BORDER);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			t.setText(par.getExchangeStateWith());
			t.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					par.setExchangeStateWith(t.getText());
				}
			});
		}

		properties.layout();
	}
}

