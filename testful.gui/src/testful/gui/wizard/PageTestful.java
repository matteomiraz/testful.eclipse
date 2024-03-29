package testful.gui.wizard;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import testful.IConfigProject.LogLevel;
import testful.evolutionary.ConfigEvolutionary;
import testful.evolutionary.jMetal.FitnessInheritance;
import testful.gui.wizard.control.ControlCombo;
import testful.gui.wizard.control.ControlInteger;
import testful.gui.wizard.control.ControlLabel;
import testful.gui.wizard.control.ControlText;
import testful.gui.wizard.control.ITestfulControl;

public class PageTestful extends WizardPage {

	private final ConfigEvolutionary config;

	public PageTestful(ConfigEvolutionary config) {
		super("Testful");
		setTitle("Test generator");
		setDescription("Set parameters to generate tests cases");

		this.config = config;
	}

	@Override
	public void createControl(final Composite parent) {

		ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrollComposite.setLayout(new FillLayout());
		setControl(scrollComposite);

		Composite cmpGrid = new Composite(scrollComposite, SWT.NONE);
		cmpGrid.setLayout(new GridLayout(2, false));
		cmpGrid.setSize(scrollComposite.getSize());
		scrollComposite.setContent(cmpGrid);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);

		{
			new ControlLabel(cmpGrid, "Time:", "The maximum execution time (in seconds)");
			new ControlInteger(cmpGrid, config.getTime(), ControlInteger.getPositive(new ITestfulControl<Integer>() {
				@Override
				public void update(Integer newValue) {
					config.setTime(newValue);
				}
			}));
		}

		{
			new ControlLabel(cmpGrid, "Preparation Time:", "Use Random Testing to start the evolution from a better population.");
			new ControlInteger(cmpGrid, config.getSmartInitialPopulation(), ControlInteger.getNotNegative(new ITestfulControl<Integer>() {
				@Override
				public void update(Integer newValue) throws Exception {
					config.setSmartInitialPopulation(newValue);
				}
			}));
		}


		{
			new ControlLabel(cmpGrid, "Test Directory:", "The directory in which generated tests will be put");
			new ControlText(cmpGrid, "genTests", ControlText.getRequired(new ITestfulControl<String>() {
				@Override
				public void update(String newValue) {
					config.setDirGeneratedTests(new File(newValue));
				}
			}));
		}

		{
			new ControlLabel(cmpGrid, "Simplify:", "Simplify tests to improve effectiveness and readability");
			ControlCombo.getBooleanCombo(cmpGrid, config.isSimplify(), new ITestfulControl<Boolean>() {
				@Override
				public void update(Boolean newValue) {
					config.setSimplify(newValue);
				}
			});
		}

		{
			new ControlLabel(cmpGrid, "Maximum test length:", "Maximum length of each test (n° of invocations)");
			new ControlInteger(cmpGrid, config.getMaxTestLen(), ControlInteger.getPositive(new ITestfulControl<Integer>() {
				@Override
				public void update(Integer newValue) {
					config.setMaxTestLen(newValue);
				}
			}));
		}

		{
			new ControlLabel(cmpGrid, "Cut variables:", "Number of variables for the CUT");
			new ControlInteger(cmpGrid, config.getNumVarCut(), ControlInteger.getPositive(new ITestfulControl<Integer>() {
				@Override
				public void update(Integer newValue) {
					config.setNumVarCut(newValue);
				}
			}));
		}

		{
			new ControlLabel(cmpGrid, "Aux variables:", "Number of variables for auxiliary classes");
			new ControlInteger(cmpGrid, config.getNumVar(), ControlInteger.getPositive(new ITestfulControl<Integer>() {
				@Override
				public void update(Integer newValue) {
					config.setNumVar(newValue);
				}
			}));
		}

		{
			new ControlLabel(cmpGrid, "Population:", "The size of the population (# of individuals)");
			new ControlInteger(cmpGrid, config.getPopSize(), ControlInteger.getPositive(new ITestfulControl<Integer>() {
				@Override
				public void update(Integer newValue) {
					config.setPopSize(newValue);
				}
			}));
		}

