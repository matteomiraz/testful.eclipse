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

import java.util.Arrays;
import java.util.List;

import testful.ConfigCut;
import testful.GenericTestCase;
import testful.regression.TestSimplifier;
import testful.runner.Context;
import testful.runner.TestfulClassLoader;

/**
 * @author matteo
 */
public class TestSimpifierTestCase extends AutoTestCase {

	@Override
	protected List<Test> perform(Test test) throws Exception {
		OperationResult.insert(test.getTest());

		Context<Operation[], TestExecutionManager> ctx = TestExecutionManager.getContext(getFinder(), test);
		ctx.setStopOnBug(false);
		ctx.setRecycleClassLoader(true);
		Operation[] ops = getExec().execute(ctx).get();
		for (int i = 0; i < ops.length; i++) {
			ops[i] = ops[i].adapt(test.getCluster(), test.getReferenceFactory());
		}

		System.out.println(new Test(test.getCluster(), test.getReferenceFactory(), ops));

		TestSimplifier s = new TestSimplifier();
		Test r = s.process(new Test(test.getCluster(), test.getReferenceFactory(), ops));

		return Arrays.asList(r);
	}

	public void testSimple1() throws Exception {
		ConfigCut config = new ConfigCut(GenericTestCase.config);
		config.setCut("dummy.Simple");
		TestCluster cluster = new TestCluster(new TestfulClassLoader(getFinder()), config);

		ReferenceFactory refFactory = new ReferenceFactory(cluster, 4, 4);

		Clazz cut = cluster.getCut();

		Reference c0 = refFactory.getReferences(cut)[0];

		Constructorz cns = cut.getConstructors()[0];

		Methodz mInc = null;
		for(Methodz m : cut.getMethods()) {
			if("mInc".equals(m.getName())) mInc = m;
		}


		Test test = new Test(cluster, refFactory, new Operation[] {
				new CreateObject(c0, cns, new Reference[] { }),
				new Invoke(null, c0, mInc, new Reference[] { })
		});

		Operation[][] expected = {
				{
					new CreateObject(c0, cns, new Reference[] { }),
					new Invoke(null, c0, mInc, new Reference[] { })
				}
		};

		check(test, expected);
	}
}
