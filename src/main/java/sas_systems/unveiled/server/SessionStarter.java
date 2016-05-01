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

package sas_systems.unveiled.server;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application Lifecycle Listener implementation class SessionStarter
 *
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
    public void contextInitialized(ServletContextEvent arg0)  {
    	LOG.trace("Initializing session...");
    	// TODO: Load host and port from config-file or sth else
    	String host = "localhost";
    	int dataPort = 6982; // controlPort will be on 6983
    	boolean wasSuccessful = sm.initSession(SessionManager.PAYLOAD_TYPE_H263, host, dataPort);
    	
    	if(!wasSuccessful) {
    		LOG.error("Could not initialize RTP session!");
    		throw new IllegalArgumentException("Configuration of RTP session was incorrect, so it could not be started.");
    	}
    }
    
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
//    	sm.terminateSession();
    }
	
}
