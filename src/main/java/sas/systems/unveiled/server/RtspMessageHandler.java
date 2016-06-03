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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.rtsp.RtspHeaders;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;
import sas.systems.imflux.participant.RtpParticipant;
import sas.systems.imflux.participant.RtspParticipant;
import sas.systems.imflux.session.rtsp.RtspRequestListener;
import sas.systems.unveiled.server.util.DatabaseConnector;
import sas.systems.unveiled.server.util.SessionManager;

/**
 * Class for handling RTSP request from the clients. It creates a new {@link FileStreamHandler}
 * for every new Participant (SSRC) - File combination.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public class RtspMessageHandler implements RtspRequestListener {

	private final SessionManager sm;
	private final Map<String, FileStreamHandler> sessions;
	
	private static final String SERVER_DESCRIPTION = "Unveiled Streaming Engine 0.0.2";
	
	private final String optionsString = RtspMethods.OPTIONS.name() + ", " +
			RtspMethods.ANNOUNCE.name() + ", " + 
    		RtspMethods.SETUP.name() + ", " +
    		RtspMethods.TEARDOWN.name() + ", " +
    		RtspMethods.RECORD.name();
	
	
	public RtspMessageHandler(SessionManager sessionManager) {
		this.sm = sessionManager;
		this.sessions = new HashMap<>();
	}

	// RtspRequestListener --------------------------------------------------------------------------------------------
	@Override
	public void optionsRequestReceived(HttpRequest message, RtspParticipant participant) {
		final String seq = message.headers().get(RtspHeaders.Names.CSEQ);
		
		System.out.println(message);
		
		if(seq != null) {
			try {
				final int cseq = Integer.valueOf(seq);
				participant.sendMessage(createOptionsResponse(cseq, this.optionsString));
				return;
			} catch(NumberFormatException e) {
				// also send a 400 bad request (see below
			}
		}
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.BAD_REQUEST, message.headers()));
	}

	@Override
	public void announceRequestReceived(HttpRequest message, RtspParticipant participant) {
		final String seq = message.headers().get(RtspHeaders.Names.CSEQ);
		final String contentType = message.headers().get(RtspHeaders.Names.CONTENT_TYPE);
		final String authorization = message.headers().get(RtspHeaders.Names.AUTHORIZATION);
		
		System.out.println(message);
		
		if(authorization == null) {
			participant.sendMessage(createErrorResponse(RtspResponseStatuses.UNAUTHORIZED, message.headers()));
			return;
		} else {
			if(!isAuthorized(authorization)) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.UNAUTHORIZED, message.headers()));
				return;
			}
		}
		
		if(message instanceof FullHttpRequest) {
			final FullHttpRequest fullResponse = (FullHttpRequest) message;
			final ByteBuf content = fullResponse.content();
			
			if(seq != null && contentType != null && contentType.contains("sdp") && content != null) {
				// parse announce content
				final String fileName = retrieveFileName(fullResponse.getUri());
				final SdpParser parser = new SdpParser(content);
				final String sessionId = participant.setup();
				
				// setup stream handler with content of announce request 
				final FileStreamHandler streamHandler = new FileStreamHandler(this.sm, participant);
				streamHandler.setFileName(fileName);
				streamHandler.setPayloadType(parser.getMediaType());
				streamHandler.setAuthor(retrieveUsername(authorization));
				streamHandler.setMediaType("video/mp4");
				
				// save session info
				this.sessions.put(sessionId, streamHandler);
				
				// send response
				try {
					int cseq = Integer.valueOf(seq);
					participant.sendMessage(createAnnounceResponse(cseq, sessionId));
					return;
				} catch(NumberFormatException e) {
					// send error message
					participant.sendMessage(createErrorResponse(RtspResponseStatuses.HEADER_FIELD_NOT_VALID, message.headers()));
					return;
				}
			}
		}
		
		// otherwise send error message
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.BAD_REQUEST, message.headers()));
	}

	@Override
	public void describeRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
	}

	@Override
	public void setupRequestReceived(HttpRequest message, RtspParticipant participant) {
		final String seq = message.headers().get(RtspHeaders.Names.CSEQ);
		final String session = message.headers().get(RtspHeaders.Names.SESSION);
		final String authorization = message.headers().get(RtspHeaders.Names.AUTHORIZATION);
		final String transport = message.headers().get(RtspHeaders.Names.TRANSPORT);
		
		System.out.println(message);
		
		// check authorization
		if(authorization == null) {
			participant.sendMessage(createErrorResponse(RtspResponseStatuses.UNAUTHORIZED, message.headers()));
			return;
		} else {
			if(!isAuthorized(authorization)) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.UNAUTHORIZED, message.headers()));
				return;
			}
		}
		
		// extract transport information
		if(seq != null && session != null && transport != null) {
			int cseq = 0;
			try {
				cseq = Integer.valueOf(seq);
			} catch(NumberFormatException e) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.HEADER_FIELD_NOT_VALID, message.headers()));
				return;
			}
			FileStreamHandler handler = this.sessions.get(session);
			if(handler == null) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.SESSION_NOT_FOUND, message.headers()));
				return;
			}
			
			// parse transport header and validate entries
			final InetSocketAddress remote = (InetSocketAddress) participant.getRemoteAddress();
			final boolean validationError = parseTransportHeader(transport, handler.getParticipant(), remote.getHostName());
			if(!validationError) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.HEADER_FIELD_NOT_VALID, message.headers()));
				return;
			}
			
			participant.sendMessage(createSetupResponse(cseq, session, buildTransportString(transport, handler.getSsrc() + "")));
		}
		
		// send error message
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.BAD_REQUEST, message.headers()));
	}	

	@Override
	public void teardownRequestReceived(HttpRequest message, RtspParticipant participant) {
		final String seq = message.headers().get(RtspHeaders.Names.CSEQ);
		final String session = message.headers().get(RtspHeaders.Names.SESSION);
		final String authorization = message.headers().get(RtspHeaders.Names.AUTHORIZATION);
		
		System.out.println(message);
		
		// check authorization
		if(authorization == null) {
			participant.sendMessage(createErrorResponse(RtspResponseStatuses.UNAUTHORIZED, message.headers()));
			return;
		} else {
			if(!isAuthorized(authorization)) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.UNAUTHORIZED, message.headers()));
				return;
			}
		}
		
		// extract transport information
		if(seq != null && session != null) {
			int cseq = 0;
			try {
				cseq = Integer.valueOf(seq);
			} catch(NumberFormatException e) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.HEADER_FIELD_NOT_VALID, message.headers()));
				return;
			}
			FileStreamHandler handler = this.sessions.get(session);
			if(handler == null) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.SESSION_NOT_FOUND, message.headers()));
				return;
			}
			
			// stop receiving data and close all resources
			handler.tieUp();
			
			// send response
			participant.sendMessage(createTeardownResponse(cseq, session));
		}
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.BAD_REQUEST, message.headers()));
	}

	@Override
	public void playRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
	}

	@Override
	public void pauseRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
	}

	@Override
	public void getParameterRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
	}

	@Override
	public void setParameterRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
	}

	@Override
	public void redirectRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
	}

	@Override
	public void recordRequestReceived(HttpRequest message, RtspParticipant participant) {
		final String seq = message.headers().get(RtspHeaders.Names.CSEQ);
		final String session = message.headers().get(RtspHeaders.Names.SESSION);
		final String authorization = message.headers().get(RtspHeaders.Names.AUTHORIZATION);
		final String range = message.headers().get(RtspHeaders.Names.RANGE);
		
		System.out.println(message);
		
		// check authorization
		if(authorization == null) {
			participant.sendMessage(createErrorResponse(RtspResponseStatuses.UNAUTHORIZED, message.headers()));
			return;
		} else {
			if(!isAuthorized(authorization)) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.UNAUTHORIZED, message.headers()));
				return;
			}
		}
		
		// extract transport information
		if(seq != null && session != null) {
			int cseq = 0;
			try {
				cseq = Integer.valueOf(seq);
			} catch(NumberFormatException e) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.HEADER_FIELD_NOT_VALID, message.headers()));
				return;
			}
			FileStreamHandler handler = this.sessions.get(session);
			if(handler == null) {
				participant.sendMessage(createErrorResponse(RtspResponseStatuses.SESSION_NOT_FOUND, message.headers()));
				return;
			}
			
			if(range == null || range.startsWith("npt=0.0") || range.startsWith("npt=now")) {
				// start receiving data
				handler.initialize();
				
				// send response
				participant.sendMessage(createRecordResponse(cseq, session));
			}
		
			participant.sendMessage(createErrorResponse(RtspResponseStatuses.HEADER_FIELD_NOT_VALID, message.headers()));
		}
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.BAD_REQUEST, message.headers()));
	}
	
	// private helpers ------------------------------------------------------------------------------------------------
	private String retrieveFileName(String uri) {
		final int lastSlash = uri.lastIndexOf('/');
		return uri.substring(lastSlash, uri.length());
	}
	
	private boolean isAuthorized(String authorization) {
// remove! ------------------------------------------------------------
		if(true) return true;
		@SuppressWarnings("unused")
// --------------------------------------------------------------------
		
		final String[] slices = authorization.split(" ");
		if(slices.length < 3) {
			return false;
		}
		
		//final String method = slices[0];
		final String username = slices[1].substring(0, slices[1].length());
		final String nonce = slices[2];
		
		// parse key-value pairs
		final Map<String, String> pairs = new HashMap<>();
		parseAndAddPair(pairs, username);
		parseAndAddPair(pairs, nonce);
		
		// check token
		try {
			final int userId = Integer.valueOf(pairs.get("username"));
			final String originalToken = loadOriginalToken(userId);
			if(originalToken != null && originalToken.equals(pairs.get("nonce"))) {
				return true;
			}
		} catch(NumberFormatException e) {
			// also return false...see below
		}
		return false;
	}
	
	private int retrieveUsername(String authorization) {
		final String[] slices = authorization.split(" ");
		final Map<String, String> pairs = new HashMap<>();
		for (int i = 1; i < slices.length; i++) {
			parseAndAddPair(pairs, slices[i]);
		}
		final String username = pairs.get("username");
		try {
			return Integer.valueOf(username);
		} catch(NumberFormatException e) {
			return -1;
		}
	}
	
	private void parseAndAddPair(Map<String, String> map, String keyValue) {
		final int iEq = keyValue.indexOf("=");
		final int iFirstQuote = keyValue.indexOf("\"");
		final int iLastQuote = keyValue.lastIndexOf("\"");
		final String key = keyValue.substring(0, iEq);
		final String value = keyValue.substring(iFirstQuote+1, iLastQuote);
		
		map.put(key, value);
	}
	
	private String loadOriginalToken(int userId) {
		DatabaseConnector db = new DatabaseConnector();
		final String token = db.getUploadToken(userId);
		db.close();
		
		return token;
	}
	
	private boolean parseTransportHeader(String transport, RtspParticipant participant, String host) {
		final String[] entries = transport.split(";");
		
		/* 
		 * this server expects at least 3 information strings:
		 *  - underlying streaming protocol: RTP
		 *  - a unicast connection
		 *  - the client ports for the data and control information
		 */
		if(entries.length<3) {
			return false;
		}
		if(!entries[0].contains("RTP")) {
			return false;
		}
		if(!entries[1].equals("unicast")) {
			return false;
		}
		
		// parse client ports:
		final int iOfEQ = entries[2].indexOf("=");
		final int iOfMin = entries[2].indexOf("-");
		final String dataPortString = entries[2].substring(iOfEQ+1, iOfMin);
		final String controlPortString = entries[2].substring(iOfMin+1, entries[2].length());
		try {
			final RtpParticipant rtpParticipant = RtpParticipant.createReceiver(
					host, 
					Integer.valueOf(dataPortString), 
					Integer.valueOf(controlPortString));
			participant.setRtpParticipant(rtpParticipant);
		} catch(NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	private String buildTransportString(String clientTransport, String ssrc) {
		String transport = clientTransport;
		transport += ";source=" + this.sm.getHost();
		transport += ";server_port=" + this.sm.getDataPort() + "-" + this.sm.getControlPort();
		transport += ";ssrc=" + ssrc;
		return transport;
	}
	
	// response creators
	private HttpResponse createOptionsResponse(int cseq, String options) {
		final HttpResponse optionsResponse = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		optionsResponse.headers().add(RtspHeaders.Names.CACHE_CONTROL, RtspHeaders.Values.NO_CACHE);
		optionsResponse.headers().add(RtspHeaders.Names.SERVER, SERVER_DESCRIPTION);
		optionsResponse.headers().add(RtspHeaders.Names.CSEQ, cseq);
		optionsResponse.headers().add(RtspHeaders.Names.PUBLIC, options);
		
		return optionsResponse;
	}
	
	private HttpResponse createAnnounceResponse(int cseq, String sessionId) {
		final String timeout = ";timeout=60";
		final HttpResponse announceResponse = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		announceResponse.headers().add(RtspHeaders.Names.CACHE_CONTROL, RtspHeaders.Values.NO_CACHE);
		announceResponse.headers().add(RtspHeaders.Names.SERVER, SERVER_DESCRIPTION);
		announceResponse.headers().add(RtspHeaders.Names.CSEQ, cseq);
		announceResponse.headers().add(RtspHeaders.Names.SESSION, sessionId + timeout);
		
		return announceResponse;
	}	

	private HttpMessage createSetupResponse(int cseq, String sessionId, String transport) {
		final String timeout = ";timeout=60";
		final Date now = new Date();
		final HttpResponse setupResponse = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		setupResponse.headers().add(RtspHeaders.Names.CACHE_CONTROL, RtspHeaders.Values.NO_CACHE);
		setupResponse.headers().add(RtspHeaders.Names.SERVER, SERVER_DESCRIPTION);
		setupResponse.headers().add(RtspHeaders.Names.CSEQ, cseq);
		setupResponse.headers().add(RtspHeaders.Names.SESSION, sessionId + timeout);
		setupResponse.headers().add(RtspHeaders.Names.DATE, now);
		setupResponse.headers().add(RtspHeaders.Names.EXPIRES, now);
		setupResponse.headers().add(RtspHeaders.Names.TRANSPORT, transport);
		
		return setupResponse;
	}
	
	private HttpMessage createRecordResponse(int cseq, String sessionId) {
		final String timeout = ";timeout=60";
		final String range = "npt=now-";
		final HttpResponse recordResponse = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		recordResponse.headers().add(RtspHeaders.Names.CACHE_CONTROL, RtspHeaders.Values.NO_CACHE);
		recordResponse.headers().add(RtspHeaders.Names.SERVER, SERVER_DESCRIPTION);
		recordResponse.headers().add(RtspHeaders.Names.CSEQ, cseq);
		recordResponse.headers().add(RtspHeaders.Names.SESSION, sessionId + timeout);
		recordResponse.headers().add(RtspHeaders.Names.RANGE, range);
		
		return recordResponse;
	}
	
	private HttpMessage createTeardownResponse(int cseq, String sessionId) {
		final String timeout = ";timeout=60";
		final HttpResponse recordResponse = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		recordResponse.headers().add(RtspHeaders.Names.CACHE_CONTROL, RtspHeaders.Values.NO_CACHE);
		recordResponse.headers().add(RtspHeaders.Names.SERVER, SERVER_DESCRIPTION);
		recordResponse.headers().add(RtspHeaders.Names.CSEQ, cseq);
		recordResponse.headers().add(RtspHeaders.Names.SESSION, sessionId + timeout);
		
		return recordResponse;
	}

	private HttpMessage createErrorResponse(HttpResponseStatus status, HttpHeaders headers) {
		final HttpResponse errorResponse = new DefaultHttpResponse(RtspVersions.RTSP_1_0, status);
		final String seq = headers.get(RtspHeaders.Names.CSEQ);
		if(seq != null)
			errorResponse.headers().add(RtspHeaders.Names.CSEQ, seq);
		return errorResponse;
	}
}
