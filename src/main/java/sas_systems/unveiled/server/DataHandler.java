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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas_systems.imflux.packet.DataPacket;
import sas_systems.imflux.participant.RtpParticipantInfo;
import sas_systems.imflux.session.RtpSession;
import sas_systems.imflux.session.RtpSessionDataListener;

public class DataHandler implements RtpSessionDataListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(DataHandler.class);
	
	private final int payloadType;
	
	public DataHandler(int payloadType) {
		this.payloadType = payloadType;
	}

	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		LOG.trace("DataHandler received DataPacket: {} from {}", packet, participant.getSsrc());
	}
	
	public int getPayloadType() {
		return this.payloadType;
	}
}
