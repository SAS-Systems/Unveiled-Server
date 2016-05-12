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
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = -6308606465526504820L;
	
	private final String mediaFolder;
	private final String urlMediaPathPrefix;

	public FileUploadServlet() {
		Properties props = PropertiesLoader.loadPropertiesFile(PropertiesLoader.SESSIONS_PROPERTIES_FILE);
		this.urlMediaPathPrefix = props.getProperty(PropertiesLoader.SessionProps.URL_MEDIA_PATH_PREFIX);
		this.mediaFolder = props.getProperty(PropertiesLoader.SessionProps.SYSTEM_PATH_TO_MEDIA);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// read parameters:
		String filename = "undefined";
		String suffix = "unv";
		int author = -1;
		String mediatype = "";
		double lat = .0;
		double lng = .0;
		boolean isPublic = false;
		boolean isVerified = false;
		if(request.getParameter("filename") != null)
			filename = request.getParameter("filename");
		if(request.getParameter("suffix") != null)
			suffix = request.getParameter("suffix");
		if(request.getParameter("author") != null) {
			try {
				author = Integer.valueOf(request.getParameter("author"));
			} catch(NumberFormatException e) {};
		}
		if(request.getParameter("mediatype") != null)
			mediatype = request.getParameter("mediatype");
		if(request.getParameter("latitude") != null) {
			try {
				lat = Double.valueOf(request.getParameter("latitude"));
			} catch(NumberFormatException e) {};
		}
		if(request.getParameter("longitude") != null) {
			try {
				lng = Double.valueOf(request.getParameter("longitude"));
			} catch(NumberFormatException e) {};
		}
		if(request.getParameter("public") != null)
			isPublic = Boolean.parseBoolean(request.getParameter("public"));
		if(request.getParameter("verified") != null)
			isVerified = Boolean.parseBoolean(request.getParameter("verified"));
		
		// create and write to file
		final long startTime = System.nanoTime();
		final String location = this.mediaFolder + String.valueOf(author) + "/";
		final FileWriter writer = new FileWriter(location, filename, suffix);
		File fileHandle;
		try {
			writer.writeToFile(request.getInputStream());
			fileHandle = writer.close();
		} catch(IOException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
		
		// create database entry
		final String caption = fileHandle.getName();
		final String fileUrl = this.urlMediaPathPrefix + String.valueOf(author) + "/" + fileHandle.getName();
		final String thumbnailUrl = ""; // FIXME generate thumbnail
		final int length = 0;			// FIXME calculate length [in seconds]
		final int height = 0;			// FIXME calculate resolution [height]x[width]
		final int width = 0;
		final String resolution = height + "x" + width; 
		FilePOJO fileEntity = new FilePOJO(author, caption, filename, fileUrl, thumbnailUrl, mediatype, 
				new Date(), fileHandle.length(), lat, lng, isPublic, isVerified, length, height, width, resolution);
		// TODO: should be a "global" member to not be created on every request
		final DatabaseConnector database = new DatabaseConnector();
		final boolean wasInserted = database.insertFile(fileEntity);
		database.close();
				
		// send result
		final long endTime = System.nanoTime();
		final long elapsedTimeNs = endTime - startTime;
		final double elapsedTimeS = elapsedTimeNs/(1e9);
		response.getWriter().println(filename + "." + suffix + " from " + author + " was succefully uploaded in " + elapsedTimeS + " seconds!");
		response.getWriter().println("Status of the database: " + wasInserted + " (was inserted)");
	}
}
