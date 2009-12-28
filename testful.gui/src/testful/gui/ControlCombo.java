package testful.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public class ControlCombo implements ITestfulControl {

	private Combo combo;
	
	public  ControlCombo(Composite parent, GridData gdt) {
		combo = new Combo(parent, SWT.READ_ONLY);
		combo.setLayoutData(gdt);
	}
	
	public ControlCombo(Composite parent) {
		this(parent, getGridData());
	}
	
	public ControlCombo(Composite parent, Object[] values) {
		this(parent, getGridData());
		addValue(values);
	}
	
	public ControlCombo(Composite parent, Object[] values, String valueSelected) {
		this(parent, getGridData());
		String str;
		for (Object value : values) {
			str = value.toString();
			combo.add(str);
			if (str.toUpperCase().equals(valueSelected.toUpperCase())) combo.select(combo.getItemCount()-1);
		}
	}
	

	public ControlCombo(Composite parent, GridData gdt, Object[] values) {
		this(parent, gdt);
		addValue(values);
	}	
	
	public ControlCombo(Composite parent, GridData gdt, Object[] values, String valueSelected) {
		this(parent, gdt);
		String str;
		for (Object value : values) {
			str = value.toString();
			combo.add(str);
			if (str.toUpperCase().equals(valueSelected.toUpperCase())) combo.select(combo.getItemCount()-1);
		}
	}

	private static GridData getGridData() {
		GridData gdtH = new GridData();
		gdtH.horizontalAlignment = GridData.FILL;
		gdtH.grabExcessHorizontalSpace = true;
		return gdtH;
	}
	
	public void addValue(Object[] values) {
		for (Object value : values)	combo.add(value.toString());
	}
	
	@Override
	public void addListener(int eventType, Listener listener) {
		combo.addListener(eventType, listener);
	}

	@Override
	public Control getMainControl() {
		return combo.getParent();
	}

	@Override
	public Control getParent() {
		return combo.getParent();
	}

	@Override
	public String getValue() {
		return combo.getItem(combo.getSelectionIndex());
	}

}
