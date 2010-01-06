package testful.gui.operator;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import testful.ConfigCut;

public class CreateXmlModel implements ITestfulOperator {

	private Result result = null;
	private String[] sourceFolders;
	private  String[] classes;

	public CreateXmlModel(String[] sourceFolders, String[] classes) {
		this.sourceFolders = sourceFolders;
		this.classes = classes;
	}

	public void run() {
		try {
			ConfigCut config = new ConfigCut();
			for(int i = 0; i < classes.length; i++) {
				config.setCut(classes[i]);
				config.setDirBase(new File(sourceFolders[i]));
				testful.model.xml.Parser.run(config);
			}

			result = new Result(true);
		} catch (Exception e) {
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
