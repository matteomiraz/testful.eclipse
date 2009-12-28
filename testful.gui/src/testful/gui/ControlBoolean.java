package testful.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public class ControlBoolean implements ITestfulControl {

	private ControlCombo combo;
	
	public ControlBoolean(Composite parent) {
		Object[] boolValue = {"False", "True"};
		combo = new ControlCombo(parent, boolValue);
	}
	
	public ControlBoolean(Composite parent, Boolean isTrue) {
		Object[] boolValue = {"False", "True"};
		combo = new ControlCombo(parent, boolValue, isTrue.toString());
	}
	
	@Override
	public void addListener(int eventType, Listener listener) {
		combo.addListener(eventType, listener);
	}

	@Override
	public Control getMainControl() {
		return combo.getMainControl();
	}

	@Override
	public Control getParent() {
		return combo.getParent();
	}

	@Override
	public String getValue() {
		return combo.getValue();
	}

}
