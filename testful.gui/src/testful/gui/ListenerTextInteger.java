package testful.gui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import testful.gui.ControlInteger.TYPE;

public class ListenerTextInteger implements Listener  {

	private Integer lastValue;
	private TYPE type;
	
	public ListenerTextInteger(Integer value, TYPE type) {
		this.lastValue = value;
		this.type = type;
	}
	
	@Override
	public void handleEvent(Event event) {
		Text txt;
		try {
			txt = ((Text)event.widget);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			if (txt.getText().isEmpty()) return;
			int i = Integer.parseInt(txt.getText());
			switch (type) {
				case NEGATIVE:
					if (i>=0) throw new NumberFormatException("Value must be a negative number.");
					break;
				case POSITIVE:
					if (i<=0) throw new NumberFormatException("Value must be a positive number.");
					break;
				case NOTNEGATIVE:
					if (i<0) throw new NumberFormatException("Value must be a not negative number.");
					break; 
				case NOTPOSITIVE:
					if (i>0) throw new NumberFormatException("Value must be a not positive number.");
			}
			lastValue = i;
		} catch (NumberFormatException e) {
			MessageDialog.openInformation(txt.getShell(), "Testful", "Insert value is not valid!\n" + e.getMessage());
			txt.setText(lastValue.toString());
			txt.setFocus();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

}
