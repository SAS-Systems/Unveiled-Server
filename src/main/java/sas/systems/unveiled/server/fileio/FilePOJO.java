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

import java.util.Date;

/**
 * File POJO class. Represents a database entity.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public class FilePOJO {
	
	private final int ownerId;
	private final String filename;
	
	private String caption;
	private String fileUrl;
	private String thumbnailUrl;
	private String mediatype;
	private Date uploadedAt;
	private long size;
	private double lat;
	private double lng;
	private boolean isPublic;
	private boolean isVerified;
	private int length;
	private int heigth;
	private int width;
	private String resolution;
	
	/**
	 * 
	 * @param ownerId foreign key
	 * @param filename with suffix
	 */
	public FilePOJO(int ownerId, String filename) {
		super();
		this.ownerId = ownerId;
		this.caption = filename;
		this.filename = filename;
		this.fileUrl = "";
		this.thumbnailUrl = "";
		this.mediatype = "video/mp4";
		this.uploadedAt = new Date();
		this.size = 0;
		this.lat = 0.;
		this.lng = 0.;
		this.isPublic = false;
		this.isVerified = false;
		this.length = 0;
		this.heigth = 0;
		this.width = 0;
		this.resolution = "n/a";
	}
	
	public int getOwnerId() {
		return ownerId;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getFilename() {
		return filename;
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
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
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
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getHeigth() {
		return heigth;
	}
	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

}
