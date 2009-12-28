package testful.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public class ControlInteger implements ITestfulControl {

	public static enum TYPE{
		ALL(0),
		POSITIVE(1),
		NEGATIVE(-1),
		NOTPOSITIVE(-2),
		NOTNEGATIVE(2);
		
		private final int value;
		TYPE(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	}
	
	private ControlText cntText;
	
	public ControlInteger(Composite parent) {
		this(parent, 0);
	}
	
	public ControlInteger(Composite parent, Integer value) {
		this(parent, value, TYPE.ALL);
	}
	
	public ControlInteger(Composite parent, Integer value, TYPE type ) {
		cntText = new ControlText(parent);
		cntText.setText(value.toString());
		cntText.addListener(SWT.Deactivate, new ListenerTextInteger(value, type));
	}
	@Override
	public void addListener(int eventType, Listener listener) {
		cntText.addListener(eventType, listener);
	}

	@Override
	public Control getMainControl() {
		return cntText.getMainControl();
	}

	@Override
	public Control getParent() {
		return cntText.getParent();
	}

	@Override
	public String getValue() {
		return cntText.getValue();
	}

}
