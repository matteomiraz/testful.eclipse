package testful.gui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class ListenerTextRequired implements Listener {
	
	private String lastValue;
	
	public ListenerTextRequired(String value) {
		this.lastValue = value;
	}
	@Override
	public void handleEvent(Event event) {
		Text txt = ((Text)event.widget);
		if (txt.getText().isEmpty()) {
			MessageDialog.openWarning(txt.getShell(), "Testful - Warning",
					"Property must have a value!");
			txt.setText(lastValue);
		} else lastValue = txt.getText();
	}
}