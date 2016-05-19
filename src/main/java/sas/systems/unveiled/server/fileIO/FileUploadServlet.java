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
package sas.systems.unveiled.server.fileIO;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas.systems.unveiled.server.util.DatabaseConnector;
import sas.systems.unveiled.server.util.PropertiesLoader;

@WebServlet("/UploadFile")
// threshold for storing files on disk (1MB), max file size (5GB)
@MultipartConfig(fileSizeThreshold=1024*1024, maxFileSize=1024*1024*1024*5)
public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = -6308606465526504820L;
	private static final Logger LOG = LoggerFactory.getLogger(FileUploadServlet.class);
	
	private final String mediaFolder;
	private final String urlMediaPathPrefix;
	private final String urlDefaultThumbnail;
	
	private DatabaseConnector database;

	public FileUploadServlet() {
		Properties props = PropertiesLoader.loadPropertiesFile(PropertiesLoader.MEDIA_PROPERTIES_FILE);
		this.urlMediaPathPrefix = props.getProperty(PropertiesLoader.MediaProps.URL_MEDIA_PATH_PREFIX);
		this.mediaFolder = props.getProperty(PropertiesLoader.MediaProps.SYSTEM_PATH_TO_MEDIA);
		this.urlDefaultThumbnail = this.urlMediaPathPrefix 
				+ props.getProperty(PropertiesLoader.MediaProps.REL_PATH_TO_DEFAULT_THUMBNAIL);
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		// create database connection
		this.database = new DatabaseConnector();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check authorization
		if(!authenticateUserWithToken(request.getHeader("user"), request.getHeader("token"))) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token is invalid or not given");
			return;
		}
		
		// get file content
		final Part filePart = request.getPart("file");
		if(filePart == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no file found in request (use: parameter 'file' for file content)");
			return;
		}
		// read parameters:
		String filePartName = "undefined.unv";
		int author = -1;
		double lat = .0;
		double lng = .0;
		boolean isPublic = false;
		if(getFileName(filePart) != null)
			filePartName = getFileName(filePart);
		if(request.getParameter("author") != null) {
			try {
				author = Integer.valueOf(request.getParameter("author"));
			} catch(NumberFormatException e) {};
		}
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
		
		// create and write to file
		final String filename = filePartName.substring(0, filePartName.indexOf('.'));
		final String suffix = filePartName.substring(filePartName.indexOf('.')+1, filePartName.length());
		final long startTime = System.nanoTime();
		final String location = this.mediaFolder + String.valueOf(author) + "/";
		final FileWriter writer = new FileWriter(location, filename, suffix);
		File fileHandle;
		try {
			writer.writeToFile(filePart.getInputStream());
			fileHandle = writer.close();
		} catch(IOException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
		
		// create database entry
		final String caption = fileHandle.getName();
		final String fileUrl = this.urlMediaPathPrefix + String.valueOf(author) + "/" + fileHandle.getName();
		final String thumbnailUrl = this.urlDefaultThumbnail; // FIXME generate thumbnail
		final String mediatype = filePart.getContentType();
		final int length = 0;			// FIXME calculate length [in seconds]
		final int height = 0;			// FIXME calculate resolution [height]x[width]
		final int width = 0;
		final String resolution = height + "x" + width; 
		FilePOJO fileEntity = new FilePOJO(author, caption, filename, fileUrl, thumbnailUrl, mediatype, 
				new Date(), fileHandle.length(), lat, lng, isPublic, false, length, height, width, resolution);
		final boolean wasInserted = this.database.insertFile(fileEntity);
		
		// release resources
		filePart.delete();
				
		// send result
		final long endTime = System.nanoTime();
		final long elapsedTimeNs = endTime - startTime;
		final double elapsedTimeS = elapsedTimeNs/(1e9);
		response.getWriter().println(filename + "." + suffix + " from " + author + " was succefully uploaded in " + elapsedTimeS + " seconds!");
		response.getWriter().println("Status of the database: " + wasInserted + " (was inserted)");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		// close database connection
		this.database.close();
	}
	
	private String getFileName(final Part part) {
	    final String partHeader = part.getHeader("content-disposition");
	    LOG.info("Part Header = {}", partHeader);
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
	
	private boolean authenticateUserWithToken(String user, String token) {
		int userId = 0;
		try {
			userId = Integer.valueOf(user);
		} catch (NumberFormatException e) {
			return false;
		}
		final String realToken = this.database.getUploadToken(userId);
		if(realToken == null) {
			return false;
		}
		return realToken.equals(token);
	}
}
