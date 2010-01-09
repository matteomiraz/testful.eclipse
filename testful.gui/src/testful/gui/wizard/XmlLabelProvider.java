package testful.gui.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import testful.gui.Activator;
import testful.model.xml.XmlConstructor;
import testful.model.xml.XmlMethod;
import testful.model.xml.XmlParameter;

public class XmlLabelProvider implements ILabelProvider {

	private final Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>();

	@Override
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		if (element instanceof String)
			descriptor = Activator.getImageDescriptor("info.gif");
		else if (element instanceof XmlConstructor)
			descriptor = Activator.getImageDescriptor("constructor.gif");
		else if (element instanceof XmlMethod)
			descriptor = Activator.getImageDescriptor("method.gif");
		else if (element instanceof XmlParameter)
			descriptor = Activator.getImageDescriptor("parameter.gif");
		else
			return null;

		// obtain the cached image corresponding to the descriptor
		Image image = imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof String)
			return (String) element;

		if (element instanceof XmlConstructor)
			return "Constructor";

		if (element instanceof XmlMethod)
			return ((XmlMethod) element).getName();

		if (element instanceof XmlParameter)
			return ((XmlParameter) element).getType();

		return element.toString();
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
		for (Image img : imageCache.values())
			img.dispose();

		imageCache.clear();
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

}