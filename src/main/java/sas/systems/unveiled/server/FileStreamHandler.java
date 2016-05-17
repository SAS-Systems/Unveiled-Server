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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas.systems.imflux.packet.DataPacket;
import sas.systems.imflux.participant.RtpParticipantInfo;
import sas.systems.imflux.session.rtp.RtpSession;
import sas.systems.imflux.session.rtp.RtpSessionDataListener;
import sas.systems.unveiled.server.fileUpload.FilePOJO;
import sas.systems.unveiled.server.fileUpload.FileWriter;
import sas.systems.unveiled.server.util.DatabaseConnector;
import sas.systems.unveiled.server.util.PropertiesLoader;

/**
 * Handler class for retrieving the streamed file content and saving it on the file
 * system and the meta information in the database.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public class FileStreamHandler implements RtpSessionDataListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileStreamHandler.class);
	
	private final int id;
	private final SessionManager sm;
	private final DatabaseConnector dbConnection;
	private final String mediaLocation;
	private final String mediaUrlPrefix;
	
	private long ssrc;
	private int author;
	private String filename;
	private String suffix;
	private FileWriter fileWriter;
	
	

	// Constructors ---------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public FileStreamHandler(SessionManager sessionManager, DatabaseConnector database) {
		this.sm = sessionManager;
		this.dbConnection = database;
		this.id = new Random().nextInt(100);
		
		final Properties props = PropertiesLoader.loadPropertiesFile(PropertiesLoader.SESSIONS_PROPERTIES_FILE);
		this.mediaLocation = props.getProperty(PropertiesLoader.SessionProps.SYSTEM_PATH_TO_MEDIA);
		this.mediaUrlPrefix = props.getProperty(PropertiesLoader.SessionProps.URL_MEDIA_PATH_PREFIX);
	}
	
	// RtpSessionDataListener -----------------------------------------------------------------------------------------
	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		if(participant.getSsrc() != this.ssrc) {
			return;
		}
		
		// logic:
		try {
			fileWriter.writeToFile(packet);
		} catch (IOException e) {
			LOG.debug("Could not write DataPacket to File! Try again...", e);
			try {
				fileWriter.writeToFile(packet);
			} catch(IOException e1) {
				LOG.error("2nd try writing DataPacket to File also failed. Will close FileStream!", e1);
				finalize();
			}
		}
	}

	// public methods -------------------------------------------------------------------------------------------------
	/**
	 * Initializes this Handler by opening a FileHandle to the specified media file and registering it at the 
	 * {@link SessionManager} for receiving the file content.
	 * 
	 * @param ssrc id of the sending participant to filter the receiving packets
	 * @param author
	 * @param filename
	 * @param suffix (filetype)
	 */
	public void initialize(long ssrc, int author, String filename, String suffix) {
		this.ssrc = ssrc;
		this.author = author;
		this.filename = filename;
		this.suffix = suffix;
		
		this.fileWriter = new FileWriter(this.mediaLocation + this.author + "/", this.filename, this.suffix);
		sm.registerListener(this);
	}
	
	/**
	 * Tells this handler, that all content was received. Writes the file to the file system and creates the 
	 * corresponding database entry. Afterwards realeses all used resources
	 */
	public void finalize() {
		sm.unregisterListener(this);
		try {
			File fileHandle = fileWriter.close();
			LOG.debug("{} ({} bytes) was written to filesystem.", fileHandle, fileHandle.length());
			
			// store meta information in the database
			final String mediatype = "";	// FIXME standard value? with mapping suffix -> MIMEtype? 
			final String thumbnailUrl = ""; // FIXME generate thumbnail
			final int length = 0;			// FIXME calculate length [in seconds]
			final int height = 0;			// FIXME calculate resolution [height]x[width]
			final int width = 0;
			final String resolution = height + "x" + width; 
			
			FilePOJO fileEntity = new FilePOJO(author, fileHandle.getName(), filename, 
					this.mediaUrlPrefix + String.valueOf(author) + "/" + fileHandle.getName(), 
					thumbnailUrl, 
					mediatype, 
					new Date(), 
					fileHandle.length(), 
					0, 0, 
					false, false, 
					length, 
					height, width, resolution);

			if(!dbConnection.insertFile(fileEntity)) {
				LOG.error("Could not write file metadata of file {} to database!", fileHandle.getName());
			}
			
		} catch (IOException e) {
			LOG.error("File was not completely written!", e);
			System.err.println("File was not completely written.");
			e.printStackTrace();
		}
	}
		
	// Getter & Setter ------------------------------------------------------------------------------------------------
	public int getId() {
		return id;
	}

	public String getFilename() {
		return filename;
	}

	public String getFiletype() {
		return suffix;
	}
}
