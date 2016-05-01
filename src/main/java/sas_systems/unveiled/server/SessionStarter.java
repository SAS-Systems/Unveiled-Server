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