		{
			new ControlLabel(cmpGrid, "Fitness Inheritance:", "Speed up the evaluation by inheriting coverage");
			ControlCombo.getEnumCombo(cmpGrid, FitnessInheritance.class, config.getFitnessInheritance(), new ITestfulControl<FitnessInheritance>() {
				@Override
				public void update(FitnessInheritance newValue) throws Exception {
					config.setFitnessInheritance(newValue);
				}
			});
		}

		{
			new ControlLabel(cmpGrid, "Local search period:", "Period of the local search (0 to disable local search)");
			new ControlInteger(cmpGrid, config.getLocalSearchPeriod(), ControlInteger.getNotNegative(new ITestfulControl<Integer>() {
				@Override
				public void update(Integer newValue) {
					config.setLocalSearchPeriod(newValue);
				}
			}));
		}

		{
			// TODO: create code and contracts options
			new ControlLabel(cmpGrid, "Consider Basic Block coverage:", "Use the basic block coverage to drive the generation process");
			ControlCombo.getBooleanCombo(cmpGrid, config.isBbd(), new ITestfulControl<Boolean>() {
				@Override
				public void update(Boolean newValue) throws Exception {
					config.setDisableBasicBlock(!newValue);
				}
			});
		}

		{
			// TODO: create code and contracts options
			new ControlLabel(cmpGrid, "Consider Branch coverage:", "Use the branch coverage to drive the generation process");
			ControlCombo.getBooleanCombo(cmpGrid, config.isBrd(), new ITestfulControl<Boolean>() {
				@Override
				public void update(Boolean newValue) throws Exception {
					config.setDisableBranch(!newValue);
				}
			});
		}

		{
			new ControlLabel(cmpGrid, "Consider test compactness:", "Use test compactness to drive the evolutionary process");
			ControlCombo.getBooleanCombo(cmpGrid, config.isLength(), new ITestfulControl<Boolean>() {
				@Override
				public void update(Boolean newValue) throws Exception {
					config.setDisableLength(!newValue);
				}
			});
		}

		{
			new ControlLabel(cmpGrid, "Cache evaluations:", "Enable evaluation cache. It may speed up the evaluation, but it requires more memory. Note that often it actually degrades performances");
			ControlCombo.getBooleanCombo(cmpGrid, config.isCache(), new ITestfulControl<Boolean>() {
				@Override
				public void update(Boolean newValue) throws Exception {
					config.setCache(newValue);
				}
			});
		}

		{
			new ControlLabel(cmpGrid, "Reload classes:", "Reload classes before each evaluation (reinitialize static fields, slow down the evaluation process)");
			ControlCombo.getBooleanCombo(cmpGrid, config.isReload(), new ITestfulControl<Boolean>() {
				@Override
				public void update(Boolean newValue) throws Exception {
					config.setReload(newValue);
				}
			});
		}

		{
			new ControlLabel(cmpGrid, "Logging level:", "Specify the logging level");
			ControlCombo.getEnumCombo(cmpGrid, LogLevel.class, LogLevel.WARNING, new ITestfulControl<LogLevel>() {
				@Override
				public void update(LogLevel newValue) throws Exception {
					config.setLogLevel(newValue);
				}
			});
		}

		{
			new ControlLabel(cmpGrid, "Log directory:", "If set, logs in the specified directory");
			new ControlText(cmpGrid, null, new ITestfulControl<String>() {
				@Override
				public void update(String newValue) {
					if(newValue == null || newValue.isEmpty())
						config.setLog(null);
					else
						config.setLog(new File(newValue));
				}
			});
		}

		{
			new ControlLabel(cmpGrid, "Local evaluation:", "Do not evaluate test locally (you must specify a remote evaluator)");
			ControlCombo.getBooleanCombo(cmpGrid, config.isLocalEvaluation(), new ITestfulControl<Boolean>() {
				@Override
				public void update(Boolean newValue) throws Exception {
					config.disableLocalEvaluation(!newValue);
				}
			});
		}

		{
			new ControlLabel(cmpGrid, "Remote:", "Use a remote test evaluator");
			new ControlText(cmpGrid, null, new ITestfulControl<String>() {
				@Override
				public void update(String newValue) {
					if(newValue == null || newValue.isEmpty())
						config.setRemote(null);
					else
						config.setRemote(newValue);
				}
			});
		}

		scrollComposite.setMinSize(cmpGrid.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
}
