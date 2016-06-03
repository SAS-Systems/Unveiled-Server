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
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas.systems.imflux.packet.DataPacket;
import sas.systems.imflux.participant.RtpParticipantInfo;
import sas.systems.imflux.participant.RtspParticipant;
import sas.systems.imflux.session.rtp.RtpSession;
import sas.systems.imflux.session.rtp.RtpSessionDataListener;
import sas.systems.unveiled.server.fileio.FilePOJO;
import sas.systems.unveiled.server.fileio.FileWriter;
import sas.systems.unveiled.server.util.DatabaseConnector;
import sas.systems.unveiled.server.util.PropertiesLoader;
import sas.systems.unveiled.server.util.SessionManager;

/**
 * Handler class for retrieving the streamed file content and saving it on the file
 * system and the meta information in the database.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public class FileStreamHandler implements RtpSessionDataListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileStreamHandler.class);
	private static final IllegalArgumentException ALREADY_INITIALIZED =  new IllegalArgumentException("This property can only be set before initialization!");
	
	private final AtomicBoolean isInitialized;
	
	private final int id;
	private final SessionManager sm;
	private final RtspParticipant participant;
	private final String mediaLocation;
	private final String mediaUrlPrefix;
	private final String urlDefaultThumbnail;
	
	private DatabaseConnector dbConnection;
	private FileWriter fileWriter;
	private int author;
	private String filename;
	private int payloadType;
	private String mediaType;
	

	// Constructors ---------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public FileStreamHandler(SessionManager sessionManager, RtspParticipant participant) {
		this.sm = sessionManager;
		this.participant = participant;
		this.id = new Random().nextInt(100);
		this.isInitialized = new AtomicBoolean(false);
		
		final Properties props = PropertiesLoader.loadPropertiesFile(PropertiesLoader.MEDIA_PROPERTIES_FILE);
		this.mediaLocation = props.getProperty(PropertiesLoader.MediaProps.SYSTEM_PATH_TO_MEDIA);
		this.mediaUrlPrefix = props.getProperty(PropertiesLoader.MediaProps.URL_MEDIA_PATH_PREFIX);
		this.urlDefaultThumbnail = this.mediaUrlPrefix 
				+ props.getProperty(PropertiesLoader.MediaProps.REL_PATH_TO_DEFAULT_THUMBNAIL);
	}
	
	// RtpSessionDataListener -----------------------------------------------------------------------------------------
	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		if(!this.isInitialized.get() && participant.getSsrc() != this.participant.getRtpParticipant().getSsrc()) {
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
				tieUp();
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
	public void initialize() {
		if(this.isInitialized.get()) {
			return;
		}
		
		this.dbConnection = new DatabaseConnector();
		final String filename = this.filename.substring(0, this.filename.indexOf('.'));
		final String suffix = this.filename.substring(this.filename.indexOf('.')+1, this.filename.length());
		
		this.fileWriter = new FileWriter(this.mediaLocation + this.author + "/", filename, suffix);
		sm.registerListener(this);
		this.isInitialized.set(true);
	}
	
	/**
	 * Tells this handler, that all content was received. Writes the file to the file system and creates the 
	 * corresponding database entry. Afterwards realeses all used resources
	 */
	public void tieUp() {
		if(!this.isInitialized.getAndSet(false)) {
			return;
		}
		
		sm.unregisterListener(this);
		try {
			File fileHandle = fileWriter.close();
			LOG.debug("{} ({} bytes) was written to filesystem.", fileHandle, fileHandle.length());
			
			// store meta information in the database
			final int length = 0;			// TODO calculate length [in seconds]
			final int height = 0;			// TODO calculate resolution [height]x[width]
			final int width = 0;
			final String resolution = height + "x" + width; 
			
			FilePOJO fileEntity = new FilePOJO(author, fileHandle.getName(), filename, 
					this.mediaUrlPrefix + String.valueOf(author) + "/" + fileHandle.getName(), 
					this.urlDefaultThumbnail, 
					this.mediaType, 
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
			System.out.println("File was not completely written.");
		} finally {
			this.dbConnection.close();
		}
	}
		
	// Getter & Setter ------------------------------------------------------------------------------------------------
	public int getId() {
		return id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFileName(String fileName) {
		if(this.isInitialized.get()) {
			throw ALREADY_INITIALIZED;
		}
		
		this.filename = fileName;
	}

	public int getPayloadType() {
		return payloadType;
	}

	public void setPayloadType(int payloadType) {
		if(this.isInitialized.get()) {
			throw ALREADY_INITIALIZED;
		}
		
		this.payloadType = payloadType;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		if(this.isInitialized.get()) {
			throw ALREADY_INITIALIZED;
		}
		
		this.mediaType = mediaType;
	}
	
	public long getSsrc() {
		return this.participant.getRtpParticipant().getSsrc();
	}

	public RtspParticipant getParticipant() {
		return participant;
	}

	public void setAuthor(int retrieveUsername) {
		if(this.isInitialized.get()) {
			throw ALREADY_INITIALIZED;
		}
		
		this.author = retrieveUsername;
	}
}
