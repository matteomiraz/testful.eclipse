package testful.evolutionary;

import java.io.File;
import java.util.List;

import org.kohsuke.args4j.Option;

import testful.ConfigGeneration;
import testful.ConfigRunner;
import testful.IConfigCut;
import testful.IConfigGeneration;
import testful.IConfigProject;
import testful.IConfigRunner;
import testful.TestfulException;
import testful.evolutionary.jMetal.FitnessInheritance;

public class ConfigEvolutionary
implements IConfigEvolutionary, IConfigFitness.Args4j, IConfigRunner.Args4j, IConfigGeneration.Args4j, IConfigCut.Args4j, IConfigProject.Args4j {

	private final IConfigGeneration.Args4j configGenerator = new ConfigGeneration();
	private final IConfigRunner.Args4j configRunner = new ConfigRunner();
	private final IConfigFitness.Args4j configFitness = new ConfigFitness();

	@Option(required = false, name = "-localSearchPeriod", usage = "Period of the local search (default: every 20 generations; <= 0 to disable local search)")
	private int localSearchPeriod = 20;

	@Option(required = false, name = "-popSize", usage = "The size of the population (# of individuals)")
	private int popSize = 512;

	@Option(required = false, name = "-fitnessInheritance", usage = "Select the type of fitness inheritance")
	private FitnessInheritance fitnessInheritance = FitnessInheritance.UNIFORM;

	@Option(required = false, name = "-quiet", usage = "Be really quiet")
	private boolean quiet;

	@Option(required = false, name = "-verbose", usage = "Be verbose")
	private boolean verbose;

	@Option(required = false, name = "-smartAncestors", usage = "Use an enhanced initial population")
	private boolean smartInitialPopulation;

	@Override
	public boolean isQuiet() {
		return quiet;
	}

	@Override
	public boolean isVerbose() {
		return verbose;
	}

	@Override
	public int getLocalSearchPeriod() {
		return localSearchPeriod;
	}

	@Override
	public int getPopSize() {
		return popSize;
	}

	@Override
	public FitnessInheritance getFitnessInheritance() {
		return fitnessInheritance;
	}

	@Override
	public void setToMinimize(boolean value) {
		configFitness.setToMinimize(value);
	}

	@Override
	public boolean isBbd() {
		return configFitness.isBbd();
	}

	@Override
	public boolean isBbn() {
		return configFitness.isBbn();
	}

	@Override
	public boolean isBrd() {
		return configFitness.isBrd();
	}

	@Override
	public boolean isBrn() {
		return configFitness.isBrn();
	}

	@Override
	public boolean isBug() {
		return configFitness.isBug();
	}

	@Override
	public boolean isLength() {
		return configFitness.isLength();
	}

	@Override
	public boolean isToMinimize() {
		return configFitness.isToMinimize();
	}

	@Override
	public boolean isSmartInitialPopulation() {
		return smartInitialPopulation;
	}

	@Override
	public void setDisableBasicBlock(boolean disable) {
		configFitness.setDisableBasicBlock(disable);
	}

	@Override
	public void setDisableBasicBlockCode(boolean disable) {
		configFitness.setDisableBasicBlockCode(disable);
	}

	@Override
	public void setDisableBasicBlockContract(boolean disable) {
		configFitness.setDisableBasicBlockContract(disable);
	}

	@Override
	public void setDisableBranch(boolean disable) {
		configFitness.setDisableBranch(disable);
	}

	@Override
	public void setDisableBranchCode(boolean disable) {
		configFitness.setDisableBranchCode(disable);
	}

	@Override
	public void setDisableBranchContract(boolean disable) {
		configFitness.setDisableBranchContract(disable);
	}

	@Override
	public void setDisableLength(boolean disable) {
		configFitness.setDisableLength(disable);
	}

	@Override
	public void setEnableBug(boolean bug) {
		configFitness.setEnableBug(bug);
	}

	@Override
	public File getDirBase() {
		return configGenerator.getDirBase();
	}

	@Override
	public String getCut() {
		return configGenerator.getCut();
	}

	@Override
	public boolean isReload() {
		return configGenerator.isReload();
	}

	@Override
	public File getDirSource() {
		return configGenerator.getDirSource();
	}

	@Override
	public File getDirCompiled() {
		return configGenerator.getDirCompiled();
	}

	@Override
	public File getDirGeneratedTests() {
		return configGenerator.getDirGeneratedTests();
	}

	@Override
	public File getDirContracts() {
		return configGenerator.getDirContracts();
	}

	@Override
	public int getTime() {
		return configGenerator.getTime();
	}

	@Override
	public File getDirInstrumented() {
		return configGenerator.getDirInstrumented();
	}

	@Override
	public boolean isCache() {
		return configGenerator.isCache();
	}

	@Override
	public int getNumVar() {
		return configGenerator.getNumVar();
	}

	@Override
	public int getNumVarCut() {
		return configGenerator.getNumVarCut();
	}

	@Override
	public void setDirBase(File dirBase) {
		configGenerator.setDirBase(dirBase);
	}

	@Override
	public int getMaxTestLen() {
		return configGenerator.getMaxTestLen();
	}

	@Override
	public void setDirSource(File dirSource) {
		configGenerator.setDirSource(dirSource);
	}

	@Override
	public void setCut(String cut) throws TestfulException {
		configGenerator.setCut(cut);
	}

	@Override
	public void setReload(boolean reload) {
		configGenerator.setReload(reload);
	}

	@Override
	public void setDirCompiled(File dirCompiled) {
		configGenerator.setDirCompiled(dirCompiled);
	}

	@Override
	public void setDirGeneratedTests(File dirGeneratedTests) {
		configGenerator.setDirGeneratedTests(dirGeneratedTests);
	}

	@Override
	public void setDirContracts(File dirContracts) {
		configGenerator.setDirContracts(dirContracts);
	}

	@Override
	public void setTime(int time) {
		configGenerator.setTime(time);
	}

	@Override
	public void setDirInstrumented(File dirInstrumented) {
		configGenerator.setDirInstrumented(dirInstrumented);
	}

	@Override
	public void setCache(boolean cache) {
		configGenerator.setCache(cache);
	}

	@Override
	public void setNumVar(int numVar) {
		configGenerator.setNumVar(numVar);
	}

	@Override
	public void setNumVarCut(int numVarCut) {
		configGenerator.setNumVarCut(numVarCut);
	}

	@Override
	public void setMaxTestLen(int maxTestLen) {
		configGenerator.setMaxTestLen(maxTestLen);
	}

	public void setSmartInitialPopulation(boolean smartInitialPopulation) {
		this.smartInitialPopulation = smartInitialPopulation;
	}

	@Override
	public List<String> getRemote() {
		return configRunner.getRemote();
	}

	@Override
	public boolean isLocalEvaluation() {
		return configRunner.isLocalEvaluation();
	}

	@Override
	public void addRemote(String remote) {
		configRunner.addRemote(remote);
	}

	@Override
	public void disableLocalEvaluation(boolean disableLocalEvaluation) {
		configRunner.disableLocalEvaluation(disableLocalEvaluation);
	}
}