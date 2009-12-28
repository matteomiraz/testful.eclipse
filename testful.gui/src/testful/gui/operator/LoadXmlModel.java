package testful.gui.operator;

import java.io.File;

import testful.Configuration;
import testful.gui.Util;
import testful.model.xml.Parser;
import testful.model.xml.XmlClass;

public class LoadXmlModel implements ITestfulOperator {

	private Result result = null;
	private Configuration config;
	private String cut;
	
	public LoadXmlModel(Configuration config) {
		this.config = config;
		this.cut = config.getCut();
	}
	
	public LoadXmlModel(Configuration config, String cut) {
		this.config = config;
		this.cut = cut;
	}
	
	public void run() {
		try {
			
			String classFile = config.getDirSource() + File.separator + cut.replace(".", File.separator) + ".xml";
			if (! new File(classFile).exists()) {
				String[]	s = {config.getDirBase().replace(Util.WORKSPACEDIR, "")},
							c = {cut};
				CreateXmlModel create = new CreateXmlModel(s, c);
				create.run();
				Result res = create.Result();
				if (!res.isSuccess) return;
			}
			
			XmlClass xmlClass = new XmlClass();
			xmlClass = Parser.singleton.parse(config, cut);
			result = new Result(true, xmlClass);
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(false, e.getMessage());
		}
	}
	
	@Override
	public Result Result() {
		return result;
	}

}
