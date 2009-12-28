package testful.gui;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import testful.Configuration;
import testful.gui.ControlInteger.TYPE;
import testful.gui.operator.Result;
import testful.gui.operator.TestGenerator;

public class PageTestful extends WizardPage implements ITestfulWizardPage {

	private final Configuration config;
	private ScrolledComposite scrMain;
	private Composite parent;
	private GridData gdtHV;
	private Object[][] options = {
			//Name, Label, Default value, Description
			{"-cutSize",	"Cut size", new Integer(4), "Number of places in the repository for the CUT"},
			{"-auxSize",	"Aux size",	new Integer(4), "Number of places in the repository for auxiliary classes"},
			{"-testSize", "Test size", new Integer(10000), "Maximum test length (n° of invocations)"},
			{"-reload", "Reload", new Boolean(false), "Reload classes before each run (reinitialize static fields)"},
			{"-time", "Time", new Integer(600), "The maximum execution time (in seconds)"},
			{"-disableLocalSearch", "Disable local search", new Boolean(false), "Disable the local search"},
			{"-disableLength", "Disable length", new Boolean(false), "Removes the length of test from the multi-objective fitness"},
			{"-disableBehavioral", "Disable behavioral", new Boolean(false), "Removes the behavioral coverage from the multi-objective fitness"},
			{"-enableBug", "Enable bug", new Boolean(false), "Inserts the number of bug found in the multi-objective fitness"},
			{"-disableDefUse", "Disable def-use", new Boolean(false), "Removes the def-use pairs coverage from the multi-objective fitness\n(shortcut for -disableDefUseCode and -disableDefUseContract)"},
			{"-disableDefUseCode", "Disable def-use code", new Boolean(false), "Removes the def-use pairs coverage on the code from the multi-objective fitness"},
			{"-disableDefUseContract", "Disable def-use contract", new Boolean(false), "Removes the def-use pairs coverage on contracts from the multi-objective fitness"},
			{"-disableBranch", "Disable branch", new Boolean(false), "Removes the branch coverage from the multi-objective fitness\n(shortcut for -disableBranchCode and -disableBranchContract)"},
			{"-disableBranchCode", "Disable branch code", new Boolean(false), "Removes the branch coverage on the code from the multi-objective fitness"},
			{"-disableBranchContract", "Disable branch contract", new Boolean(false), "Removes the branch coverage on contracts from the multi-objective fitness"},
			{"-disableBasicBlock", "Disable basic block", new Boolean(false), "Removes the basic block coverage from the multi-objective fitness\n(shortcut for -disableBranchCode and -disableBranchContract)"},
			{"-disableBasicBlockCode", "Disable basic block code", new Boolean(false), "Removes the basic block coverage on the code from the multi-objective fitness"},
			{"-disableBasicBlockContract", "Disable basic block contract", new Boolean(false), "Removes the basic block coverage on contracts from the multi-objective fitness"},
			{"-remote", "Remote", new String(""), "Use the specified remote evaluator"},
			{"-noLocal", "No local", new Boolean(false), "Do not use local evaluators"},
			{"-enableCache", "Enable cache", new Boolean(false), "Enable evaluation cache. Notice that it can degrade performances"}
	};
	private ArrayList <ITestfulControl> controls; 
		
	public PageTestful(Configuration config) {
		super("Testful");
		setTitle("Test generator");
		setDescription("Set parameters to generate tests cases");
		this.config = config;
	}
	
	@Override
	public void createControl(final Composite parent) {
		this.parent = parent;
		initGUI();	
		
		Composite cmpMain = new Composite(parent, SWT.NONE);
		cmpMain.setLayout(new GridLayout());
		
		new Label(cmpMain, SWT.NONE).setText("Testful options:");
		
		scrMain = new ScrolledComposite(cmpMain, SWT.BORDER | SWT.V_SCROLL);
		scrMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
		Composite co = new Composite(scrMain, SWT.NONE);
		co.setLayout(new GridLayout());
		Composite cmpGrid = new Composite(co, SWT.NONE);
		cmpGrid.setLayout(new GridLayout(3, false));
		cmpGrid.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		
		try {
			Object value;
			for (int i = 0; i < options.length; i++) {
				ITestfulControl c = null;
				value = options[i][2];
				
				new ControlLabel(cmpGrid, (String)options[i][1] + ":");
				
				if (value instanceof Integer) c = new ControlInteger(cmpGrid, (Integer)value, TYPE.POSITIVE);
				else if (value instanceof Boolean) c = new ControlBoolean(cmpGrid, (Boolean)value);
				else if (value instanceof String) c = new ControlText(cmpGrid, (String)value);
				else throw new Exception("Not valid data.");
				
				controls.add(c);
				new ControlInfo(cmpGrid, (String)options[i][0], (String)options[i][3]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		scrMain.setContent(co);
		scrMain.setExpandHorizontal(true);
		scrMain.setExpandVertical(true);
		scrMain.setMinSize(co.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(cmpMain);
	}

	private void initGUI() {
		gdtHV = new GridData();
		gdtHV.horizontalAlignment = GridData.FILL;
		gdtHV.verticalAlignment = GridData.FILL;
		gdtHV.grabExcessHorizontalSpace = true;
		gdtHV.grabExcessVerticalSpace = true;
		controls = new ArrayList<ITestfulControl>();
	}
	
	@Override
	public Result finish() {
		Result r;
	    try {
			String value;
			boolean toAdd;
			ArrayList <String> alArg = new ArrayList<String>();
			int totalTime = 600;
			for (int i=0; i<controls.size(); i++) {
				value = controls.get(i).getValue();
				if (controls.get(i) instanceof ControlBoolean) {				
					toAdd = value.toLowerCase().equals("true");
					value = "";
				} else toAdd = !(controls.get(i) instanceof ControlText && value.isEmpty());
				if (toAdd) {
					alArg.add((String)this.options[i][0]);
					alArg.add(value);
					if (((String)this.options[i][0]).equals("-time")) totalTime = Integer.parseInt(value);
				}
			}
			
			String[] args = new String[alArg.size()+4];
			args[0] = "-baseDir";
			args[1] = config.getDirBase();
			args[2] = "-cut";
			args[3] = config.getCut();
			
			
			for (int i = 4; i < args.length; i++) {
				args[i] = alArg.get(i-4);
			}
			
			TestGenerator test = new TestGenerator("TestGenerator", config, args);
			test.setTime(totalTime);
			test.schedule();
			r = test.Result();			
			
		} catch (Exception e) {
			e.printStackTrace();
			r = new Result(false);
		}
		return r;

	}

	@Override
	public Control getParentControl() {
		return parent;
	}

	@Override
	public void start() {
	}
	
}
