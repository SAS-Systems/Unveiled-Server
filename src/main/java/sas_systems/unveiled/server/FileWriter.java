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
import java.io.InputStream;
import java.io.OutputStream;

import sas_systems.imflux.packet.DataPacket;

public class FileWriter {
	
	private static final int BUFFERSIZE = 1024;
	
	private File fileHandle;
	private OutputStream out;

	public FileWriter(String author, String location, String filename, String suffix) {
		try {
			initFileHandle(author, location, filename, suffix);
			out = new FileOutputStream(fileHandle);
		} catch(IOException e) {
			fileHandle = null;
			out = null;
			System.err.println("Could not initialize file");
		}
	}

	public void writeToFile(InputStream in) throws IOException {
		if(out == null)
			throw new IOException("FileOutputStream was not created!");
		
		byte[] buffer = new byte[BUFFERSIZE];
		int readBytes = 0;
		
		// write content
		while((readBytes = in.read(buffer)) != -1) {
			out.write(buffer, 0, readBytes);
		}
		out.flush();
		in.close();
	}
	
	public void writeToFile(DataPacket packet) throws IOException {
		if(out == null)
			throw new IOException("FileOutputStream was not created!");
		
		out.write(packet.getDataAsArray(), 0, packet.getDataSize());
		out.flush();
	}
	
	public void writeToFile(byte[] bytes, int offset, int length) throws IOException {
		if(out == null)
			throw new IOException("FileOutputStream was not created!");
		
		out.write(bytes, offset, length);
		out.flush();
	}

	public void initFileHandle(String author, String location, String filename, String suffix) throws IOException {
		// create folders and file
//		String catalinaHome = System.getenv("CATALINA_HOME"); // FIXME: isnt set -> returns null!
//		if(catalinaHome == null) 
//			catalinaHome = "C:/apache-tomcat-7.0.69"; 
		
		File folderHandle = new File(location + author);
		if(!folderHandle.exists())
			folderHandle.mkdirs();
		File fileHandle = new File(folderHandle, filename + "." + suffix);
		int i = 2;
		while(!fileHandle.createNewFile()) {
			fileHandle = new File(folderHandle, filename + i++ + "." + suffix);
		}
		this.fileHandle = fileHandle;
	}
	
	public void close() throws IOException {
		if(out != null) {
			out.flush();
			out.close();
		}
		if(fileHandle != null) {
			fileHandle.setExecutable(false);
			fileHandle.setReadable(true);
			fileHandle.setWritable(true);
			fileHandle.setLastModified(System.currentTimeMillis());
		}
	}
}
