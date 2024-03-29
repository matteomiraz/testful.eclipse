/*
 * TestFul - http://code.google.com/p/testful/
 * Copyright (C) 2010  Matteo Miraz
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package testful.evolutionary.jMetal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.operator.crossover.OnePointCrossoverVarLen;
import jmetal.base.operator.localSearch.LocalSearch;
import jmetal.base.operator.selection.BinaryTournament2;
import jmetal.base.operator.selection.Selection;
import jmetal.util.JMException;
import testful.IUpdate.Callback;
import testful.TestFul;
import testful.TestfulException;
import testful.coverage.TrackerDatum;
import testful.coverage.whiteBox.AnalysisWhiteBox;
import testful.evolutionary.ConfigEvolutionary;
import testful.evolutionary.IConfigEvolutionary;
import testful.model.Operation;
import testful.model.Test;
import testful.model.TestCoverage;
import testful.model.TestExecutionManager;
import testful.model.TestSuite;
import testful.random.RandomTest;
import testful.random.RandomTestSplit;
import testful.regression.JUnitTestGenerator;
import testful.regression.TestSuiteReducer;
import testful.runner.RunnerPool;
import testful.utils.Utils;

public class Launcher {
	private static Logger logger = Logger.getLogger("testful.evolutionary");

	public static void main(String[] args) throws TestfulException {
		ConfigEvolutionary config = new ConfigEvolutionary();
		TestFul.parseCommandLine(config, args, Launcher.class, "Evolutionary test generator");

		if(!config.isQuiet())
			testful.TestFul.printHeader("Evolutionary test generator");

		TestFul.setupLogging(config);

		logger.config(TestFul.getProperties(config));

		run(config);

		System.exit(0);
	}

	public static void run(IConfigEvolutionary config, Callback ... callBacks) throws TestfulException {
		RunnerPool.getRunnerPool().config(config);

		if(config.getLog() != null && config.getLogLevel().getLoggingLevel().intValue() > Level.FINE.intValue()) {
			try {
				final String logFile = config.getLog().getAbsolutePath() + File.separator + "NSGAII_main.log";
				jmetal.base.Configuration.logger_.addHandler(new FileHandler(logFile));

				logger.info("Logging NSGAII to " + logFile);
			} catch (IOException e) {
				logger.warning("Cannot enable logging for NSGAII: " + e.getMessage());
			}
		}

		JMProblem problem;
		try {
			problem = new JMProblem(config);
		} catch (JMException e) {
			throw new TestfulException(e);
		}

		NSGAII<Operation> algorithm = new NSGAII<Operation>(problem);
		algorithm.setPopulationSize(config.getPopSize());
		algorithm.setMaxEvaluations(config.getTime() * 1000);
		algorithm.setInherit(config.getFitnessInheritance());
		algorithm.setUseCpuTime(config.isUseCpuTime());

		try {
			problem.addReserve(genSmartPopulation(config, problem));
		} catch (Exception e) {
			logger.log(Level.WARNING, "Cannot create the initial population: " + e.getMessage(), e);
		}

		// Mutation and Crossover for Real codification
		OnePointCrossoverVarLen<Operation> crossover = new OnePointCrossoverVarLen<Operation>();
		crossover.setProbability(0.50);
		crossover.setMaxLen(config.getMaxTestLen());

		algorithm.setCrossover(crossover);

		TestfulMutation mutation = new TestfulMutation(problem);
		mutation.setProbability(0.05);
		mutation.setProbRemove(0.75f);
		algorithm.setMutation(mutation);

		/* Selection Operator */
		Selection<Operation,Solution<Operation>> selection = new BinaryTournament2<Operation>();
		algorithm.setSelection(selection);

		if(config.getLocalSearchPeriod() > 0) {
			LocalSearch<Operation> localSearch = problem.getLocalSearch();
			algorithm.setImprovement(localSearch);
			algorithm.setLocalSearchPeriod(config.getLocalSearchPeriod());
			algorithm.setLocalSearchNum(config.getLocalSearchElements()/100.0f);
		}

		for (Callback callBack : callBacks)
			algorithm.register(callBack);

		/* Execute the Algorithm */
		SolutionSet<Operation> population;
		try {
			population = algorithm.execute();
		} catch (JMException e) {
			throw new TestfulException(e);
		}

		/* simplify tests */
		final TestSuiteReducer reducer = new TestSuiteReducer(problem.getFinder(), problem.getData(), config.isSimplify());
		for (Solution<Operation> sol : population)
			reducer.process(problem.getTest(sol));

		/* get Operation status */
		List<TestCoverage> optimal = new ArrayList<TestCoverage>();
		for (TestCoverage testCoverage : reducer.getOutput()) {
			try {
				Operation[] ops = TestExecutionManager.getOpStatus(problem.getFinder(), testCoverage);
				optimal.add(new TestCoverage(new Test(testCoverage.getCluster(), testCoverage.getReferenceFactory(), ops), testCoverage.getCoverage()));
			} catch (Exception e) {
				logger.log(Level.WARNING, "Cannot execute the test: " + e.getLocalizedMessage(), e);
				optimal.add(testCoverage);
			}
		}


		/* convert tests to jUnit */
		JUnitTestGenerator gen = new JUnitTestGenerator(config.getDirGeneratedTests());
		gen.process(optimal);
		gen.writeSuite();

	}//main


	/**
	 * This function uses random.Launcher to generate a smarter initial population
	 * @author Tudor
	 * @return
	 * @throws TestfulException
	 * @throws RemoteException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 */
	private static TestSuite genSmartPopulation(IConfigEvolutionary config, JMProblem problem) throws TestfulException, RemoteException, ClassNotFoundException, FileNotFoundException{
		int smartTime = config.getSmartInitialPopulation();

		if(smartTime <= 0) return null;

		logger.info("Generating smart population");

		AnalysisWhiteBox whiteAnalysis = AnalysisWhiteBox.read(config.getDirInstrumented(), config.getCut());
		TrackerDatum[] data = Utils.readData(whiteAnalysis);

		RandomTest rt = new RandomTestSplit(config.isCache(), null, problem.getFinder() , problem.getCluster(), problem.getRefFactory(), data);

		rt.test(smartTime * 1000);

		return rt.getResults();
	} //genSmartPopulation
} // NSGAII_main
