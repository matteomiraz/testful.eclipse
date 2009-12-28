package testful.model.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import testful.Configuration;
import testful.model.Methodz;
import testful.model.xml.XmlMethod.Kind;

public class Parser {

	/** Should I create the behavior element? */
	private static final boolean ADD_EMPTY_ELEMENTS = false;

	private static final Class<?>[] CLASSES = { XmlClass.class };

	public static final Parser singleton;
	static {
		Parser tmp = null;
		try {
			tmp = new Parser();
		} catch(JAXBException e) {
			System.err.println("FATAL ERROR: " + e);
			e.printStackTrace();
		}
		singleton = tmp;
	}

	private final JAXBContext jaxbContext;
	private final Unmarshaller unmarshaller;
	private final Marshaller marshaller;

	private Parser() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(CLASSES);

		unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setEventHandler(new TestfulValidationEventHandler());

		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	}

	public XmlClass parse(Configuration config, String fullQualifiedClassName) throws JAXBException {
		String fileName = config.getDirSource() + File.separator + fullQualifiedClassName.replace('.', File.separatorChar) + ".xml";

		return (XmlClass) unmarshaller.unmarshal(new File(fileName));
	}

	public boolean encode(XmlClass xml, Configuration config) throws JAXBException {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(config.getDirSource() + File.separator + xml.getName().replace('.', File.separatorChar) + ".xml");
			marshaller.marshal(xml, out);
			return true;
		} catch(IOException e) {
			System.err.println("Cannot write to file: " + e);
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch(IOException e) {
					System.err.println("Cannot close the xml file: " + e);
				}
			}
		}
		return false;
	}

	public XmlClass createClassModel(Class<?> c) {
		XmlClass xmlClass = testful.model.xml.ObjectFactory.factory.createClass();
		xmlClass.setName(c.getCanonicalName());

		if(ADD_EMPTY_ELEMENTS) xmlClass.getAux().add(new XmlAux());

		for(Constructor<?> cns : c.getConstructors()) {
			XmlConstructor xcns = testful.model.xml.ObjectFactory.factory.createConstructor();

			for(Class<?> p : cns.getParameterTypes()) {
				XmlParameter xmlParam = testful.model.xml.ObjectFactory.factory.createParameter();
				xmlParam.setType(p.getCanonicalName());
				xmlParam.setCaptured(p.isArray());
				xmlParam.setMutated(p.isArray());
				xmlParam.setExposedByReturn(false);
				xmlParam.setExchangeStateWith("");
				xcns.getParameter().add(xmlParam);
			}

			xmlClass.getConstructor().add(xcns);
		}

		for(Method meth : c.getMethods()) {
			if(Methodz.toSkip(meth)) {
				System.out.println("Skipping " + meth.getName());
				continue;
			}

			XmlMethod xmeth = testful.model.xml.ObjectFactory.factory.createMethod();

			xmeth.setExposeState(meth.getReturnType().isArray());
			xmeth.setName(meth.getName());
			if(Modifier.isStatic(meth.getModifiers())) xmeth.setKind(Kind.STATIC);
			else xmeth.setKind(Kind.MUTATOR);

			for(Class<?> p : meth.getParameterTypes()) {
				XmlParameter xmlParam = testful.model.xml.ObjectFactory.factory.createParameter();
				xmlParam.setType(p.getCanonicalName());
				xmlParam.setCaptured(p.isArray());
				xmlParam.setMutated(p.isArray());
				xmlParam.setExposedByReturn(p.isArray() && meth.getReturnType().isArray());
				xmlParam.setExchangeStateWith("");
				xmeth.getParameter().add(xmlParam);
			}

			xmlClass.getMethod().add(xmeth);
		}

		return xmlClass;
	}

	public static void main(String[] args) throws JAXBException, IOException {
		run("", new Object[args.length], args);
	}

	/**
	 * @param	baseDir			the eclipse workspace directory.
	 * @param	projectFolders	an array contains project folders, one for each class.
	 * @param	classes			an array contains classes
	 */
	public static void run(String baseDir, Object[] projectFolders, Object[] classes) throws JAXBException, IOException {

		Configuration config;
		URLClassLoader loader;

		for (int i = 0; i < classes.length; i++) {
			try {
				config = new Configuration(baseDir+(String)projectFolders[i]);
				loader = new URLClassLoader(new URL[] { new File(config.getDirVanilla()).toURI().toURL()});

				Class<?> clazz = loader.loadClass((String)classes[i]);

				XmlClass xmlClass = singleton.createClassModel(clazz);
				xmlClass.setInstrument(true);
				singleton.encode(xmlClass, config);

			} catch(ClassNotFoundException e) {
				System.err.println("Class not found: " + e);
			}
		}
	}

	private static class TestfulValidationEventHandler implements ValidationEventHandler {

		@Override
		public boolean handleEvent(ValidationEvent ve) {
			if(ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				//Print message from validation event
				System.out.println("Invalid booking document: " + locator.getURL());
				System.out.println("Error: " + ve.getMessage());
				//Output line and column number
				System.out.println("Error at column " + locator.getColumnNumber() + ", line " + locator.getLineNumber());
			}
			return true;
		}
	}
}
