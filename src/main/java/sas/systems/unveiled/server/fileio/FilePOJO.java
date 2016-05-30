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

/**
 * File POJO class. Represents a database entity.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public class FilePOJO {
	
	private int ownerId;
	private String filename;
	private String fileUrl;
	private String thumbnailUrl;
	private FileLocation location;
	private FileMetadata metadata;
	private FileMediaData mediadata;
	private boolean isPublic;
	private boolean isVerified;
	
	/**
	 * 
	 * @param ownerId foreign key
	 * @param filename with suffix
	 * @param fileUrl absolute
	 * @param thumbnailUrl absolute
	 * @param location
	 * @param metadata
	 * @param isPublic
	 * @param isVerified
	 */
	public FilePOJO(int ownerId, String filename, String fileUrl, String thumbnailUrl,
			FileLocation location, FileMetadata metadata, FileMediaData mediadata, boolean isPublic, boolean isVerified) {
		super();
		this.ownerId = ownerId;
		this.filename = filename;
		this.fileUrl = fileUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.location = location;
		this.metadata = metadata;
		this.isPublic = isPublic;
		this.isVerified = isVerified;
		this.mediadata = mediadata;
	}
	
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public boolean isVerified() {
		return isVerified;
	}
	public void setVerified(boolean isVerfied) {
		this.isVerified = isVerfied;
	}
	public FileLocation getLocation() {
		return location;
	}
	public void setLocation(FileLocation location) {
		this.location = location;
	}
	public FileMetadata getMetadata() {
		return metadata;
	}
	public void setMetadata(FileMetadata metadata) {
		this.metadata = metadata;
	}
	public FileMediaData getMediadata() {
		return mediadata;
	}
	public void setMediadata(FileMediaData mediadata) {
		this.mediadata = mediadata;
	}
}
