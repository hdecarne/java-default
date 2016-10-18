/*
 * Copyright (c) 2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class is the generic entry point for all kind of applications.
 * <p>
 * It provides the necessary services for setting up the execution environment
 * and invoking the actual application code. Further more it acts as a
 * {@link ClassLoader} and {@link URLStreamHandlerFactory} making 3rd party jars
 * which are included in the application jar available to the application during
 * runtime.
 * <p>
 * This class retrieves the actual application information from a file resource
 * named ApplicationLoader which has be to be located in the application root
 * resource folder. The first line of this resource defines the actual
 * {@link Main} class to be loaded and executed. All remaining lines are
 * considered as system properties that are set prior to loading and invoking
 * the main class.
 */
public final class ApplicationLoader extends URLClassLoader implements URLStreamHandlerFactory {

	private static final boolean DEBUG = System.getProperty(ApplicationLoader.class.getName() + ".DEBUG") != null;

	// resource URL handling code

	private static final String RESOURCE_PROTOCOL = "resource";

	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		URLStreamHandler handler = null;

		if (RESOURCE_PROTOCOL.equals(protocol)) {
			handler = new URLStreamHandler() {

				@Override
				protected URLConnection openConnection(URL u) throws IOException {
					return ApplicationLoader.openResourceConnection(u);
				}

			};
		}
		return handler;
	}

	static URLConnection openResourceConnection(URL u) {
		return new URLConnection(u) {

			@Override
			public void connect() throws IOException {
				// Nothing to do here
			}

			@Override
			public InputStream getInputStream() throws IOException {
				URL resourceURL = getURL();
				InputStream resourceStream = ApplicationLoader.class.getResourceAsStream(resourceURL.getFile());

				if (resourceStream == null) {
					throw new FileNotFoundException("Unknown resource: " + resourceURL);
				}
				return resourceStream;
			}

		};
	}

	/**
	 * Get the direct {@linkplain URL} and remove a possibly existing resource
	 * re-direct.
	 * <p>
	 * By using this function the application can provide resource access via
	 * one of the JDK's standard {@link URLStreamHandler} classes for code that
	 * cannot handle our custom {@link URLStreamHandler}.
	 *
	 * @param u The {@linkplain URL} to get the direct {@linkplain URL}.
	 * @return The native resource {@linkplain URL}.
	 */
	public static URL getDirectURL(URL u) {
		return (u != null && RESOURCE_PROTOCOL.equals(u.getProtocol())
				? ApplicationLoader.class.getResource(u.getFile()) : u);
	}

	// class loading code

	// Prefix of class names that need to be loaded via system classloader (e.g.
	// log handlers).
	private static String[] SYSTEM_CLASS_PREFIXES = new String[] { ApplicationLoader.class.getName(),
			"de.carne.util.logging" };

	private final ClassLoader systemClassLoader = getSystemClassLoader();

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> clazz = null;

		for (String systemClassPrefix : SYSTEM_CLASS_PREFIXES) {
			if (name.startsWith(systemClassPrefix)) {
				clazz = this.systemClassLoader.loadClass(name);
				break;
			}
		}
		if (clazz == null) {
			clazz = super.loadClass(name);
		}
		return clazz;
	}

	// Application Jar handling code

	private static Path CODE_PATH;

	static {
		Path codePath;

		try {
			URL codeURL = ApplicationLoader.class.getProtectionDomain().getCodeSource().getLocation();

			if (DEBUG) {
				logOut("Code source URL: " + codeURL);
			}

			URI codeURI = codeURL.toURI();

			if (DEBUG) {
				logOut("Code source URI: " + codeURI);
			}

			codePath = Paths.get(codeURI);

			if (DEBUG) {
				logOut("Code source path: " + codePath);
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		CODE_PATH = codePath;
	}

	/**
	 * Get the {@code JarFile} this application has been loaded from.
	 *
	 * @return The {@code JarFile} or {@code null} if the application was not
	 *         loaded from a release Jar.
	 * @throws IOException if an I/O error occurs while opening the Jar.
	 */
	public static JarFile getApplicationJarFile() throws IOException {
		JarFile applicationJarFile = null;

		if (CODE_PATH != null && Files.isRegularFile(CODE_PATH)) {
			applicationJarFile = new JarFile(CODE_PATH.toFile());
		}
		return applicationJarFile;
	}

	// initialization code

	private static final URL[] RESOURCE_URLS;

	static {
		ArrayList<URL> libURLs = new ArrayList<>();

		try (JarFile codeJar = getApplicationJarFile()) {
			if (codeJar != null) {
				libURLs.add(new URL(RESOURCE_PROTOCOL + ":/"));

				if (DEBUG) {
					logOut("Adding internal JARs to classpath...");
				}

				Iterator<JarEntry> jarEntries = codeJar.stream().filter(e -> e.getName().endsWith(".jar")).iterator();

				while (jarEntries.hasNext()) {
					String jarEntryName = jarEntries.next().getName();

					if (DEBUG) {
						logOut("Adding internal JAR: " + jarEntryName);
					}
					libURLs.add(new URL("jar:" + RESOURCE_PROTOCOL + ":/" + jarEntryName + "!/"));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		RESOURCE_URLS = libURLs.toArray(new URL[libURLs.size()]);
	}

	private ApplicationLoader() {
		super(RESOURCE_URLS, (RESOURCE_URLS.length > 0 ? null : getSystemClassLoader()));
		ClassLoader.registerAsParallelCapable();
	}

	/**
	 * Generic application main function.
	 *
	 * @param args The application's command line.
	 */
	public static void main(String[] args) {
		int status;

		try (ApplicationLoader classLoader = new ApplicationLoader()) {
			Thread.currentThread().setContextClassLoader(classLoader);
			if (DEBUG) {
				logOut("Invoking application...");
			}
			status = loadMain(classLoader).newInstance().run(args);
		} catch (Exception e) {
			e.printStackTrace();
			status = -1;
		}
		if (DEBUG) {
			logOut("Exit status " + status);
		}
		System.exit(status);
	}

	private static Class<? extends Main> loadMain(ClassLoader classLoader) throws IOException, ClassNotFoundException {
		Class<? extends Main> mainClass;

		try (InputStream mainStream = getConfigAsInputStream();
				BufferedReader mainReader = new BufferedReader(
						new InputStreamReader(mainStream, StandardCharsets.UTF_8))) {
			String mainClassName = mainReader.readLine();

			if (DEBUG) {
				logOut("Main-Class " + mainClassName);
			}

			String propertyLine;

			while ((propertyLine = mainReader.readLine()) != null) {
				int splitIndex = propertyLine.indexOf('=');
				String propertyKey;
				String propertyValue;

				if (splitIndex < 0) {
					propertyKey = propertyLine.trim();
					propertyValue = "true";
				} else if (splitIndex > 0) {
					propertyKey = propertyLine.substring(0, splitIndex).trim();
					propertyValue = propertyLine.substring(splitIndex + 1).trim();
				} else {
					propertyKey = "";
					propertyValue = "";
				}
				if (propertyKey.length() > 0 && propertyValue.length() > 0) {
					if (DEBUG) {
						logOut("Setting system property \"" + propertyKey + "\" = \"" + propertyValue + "\"");
					}
					System.setProperty(propertyKey, propertyValue);
				} else {
					logErr("Ignoring invalid system property definition '" + propertyLine + "'");
				}
			}
			mainClass = classLoader.loadClass(mainClassName).asSubclass(Main.class);
		}
		return mainClass;
	}

	private static InputStream getConfigAsInputStream() {
		String resourceName = "/" + ApplicationLoader.class.getSimpleName();
		InputStream mainStream = ApplicationLoader.class.getResourceAsStream(resourceName);

		if (mainStream == null) {
			throw new RuntimeException("Unable to access resource " + resourceName);
		}
		return mainStream;
	}

	private static void logOut(String msg) {
		System.out.println("ApplicationLoader: " + msg);
	}

	private static void logErr(String msg) {
		System.err.println("ApplicationLoader: " + msg);
	}

}
