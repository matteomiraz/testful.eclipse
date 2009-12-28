package testful.gui.operator;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import testful.Configuration;
import testful.core.Instrument;

public class Instrumenter implements IRunnableWithProgress, ITestfulOperator {

	private Configuration config;
	private Result result = null;

	public Instrumenter(Configuration config) {
		this.config = config;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Instrumenting...", IProgressMonitor.UNKNOWN);

		try {
			StringBuilder msg = new StringBuilder();
			boolean ok  = Instrument.instrument(config.getCut(), config.getDirBase(), msg);
			result = new Result(ok, (Object) msg.toString());
		} catch (Exception e) {
			result =new Result(false, (Object) e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public Result Result() {
		return result;
	}


}
