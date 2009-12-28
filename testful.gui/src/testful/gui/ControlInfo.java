package testful.gui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import testful.gui.TestfulImage.IMAGE;

public class ControlInfo implements ITestfulControl {

	private Button button;
	private String title;
	private String message;
	
	public ControlInfo(Composite parent) {
		
		button = new Button(parent, SWT.PUSH);
		button.setImage(new TestfulImage(parent.getDisplay()).loadImage(IMAGE.INFO));
		button.setSize(16, 16);
		addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MessageDialog.openInformation(getParent().getShell(), title, message);
			}
		});
	}
	
	public ControlInfo(Composite parent, String title, String message) {
		this(parent);
		setTitle(title);
		setMessage(message);
	}
	
	@Override
	public void addListener(int eventType, Listener listener) {
		button.addListener(eventType, listener);
	}

	@Override
	public Control getMainControl() {
		return button;
	}

	@Override
	public Control getParent() {
		return button.getParent();
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String getValue() {
		return message;
	}

}
