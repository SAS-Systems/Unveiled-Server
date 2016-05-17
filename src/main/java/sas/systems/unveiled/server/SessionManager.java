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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Properties;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas.systems.imflux.participant.RtpParticipant;
import sas.systems.imflux.session.rtp.MultiParticipantSession;
import sas.systems.imflux.session.rtp.RtpSessionDataListener;
import sas.systems.imflux.session.rtsp.SimpleRtspSession;
import sas.systems.unveiled.server.util.PropertiesLoader;

/**
 * Session Bean implementation class SessionManager.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
@LocalBean
@Singleton
public class SessionManager {
	
	public static final int PAYLOAD_TYPE_H263 = 34;
	private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);
	
	private String mediaLocation;
	
	private final String ID = "Unveiled/0";
	
	private MultiParticipantSession rtpSession;
	private SimpleRtspSession rtspSession;
	private String host;
	private int dataPort;
	private int controlPort;
	private int rtspPort;
	private int payloadType;
	

    /**
     * Creates a new Session Manager. This method is called from the EJB container, therefore it must be
     * the default constructor.
     */
    public SessionManager() {
    	// load configuration from file
    	Properties props = PropertiesLoader.loadPropertiesFile(PropertiesLoader.SESSIONS_PROPERTIES_FILE);
    	this.host = props.getProperty(PropertiesLoader.SessionProps.HOST);
    	try {
    		this.dataPort = Integer.valueOf(props.getProperty(PropertiesLoader.SessionProps.RTP_PORT));
    		this.controlPort = Integer.valueOf(props.getProperty(PropertiesLoader.SessionProps.RTCP_PORT));
    		this.rtspPort = Integer.valueOf(props.getProperty(PropertiesLoader.SessionProps.RTSP_PORT));
    	} catch(NumberFormatException e) {
    		LOG.error("Could not load port and host information from {}!", e, PropertiesLoader.SESSIONS_PROPERTIES_FILE);
    	}
    	
    	// check port information
    	if(this.dataPort%2 != 0) {
    		LOG.warn("DataPort was uneven, switching to an even dataPort number to be RFC compliant!");
    		System.out.println("Data port was uneven, switching to an even port number to be RFC compliant!");
    		this.dataPort++;
    	}
    	
    	if(this.controlPort%2 == 0) {
    		LOG.warn("ControlPort was even, switching to an uneven controlPort number to be RFC compliant!");
    		System.out.println("Control port was even, switching to an uneven port number to be RFC compliant!");
    		this.controlPort++;
    	}
    }
    
    /**
     * Uses the loaded configuration from the properties file to initialize the RTP and RTSP session.
     * 
     * @param payloadType
     * @return {@code true} only if both sessions were initialized successfully, {@code false} otherwise  
     */
    public boolean initSessions(int payloadType) {
    	LOG.debug("RTP session will listen on UDP {}:{} and {}:{}", this.host, this.dataPort, this.host, this.controlPort);
    	LOG.debug("RTSP session will listen on TCP {}:{}", this.host, this.rtspPort);
    	
    	// create and initialize RTP session
    	RtpParticipant local = RtpParticipant.createReceiver(this.host, this.dataPort, this.controlPort);
    	rtpSession = new MultiParticipantSession(this.ID, this.payloadType, local);
		rtpSession.setUseNio(true);
		rtpSession.setAutomatedRtcpHandling(true);
//		rtpSession.addDataListener(new DataHandler(this.payloadType, this.mediaLocation));
		
		// create and initialize RTSP session
		SocketAddress rtspLocalAddress = new InetSocketAddress(host, rtspPort);
		rtspSession = new SimpleRtspSession(this.ID, local, rtspLocalAddress);
		rtspSession.setUseNio(true);
		rtspSession.setAutomatedRtspHandling(false);
//		rtspSession.setOptionsString("");
		
    	return (rtpSession.init() && rtspSession.init());
    }
    
    public String getHost() {
		return host;
	}

	public int getDataPort() {
		return dataPort;
	}

	public int getControlPort() {
		return controlPort;
	}
	
	public int getSessionControlPort() {
		return rtspPort;
	}

	public boolean isRunning() {
    	return (rtpSession.isRunning() && rtspSession.isRunning());
    }
    
    public void terminateSession() {
    	if(rtpSession != null) {
    		rtpSession.terminate();
    	}
    	if(rtspSession != null) {
    		rtspSession.terminate();
    	}
    }
    
    public void registerListener(RtpSessionDataListener listener) {
    	rtpSession.addDataListener(listener);
    }
    
    public void unregisterListener(RtpSessionDataListener listener) {
    	rtpSession.removeDataListener(listener);
    }

	public int getPayloadType() {
		return payloadType;
	}

	public String getMediaLocation() {
		return mediaLocation;
	}

	public void setMediaLocation(String resourceLocation) {
		this.mediaLocation = resourceLocation;
	}
	
	public RtpParticipant getLocalRtpParticipant() {
		return this.rtpSession.getLocalParticipant();
	}
}
