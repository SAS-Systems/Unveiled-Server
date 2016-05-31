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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;

/**
 * Class for parsing the content of ANNOUNCE requests. The used protocol is
 * <a href="https://en.wikipedia.org/wiki/Session_Description_Protocol">SDP 
 * (session description protocol)</a>. This class does not implement any RFC 
 * and is only applicable for the Unveiled Server use case!
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public class SdpParser {
	
	private static final char VERSION = 'v';
	private static final char ORIGINATOR = 'o';
	private static final char NAME = 's';
	private static final char SESSION_INFO = 'i';
	private static final char CONNECTION_INFO = 'c';
	private static final char SESSION_TIME = 't';
	private static final char ATTRIBUTE = 'a';
	private static final char MEDIA_INFO = 'm';
	
	private final ByteBuf content;
	
	private int version;
	private String originator;
	private String name;
	private String sessionInfo;
	private String connectionInfo;
	private long sessionTime;
	private int mediaType;
	private String mediaProtocol;
	private int mediaPort;
	private String detailledMediaType;
	
	public SdpParser(ByteBuf content) {
		super();
		this.content = content;
		decode1();
	}
	
	private void decode1() {
		final List<String> lines = new LinkedList<>();
		
		// parse to characters
		final byte[] buf = new byte[content.readableBytes()];
		content.readBytes(buf);
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf), Charset.forName("UTF-8")));
		String line;
		try {
			while((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			throw new RuntimeException("There was an unexpected error parsing the content!");
			// should not occur
		}
		
		// extract key-value pairs
		final List<String> attributes = new LinkedList<>();
		final Map<Character, String> properties = new HashMap<>();
		for (String string : lines) {
			final int eqIndex = 2;
			final int eol = string.length();
			
			// save attributes in separate list
			if(string.startsWith(ATTRIBUTE + "=")) {
				attributes.add(string.substring(eqIndex, eol).trim());
				continue;
			}
			properties.put(string.charAt(0), string.substring(eqIndex, eol).trim());
		}
		decode2(properties);
		decode3(attributes);
	}
	
	private void decode2(Map<Character, String> properties) {
		// parse properties
		this.version = Integer.valueOf(properties.get(VERSION));
		this.originator = properties.get(ORIGINATOR);
		this.name = properties.get(NAME);
		this.sessionInfo = properties.get(SESSION_INFO);
		this.connectionInfo = properties.get(CONNECTION_INFO);
		
		final String timeString = properties.get(SESSION_TIME);
		this.sessionTime = Long.parseLong(timeString.substring(0, timeString.indexOf(' ')));
		
		final String[] mediaInfo = properties.get(MEDIA_INFO).split(" ");
		if(mediaInfo.length > 1) {
			this.mediaPort = Integer.parseInt(mediaInfo[1]);
		}
		if(mediaInfo.length > 2) {
			this.mediaProtocol = mediaInfo[2];
		}
		if(mediaInfo.length > 3) {
			this.mediaType = Integer.parseInt(mediaInfo[3]);
		}
	}
	
	private void decode3(List<String> attributes) {
		// parse attributes
		final String mediaAttr = attributes.get(1);
		this.detailledMediaType = mediaAttr;
		//...
	}

	public int getVersion() {
		return version;
	}

	public String getOriginator() {
		return originator;
	}

	public String getName() {
		return name;
	}

	public String getSessionInfo() {
		return sessionInfo;
	}

	public String getConnectionInfo() {
		return connectionInfo;
	}

	public long getSessionTime() {
		return sessionTime;
	}

	public int getMediaType() {
		return mediaType;
	}

	public String getMediaProtocol() {
		return mediaProtocol;
	}

	public int getMediaPort() {
		return mediaPort;
	}

	public String getDetailledMediaType() {
		return detailledMediaType;
	}
}
