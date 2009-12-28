package testful.gui.operator;

import testful.Configuration;
import testful.model.xml.Parser;
import testful.model.xml.XmlClass;

public class SaveXmlModel implements ITestfulOperator {

	private Result result = null;
	private Configuration config;
	private XmlClass xmlClass;
	
	public SaveXmlModel(Configuration config, XmlClass xmlClass){
		this.config = config;
		this.xmlClass = xmlClass;
	}
	
	public void run() {
		try {
			result = new Result((Parser.singleton.encode(xmlClass, config))); 
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
