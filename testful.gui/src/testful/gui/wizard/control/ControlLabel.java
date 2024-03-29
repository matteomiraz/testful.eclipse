package testful.gui.wizard.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class ControlLabel {

	private Composite cmpMain;
	private Label label;

	public ControlLabel(Composite parent) {
		cmpMain = new Composite(parent, SWT.NONE);
		cmpMain.setLayout(new GridLayout(1, true));
		GridData gdtH = new GridData();
		gdtH.horizontalAlignment = GridData.FILL;
		gdtH.verticalAlignment = GridData.FILL;
		cmpMain.setLayoutData(gdtH);
		label = new Label(cmpMain, SWT.NONE);
		//label.setLayoutData(gdtHV);
	}

	public ControlLabel(Composite parent, String text) {
		this(parent);
		label.setText(text);
	}

	public ControlLabel(Composite parent, String text, String hint) {
		this(parent, text);
		label.setToolTipText(hint);
	}

	public void setFontStyle(int style) {
		FontData fontData = label.getFont().getFontData()[0];
		Font font = new Font(cmpMain.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), style));
		label.setFont(font);
	}
}
