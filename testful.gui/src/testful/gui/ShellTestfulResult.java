package testful.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import testful.gui.TestfulImage.IMAGE;
import testful.gui.operator.Result;

public class ShellTestfulResult extends Thread{
	
	private Result r;
	private Display display;
	
	public ShellTestfulResult(Display display,  Result res) {
		r = res;
		this.display = display;
	}

	@Override
	public void run() {
		PageTestfulResult pRes = new PageTestfulResult(r);
		
		Shell shell = new Shell(display, SWT.TITLE | SWT.CLOSE);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		shell.setText("Testful Generator result");		
		shell.setImage(new  TestfulImage(display).loadImage(IMAGE.TESTFUL_EMPTY));
				
		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new GridLayout(1, true));
		pRes.createControl(comp);
		pRes.start();
	
		shell.pack();
		shell.open();
		shell.setSize(600, 400);
		
		while( !shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
	}
}
