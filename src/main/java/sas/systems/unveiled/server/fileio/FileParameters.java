package sas.systems.unveiled.server.fileio;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Part;

public class FileParameters {
	
	private static final String FILENAME = "filename";
	private static final String USER = "user";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String IS_PUBLIC = "public";

	private final Map<String, Object> parameters;
	private final Part file;
	
	public FileParameters(Part file) {
		this.file = file;
		this.parameters = new HashMap<>();
		
		String filePartName = extractFileName(file);
		if(filePartName == null) {
			filePartName = "undefined.unv";
		}
		this.parameters.put(FILENAME, filePartName);
	}
	
	public Part getFile() {
		return this.file;
	}
	
	public String getFileName() {
		return (String) this.parameters.get(FILENAME);
	}
	
	public int getUser() {
		return (int) this.parameters.get(USER);
	}
	
	public double getLatitude() {
		return (double) this.parameters.get(LATITUDE);
	}
	
	public double getLongitude() {
		return (double) this.parameters.get(LONGITUDE);
	}
	
	public boolean getPublic() {
		return (boolean) this.parameters.get(IS_PUBLIC);
	}
	
	public void setUser(int user) {
		this.parameters.put(USER, user);
	}
	
	public void setLatitude(double latitude) {
		this.parameters.put(LATITUDE, latitude);
	}
	
	public void setLongitude(double longitude) {
		this.parameters.put(LONGITUDE, longitude);
	}
	
	public void setPublic(boolean isPublic) {
		this.parameters.put(IS_PUBLIC, isPublic);
	}
	
	private String extractFileName(final Part part) {
	    final String partHeader = part.getHeader("content-disposition");
	    for (String content : partHeader.split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
}
