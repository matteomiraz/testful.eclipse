package testful.gui.handler;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;

import testful.ConfigCut;

public class ConfigUtils {

	public static IResource getProjectResource(IStructuredSelection selection) throws Exception {
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof ICompilationUnit) {
			ICompilationUnit compilationUnit = (ICompilationUnit) firstElement;

			IResource resource = compilationUnit.getResource();
			return resource.getProject();
		} else {
			throw new Exception("Please select a Java source file");
		}
	}

	public static ConfigCut getConfigCut(IStructuredSelection selection) throws Exception {
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof ICompilationUnit) {
			ICompilationUnit compilationUnit = (ICompilationUnit) firstElement;

			IResource resource = compilationUnit.getResource();
			IProject project = resource.getProject();
			IJavaProject javaProject = compilationUnit.getJavaProject();

			File baseDir = project.getLocation().toFile();
			String className = getPublicClassFullQualifiedName(compilationUnit);
			IPath sourcePath = getSourceDir(compilationUnit);
			File srcDir = sourcePath.removeFirstSegments(1).toFile();
			File compiledDir = javaProject.getOutputLocation().removeFirstSegments(1).toFile();

			ConfigCut configCut = new ConfigCut();
			configCut.setCut(className);
			configCut.setDirBase(baseDir);
			configCut.setDirSource(srcDir);
			configCut.setDirCompiled(compiledDir);

			return configCut;
		} else {
			throw new Exception("Please select a Java source file");
		}
	}

	private static IPath getSourceDir(ICompilationUnit cu) {
		IJavaElement parent = cu.getParent();

		while(!(parent instanceof IPackageFragmentRoot)) {
			parent = parent.getParent();
		}


		return parent.getPath();
	}

	private static String getPublicClassFullQualifiedName(ICompilationUnit cu) throws Exception {
		for (IType t : cu.getAllTypes()) {
			if(Flags.isPublic(t.getFlags()))
				return t.getFullyQualifiedName();
		}

		throw new NullPointerException("No public class");
	}

}
