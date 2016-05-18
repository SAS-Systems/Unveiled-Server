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
package sas.systems.unveiled.server;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas.systems.unveiled.server.fileIO.FileWriter;
import sas.systems.unveiled.server.util.PropertiesLoader;
import sas.systems.unveiled.server.util.SessionManager;

/**
 * Application Lifecycle Listener implementation class SessionStarter
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
@WebListener
public class SessionStarter implements ServletContextListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(SessionStarter.class);
	
	@EJB
	private SessionManager sm;

    /**
     * Default constructor. 
     */
    public SessionStarter() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	LOG.debug("----------------------------Initializing session...");
    	// copy default thumbnail to servers media folder
    	generateDefaultThumbnail();
    	
    	// initialize sessions
    	boolean wasSuccessful = sm.initSessions(SessionManager.PAYLOAD_TYPE_H263);
    	if(!wasSuccessful) {
    		LOG.error("Could not initialize RTP and RTSP sessions!");
    		throw new IllegalArgumentException("Configuration of RTP or RTSP session was incorrect, so it could not be started.");
    	}
    	
    	LOG.debug("----------------------------Sessions successfully created and initialized");
    }
    
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
    	if(sm != null) sm.terminateSessions();
    }
	
    private void generateDefaultThumbnail() {
    	final String defaultFile = "default_thumbnail.jpg";
    	final Properties props = PropertiesLoader.loadPropertiesFile(PropertiesLoader.MEDIA_PROPERTIES_FILE);
    	final String destination = props.getProperty(PropertiesLoader.MediaProps.SYSTEM_PATH_TO_MEDIA);
    	String thumbnailFilePath = props.getProperty(PropertiesLoader.MediaProps.REL_PATH_TO_DEFAULT_THUMBNAIL);
    	
    	// check existence
    	final File existingFile = new File(destination + thumbnailFilePath);
    	if(existingFile.exists()) {
    		LOG.info("Default Thumbnail was not copied, because it already existed.");
    		return;
    	}
    	
    	// extract location, filename, suffix
    	final String fileName = thumbnailFilePath.substring(thumbnailFilePath.lastIndexOf('/')+1, thumbnailFilePath.lastIndexOf('.'));
    	final String fileSuffix = thumbnailFilePath.substring(thumbnailFilePath.lastIndexOf('.')+1, thumbnailFilePath.length());
    	thumbnailFilePath = thumbnailFilePath.substring(0, thumbnailFilePath.lastIndexOf('/'));
    	
    	// load default file and write to media folder
    	final ClassLoader classLoader = getClass().getClassLoader();
    	final FileWriter writer = new FileWriter(destination  + thumbnailFilePath, fileName, fileSuffix);
    	try {
    		writer.writeToFile(classLoader.getResourceAsStream(defaultFile));
    	} catch(IOException e) {
    		LOG.error("could not copy default thumbnail!", e);
    	} finally {
    		try {
    			writer.close();
    		} catch (IOException e) {
    			LOG.error("FATAL ERROR: Unable to close file writer for default thumbnail!", e);
    		}
		}
    }
}
