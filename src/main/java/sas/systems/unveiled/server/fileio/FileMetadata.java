package sas.systems.unveiled.server.fileio;

import java.util.Date;

public class FileMetadata {

	private String caption;
	private String mediatype;
	private Date uploadedAt;
	private long size;
	
	public FileMetadata(String caption, String mediatype, Date uploadedAt, long size) {
		super();
		this.caption = caption;
		this.mediatype = mediatype;
		this.uploadedAt = uploadedAt;
		this.size = size;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getMediatype() {
		return mediatype;
	}

	public void setMediatype(String mediatype) {
		this.mediatype = mediatype;
	}

	public Date getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(Date uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
}
