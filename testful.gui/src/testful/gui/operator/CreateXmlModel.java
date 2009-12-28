package testful.gui.operator;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import testful.gui.Util;

public class CreateXmlModel implements ITestfulOperator {

	private Result result = null;
	private Object[] sourceFolders;
	private  Object[] classes;
	
	public CreateXmlModel(Object[] sourceFolders, Object[] classes) {
		this.sourceFolders = sourceFolders;
		this.classes = classes;
	}

	public void run() {
		try {
			testful.model.xml.Parser.run(Util.WORKSPACEDIR, sourceFolders, classes);
			result = new Result(true);
		} catch (JAXBException e) {
			e.printStackTrace();
			result = new Result(false, "Error occurred on creation of Xml Models");
		} catch (IOException e) {
			e.printStackTrace();
			result = new Result(false, "Error occurred on creation of Xml Models");
		}
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Result Result() {
		return result;
	}

}
