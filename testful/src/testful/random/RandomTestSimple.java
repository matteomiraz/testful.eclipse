package testful.random;

import java.util.concurrent.Future;

import testful.coverage.CoverageInformation;
import testful.coverage.TrackerDatum;
import testful.model.Operation;
import testful.model.ReferenceFactory;
import testful.model.Test;
import testful.model.TestCluster;
import testful.runner.ClassFinder;
import testful.utils.ElementManager;
import testful.utils.SimpleEntry;

public class RandomTestSimple extends RandomTest {

	private final int TEST_SIZE = 500;

	public RandomTestSimple(boolean enableCache, ClassFinder finder, TestCluster cluster, ReferenceFactory refFactory, TrackerDatum ... data) {
		super(enableCache, finder, cluster, refFactory, data);
	}

	@Override
	public void test(long duration) {
		start = System.currentTimeMillis();
		stop = start + duration;

		try {
			while(System.currentTimeMillis() < stop) {

				Operation[] ops = new Operation[TEST_SIZE];
				for(int i = 0; i < TEST_SIZE; i++)
					ops[i] = Operation.randomlyGenerate(cluster, refFactory, random);

				Future<ElementManager<String, CoverageInformation>> fut = runner.executeParts(finder, true, new Test(cluster, refFactory, ops), data);
				tests.put(new SimpleEntry<Operation[], Future<ElementManager<String, CoverageInformation>>>(ops, fut));
			}
		} catch(InterruptedException e) {
			System.err.println("Interrupted: " + e);
		}

		try {
			while(getRunningJobs() > 0)
				Thread.sleep(1000);
		} catch(InterruptedException e) {}

		keepRunning = false;
	}
}
