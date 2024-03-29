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


package testful.coverage.whiteBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class AnalysisWhiteBox implements Serializable {

	private static final long serialVersionUID = -8517242673275381964L;

	private static final String FILE_SUFFIX = ".wb.gz";
	private static final Logger logger = Logger.getLogger("testful.coverage.whiteBox");

	public static AnalysisWhiteBox read(File baseDir, String className) {
		ObjectInput oi = null;
		final File file = getFile(baseDir, className);
		try {
			oi = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
			return (AnalysisWhiteBox) oi.readObject();
		} catch(Exception e) {
			logger.log(Level.WARNING, "Exception while reading the file " + file.getAbsolutePath() + ": " + e.toString(), e);
			return null;
		} finally {
			if(oi != null)
				try {
					oi.close();
				} catch(IOException e) {
				}
		}
	}

	public void write(File baseDir, String className) {
		ObjectOutput oo = null;
		try {
			oo = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(getFile(baseDir, className))));
			oo.writeObject(this);
		} catch(IOException e) {
			Logger.getLogger("testful.coverage.whitebox").log(Level.WARNING, "Cannot write the white box data file: " + e.getMessage(), e);
		} finally {
			if(oo != null)
				try {
					oo.close();
				} catch(IOException e) {
				}
		}
	}

	private static File getFile(File baseDir, String className) {
		return new File(baseDir, className.replace('.', File.separatorChar) + AnalysisWhiteBox.FILE_SUFFIX);
	}

	private final Map<String, BlockClass> classes;

	public AnalysisWhiteBox() {
		classes = new HashMap<String, BlockClass>();
	}

	public void addClass(BlockClass bClass) {
		classes.put(bClass.getName(), bClass);
	}

	public BlockClass getBlockClass(String className) {
		return classes.get(className);
	}

	@Override
	public AnalysisWhiteBox clone() throws CloneNotSupportedException {
		return (AnalysisWhiteBox) super.clone();
	}

	private transient WhiteBoxData data = null;
	public WhiteBoxData getData() {
		if(data == null) {
			BitSet blocksCode = new BitSet();
			BitSet blocksContract = new BitSet();
			BitSet conditionsCode = new BitSet();
			BitSet conditionsContract = new BitSet();
			for(BlockClass bClass : classes.values()) {
				blocksCode.or(bClass.getBlocksCode());
				blocksContract.or(bClass.getBlocksContract());

				conditionsCode.or(bClass.getConditionsCode());
				conditionsContract.or(bClass.getConditionsContract());
			}
			data = new WhiteBoxData(blocksCode, blocksContract, conditionsCode, conditionsContract);
		}
		return data;
	}

	private transient BitSet conditionBlocks;
	private transient Map<Integer, BitSet> mapBlockCondition;
	public BitSet getReachableBranches(BitSet blockCoverage) {
		if(conditionBlocks == null) {
			conditionBlocks = new BitSet();
			mapBlockCondition = new HashMap<Integer, BitSet>();

			for(BlockClass bClass : classes.values()) {
				for(Block block : bClass) {
					Condition condition = block.getCondition();
					if(condition != null) {

						BitSet branches = new BitSet();
						if(condition instanceof ConditionIf) {
							ConditionIf cIf = (ConditionIf) condition;
							branches.set(cIf.getTrueBranch().getId());
							branches.set(cIf.getFalseBranch().getId());

						} else if(condition instanceof ConditionSwitch) {
							ConditionSwitch cSwi = (ConditionSwitch) condition;

							branches.set(cSwi.getDefaultBranch().getId());
							for(EdgeConditional br : cSwi.getBranches().values())
								branches.set(br.getId());
						}

						conditionBlocks.set(block.getId());
						mapBlockCondition.put(block.getId(), branches);
					}
				}
			}
		}

		blockCoverage = (BitSet) blockCoverage.clone();
		blockCoverage.and(conditionBlocks);

		BitSet reachable = new BitSet();
		for (int blockId = blockCoverage.nextSetBit(0); blockId >= 0; blockId = blockCoverage.nextSetBit(blockId+1))
			reachable.or(mapBlockCondition.get(blockId));

		return reachable;
	}

	private transient Map<Integer, Condition> mapBranchCondition;
	public Condition getConditionFromBranch(int branchId) {
		if(mapBranchCondition == null) {
			mapBranchCondition = new HashMap<Integer, Condition>();

			for(BlockClass bClass : classes.values()) {
				for(Block block : bClass) {
					Condition condition = block.getCondition();
					if(condition != null) {

						if(condition instanceof ConditionIf) {
							ConditionIf cIf = (ConditionIf) condition;
							mapBranchCondition.put(cIf.getTrueBranch().getId(), cIf);
							mapBranchCondition.put(cIf.getFalseBranch().getId(), cIf);

						} else if(condition instanceof ConditionSwitch) {
							ConditionSwitch cSwi = (ConditionSwitch) condition;

							mapBranchCondition.put(cSwi.getDefaultBranch().getId(), cSwi);
							for(EdgeConditional br : cSwi.getBranches().values())
								mapBranchCondition.put(br.getId(), cSwi);
						}
					}
				}
			}
		}

		return mapBranchCondition.get(branchId);
	}

}
