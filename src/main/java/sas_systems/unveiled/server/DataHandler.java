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
