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

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import sas.systems.imflux.participant.RtspParticipant;
import sas.systems.imflux.session.rtsp.RtspRequestListener;
import sas.systems.imflux.session.rtsp.RtspResponseListener;
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
	
	public RtspMessageHandler(SessionManager sessionManager) {
		this.sm = sessionManager;
		this.sessions = new HashMap<>();
	}

	// RtspRequestListener --------------------------------------------------------------------------------------------
	@Override
	public void optionsRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void describeRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void announceRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void setupRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void teardownRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void playRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void pauseRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void getParameterRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void setParameterRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void redirectRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	@Override
	public void recordRequestReceived(HttpRequest message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}

	// RtspResponseListener -------------------------------------------------------------------------------------------
	@Override
	public void responseReceived(HttpResponse message, RtspParticipant participant) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}
}
