package testful.gui.wizard;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import testful.IConfigCut;
import testful.model.Clazz;
import testful.model.TestCluster;
import testful.model.xml.Parser;
import testful.model.xml.XmlClass;
import testful.model.xml.XmlConstructor;
import testful.model.xml.XmlMethod;
import testful.model.xml.XmlParameter;

public class XmlContentProvider implements ITreeContentProvider {

	public static final String EMPTY_TEST_CLUSTER = "";
	private final IConfigCut config;
	private final Map<String, XmlClass> xml = new HashMap<String, XmlClass>();

	public XmlContentProvider(IConfigCut config) {
		this.config = config;
	}

	@Override
	public void dispose() {
		xml.clear();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// If it is a class
		if (parentElement instanceof String) {
			XmlClass xmlClass = getClassXml((String) parentElement);

			if (xmlClass == null)
				return null;

			Object ret[] = new Object[xmlClass.getConstructor().size()
			                          + xmlClass.getMethod().size()];

			int i = 0;
			for (XmlConstructor c : xmlClass.getConstructor())
				ret[i++] = c;
			for (XmlMethod m : xmlClass.getMethod())
				ret[i++] = m;

			return ret;
		}

		if (parentElement instanceof XmlConstructor)
			return ((XmlConstructor) parentElement).getParameter()
			.toArray();

		if (parentElement instanceof XmlMethod)
			return ((XmlMethod) parentElement).getParameter().toArray();

		if (parentElement instanceof XmlParameter)
			return null;

		System.err.println("getChildren: Unknown type "
				+ parentElement.getClass().getCanonicalName() + ": "
				+ parentElement);

		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// If it is a class
		if (element instanceof String) {
			XmlClass xmlClass = getClassXml((String) element);

			if (xmlClass == null)
				return false;

			return !(xmlClass.getConstructor().isEmpty() && xmlClass
					.getMethod().isEmpty());
		}

		if (element instanceof XmlConstructor)
			return !((XmlConstructor) element).getParameter().isEmpty();
		if (element instanceof XmlMethod)
			return !((XmlMethod) element).getParameter().isEmpty();

		if (element instanceof XmlParameter)
			return false;

		// safe value for unknown classes
		System.err.println("hasChildren: Unknown type "
				+ element.getClass().getCanonicalName() + ": " + element);

		return true;
	}

	/**
	 * Accepts a test cluster, and returns the classes (their name) as root
	 * elements.
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TestCluster) {
			Clazz[] cluster = ((TestCluster) inputElement).getCluster();

			Object[] ret = new Object[cluster.length];
			for (int i = 0; i < ret.length; i++)
				ret[i] = cluster[i].getClassName();

			return ret;
		}

		if(inputElement == EMPTY_TEST_CLUSTER) {
			return new Object[] { config.getCut() };
		}

		System.err.println("getElements: Unsupported input type: " + inputElement.getClass().getCanonicalName());
		return null;
	}

	public XmlClass getClassXml(String className) {
		XmlClass ret = xml.get(className);

		if (ret == null) {
			try {
				ret = Parser.singleton.parse(config, className);

				if (ret != null)
					xml.put(className, ret);
			} catch (Exception e) {
			}
		}

		return ret;
	}

	public void reset() {
		xml.clear();
	}

	public void save() throws JAXBException {
		for (XmlClass xmlClass : xml.values()) {
			Parser.singleton.encode(xmlClass, config);
		}
	}

}

