package testful.gui;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public interface ITestfulControl {

	public Control getParent();
	public Control getMainControl();
	public void addListener(int eventType, Listener listener);
	public String getValue();
	
}
