package org.andrill.util.image;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple wrapper class for working with ImageMagick.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class ImageMagick {
	private static String SHELL_WIN = "cmd.exe";
	private static String SHELL_UNIX = "/bin/sh";
	public static String MAC_PORTS = "/opt/local/bin/";

	private final String convert;
	private File in = null;
	private File out = null;
	private final List<String> options = new ArrayList<String>();

	/**
	 * Create a new ImageMagick.
	 */
	public ImageMagick() {
		this(new ArrayList<String>());
	}

	/**
	 * Create a new ImageMagick.
	 *
	 * @param paths
	 *			  the paths to check.
	 */
	public ImageMagick(List<String> paths) {
		convert = findConvert(paths);
		if (convert == null) {
			throw new IllegalArgumentException("ImageMagick not found");
		}
	}

	/**
	 * Gets the location of ImageMagick or null if not found.
	 *
	 * @return the location of ImageMagick.
	 */
	public String getCommand() {
		return convert;
	}

	private String findConvert(List<String> paths) {
		// try path
		String command = "convert" + (isWindows() ? ".exe" : "");
		if (tryCommand(command)) {
			return command;
		}

		// try macports
		if (!isWindows() && tryCommand(MAC_PORTS + command)) {
			return MAC_PORTS + command;
		}

		// try additional paths
		for (String path : paths) {
			String exec = path
					+ (path.endsWith(File.separator) ? "" : File.separator)
					+ command;
			if (tryCommand(escape(exec))) {
				return escape(exec);
			}
		}
		return null;
	}

	/**
	 * Set the input file.
	 *
	 * @param in
	 *			  the file.
	 * @return this.
	 */
	public ImageMagick in(File in) {
		this.in = in;
		return this;
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("win");
	}

	/**
	 * Add an option.
	 *
	 * @param option
	 *			  the option.
	 * @return this.
	 */
	public ImageMagick option(String option) {
		options.add(option);
		return this;
	}

	/**
	 * Add an option.
	 *
	 * @param option
	 *			  the option.
	 * @param value
	 *			  the value.
	 * @return this.
	 */
	public ImageMagick option(String option, String value) {
		options.add(option);
		options.add(value);
		return this;
	}

	/**
	 * Set the output file.
	 *
	 * @param out
	 *			  the file.
	 * @return this.
	 */
	public ImageMagick out(File out) {
		this.out = out;
		return this;
	}

	/**
	 * Reset the options.
	 */
	public void reset() {
		this.in = null;
		this.out = null;
		this.options.clear();
	}

	private String escape(String str) {
		if (str.indexOf(' ') > -1 && !str.startsWith("\"")) {
			return "\"" + str + "\"";
		} else {
			return str;
		}
	}

	/**
	 * Run the command.
	 *
	 * @return true if the command completed without errors, false otherwise.
	 */
	public boolean run() {
		// build our command
		StringBuilder command = new StringBuilder();
		command.append(convert);
		if (in != null) {
			command.append(" " + escape(in.getAbsolutePath()));
		}
		for (String option : options) {
			command.append(" " + option);
		}
		if (out != null) {
			command.append(" " + escape(out.getAbsolutePath()));
		}

		// execute
		int exit = -1;
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			// create our process
			process = runtime.exec(new String[] {
					(isWindows() ? SHELL_WIN : SHELL_UNIX),
					(isWindows() ? "/c" : "-c"), command.toString() });

			// handle I/O
			new StreamGobbler(process.getErrorStream()).start();
			new StreamGobbler(process.getInputStream()).start();

			// get our status
			exit = process.waitFor();
		} catch (IOException e) {
			// ignore
		} catch (InterruptedException e) {
			// ignore
		}

		// reset
		reset();

		// successful?
		return (exit == 0);
	}

	/**
	 * Run the command.
	 *
	 * @param in
	 *			  the input file.
	 * @param out
	 *			  the output file.
	 * @return true if the command succeeded, false otherwise.
	 */
	public boolean run(File in, File out) {
		this.in = in;
		this.out = out;
		return run();
	}

	private boolean tryCommand(String command) {
		Runtime runtime = Runtime.getRuntime();
		try {
			// create our process
			Process process = runtime.exec(new String[] {
					(isWindows() ? SHELL_WIN : SHELL_UNIX),
					(isWindows() ? "/c" : "-c"), command });

			// handle I/O
			new StreamGobbler(process.getErrorStream()).start();
			new StreamGobbler(process.getInputStream()).start();

			// get the status code
			int status = process.waitFor();
			return status == 0 || status == 1;
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}
	}

	private class StreamGobbler extends Thread {
		private InputStream stream;
		private StreamGobbler(InputStream in) {
			this.stream = in;
		}
		public void run() {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(stream));
				while (br.readLine() != null) {
				}
			} catch (IOException e) {
				// ignore
			} finally {
				if (br != null) {
					try { br.close(); } catch (IOException e) {}
				}
			}

		}
	}


	public static void main(String[] args) {
		try {
			System.out.println("ImageMagick found at: " + new ImageMagick(Arrays.asList(args)).getCommand());
		} catch (Exception e) {
			System.out.println("ImageMagick not found.	Try passing in additional search paths");
		}
	}
}
