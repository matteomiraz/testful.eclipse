package testful.gui;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class TestfulImage {

	private Display display;

	public static enum IMAGE {
		ARROW_DOWN("arrow_down.gif"),
		ARROW_UP("arrow_up.gif"),
		REMOVE("remove.gif"),
		INFO("info.gif"),
		WAIT("wait.gif"),

		MODEL_AUX("auxilary.gif"),
		MODEL_CONSTRUCTOR("constructor.gif"),
		MODEL_METHOD("method.gif"),
		MODEL_PARAMETER("parameter.gif"),
		MODEL_ADD_AUX("auxilary_add.gif"),
		MODEL_ADD_CONSTRUCTOR("constructor_add.gif"),
		MODEL_ADD_METHOD("method_add.gif"),
		MODEL_ADD_PARAMETER("parameter_add.gif"),

		TESTFUL("testful.gif"),
		TESTFUL_ADD("testful_add.gif"),
		TESTFUL_EMPTY("testful_empty.gif"),
		TESTFUL_WIZARD("testful_wizard.gif"),
		TESTFUL_REMOVE("testful_remove.gif"),
		TESTFUL_WARNING("testful_warning.gif"),
		TESTFUL_MODIFY("testful_modify.gif"),
		TESTFUL_WIZARD_BIG("testful_wizard_big.gif");

		private final String value;

		IMAGE(String v) {
			value = v;
		}

		public String value() {
			return value;
		}

		public String path() {
			return Util.IMAGEDIR + value;
		}

		public static IMAGE fromValue(String v) {
			for(IMAGE c : IMAGE.values())
				if(c.value.equals(v)) return c;
			throw new IllegalArgumentException(v.toString());
		}

	}

	public TestfulImage(Display display) {
		this.display = display;
	}

	public Image loadImage(IMAGE img) {
		return loadImage(img.path());
	}

	public Image loadImage(String imgFileName) {
		Image img = new Image(display, 20, 20);
		try {
			if (new File(imgFileName).exists()) img = new Image(display, imgFileName);
			else img = ImageDescriptor.createFromURL(FileLocator.find(Activator.getDefault().getBundle(), new Path(imgFileName),null)).createImage(display);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		}
		return img;
	}

}