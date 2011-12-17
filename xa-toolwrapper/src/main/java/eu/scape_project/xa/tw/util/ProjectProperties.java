/*
 *  Copyright 2011 IMPACT (www.impact-project.eu)/SCAPE (www.scape-project.eu)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package eu.scape_project.xa.tw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import eu.scape_project.xa.tw.Constants;

/**
 * Singleton class wrapping the properties for the toolwrapper. These are initially loaded from a default properties
 * file, see the getInstance() method for location.
 * 
 * The class is not immutable, calling getInstance(java.io.InputStream) reloads the properties from the stream contents.
 * 
 * TODO:The properties need dividing into two parts, the constants, and those that can be overridden by values supplied
 * by the user in a custom properties file.
 * 
 * @author shsdev https://github.com/shsdev
 * @version 0.3
 */
public class ProjectProperties {
	static final ProjectProperties INSTANCE = new ProjectProperties();
	private static Map<String, String> MAP = null;

	private ProjectProperties() {
		/** Prevent Instantiation */
	}

	/**
	 * @return an unmodifiable version of the properties map
	 */
	public final Map<String, String> getKeyValuePairs() {
		return Collections.unmodifiableMap(MAP);
	}

	/**
	 * @param key
	 *        key of the property to search for
	 * @return the String value from the Map for the key paramter
	 */
	public final String getProp(String key) {
		return MAP.get(key);
	}

	/**
	 * Factory method that returns the properties instance. If the static properties map is null then the defaults are
	 * loaded by invoking the stream constructor. If the map is not null the current instance is returned.
	 * 
	 * @return the instance of the properties object.
	 * @throws IllegalStateException
	 *         if the default properties file cannot be read
	 */
	static public ProjectProperties getInstance() {
		if (MAP == null)
			try {
				return getInstance(ClassLoader
						.getSystemResourceAsStream(Constants.RESOURCE_PACKAGE
								+ Constants.DEFAULT_PROJECT_PROPERTIES));
			} catch (IOException e) {
				throw new IllegalStateException(
						"Default properties file could not be read.", e);
			}
		return INSTANCE;
	}

	/**
	 * Static factory method that loads the properties from the param stream and returns the properties instance.
	 * 
	 * @param is an input stream to a properties set
	 * @return the ProjectProperties instance
	 * @throws IOException when the passed property stream cannot be read.
	 */
	static public ProjectProperties getInstance(InputStream is)
			throws IOException {
		if (is == null)
			throw new IllegalArgumentException("Input stream cannot be null");
		MAP = getPropertyStreamAsMap(is);
		return INSTANCE;
	}

	/**
	 * Static factory method that loads the properties from the File param.
	 * 
	 * @param file a Java properties from which the properties will be loaded. 
	 * @return the instance re-initialised from the file
	 * @throws FileNotFoundException if the passed properties file cannot be found
	 * @throws IOException if there are problems reading the file.
	 */
	static public ProjectProperties getInstance(File file)
			throws FileNotFoundException, IOException {
		return getInstance(new FileInputStream(file));
	}

	private final static Map<String, String> getPropertyStreamAsMap(
			InputStream is) throws IOException {
		Properties props = new Properties();
		props.load(is);
		HashMap<String, String> map = new HashMap<String, String>();
		for (Entry<Object, Object> entry : props.entrySet()) {
			map.put(String.valueOf(entry.getKey()),
					String.valueOf(entry.getValue()));
		}
		return map;
	}
}
