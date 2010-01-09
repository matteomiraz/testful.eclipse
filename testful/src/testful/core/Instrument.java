package testful.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

public class Instrument {
	public static boolean instrument(String cut, String dirSource, String compiledDir, String dirContracts, String destinationDir, StringBuilder msg) throws IOException, InterruptedException {
		List<String> cmd = new ArrayList<String>();

		if (System.getProperty("os.name").contains("Windows")) {
			cmd.add("cmd");
			cmd.add("/c");
		}

		cmd.add("java");
		cmd.add("-cp");
		cmd.add(getPluginResource("/instrumenter.jar").getAbsolutePath());
		cmd.add("testful.coverage.Launcher");

		cmd.add("-dirSource");
		cmd.add(dirSource);

		cmd.add("-dirCompiled");
		cmd.add(compiledDir);

		cmd.add("-dirContracts");
		cmd.add(dirContracts);

		cmd.add("-dirInstrumented");
		cmd.add(destinationDir);

		cmd.add("-cut");
		cmd.add(cut);

		Process p = Runtime.getRuntime().exec(cmd.toArray(new String[cmd.size()]));
		p.waitFor();

		if(msg != null) {
			append(msg, p.getInputStream());
			append(msg, p.getErrorStream());
		}

		return p.exitValue() == 0;
	}

	private static File getPluginResource(String path) throws IOException {
		try {
			URL eclipseURL = FileLocator.find(Activator.getDefault().getBundle(), new Path(path), null);
			URL javaURL = FileLocator.toFileURL(eclipseURL);
			return new File(javaURL.toURI());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	private static void append(StringBuilder sb, InputStream stream) {
		BufferedReader br = new  BufferedReader(new InputStreamReader(stream));

		try {
			String line;
			while((line = br.readLine()) != null)
				sb.append(line).append("\n");
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

}
