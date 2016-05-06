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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas_systems.imflux.packet.DataPacket;
import sas_systems.imflux.participant.RtpParticipantInfo;
import sas_systems.imflux.session.rtp.RtpSession;
import sas_systems.imflux.session.rtp.RtpSessionDataListener;

public class DataHandler implements RtpSessionDataListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(DataHandler.class);
	
	private final int payloadType;
	private final String mediaLocation;
	private final Map<Long, RtpSessionDataListener> dataListeners;
	
	public DataHandler(int payloadType, String mediaLocation) {
		this.payloadType = payloadType;
		this.mediaLocation = mediaLocation;
		this.dataListeners = new HashMap<>();
	}

	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		final long ssrc = participant.getSsrc();
		RtpSessionDataListener listener = dataListeners.get(ssrc);
		if(listener == null) {
			listener = addDataListener(ssrc);
		}
		listener.dataPacketReceived(session, participant, packet);
		
		LOG.trace("DataHandler received DataPacket: {} from {}", packet, participant.getSsrc());
		System.out.println("DataHandler received DataPacket: " + packet + " from " + participant.getSsrc());
	}
	
	public int getPayloadType() {
		return payloadType;
	}
	
	private RtpSessionDataListener addDataListener(long ssrc) {
		DataToFileWriter listener = new DataToFileWriter(ssrc, payloadType, mediaLocation);
		dataListeners.put(ssrc, listener);
		return listener;
	}
	
	public void removeDataListener(long ssrc) {
		dataListeners.remove(ssrc);
	}
}
