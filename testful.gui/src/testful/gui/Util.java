package testful.gui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

public class Util {

	public static File getPluginResource(String path) throws IOException {
		try {
			URL eclipseURL = FileLocator.find(Activator.getDefault().getBundle(), new Path(path), null);
			URL javaURL = FileLocator.toFileURL(eclipseURL);
			return new File(javaURL.toURI());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}


	public String getAbsolutePath(String resource) {
		try {
			return getClass().getClassLoader().getResource(resource).toURI().getPath();
		} catch (URISyntaxException e) {
			return resource;
		}
	}

	/**
	 * turns "/dir1/dir2/.../myClass.java" in "dir1.dir2.myClass"
	 * @param javaFile the name of the java File
	 * @return the corresponding class file
	 */
	public static String getClassName(String javaFile) {
		if (javaFile.endsWith(".java"))
			javaFile = javaFile.substring(0, javaFile.length()-5);

		javaFile = javaFile.replace(File.separator, ".");
		if (javaFile.startsWith(".")) javaFile = javaFile.substring(1);
		return javaFile;
	}
}
