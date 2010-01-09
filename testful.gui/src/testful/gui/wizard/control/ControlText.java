package testful.gui.wizard.control;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class ControlText {

	private final Composite cmpMain;
	private Text txt;

	public ControlText(Composite parent, String text, final ITestfulControl<String> control) {
		this(parent, getGridData(), text, control);
	}

	public ControlText(Composite parent, GridData gdt, String defaultText, final ITestfulControl<String> control) {
		cmpMain = new Composite(parent, SWT.NONE);
		cmpMain.setLayout(new GridLayout(1, true));
		cmpMain.setLayoutData(gdt);
		cmpMain.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		txt = new Text(cmpMain, SWT.None);
		txt.setText(defaultText == null ? "" : defaultText);
		txt.setLayoutData(gdt);

		if(control != null) {
			txt.addListener(SWT.Deactivate, new Listener() {
				private String value = txt.getText();

				@Override
				public void handleEvent(Event event) {
					try {
						control.update(txt.getText());
					} catch (Exception e1) {
						MessageDialog.openInformation(txt.getShell(), "Testful", "Insert value is not valid!\n" + e1.getMessage());
						txt.setText(value);
						txt.setFocus();
					}
				}
			});

			try {
				control.update(defaultText);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static GridData getGridData() {
		GridData gdtH = new GridData();
		gdtH.horizontalAlignment = GridData.FILL;
		gdtH.grabExcessHorizontalSpace = true;
		return gdtH;
	}

	public String getText() {
		return txt.getText();
	}

	public void setText(String text) {
		txt.setText(text);
	}

	public static ITestfulControl<String> getRequired(final ITestfulControl<String> config) {
		return new ITestfulControl<String>() {
			@Override
			public void update(String newValue) throws Exception {
				if(newValue == null || newValue.isEmpty()) throw new Exception("This property must have a value");
				config.update(newValue);
			}
		};
	}
}
