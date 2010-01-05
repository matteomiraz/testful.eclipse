package testful.random;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import testful.coverage.CoverageInformation;
import testful.coverage.RunnerCaching;
import testful.coverage.TrackerDatum;
import testful.model.Operation;
import testful.model.OptimalTestCreator;
import testful.model.ReferenceFactory;
import testful.model.Test;
import testful.model.TestCluster;
import testful.model.TestCoverage;
import testful.model.TestSuite;
import testful.runner.ClassFinder;
import testful.utils.ElementManager;
import testful.utils.TestfulLogger;
import testful.utils.TestfulLogger.CombinedCoverageWriter;
import ec.util.MersenneTwisterFast;

public abstract class RandomTest {
	protected static Logger logger = Logger.getLogger("testful.random");

	protected long start, stop;
	private long numCall;

	protected final RunnerCaching runner;

	protected final TestCluster cluster;
	protected final ReferenceFactory refFactory;

	private AtomicInteger testsDone = new AtomicInteger();

	protected final BlockingQueue<Entry<Operation[], Future<ElementManager<String, CoverageInformation>>>> tests = new LinkedBlockingQueue<Entry<Operation[], Future<ElementManager<String, CoverageInformation>>>>();
	private final OptimalTestCreator optimal = new OptimalTestCreator();
	protected final ClassFinder finder;
	protected final TrackerDatum[] data;

	protected final MersenneTwisterFast random;

	protected volatile boolean keepRunning = true;

	public RandomTest(boolean enableCache, ClassFinder finder, TestCluster cluster, ReferenceFactory refFactory, TrackerDatum ... data) {
		long seed = System.currentTimeMillis();
		System.out.println("MersenneTwisterFast: seed=" + seed);
		random = new MersenneTwisterFast(seed);

		runner = new RunnerCaching(enableCache);

		this.cluster = cluster;
		this.refFactory = refFactory;

		this.finder = finder;
		this.data = data;
	}

	public abstract void test(long duration);

	public ElementManager<String, CoverageInformation> getExecutionInformation() {
		return optimal.getCoverage();
	}

	public int getRunningJobs() {
		return tests.size();
	}

	public void startNotificationThread(final boolean verbose) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while(keepRunning)
					try {
						Entry<Operation[], Future<ElementManager<String, CoverageInformation>>> entry = tests.take();
						ElementManager<String, CoverageInformation> cov = entry.getValue().get();
						testsDone.incrementAndGet();
						numCall += entry.getKey().length;

						final TestCoverage testCoverage = new TestCoverage(new Test(cluster, refFactory, entry.getKey()), cov);
						optimal.update(testCoverage);

					} catch(InterruptedException e) {
						System.err.println("Interrupted: " + e);
					} catch(ExecutionException e) {
						logger.warning(e.getMessage());
					}
			}
		}, "futureWaiter");
		t.setDaemon(true);
		t.start();

		CombinedCoverageWriter wrTmp = null;
		try {
			wrTmp = TestfulLogger.singleton.getCombinedCoverageWriter(("combined.csv"));
		} catch (FileNotFoundException e1) {
			System.err.println("Cannot write to file: " + e1.getMessage());
		}
		final CombinedCoverageWriter wr = wrTmp;

		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while(keepRunning) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch(InterruptedException e) {
						return;
					}

					if(wr != null)
						wr.write(0, numCall, optimal.getCoverage(), null);
					optimal.write();

					runner.updateCacheScore();

					if(verbose) {
						long now = System.currentTimeMillis();
						StringBuilder sb = new StringBuilder();

						sb.append("Start: ").append(new Date(start)).append(" ").append(((now - start) / 1000) / 60).append(" minutes ").append(((now - start) / 1000) % 60).append(" seconds ago\n");
						sb.append("Now  : ").append(new Date()).append("\n");
						sb.append("End  : ").append(new Date(stop)).append(" ").append(((stop - now) / 1000) / 60).append(" minutes ").append(((stop - now) / 1000) % 60).append(" seconds").append("\n");

						sb.append("Running ").append(getRunningJobs()).append(" jobs (").append(testsDone.get()).append(" done)\n");
						if(runner.isEnabled()) sb.append(runner).append("\n");

						if(!optimal.getCoverage().isEmpty()) {
							sb.append("Coverage:\n");
							for(CoverageInformation info : optimal.getCoverage())
								sb.append("  ").append(info.getName()).append(": ").append(info.getQuality()).append("\n");
						}

						sb.append("---");
						System.out.println(sb);
					}
				}
			}
		}, "notification");

		t.setDaemon(true);
		t.start();
	}

	/**
	 * @author Tudor
	 * @return The list of Tests(OpSequences) generated and selected by RT
	 * Note: This function is useful only after it processes something :)
	 */
	public TestSuite getResults() {
		TestSuite ret = new TestSuite();
		for (TestCoverage test : optimal.get())
			ret.add(test);

		return ret;
	}
}
