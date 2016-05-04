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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import sas_systems.imflux.packet.DataPacket;
import sas_systems.imflux.participant.RtpParticipantInfo;
import sas_systems.imflux.session.RtpSession;
import sas_systems.imflux.session.RtpSessionDataListener;

public class DataToFileWriter implements RtpSessionDataListener {
	
	private final DataHandler parent;
	private final long ssrc;
	private final int payloadType;
	private File fileHandle;
	private OutputStream out;

	public DataToFileWriter(DataHandler parent, long ssrc, int payloadType) {
		this.parent = parent;
		this.ssrc = ssrc;
		this.payloadType = payloadType;
		
		try {
			initFileHandle();
			this.out = new FileOutputStream(fileHandle);
		} catch (IOException e) {
			System.err.println(e.toString());
			this.out = null;
		}
	}

	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		try {
			if(out != null) {
				out.write(packet.getDataAsArray(), 0, packet.getDataSize());
				System.out.println(packet.getDataSize() + " bytes were written to " + fileHandle.getPath());
			}
		} catch (IOException e) {
			System.err.println("Error during writing file!");
		}
	}

	public boolean closeFile() {
		try {
			out.flush();
			out.close();
			System.out.println(fileHandle.getAbsolutePath() + " was created successfully");
			return true;
		} catch(IOException e) {
			try {
				out.close();
			} catch (IOException e1) {
				System.err.println("File can not be closed. Removing listener from context regardless.");
			}
		} finally {
			parent.removeDataListener(this.ssrc);
		}
		return false;
	}
	
	private void initFileHandle() throws IOException {
		String catalinaHome = System.getenv("CATALINA_HOME"); // FIXME: isnt set -> returns null!
		if(catalinaHome == null) 
			catalinaHome = "C:/apache-tomcat-7.0.69"; 
		
		this.fileHandle = new File(catalinaHome + "/temp/" + this.payloadType + "_file" + this.ssrc + ".unv");
		this.fileHandle.createNewFile();
	}
}
