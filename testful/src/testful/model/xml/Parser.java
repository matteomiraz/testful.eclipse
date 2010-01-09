package testful.model.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import testful.ConfigCut;
import testful.IConfigCut;
import testful.IConfigProject;
import testful.TestFul;
import testful.model.Methodz;
import testful.model.xml.XmlMethod.Kind;

public class Parser {

	private static final Logger logger = Logger.getLogger("testful.model.xml");

	/** Should I create the behavior element? */
	private static final boolean ADD_EMPTY_ELEMENTS = false;

	private static final Class<?>[] CLASSES = { XmlClass.class };

	public static final Parser singleton;
	static {
		Parser tmp = null;
		try {
			tmp = new Parser();
		} catch(JAXBException e) {
			logger.log(Level.SEVERE, "Problem creating the XML parser: " + e.getMessage(), e);
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

	public XmlClass parse(IConfigProject config, String fullQualifiedClassName) throws JAXBException {
		File file = new File(config.getDirSource(), fullQualifiedClassName.replace('.', File.separatorChar) + ".xml");
		return (XmlClass) unmarshaller.unmarshal(file);
	}

	public boolean encode(XmlClass xml, IConfigProject config) throws JAXBException {
		FileOutputStream out = null;

		try {
			final File outFile = new File(config.getDirSource(), xml.getName().replace('.', File.separatorChar) + ".xml");
			if(!outFile.getParentFile().exists())
				outFile.getParentFile().mkdirs();

			out = new FileOutputStream(outFile);
			marshaller.marshal(xml, out);
			return true;
		} catch(IOException e) {
			logger.log(Level.WARNING, "Cannot write to file: " + e.getMessage(), e);
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch(IOException e) {
					logger.log(Level.WARNING, "Cannot close the file: " + e.getMessage(), e);
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
				logger.fine("Skipping " + meth.getName());
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
		IConfigCut config = new ConfigCut();
		TestFul.parseCommandLine(config, args, Parser.class, "XML Creator");

		testful.TestFul.setupLogging(config);

		if(!config.isQuiet())
			TestFul.printHeader("XML Creator");

		run(config);
	}

	/**
	 * @param	baseDir			the eclipse workspace directory.
	 * @param	projectFolders	an array contains project folders, one for each class.
	 * @param	classes			an array contains classes
	 */
	public static void run(IConfigCut config) throws JAXBException, IOException {

		final URLClassLoader loader = new URLClassLoader(new URL[] { config.getDirCompiled().toURI().toURL() });

		try {
			Class<?> clazz = loader.loadClass(config.getCut());
			XmlClass xmlClass = singleton.createClassModel(clazz);
			xmlClass.setInstrument(true);
			singleton.encode(xmlClass, config);
		} catch(ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Class not found: " + e.getMessage(), e);
		}

	}

	private static class TestfulValidationEventHandler implements ValidationEventHandler {

		@Override
		public boolean handleEvent(ValidationEvent ve) {
			if(ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				//Print message from valdation event

				logger.warning(
						"Invalid xml document: " + locator.getURL() + "\n" +
						"Error: " + ve.getMessage() + "\n" +
						"Error at column " + locator.getColumnNumber() + ", line " + locator.getLineNumber()
				);
			}
			return true;
		}
	}
}
