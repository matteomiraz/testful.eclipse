package testful.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

public class Instrument {
	public static boolean instrument(String cut, String baseDir, StringBuilder msg) throws IOException, InterruptedException {
		StringBuilder cmd = new StringBuilder();
		
		cmd.append("java");
		cmd.append(" -cp ").append(getPluginResource("/instrumenter.jar")).append(" testful.coverage.Launcher"); 
		cmd.append(" -cut ").append(cut);
		if(baseDir != null) 
			cmd.append(" -baseDir ").append(baseDir);
		
		Process p = Runtime.getRuntime().exec(cmd.toString());
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
