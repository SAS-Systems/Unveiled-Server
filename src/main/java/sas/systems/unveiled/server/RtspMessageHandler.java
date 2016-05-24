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
import sas.systems.imflux.participant.RtspParticipant;
import sas.systems.imflux.session.rtsp.RtspRequestListener;
import sas.systems.imflux.session.rtsp.RtspResponseListener;
import sas.systems.unveiled.server.util.DatabaseConnector;
import sas.systems.unveiled.server.util.SessionManager;

/**
 * Class for handling RTSP request from the clients. It creates a new {@link FileStreamHandler}
 * for every new Participant (SSRC) - File combination.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public class RtspMessageHandler implements RtspRequestListener, RtspResponseListener {

	private final SessionManager sm;
	private final Map<String, RtspParticipant> sessions;
	
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
	public void describeRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
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
			FullHttpRequest fullResponse = (FullHttpRequest) message;
			final ByteBuf content = fullResponse.content();
			
			if(seq != null && contentType != null && contentType.contains("sdp") && content != null) {
				// parse announce content
				
				return;
			}
		}
		
		// otherwise send error message
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.BAD_REQUEST, message.headers()));
	}

	@Override
	public void setupRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
	}

	@Override
	public void teardownRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
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
		// TODO Auto-generated method stub
		System.out.println(message);
		participant.sendMessage(createErrorResponse(RtspResponseStatuses.NOT_IMPLEMENTED, message.headers()));
	}

	// RtspResponseListener -------------------------------------------------------------------------------------------
	@Override
	public void responseReceived(HttpResponse message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}
	
	// private helpers ------------------------------------------------------------------------------------------------

	private boolean isAuthorized(String authorization) {
		final String[] slices = authorization.split(" ");
		if(slices.length < 3) {
			return false;
		}
		
		//final String method = slices[0];
		final String username = slices[1].substring(0, slices[1].length());
		final String nonce = slices[2];
		
		// parse key-value pairs
		Map<String, String> pairs = new HashMap<>();
		parseAndAddPair(pairs, username);
		parseAndAddPair(pairs, nonce);
		
		// check token
		try {
			int userId = Integer.valueOf(pairs.get("username"));
			final String originalToken = loadOriginalToken(userId);
			if(originalToken != null && originalToken.equals(pairs.get("nonce"))) {
				return true;
			}
		} catch(NumberFormatException e) {
			// also return false...see below
		}
		return false;
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
	
	// response creators
	private static HttpResponse createOptionsResponse(int cseq, String options) {
		HttpResponse optionsResponse = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
		optionsResponse.headers().add(RtspHeaders.Names.CACHE_CONTROL, RtspHeaders.Values.NO_CACHE);
		optionsResponse.headers().add(RtspHeaders.Names.SERVER, SERVER_DESCRIPTION);
		optionsResponse.headers().add(RtspHeaders.Names.CSEQ, cseq);
		optionsResponse.headers().add(RtspHeaders.Names.PUBLIC, options);
		
		return optionsResponse;
	}
	
	private HttpMessage createErrorResponse(HttpResponseStatus status, HttpHeaders headers) {
		HttpResponse errorResponse = new DefaultHttpResponse(RtspVersions.RTSP_1_0, status);
		errorResponse.headers().add(headers);
		return errorResponse;
	}
	
	
}
