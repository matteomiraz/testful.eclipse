package testful.gui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class Util {

	public static final String WORKSPACEDIR;
	static {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IPath location = root.getLocation();
		WORKSPACEDIR = location.toOSString();
	}

	public static File getPluginResource(String path) throws IOException {
		try {
			URL eclipseURL = FileLocator.find(Activator.getDefault().getBundle(), new Path(path), null);
			URL javaURL = FileLocator.toFileURL(eclipseURL);
			return new File(javaURL.toURI());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	public static final String IMAGEDIR = "./images/";

	public static final String SOURCEFOLDER = "src";
	public static final String BINARYFOLDER = "bin";

	public String getAbsolutePath(String resource) {
		try {
			return getClass().getClassLoader().getResource(resource).toURI().getPath();
		} catch (URISyntaxException e) {
			return resource;
		}
	}

	public static String getISelectionPath(Object elem) throws Exception {
		if (!(elem instanceof IFile)) throw new Exception("Not valid selection");
		return ((IFile)elem).getFullPath().toOSString(); //The full Class path from baseDir
	}

	public static String getClassPath(String path) {
		try {
			//Return the Class path from Source Folder
			//If path is "\\draw1\\src\\draw2\\Punto2D.java" it returns "\\draw\\Punto2D.java"
			String tmp = path.substring(path.indexOf(File.separator +SOURCEFOLDER+File.separator)+SOURCEFOLDER.length()+1);
			if (!tmp.startsWith(File.separator)) tmp = File.separator + tmp;
			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
			return path;
		}
	}

	public static String getSourceFolderPath(String path) {
		try {
			//Return the Source Folder path from baseDir
			//If path is "\\draw0\\draw1\\src\\draw2\\Punto2D.java" it returns "\\draw0\\draw1"
			String tmp = path.substring(0, path.indexOf(File.separator +SOURCEFOLDER+File.separator));
			if (!tmp.startsWith(File.separator)) tmp = File.separator + tmp;
			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
			return path;
		}
	}

	public static String getClassName(String classPath) {
		try {
			//turns "/dir1/dir2/.../myClass.java" in "dir1.dir2.myClass"
			String tmp = classPath;
			String[] toReplace = {".java", ".class", ".xml"};
			for (String ext : toReplace) {
				if (tmp.endsWith(ext)) {
					tmp = tmp.substring(0, tmp.length()-ext.length());
					break;
				}
			}
			tmp = tmp.replace(File.separator, ".");
			if (tmp.startsWith(".")) tmp = tmp.substring(1);
			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
			return classPath;
		}
	}
}
