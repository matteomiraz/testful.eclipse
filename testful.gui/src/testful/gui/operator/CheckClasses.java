package testful.gui.operator;

import java.io.File;
import java.rmi.RemoteException;

import testful.Configuration;
import testful.model.Clazz;
import testful.model.TestCluster;
import testful.runner.ClassFinderCaching;
import testful.runner.ClassFinderImpl;
import testful.runner.TestfulClassLoader;

public class CheckClasses implements ITestfulOperator {

	private Configuration config;
	private Result result = null;
	
	public CheckClasses(Configuration config) {
		this.config = config;
	}

	public void run() {
		try {
			ClassFinderCaching finder = new ClassFinderCaching(new ClassFinderImpl(new File(config.getDirInstrumented()), new File(config.getDirJml()), new File(config.getDirVanilla())));
			TestfulClassLoader tcl = new TestfulClassLoader(finder);
			TestCluster tc = new TestCluster(tcl, config);
			String message = "";
			if (tc.getCut().isAbstract()) 
				for (Clazz c : tc.getCluster()) {
					if (!checkClass(c))
						message += "- Abstract class " + c.getClassName() + " has not non-abstract subclasses.\n";
				}
			boolean isSuccess = message.isEmpty();
			if (isSuccess) message = "Classes have been checked successfully.";
			result = new Result(isSuccess, message);
			return;
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		result = new Result(false, "Impossible to check Classes");
	}
	
	private boolean checkClass(Clazz c) {
		if (!c.isAbstract()) return true;
		
		for (Clazz s: c.getSubClasses()) {
			if (checkClass(s)) return true;
		}
		return false;
	}
	
	@Override
	public Result Result() {
		return result;
	}

}
