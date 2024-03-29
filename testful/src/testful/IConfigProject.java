/*
 * TestFul - http://code.google.com/p/testful/
 * Copyright (C) 2010  Matteo Miraz
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package testful;

import java.io.File;
import java.util.logging.Level;

import org.kohsuke.args4j.Option;

public interface IConfigProject {

	/**
	 * Returns the base directory (e.g. $HOME/workspace/project/ )
	 * @return the base directory
	 */
	public File getDirBase();

	/**
	 * Returns the source directory (e.g. $HOME/workspace/project/src/ )
	 * @return the source directory
	 */
	public File getDirSource();

	/**
	 * Returns the compiled directory (e.g. $HOME/workspace/project/bin/ )
	 * @return the compiled directory
	 */
	public File getDirCompiled();

	/**
	 * Returns the directory containing contract-enabled binaries (e.g. $HOME/workspace/project/jml-compiled/ )
	 * @return the directory containing contract-enabled binaries
	 */
	public File getDirContracts();

	/**
	 * Returns the directory containing instrumented binaries (e.g. $HOME/workspace/project/instrumented/ )
	 * @return the directory containing instrumented binaries
	 */
	public File getDirInstrumented();

	/**
	 * Returns the directory in which all logging activities must be performed.
	 * If the return value is null, it means that the logging is disabled.
	 * @return the logging directory (or null if the logging is disabled)
	 */
	public File getLog();

	/**
	 * Do not print anything to the console
	 * @return true if the tool is running in a quiet mode
	 */
	public boolean isQuiet();

	public static enum LogLevel {
		SEVERE,
		WARNING,
		INFO,
		CONFIG,
		FINE,
		FINER,
		FINEST;

		public Level getLoggingLevel() {
			switch(this) {
			case SEVERE: return Level.SEVERE;
			case WARNING: return Level.WARNING;
			case INFO: return Level.INFO;
			case CONFIG: return Level.CONFIG;
			case FINE: return Level.FINE;
			case FINER: return Level.FINER;
			case FINEST: return Level.FINEST;
			}
			return Level.OFF;
		}
	}

	/**
	 * Returns the logging level
	 * @return the logging level
	 */
	public LogLevel getLogLevel();

	/**
	 * Stores the configuration of the project being tested.
	 * This interface is usable with args4j.
	 * @author matteo
	 */
	public interface Args4j extends IConfigProject {

		/**
		 * Sets the project's base directory (e.g. $HOME/workspace/project/ )
		 * @param dirBase the project's base directory
		 */
		@Option(required = false, name = "-dir", usage = "Specify the project's base directory (default: the current directory)")
		public void setDirBase(File dirBase);

		/**
		 * Sets the directory containing source files.
		 * It can be both a path relative to the base directory (e.g., "bar" or "../foo/bar")
		 * or an absolute path (e.g., $HOME/workspace/foo/bar")
		 * @param dirSource the directory containing source files.
		 */
		@Option(required = false, name = "-dirSource", usage = "Specify the source directory (default: src)")
		public void setDirSource(File dirSource);

		/**
		 * Sets the directory containing compiled files.
		 * It can be both a path relative to the base directory (e.g., "bar" or "../foo/bar")
		 * or an absolute path (e.g., $HOME/workspace/foo/bar")
		 * @param dirCompiled the directory containing compiled files.
		 */
		@Option(required = false, name = "-dirCompiled", usage = "Specify the directory containing compiled files (default: bin)")
		public void setDirCompiled(File dirCompiled);

		/**
		 * Sets the directory containing contract-enabled compiled files.
		 * It can be both a path relative to the base directory (e.g., "bar" or "../foo/bar")
		 * or an absolute path (e.g., $HOME/workspace/foo/bar")
		 * @param dirContracts the directory containing contract-enabled compiled files.
		 */
		@Option(required = false, name = "-dirContracts", usage = "Specify the directory with contract-enabled compiled files (default: jml-compiled)")
		public void setDirContracts(File dirContracts);

		/**
		 * Sets the directory containing instrumented files.
		 * It can be both a path relative to the base directory (e.g., "bar" or "../foo/bar")
		 * or an absolute path (e.g., $HOME/workspace/foo/bar")
		 * @param dirInstrumented the directory containing instrumented files.
		 */
		@Option(required = false, name = "-dirInstrumented", usage = "Specify the directory with instrumented files (default: instrumented)")
		public void setDirInstrumented(File dirInstrumented);

		/**
		 * Enables the logging and sets the output directory.
		 * It can be both a path relative to the base directory (e.g., "bar" or "../foo/bar")
		 * or an absolute path (e.g., $HOME/workspace/foo/bar")
		 * @param log the logging directory.
		 */
		@Option(required = false, name = "-log", usage = "Enables logging in the specified directory")
		public void setLog(File log);

		/**
		 * Sets the logging level
		 * @param logLevel the logging directory.
		 */
		@Option(required = false, name = "-logLevel", usage = "Sets the logging level")
		public void setLogLevel(LogLevel logLevel);

		/**
		 * Disable all the output to the console
		 * @param quiet disable all the output to the console
		 */
		@Option(required = false, name = "-quiet", usage = "Do not print anything to the console")
		public void setQuiet(boolean quiet);


	}
}