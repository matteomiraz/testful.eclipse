package testful.core;

import static testful.core.Instrument.OutHandler.handle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

public class Instrument {
	public static boolean instrument(String cut, String dirSource, String compiledDir, String dirContracts, String destinationDir, StringBuilder msg) throws IOException, InterruptedException {

		final List<String> cmd = new ArrayList<String>();

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

		final Process p = Runtime.getRuntime().exec(cmd.toArray(new String[cmd.size()]));

		StringBuffer tmp = new StringBuffer();
		handle(p.getInputStream(), tmp);
		handle(p.getErrorStream(), tmp);

		p.waitFor();

		if(msg != null) {
			msg.append(tmp.toString());
			msg.append(tmp.toString());
		}

		return p.exitValue() == 0;
	}

	private static File getPluginResource(String path) throws IOException {
		URL eclipseURL = FileLocator.find(Activator.getDefault().getBundle(), new Path(path), null);
		URL javaURL = FileLocator.toFileURL(eclipseURL);
		return new File(javaURL.getFile());
	}


	static class OutHandler extends Thread {
		private final InputStream is;
		private final StringBuffer out;

		public OutHandler(InputStream is, StringBuffer out) {
			this.is = is;
			this.out = out;
		}

		@Override
		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line=null;
				while ((line = br.readLine()) != null)
					out.append(line);

			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

		public static void handle(InputStream stream, StringBuffer out) {
			new OutHandler(stream, out).start();
		}
	}
}
