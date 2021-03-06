/*
 * Copyright 2016 Sebastian Schmidl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sas.systems.unveiled.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for dealing with properties files. It defines necessary constants (names) 
 * and loads the properties from the files.
 * 
 * TODO: implement caching?
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public final class PropertiesLoader {

	private static final Logger LOG = LoggerFactory.getLogger(Properties.class);

	public static final String DATABASE_PROPERTIES_FILE = "database.properties";
	public static final String SESSIONS_PROPERTIES_FILE = "sessions.properties";
	public static final String MEDIA_PROPERTIES_FILE = "media.properties";
	
	private static final Map<String, Properties> defaultProperties = new HashMap<>();

	static {
		// init default properties values:
		Properties databaseDefault = new Properties();
		databaseDefault.setProperty(DBProps.DB_HOST, "sas.systemgrid.de");
		databaseDefault.setProperty(DBProps.DB_USER, "unveiled");
		databaseDefault.setProperty(DBProps.DB_PASSWORD, "");
		databaseDefault.setProperty(DBProps.DB_NAME, "unveiled");
		defaultProperties.put(DATABASE_PROPERTIES_FILE, databaseDefault);

		Properties sessionDefault = new Properties();
		sessionDefault.setProperty(SessionProps.HOST, "localhost");
		sessionDefault.setProperty(SessionProps.RTP_PORT, "6982");
		sessionDefault.setProperty(SessionProps.RTCP_PORT, "6983");
		sessionDefault.setProperty(SessionProps.RTSP_PORT, "1935");
		defaultProperties.put(SESSIONS_PROPERTIES_FILE, sessionDefault);

		Properties mediaDefault = new Properties();
		mediaDefault.setProperty(MediaProps.REL_PATH_TO_DEFAULT_THUMBNAIL, "default/default_thumbnail.jpg");
		mediaDefault.setProperty(MediaProps.URL_MEDIA_PATH_PREFIX, "media/");
		mediaDefault.setProperty(MediaProps.SYSTEM_PATH_TO_MEDIA, "media/");
		defaultProperties.put(MEDIA_PROPERTIES_FILE, mediaDefault);
	}
	/**
	 * Group of database properties access names.
	 * 
	 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
	 */
	public static final class DBProps {
		public static final String DB_HOST = "database.host";
		public static final String DB_USER = "database.user";
		public static final String DB_PASSWORD = "database.password";
		public static final String DB_NAME = "database.name";
		/**
		 * Private constructor to hide implicit public one
		 */
		private DBProps(){};
	}

	/**
	 * Group of session properties access names.
	 * 
	 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
	 */
	public static final class SessionProps {		
		public static final String HOST = "host";
		public static final String RTP_PORT = "rtp.port";
		public static final String RTCP_PORT = "rtcp.port";
		public static final String RTSP_PORT = "rtsp.port";
		/**
		 * Private constructor to hide implicit public one
		 */
		private SessionProps(){};
	}

	/**
	 * Group of media properties access names.
	 * 
	 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
	 */
	public static final class MediaProps {
		public static final String URL_MEDIA_PATH_PREFIX = "media.location.urlPathPrefix";
		public static final String SYSTEM_PATH_TO_MEDIA = "media.location.systemPathPrefix";
		public static final String REL_PATH_TO_DEFAULT_THUMBNAIL = "media.default.thumbnail";
		/**
		 * Private constructor to hide implicit public one
		 */
		private MediaProps(){};
	}

	/**
	 * Private constructor, because this is just a utils class which should not be instantiated.
	 */
	private PropertiesLoader() {};

	/**
	 * Loads properties from a {@code *.properties}-file in the context classpath.
	 * 
	 * @param fileName name of the properties file
	 * @return a {@link Properties} instance containing the loaded properties, or {@code null} if not found
	 * 	or an exception occured
	 */
	public static Properties loadPropertiesFile(String fileName) {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final InputStream resource = classLoader.getResourceAsStream(fileName);
		final Properties properties = new Properties(defaultProperties.get(fileName));

		try {
			if(resource != null) {
				properties.load(resource);
				LOG.trace("Using properties from file: {}", fileName);
				System.out.println("Using properties from file: " + fileName);
				return properties;
			}

		} catch (IOException e) {
			LOG.error("Could not load {}!", fileName, e);
			System.out.println("Could not load " + fileName);
		}
		return null;
	}
}
