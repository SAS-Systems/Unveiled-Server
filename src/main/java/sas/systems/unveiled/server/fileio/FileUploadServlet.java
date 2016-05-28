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
package sas.systems.unveiled.server.fileio;

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

/**
 * File upload servlet. It receives a POST request with the file content and the parameters
 * in the multipart body. The file is stored on the disk and a corresponding database entry
 * is stored in the database.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		// create database connection
		this.database = new DatabaseConnector();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check authorization
		if(!authenticateUserWithToken(request.getHeader("user"), request.getHeader("token"))) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token is invalid or not given");
			return;
		}
		
		// read parameters:
		FileParameters params;
		try {
			params = readRequest(request);
		} catch(BadRequestException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		
		// create and write to file
		final long startTime = System.nanoTime();
		File fileHandle;
		try {
			fileHandle = writeFile(params);
		} catch(IOException e) {
			LOG.error("Error during writing of uploaded file!", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
		
		// create database entry
		final boolean wasInserted = createDbEntry(params, fileHandle);
		
		// release resources
		try {
			params.getFile().delete();
		} catch(IOException e) {
			LOG.warn("Was not able to delete temporary file!", e);
		}
				
		// send result
		final long endTime = System.nanoTime();
		final long elapsedTimeNs = endTime - startTime;
		final double elapsedTimeS = elapsedTimeNs/(1e9);
		try {
			response.getWriter().println(params.getFileName() + " from " + params.getUser() + " was succefully uploaded in " + elapsedTimeS + " seconds!");
			response.getWriter().println("Status of the database: " + wasInserted + " (was inserted)");
		} catch(IOException e) {
			LOG.error("No response was send!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		super.destroy();
		
		// close database connection
		this.database.close();
	}
	
	/**
	 * Creates the corresponding database entry to the uploaded file.
	 * 
	 * @param params
	 * @param fileHandle
	 * @return {@code true} if the record was successfully inserted, {@code false} otherwise
	 */
	private boolean createDbEntry(FileParameters params, File fileHandle) {
		final String caption = params.getFile().getName();
		final String fileUrl = this.urlMediaPathPrefix + String.valueOf(params.getUser()) + "/" + caption;
		final String thumbnailUrl = this.urlDefaultThumbnail; // TODO generate thumbnail
		final String mediatype = params.getFile().getContentType();
		final int length = 0;			// TODO calculate length [in seconds]
		final int height = 0;			// TODO calculate resolution [height]x[width]
		final int width = 0;
		final String resolution = height + "x" + width; 
		FilePOJO fileEntity = new FilePOJO(params.getUser(), caption, params.getFileName(), fileUrl, thumbnailUrl, mediatype, 
				new Date(), fileHandle.length(), params.getLatitude(), params.getLongitude(), params.getPublic(), false, length, height, width, resolution);
		
		return this.database.insertFile(fileEntity);
	}

	/**
	 * Saves the uploaded file on the disk.
	 * 
	 * @param params
	 * @return the file handle to get some file metadata
	 * @throws IOException
	 */
	private File writeFile(FileParameters params) throws IOException {
		// build folder and file descriptions
		final String filePartName = params.getFileName();
		final String filename = filePartName.substring(0, filePartName.indexOf('.'));
		final String suffix = filePartName.substring(filePartName.indexOf('.')+1, filePartName.length());
		final String location = this.mediaFolder + String.valueOf(params.getUser()) + "/";
		// write file
		final FileWriter writer = new FileWriter(location, filename, suffix);
		writer.writeToFile(params.getFile().getInputStream());
		
		return writer.close();
	}
	
	/**
	 * Parses the request and extracts the file part and all required parameters.
	 * 
	 * @param request
	 * @return the parsed request enclosed in a {@link FileParameters} object.
	 * @throws BadRequestException
	 */
	private FileParameters readRequest(HttpServletRequest request) throws BadRequestException {
		Part filePart = null;
		
		// get file content
		try{
			filePart = request.getPart("file");
		} catch(IOException | ServletException e) {
			LOG.error("Could not read file part", e);
			filePart = null;
		}
		if(filePart == null) {
			throw new BadRequestException("no file found in request (use: parameter 'file' for file content)");
		}
		
		// get parameters
		final FileParameters fileParameters = new FileParameters(filePart);
		fileParameters.setUser(getIntSilently(request.getHeader("user")));
		fileParameters.setLatitude(getDoubleSilently(request.getParameter("latitude")));
		fileParameters.setLongitude(getDoubleSilently(request.getParameter("longitude")));
		fileParameters.setPublic(getBooleanSilently(request.getParameter("public")));
		
		// return result
		return fileParameters;
	}
	
	/**
	 * Checks whether the request is a valid request or not using the provided user and token headers.
	 * 
	 * @param user
	 * @param token
	 * @return {@code true} if authentication was successful, {@code false} otherwise
	 */
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
	
	/**
	 * Silently parsing the value to a double.
	 * 
	 * @param value
	 * @return default: {@code 0.0}
	 */
	private double getDoubleSilently(String value) {
		if(value != null) {
			try {
				return Double.valueOf(value);
			} catch(NumberFormatException e) {
				LOG.warn("Could not parse parameter: longitude", e);
			}
		}
		return .0;
	}
	
	/**
	 * Silently parsing the value to an integer.
	 * 
	 * @param value
	 * @return default: {@code 0}
	 */
	private int getIntSilently(String value) {
		if(value != null) {
			try {
				return Integer.valueOf(value);
			} catch(NumberFormatException e) {
				LOG.warn("Could not parse parameter: longitude", e);
			}
		}
		return 0;
	}
	
	/**
	 * Silently parsing the value into a boolean.
	 * 
	 * @param value
	 * @return default {@code false} and if successful the parsed value
	 */
	private boolean getBooleanSilently(String value) {
		if(value != null)
			return Boolean.parseBoolean(value);
		
		return false;
	}
	
	/**
	 * Exception class for the parameter parsing method ({@link FileUploadServlet#readRequest(HttpServletRequest)}.
	 * 
	 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
	 */
	private class BadRequestException extends Exception {
		
		private static final long serialVersionUID = 1802835677175282599L;

		public BadRequestException(String string) {
			super(string);
		}
	}
}
