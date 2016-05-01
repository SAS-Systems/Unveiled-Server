package sas_systems.unveiled.server;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas_systems.imflux.participant.RtpParticipant;
import sas_systems.imflux.session.MultiParticipantSession;

/**
 * Session Bean implementation class SessionManager
 */
@Stateless
@LocalBean
public class SessionManager {
	
	public static final int PAYLOAD_TYPE_H263 = 34;
	public static final int DEFAULT_DATA_PORT = 6982;
	public static final int DEFAULT_CONTROL_PORT = 6983;
	
	private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);
	
	private final String ID = "Unveiled/0";
	
	private MultiParticipantSession session;
	private String host;
	private int dataPort;
	private int controlPort;
	private int payloadType;

    /**
     * Default constructor. 
     */
    public SessionManager() {
    }
    
    public boolean initSession(int payloadType, String host) {
    	return initSession(payloadType, host, SessionManager.DEFAULT_DATA_PORT);
    }
    
    public boolean initSession(int payloadType, String host, int port) {
    	this.host = host;
    	this.payloadType = payloadType;
    	
    	if(dataPort%2 != 0) {
    		LOG.warn("Data port was uneven, switching to an even port number to be RFC compliant!");
    		this.dataPort = port+1;
    		this.controlPort = port+2;
    	}
    	LOG.debug("RTP session will listen on {}:{} and {}:{}", this.host, this.dataPort, this.host, this.controlPort);
    	System.out.println(this.dataPort + "/" + this.controlPort);
    	RtpParticipant local = RtpParticipant.createReceiver(this.host, this.dataPort, this.controlPort);
    	this.session = new MultiParticipantSession(this.ID, this.payloadType, local);
		this.session.setUseNio(true);
		this.session.setAutomatedRtcpHandling(true);
		this.session.addDataListener(new DataHandler(this.payloadType));
		
    	return this.session.init();
    }
    
    public boolean isRunning() {
    	return this.session.isRunning();
    }
    
    public void terminateSession() {
    	if(this.session != null) {
    		this.session.terminate();
    	}
    }
}
