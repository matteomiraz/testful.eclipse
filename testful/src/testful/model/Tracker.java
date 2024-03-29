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


package testful.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import testful.TestFul;
import testful.coverage.CoverageInformation;

/**
 * Collects tests with a given coverage criterion.
 */
public class Tracker {

	private static final Logger logger = Logger.getLogger("testful.model");

	public static class DataLight implements Serializable, Comparable<DataLight> {

		private static final long serialVersionUID = 8103127933129031049L;

		public final int selectedCoverage;
		public final TestCoverage[] tests;

		private DataLight(int coverage, Set<TestCoverage> testSet) {
			selectedCoverage = coverage;
			tests = testSet.toArray(new TestCoverage[testSet.size()]);
		}

		@Override
		public int compareTo(DataLight o) {
			return selectedCoverage - o.selectedCoverage;
		}

		public static DataLight[] toArray(Map<Integer, SortedSet<TestCoverage>> map) {
			DataLight[] ret = new DataLight[map.size()];

			int i = 0;
			for(Integer cov : new TreeSet<Integer>(map.keySet()))
				ret[i++] = new DataLight(cov, map.get(cov));

			return ret;
		}
	}

	private static final int ELEMS = 10;
	private static final boolean KEEP_TOP_HALF = true;

	private final File baseDir;

	private final String coverageKey;
	private final Map<Integer, SortedSet<TestCoverage>> coverage;

	/**
	 * A value < 0 implies no new data to write.<br>
	 * A value >= 0 indicates the number of iteration the tracker is waiting to
	 * write.
	 */
	private int wait = -1;
	private static final int MAX_WAIT = 5;

	private float prevRatio;
	private boolean improved;

	private int maxCov = Integer.MIN_VALUE;

	public Tracker(File baseDir, String coverageKey) {
		this.baseDir = baseDir;
		this.coverageKey = coverageKey;
		coverage = new HashMap<Integer, SortedSet<TestCoverage>>();
	}

	/**
	 * Updates optimal solutions and combined coverage
	 * 
	 * @param test the test
	 */
	public void update(TestCoverage test) {

		if(test == null) return;

		CoverageInformation info = test.getCoverage().get(coverageKey);
		if(info == null) return;

		int covValue = (int) Math.ceil(info.getQuality());

		// keep only tests with coverage between 50% to 100%
		if(KEEP_TOP_HALF) {
			if(covValue < (maxCov / 2)) return;
			if(covValue > maxCov) {
				improved = true;
				maxCov = covValue;

				Set<Integer> toRemove = new HashSet<Integer>();
				for(Integer i : coverage.keySet())
					if(i < (maxCov / 2)) toRemove.add(i);
				for(Integer i : toRemove)
					coverage.remove(i);
			}
		}

		// if the coverage map doesn't contain any test with the same coverage value
		if(!coverage.containsKey(covValue)) {
			SortedSet<TestCoverage> tests = new TreeSet<TestCoverage>(Test.sizeComparator);
			coverage.put(covValue, tests);
			tests.add(CoverageLight.convert(test));

			if(wait < 0) wait = 0;

		} else {
			SortedSet<TestCoverage> tests = coverage.get(covValue);

			if(tests.size() >= ELEMS) if(test.getTest().length < tests.last().getTest().length) tests.remove(tests.last());
			else return;

			tests.add(CoverageLight.convert(test));

			if(wait < 0) wait = 0;
		}
	}

	public void write() {
		if(wait < 0) {
			logger.fine("Tracker" + coverageKey + ": tests not modified, skipping the update!");
			return;
		} else if(++wait < MAX_WAIT) return;

		long start = System.currentTimeMillis();
		ObjectOutput oo = null;
		try {
			oo = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(TestFul.createFileWithBackup(baseDir, "tracker-" + coverageKey + ".ser.gz"))));
			oo.writeObject(DataLight.toArray(coverage));
			wait = -1;
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(oo != null) try {
				oo.close();
			} catch(IOException e) {
				e.printStackTrace();
			}

			long stop = System.currentTimeMillis();

			int numTest = 0;
			int totLength = 0;
			for(SortedSet<TestCoverage> set : coverage.values()) {
				numTest += set.size();
				for(TestCoverage t : set)
					totLength += t.getTest().length;
			}

			float newRatio = 1.0f * totLength / numTest;
			StringBuilder sb = new StringBuilder();
			sb.append("Tracker").append(coverageKey).append(": ").append(maxCov).append(" ").append((stop - start) / 1000.0).append("s for ").append(numTest).append(" tests ").append(totLength).append(
			" operations, ").append(String.format("%.2f", newRatio)).append("op/test");

			if(!improved) sb.append(" ").append(String.format("%.2f%%", ((newRatio - prevRatio) / prevRatio) * 100));
			logger.info(sb.toString());
			prevRatio = newRatio;
			improved = false;
		}
	}
}
