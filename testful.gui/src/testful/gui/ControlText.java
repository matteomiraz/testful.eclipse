package testful.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class ControlText implements ITestfulControl {

	private final Composite cmpMain;
	private boolean isRequired;
	private Text txt;

	public ControlText(Composite parent) {
		this(parent, getGridData(), "", false);
	}

	public ControlText(Composite parent, boolean isRequired) {
		this(parent, getGridData(), "", isRequired);
	}

	public ControlText(Composite parent, String text, boolean isRequired) {
		this(parent, getGridData(), text, isRequired);
	}

	public ControlText(Composite parent, String text) {
		this(parent, getGridData(), text, false);
	}

	public ControlText(Composite parent, GridData gdt, String text, boolean isRequired) {
		cmpMain = new Composite(parent, SWT.NONE);
		cmpMain.setLayout(new GridLayout(1, true));
		cmpMain.setLayoutData(gdt);
		cmpMain.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		txt = new Text(cmpMain, SWT.None);
		txt.setText(text);
		txt.setLayoutData(gdt);
		this.isRequired = isRequired;
		if (isRequired) txt.addListener(SWT.Deactivate, new ListenerTextRequired(text));
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
		if (!isRequired || !text.isEmpty()) txt.setText(text);
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		txt.addListener(eventType, listener);
	}

	@Override
	public Control getMainControl() {
		return cmpMain;
	}

	@Override
	public Control getParent() {
		return cmpMain.getParent();
	}

	@Override
	public String getValue() {
		return txt.getText();
	}

}
